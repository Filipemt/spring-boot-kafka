package com.filipecode.icompras.pedidos.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filipecode.icompras.pedidos.service.AtualizacaoStatusPedidoService;
import com.filipecode.icompras.pedidos.subscriber.dto.AtualizacaoStatusPedidoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AtualizacaoStatusPedidoSubscribe {

    private final AtualizacaoStatusPedidoService atualizacaoStatusPedidoService;
    private final ObjectMapper objectMapper;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = {
            "${icompras.config.kafka.topics.pedidos-faturados}",
            "${icompras.config.kafka.topics.pedidos-enviados}"
    })
    private void receberAtualizacao(String json) {
        log.info("Recebendo atualização de status do pedido: {}", json);

        try {
            var atualizacaoStatus = objectMapper.readValue(json, AtualizacaoStatusPedidoDTO.class);
            atualizacaoStatusPedidoService.atualizarStatus(
                    atualizacaoStatus.codigo(),
                    atualizacaoStatus.statusPedido(),
                    atualizacaoStatus.urlNotaFiscal(),
                    atualizacaoStatus.codigoRastreio()
            );

            log.info("Status do pedido atualizado com sucesso: {}", json);
        } catch (Exception e) {
            log.error("Erro ao processar atualização de status do pedido: {}", json, e.getMessage());
        }


    }
}
