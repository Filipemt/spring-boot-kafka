package com.filipecode.icompras.pedidos.client.dto;

public record ClienteDTO(Long codigo, String nome, String cpf,
                         String logradouro, String numero, String bairro,
                         String email, String telefone) {
}
