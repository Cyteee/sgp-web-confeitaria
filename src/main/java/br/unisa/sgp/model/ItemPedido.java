package br.unisa.sgp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_pedido")
@Getter
@Setter
@NoArgsConstructor
public class ItemPedido {

    @EmbeddedId
    private ItemPedidoId id = new ItemPedidoId();

    @MapsId("pedidoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Pedido pedido;

    @MapsId("produtoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Produto produto;

    private int quantidade;
}
