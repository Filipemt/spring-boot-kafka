package com.filipecode.icompras.clientes.controller.dto;

public record ClienteResponse(
        Long codigo,
        String nome,
        String cpf,
        String logradouro,
        String numero,
        String bairro,
        String email,
        String telefone
) {
}
