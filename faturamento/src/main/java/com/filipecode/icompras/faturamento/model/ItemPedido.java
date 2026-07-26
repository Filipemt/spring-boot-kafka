package com.filipecode.icompras.faturamento.model;

import java.math.BigDecimal;

public record ItemPedido(
        Long codigo, String descricao, Integer quantidade, BigDecimal valorUnitario) {

    public BigDecimal getTotal() {
        return BigDecimal.valueOf(this.quantidade).multiply(this.valorUnitario);
    }

}
