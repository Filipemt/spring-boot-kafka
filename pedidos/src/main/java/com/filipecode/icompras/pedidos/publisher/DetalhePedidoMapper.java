package com.filipecode.icompras.pedidos.publisher;

import com.filipecode.icompras.pedidos.model.Pedido;
import com.filipecode.icompras.pedidos.publisher.representation.DetalhePedidoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DetalhePedidoMapper {

    @Mapping(source = "codigo", target = "codigo")
    @Mapping(source = "codigoCliente", target = "codigoCliente")
    @Mapping(source = "dadosCliente.nome", target = "nome")
    @Mapping(source = "dadosCliente.cpf", target = "cpf")
    @Mapping(source = "dadosCliente.logradouro", target = "logradouro")
    @Mapping(source = "dadosCliente.numero", target = "numero")
    @Mapping(source = "dadosCliente.bairro", target = "bairro")
    @Mapping(source = "dadosCliente.email", target = "email")
    @Mapping(source = "dadosCliente.telefone", target = "telefone")
    @Mapping(source = "dataPedido", target = "dataPedido", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "total", target = "total")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "itens", target = "itens")
    DetalhePedidoDTO map(Pedido pedido);
}
