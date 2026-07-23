package com.filipecode.icompras.pedidos.controller;

import com.filipecode.icompras.pedidos.controller.dto.AdicionarNovoPagamentoDTO;
import com.filipecode.icompras.pedidos.controller.dto.NovoPedidoDTO;
import com.filipecode.icompras.pedidos.controller.mappers.PedidoMapper;
import com.filipecode.icompras.pedidos.publisher.DetalhePedidoMapper;
import com.filipecode.icompras.pedidos.publisher.representation.DetalhePedidoDTO;
import com.filipecode.icompras.pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapper mapper;
    private final DetalhePedidoMapper detalhePedidoMapper;

    @GetMapping("{codigo}")
    public ResponseEntity<DetalhePedidoDTO> obterDetalhesPedido(
            @PathVariable Long codigo) {

        return pedidoService
                .carregarDadosCompletosPedido(codigo)
                .map(detalhePedidoMapper::map)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Object> criarPedido(@RequestBody NovoPedidoDTO requestDTO) {
        var pedido = mapper.map(requestDTO);
        var novoPedido = pedidoService.criarPedido(pedido);

        return ResponseEntity.ok(novoPedido.getCodigo());
    }

    @PostMapping("/pagamentos")
    public ResponseEntity<Object> adicionarNovoPagamento(
            @RequestBody AdicionarNovoPagamentoDTO requestDTO) {

        pedidoService.adicionarNovoPagamento(
                requestDTO.codigoPedido(),
                requestDTO.dadosCartao(),
                requestDTO.tipoPagamento()
        );

        return ResponseEntity.noContent().build();
    }
}
