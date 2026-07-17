package com.filipecode.icompras.pedidos.controller.dto;

import com.filipecode.icompras.pedidos.model.enums.TipoPagamento;

public record DadosPagamentoDTO(String dados, TipoPagamento tipoPagamento) {
}
