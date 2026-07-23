package com.filipecode.icompras.pedidos.publisher.representation;

import com.filipecode.icompras.pedidos.model.enums.StatusPedido;

import java.math.BigDecimal;
import java.util.List;

public record DetalhePedidoDTO(
        Long codigo, Long codigoCliente, String nome, String cpf,
        String logradouro, String numero, String bairro, String email,
        String telefone, String dataPedido,
        BigDecimal total, StatusPedido status,
        List<DetalheItemPedidoDTO> itens
) {
}
