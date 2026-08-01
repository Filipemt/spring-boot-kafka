package com.filipecode.icompras.faturamento.dtos;

import com.filipecode.icompras.faturamento.dtos.enums.StatusPedido;

public record AtualizacaoStatusPedidoDTO(
        Long codigo, StatusPedido statusPedido, String urlNotaFiscal) {
}
