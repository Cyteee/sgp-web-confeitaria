package br.unisa.sgp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "insumo")
@Getter
@Setter
@NoArgsConstructor
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String unidade;

    @NotNull
    @DecimalMin("0")
    private BigDecimal quantidadeEstoque = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0")
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;

    public boolean isAbaixoDoMinimo() {
        return quantidadeEstoque.compareTo(estoqueMinimo) <= 0;
    }
}
