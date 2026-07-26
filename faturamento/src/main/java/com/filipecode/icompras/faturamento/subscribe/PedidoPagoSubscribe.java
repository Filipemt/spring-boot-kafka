package com.filipecode.icompras.faturamento.subscribe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filipecode.icompras.faturamento.mapper.PedidoMapper;
import com.filipecode.icompras.faturamento.model.Pedido;
import com.filipecode.icompras.faturamento.service.GeradorNotaFiscalService;
import com.filipecode.icompras.faturamento.subscribe.representation.DetalhePedidoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PedidoPagoSubscribe {

    private final ObjectMapper objectMapper;
    private final GeradorNotaFiscalService geradorNotaFiscalService;
    private final PedidoMapper pedidoMapper;

    @KafkaListener(groupId = "i-compras-faturamento",
            topics = "${icompras.config.kafka.topics.pedidos-pagos}")
    public void listen(String json) {

        try {
            log.info("Recebendo pedido para faturamento: {}", json);
            var representation = objectMapper.readValue(json, DetalhePedidoDTO.class);
            Pedido pedido = pedidoMapper.map(representation);

            geradorNotaFiscalService.gerar(pedido);

        } catch (Exception e) {
            log.error("Erro na consumação do tópico de pedidos pagos", e.getMessage());
        }
    }
}
