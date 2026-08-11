package com.filipecode.icompras.clientes.model;

import com.filipecode.icompras.clientes.model.valueObject.Cpf;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "clientes")
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Long codigo;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "cpf", nullable = false, length = 11)
    private Cpf cpf;

    @Column(name = "logradouro", length = 100)
    private String logradouro;

    @Column(name = "numero", length = 10)
    private String numero;

    @Column(name = "bairro", length = 100)
    private String bairro;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    public void atualizarDados(String nome, Cpf cpf, String logradouro, String numero, String bairro, String email, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.email = email;
        this.telefone = telefone;
    }

    public void atualizarDadosParciais(String nome, Cpf cpf, String logradouro, String numero, String bairro, String email, String telefone) {
        if (nome != null) {
            this.nome = nome;
        }
        if (cpf != null) {
            this.cpf = cpf;
        }
        if (logradouro != null) {
            this.logradouro = logradouro;
        }
        if (numero != null) {
            this.numero = numero;
        }
        if (bairro != null) {
            this.bairro = bairro;
        }
        if (email != null) {
            this.email = email;
        }
        if (telefone != null) {
            this.telefone = telefone;
        }
    }

    public void desativar() {
        this.ativo = false;
    }
}
