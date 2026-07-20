package com.filipecode.icompras.pedidos.controller;

import com.filipecode.icompras.pedidos.controller.dto.NovoPedidoDTO;
import com.filipecode.icompras.pedidos.controller.mappers.PedidoMapper;
import com.filipecode.icompras.pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapper mapper;

    @PostMapping
    public ResponseEntity<Object> criarPedido(@RequestBody NovoPedidoDTO requestDTO) {
        var pedido = mapper.map(requestDTO);
        var novoPedido = pedidoService.criarPedido(pedido);
        return ResponseEntity.ok(novoPedido.getCodigo());
    }
}
