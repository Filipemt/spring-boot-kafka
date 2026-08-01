package com.filipecode.icompras.logistica.subscriber.dtos;

import com.filipecode.icompras.logistica.model.enums.StatusPedido;

public record AtualizacaoFaturamentoDTO(
        Long codigo, StatusPedido statusPedido, String urlNotaFiscal, String codigoRastreio) {
}
