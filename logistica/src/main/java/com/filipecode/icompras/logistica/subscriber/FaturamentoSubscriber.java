package com.filipecode.icompras.logistica.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filipecode.icompras.logistica.service.EnvioPedidoService;
import com.filipecode.icompras.logistica.subscriber.dtos.AtualizacaoFaturamentoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FaturamentoSubscriber {

    private final ObjectMapper objectMapper;
    private final EnvioPedidoService envioPedidoService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${icompras.config.kafka.topics.pedidos-faturados}")
    public void listen(String json) {

        log.info("Recebendo pedido para envio: {}", json);

        try {
            var representation = objectMapper.readValue(json, AtualizacaoFaturamentoDTO.class);
            envioPedidoService.enviar(representation.codigo(), representation.urlNotaFiscal());

            log.info("Pedido de código {} processado com sucesso: {}", representation.codigo(), json);
        } catch (Exception e) {
            log.error("Erro ao preparar pedido para envio", e);
        }
    }
}
