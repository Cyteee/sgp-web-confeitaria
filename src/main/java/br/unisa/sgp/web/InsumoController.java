package br.unisa.sgp.web;

import br.unisa.sgp.model.Insumo;
import br.unisa.sgp.service.InsumoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insumos")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @GetMapping
    public List<Insumo> listar() {
        return insumoService.listar();
    }

    @GetMapping("/abaixo-do-minimo")
    public List<Insumo> abaixoDoMinimo() {
        return insumoService.listarAbaixoDoMinimo();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Insumo criar(@Valid @RequestBody Insumo insumo) {
        return insumoService.salvar(insumo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        insumoService.excluir(id);
    }
}
