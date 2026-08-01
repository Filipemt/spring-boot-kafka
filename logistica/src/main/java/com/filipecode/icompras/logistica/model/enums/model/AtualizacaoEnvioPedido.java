package com.filipecode.icompras.logistica.model.enums.model;

import com.filipecode.icompras.logistica.model.enums.StatusPedido;

public record AtualizacaoEnvioPedido(
        Long codigo, StatusPedido statusPedido, String codigoRastreio) {
}
