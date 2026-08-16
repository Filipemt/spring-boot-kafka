package com.filipecode.icompras.produtos.controller.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProdutoPatchRequest(
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nome,

        @Positive(message = "Valor unitário deve ser maior que zero")
        BigDecimal valorUnitario
) {
}
