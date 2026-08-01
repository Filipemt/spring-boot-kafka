package com.filipecode.icompras.pedidos.subscriber.dto;

import com.filipecode.icompras.pedidos.model.enums.StatusPedido;

public record AtualizacaoStatusPedidoDTO(
        Long codigo, StatusPedido statusPedido, String urlNotaFiscal, String codigoRastreio) {
}
