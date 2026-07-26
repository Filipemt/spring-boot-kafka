package com.filipecode.icompras.faturamento.mapper;

import com.filipecode.icompras.faturamento.model.Cliente;
import com.filipecode.icompras.faturamento.model.ItemPedido;
import com.filipecode.icompras.faturamento.model.Pedido;
import com.filipecode.icompras.faturamento.subscribe.representation.DetalheItemPedidoDTO;
import com.filipecode.icompras.faturamento.subscribe.representation.DetalhePedidoDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PedidoMapper {

    public Pedido map(DetalhePedidoDTO detalhePedidoDTO) {
        Cliente cliente = new Cliente(
                detalhePedidoDTO.nome(),
                detalhePedidoDTO.cpf(),
                detalhePedidoDTO.logradouro(),
                detalhePedidoDTO.numero(),
                detalhePedidoDTO.bairro(),
                detalhePedidoDTO.email(),
                detalhePedidoDTO.telefone()
        );

        List<ItemPedido> itens = detalhePedidoDTO.itens().stream()
                .map(this::mapItem)
                .toList();

        return new Pedido(
                detalhePedidoDTO.codigo(),
                cliente,
                detalhePedidoDTO.dataPedido(),
                detalhePedidoDTO.total(),
                itens
        );
    }

    private ItemPedido mapItem(DetalheItemPedidoDTO detalheItemPedidoDTO) {
        return new ItemPedido(
                detalheItemPedidoDTO.codigoProduto(),
                detalheItemPedidoDTO.nome(),
                detalheItemPedidoDTO.quantidade(),
                detalheItemPedidoDTO.valorUnitario()
        );
    }
}
