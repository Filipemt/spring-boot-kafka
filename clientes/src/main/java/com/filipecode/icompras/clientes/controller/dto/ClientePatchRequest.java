package com.filipecode.icompras.clientes.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record ClientePatchRequest(
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String nome,

        @CPF(message = "CPF inválido")
        String cpf,

        @Size(max = 100, message = "Logradouro deve ter no máximo 100 caracteres")
        String logradouro,

        @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
        String numero,

        @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
        String bairro,

        @Email(message = "E-mail inválido")
        @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
        String email,

        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        String telefone
) {
}
