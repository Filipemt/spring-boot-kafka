package com.filipecode.icompras.pedidos.service;

import com.filipecode.icompras.pedidos.model.Pedido;
import com.filipecode.icompras.pedidos.repository.ItemPedidoRepository;
import com.filipecode.icompras.pedidos.repository.PedidoRepository;
import com.filipecode.icompras.pedidos.validator.PedidoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoValidator pedidoValidator;

    public Pedido criarPedido(Pedido pedido) {
        pedidoRepository.save(pedido);
        itemPedidoRepository.saveAll(pedido.getItens());

        return pedido;
    }
}
