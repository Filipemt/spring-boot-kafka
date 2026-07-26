package com.filipecode.icompras.faturamento.model;

public record Cliente(
        String nome, String cpf, String logradouro, String numero,
        String bairro, String email, String telefone) {
}
