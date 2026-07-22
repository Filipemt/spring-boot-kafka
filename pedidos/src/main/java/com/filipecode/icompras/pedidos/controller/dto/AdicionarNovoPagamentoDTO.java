package com.filipecode.icompras.pedidos.controller.dto;

import com.filipecode.icompras.pedidos.model.enums.TipoPagamento;

public record AdicionarNovoPagamentoDTO(
        Long codigoPedido,
        String dadosCartao,
        TipoPagamento tipoPagamento
) {
}
