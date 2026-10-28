package br.unisa.sgp.service;

import br.unisa.sgp.exception.EstoqueInsuficienteException;
import br.unisa.sgp.exception.PedidoNaoEncontradoException;
import br.unisa.sgp.exception.RegraDeNegocioException;
import br.unisa.sgp.model.*;
import br.unisa.sgp.repository.InsumoRepository;
import br.unisa.sgp.repository.PedidoRepository;
import br.unisa.sgp.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    public record ItemSolicitado(Long produtoId, int quantidade) {
    }

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final InsumoRepository insumoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoRepository produtoRepository,
                         InsumoRepository insumoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.insumoRepository = insumoRepository;
    }

    @Transactional
    public Pedido criar(String clienteNome, List<ItemSolicitado> itens) {
        Pedido pedido = new Pedido();
        pedido.setClienteNome(clienteNome);
        for (ItemSolicitado solicitado : itens) {
            Produto produto = produtoRepository.findById(solicitado.produtoId())
                    .orElseThrow(() -> new RegraDeNegocioException("Produto não encontrado: " + solicitado.produtoId()));
            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(solicitado.quantidade());
            pedido.getItens().add(item);
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pedido buscar(Long id) {
        return pedidoRepository.findById(id).orElseThrow(() -> new PedidoNaoEncontradoException(id));
    }

    /**
     * Valida o estoque de todos os insumos exigidos pelas fichas técnicas e só então dá a baixa.
     * Se faltar qualquer insumo a exceção desfaz a transação e nada é alterado.
     */
    @Transactional
    public Pedido confirmarPedido(Long id) {
        Pedido pedido = buscar(id);
        if (pedido.getStatus() != StatusPedido.ABERTO) {
            throw new RegraDeNegocioException("Pedido " + id + " já está " + pedido.getStatus());
        }

        Map<Long, BigDecimal> necessario = new HashMap<>();
        for (ItemPedido item : pedido.getItens()) {
            for (FichaTecnica linha : item.getProduto().getFicha()) {
                BigDecimal total = linha.getQuantidade().multiply(BigDecimal.valueOf(item.getQuantidade()));
                necessario.merge(linha.getInsumo().getId(), total, BigDecimal::add);
            }
        }

        List<Insumo> insumos = necessario.isEmpty() ? List.of()
                : insumoRepository.findAllByIdForUpdate(necessario.keySet());

        for (Insumo insumo : insumos) {
            BigDecimal precisa = necessario.get(insumo.getId());
            if (insumo.getQuantidadeEstoque().compareTo(precisa) < 0) {
                throw new EstoqueInsuficienteException("Estoque insuficiente de %s: disponível %s, necessário %s"
                        .formatted(insumo.getNome(), insumo.getQuantidadeEstoque().stripTrailingZeros().toPlainString(),
                                precisa.stripTrailingZeros().toPlainString()));
            }
        }
        for (Insumo insumo : insumos) {
            insumo.setQuantidadeEstoque(insumo.getQuantidadeEstoque().subtract(necessario.get(insumo.getId())));
        }

        pedido.setStatus(StatusPedido.CONFIRMADO);
        return pedido;
    }
}
