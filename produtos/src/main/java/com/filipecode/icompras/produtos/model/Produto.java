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
    @Column(name = "codigo")
    private Long codigo;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "valor_unitario", nullable = false, precision = 16, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    public void atualizarDados(String nome, BigDecimal valorUnitario) {
        this.nome = nome;
        this.valorUnitario = valorUnitario;
    }

    public void atualizarDadosParciais(String nome, BigDecimal valorUnitario) {
        if (nome != null) {
            this.nome = nome;
        }
        if (valorUnitario != null) {
            this.valorUnitario = valorUnitario;
        }
    }

    public void desativar() {
        this.ativo = false;
    }
}
