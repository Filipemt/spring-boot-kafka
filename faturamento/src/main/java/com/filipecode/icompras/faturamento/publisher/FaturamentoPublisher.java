package com.filipecode.icompras.faturamento.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filipecode.icompras.faturamento.dtos.AtualizacaoStatusPedidoDTO;
import com.filipecode.icompras.faturamento.dtos.enums.StatusPedido;
import com.filipecode.icompras.faturamento.model.Pedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FaturamentoPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${icompras.config.kafka.topics.pedidos-faturados}")
    private String topico;

    public void publicar(Pedido pedido, String urlNotaFiscal) {
        log.info("Publicando pedido no tópico de faturamento: {}", pedido);

        try {
            var representation = new AtualizacaoStatusPedidoDTO(pedido.codigo(), StatusPedido.FATURADO, urlNotaFiscal);
            String json = objectMapper.writeValueAsString(representation);
            log.info("JSON a ser enviado: {}", json);
            
            kafkaTemplate.send(topico, json);
        } catch (Exception e) {
            log.error("Erro ao publicar pedido no tópico de faturamento", e);
        }
    }
}
