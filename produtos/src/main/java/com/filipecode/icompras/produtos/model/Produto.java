package com.filipecode.icompras.produtos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Data
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "valor_unitario",  nullable = false, precision = 16, scale = 2)
    private BigDecimal valorUnitario;
}
