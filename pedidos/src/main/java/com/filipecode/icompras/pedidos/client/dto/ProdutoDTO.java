package com.filipecode.icompras.pedidos.client.dto;

import java.math.BigDecimal;

public record ProdutoDTO(Long codigo, String nome, BigDecimal valorUnitario) {
}
