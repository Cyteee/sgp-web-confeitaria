package br.unisa.sgp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ficha_tecnica")
@Getter
@Setter
@NoArgsConstructor
public class FichaTecnica {

    @EmbeddedId
    private FichaTecnicaId id = new FichaTecnicaId();

    @MapsId("produtoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Produto produto;

    @MapsId("insumoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Insumo insumo;

    private BigDecimal quantidade;
}
