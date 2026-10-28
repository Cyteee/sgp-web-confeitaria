package br.unisa.sgp.web;

import br.unisa.sgp.repository.ProdutoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    public record IngredienteResponse(String insumo, BigDecimal quantidade, String unidade) {
    }

    public record ProdutoResponse(Long id, String nome, BigDecimal preco, List<IngredienteResponse> ficha) {
    }

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar() {
        return produtoRepository.findAll().stream()
                .map(p -> new ProdutoResponse(p.getId(), p.getNome(), p.getPreco(),
                        p.getFicha().stream()
                                .map(f -> new IngredienteResponse(f.getInsumo().getNome(), f.getQuantidade(), f.getInsumo().getUnidade()))
                                .toList()))
                .toList();
    }
}
