package com.filipecode.icompras.faturamento.subscribe.representation;

import java.math.BigDecimal;

public record DetalheItemPedidoDTO(
        Long codigoProduto, String nome, Integer quantidade, BigDecimal valorUnitario
) {

    public BigDecimal getTotal() {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
