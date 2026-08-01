package com.filipecode.icompras.logistica.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filipecode.icompras.logistica.model.enums.model.AtualizacaoEnvioPedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnvioPedidoPublisher {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${icompras.config.kafka.topics.pedidos-enviados}")
    private String topico;

    public void enviar(AtualizacaoEnvioPedido atualizacaoEnvioPedido) {
        log.info("Publicando pedido enviado: {}", atualizacaoEnvioPedido);

        try {
            var json = objectMapper.writeValueAsString(atualizacaoEnvioPedido);
            kafkaTemplate.send(topico, "Dados pedido enviado", json);
            log.info("Pedido publicado com sucesso: {}, codigo de rastreio: {}", json, atualizacaoEnvioPedido.codigoRastreio());
        } catch (Exception e) {
            log.info("Erro ao publicar envio do pedido", e);
        }
    }
}
