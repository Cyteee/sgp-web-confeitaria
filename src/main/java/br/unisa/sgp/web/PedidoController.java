package br.unisa.sgp.web;

import br.unisa.sgp.model.Pedido;
import br.unisa.sgp.service.PedidoService;
import br.unisa.sgp.service.PedidoService.ItemSolicitado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    public record ItemRequest(@NotNull Long produtoId, @Min(1) int quantidade) {
    }

    public record PedidoRequest(@NotBlank String clienteNome, @NotEmpty List<@Valid ItemRequest> itens) {
    }

    public record PedidoResponse(Long id, String clienteNome, String status) {
        static PedidoResponse de(Pedido p) {
            return new PedidoResponse(p.getId(), p.getClienteNome(), p.getStatus().name());
        }
    }

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@Valid @RequestBody PedidoRequest request) {
        List<ItemSolicitado> itens = request.itens().stream()
                .map(i -> new ItemSolicitado(i.produtoId(), i.quantidade()))
                .toList();
        return PedidoResponse.de(pedidoService.criar(request.clienteNome(), itens));
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id) {
        return PedidoResponse.de(pedidoService.buscar(id));
    }

    @PostMapping("/{id}/confirmar")
    public PedidoResponse confirmar(@PathVariable Long id) {
        return PedidoResponse.de(pedidoService.confirmarPedido(id));
    }
}
