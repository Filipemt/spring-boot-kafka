package com.filipecode.icompras.faturamento.model;

import java.math.BigDecimal;

public record ItemPedido(Long codigo, String descricao, Integer quantidade, BigDecimal valorUnitario) {


}
