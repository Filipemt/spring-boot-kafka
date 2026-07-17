package com.filipecode.icompras.pedidos.controller.mappers;

import com.filipecode.icompras.pedidos.controller.dto.ItemPedidoDTO;
import com.filipecode.icompras.pedidos.model.ItemPedido;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

    ItemPedido map(ItemPedidoDTO itemPedidoDTO);
}
