package br.unisa.sgp;

import br.unisa.sgp.exception.EstoqueInsuficienteException;
import br.unisa.sgp.exception.RegraDeNegocioException;
import br.unisa.sgp.model.*;
import br.unisa.sgp.repository.*;
import br.unisa.sgp.service.InsumoService;
import br.unisa.sgp.service.PedidoService;
import br.unisa.sgp.service.PedidoService.ItemSolicitado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class PedidoEstoqueIntegrationTest {

    @Autowired PedidoService pedidoService;
    @Autowired InsumoService insumoService;
    @Autowired InsumoRepository insumoRepository;
    @Autowired ProdutoRepository produtoRepository;
    @Autowired PedidoRepository pedidoRepository;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void limparBanco() {
        jdbc.execute("SET FOREIGN_KEY_CHECKS = 0");
        for (String t : List.of("item_pedido", "pedido", "ficha_tecnica", "produto", "insumo")) {
            jdbc.execute("TRUNCATE TABLE " + t);
        }
        jdbc.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    private Insumo insumo(String nome, String estoque, String minimo) {
        Insumo i = new Insumo();
        i.setNome(nome);
        i.setUnidade("g");
        i.setQuantidadeEstoque(new BigDecimal(estoque));
        i.setEstoqueMinimo(new BigDecimal(minimo));
        return insumoRepository.save(i);
    }

    private Produto produto(String nome, Object... insumoEQuantidade) {
        Produto p = new Produto();
        p.setNome(nome);
        p.setPreco(new BigDecimal("10.00"));
        for (int k = 0; k < insumoEQuantidade.length; k += 2) {
            FichaTecnica f = new FichaTecnica();
            f.setProduto(p);
            f.setInsumo((Insumo) insumoEQuantidade[k]);
            f.setQuantidade(new BigDecimal((String) insumoEQuantidade[k + 1]));
            p.getFicha().add(f);
        }
        return produtoRepository.save(p);
    }

    private BigDecimal estoque(Insumo i) {
        return insumoRepository.findById(i.getId()).orElseThrow().getQuantidadeEstoque();
    }

    @Test
    @DisplayName("CT01 pedido com estoque suficiente confirma e reduz estoque")
    void ct01() {
        Insumo farinha = insumo("Farinha", "1000", "100");
        Produto bolo = produto("Bolo", farinha, "200");
        Pedido pedido = pedidoService.criar("Ana", List.of(new ItemSolicitado(bolo.getId(), 2)));

        Pedido confirmado = pedidoService.confirmarPedido(pedido.getId());

        assertThat(confirmado.getStatus()).isEqualTo(StatusPedido.CONFIRMADO);
        assertThat(pedidoRepository.findById(pedido.getId()).orElseThrow().getStatus()).isEqualTo(StatusPedido.CONFIRMADO);
        assertThat(estoque(farinha)).isEqualByComparingTo("600");
    }

    @Test
    @DisplayName("CT02 insumo insuficiente: erro, estoque intacto, status mantido")
    void ct02() {
        Insumo farinha = insumo("Farinha", "1000", "100");
        Insumo leite = insumo("Leite", "50", "10");
        Produto bolo = produto("Bolo", farinha, "200", leite, "100");
        Pedido pedido = pedidoService.criar("Bia", List.of(new ItemSolicitado(bolo.getId(), 1)));

        assertThatThrownBy(() -> pedidoService.confirmarPedido(pedido.getId()))
                .isInstanceOf(EstoqueInsuficienteException.class)
                .hasMessageContaining("Leite");

        assertThat(estoque(farinha)).isEqualByComparingTo("1000");
        assertThat(estoque(leite)).isEqualByComparingTo("50");
        assertThat(pedidoRepository.findById(pedido.getId()).orElseThrow().getStatus()).isEqualTo(StatusPedido.ABERTO);
    }

    @Test
    @DisplayName("CT03 vários itens usando o mesmo insumo somam a baixa")
    void ct03() {
        Insumo acucar = insumo("Açúcar", "1000", "100");
        Produto brigadeiro = produto("Brigadeiro", acucar, "10");
        Produto torta = produto("Torta", acucar, "150");
        Pedido pedido = pedidoService.criar("Caio", List.of(
                new ItemSolicitado(brigadeiro.getId(), 10),
                new ItemSolicitado(torta.getId(), 2)));

        pedidoService.confirmarPedido(pedido.getId());

        // 10*10 + 2*150 = 400
        assertThat(estoque(acucar)).isEqualByComparingTo("600");
    }

    @Test
    @DisplayName("CT04 insumo abaixo do mínimo é sinalizado")
    void ct04() {
        Insumo ovos = insumo("Ovos", "30", "20");
        Insumo farinha = insumo("Farinha", "1000", "100");
        Produto omelete = produto("Omelete", ovos, "12");
        Pedido pedido = pedidoService.criar("Dani", List.of(new ItemSolicitado(omelete.getId(), 1)));
        assertThat(insumoService.listarAbaixoDoMinimo()).isEmpty();

        pedidoService.confirmarPedido(pedido.getId());

        List<Insumo> alertas = insumoService.listarAbaixoDoMinimo();
        assertThat(alertas).extracting(Insumo::getNome).containsExactly("Ovos");
        assertThat(alertas.get(0).isAbaixoDoMinimo()).isTrue();
        assertThat(insumoRepository.findById(farinha.getId()).orElseThrow().isAbaixoDoMinimo()).isFalse();
    }

    @Test
    @DisplayName("CT05 excluir insumo vinculado a ficha técnica é recusado")
    void ct05() {
        Insumo chocolate = insumo("Chocolate", "500", "50");
        produto("Brownie", chocolate, "100");

        assertThatThrownBy(() -> insumoService.excluir(chocolate.getId()))
                .isInstanceOf(RegraDeNegocioException.class);

        assertThat(insumoRepository.existsById(chocolate.getId())).isTrue();
    }
}
