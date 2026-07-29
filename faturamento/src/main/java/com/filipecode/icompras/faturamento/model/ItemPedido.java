package com.filipecode.icompras.faturamento.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ItemPedido {

    private Long codigo;
    private String nome;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal total;
}
