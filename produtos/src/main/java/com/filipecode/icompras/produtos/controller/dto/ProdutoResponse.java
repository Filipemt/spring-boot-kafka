package com.filipecode.icompras.produtos.controller.dto;

import java.math.BigDecimal;

public record ProdutoResponse(
        Long codigo,
        String nome,
        BigDecimal valorUnitario
) {
}
