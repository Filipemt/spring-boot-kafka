package com.filipecode.icompras.logistica.service;

import com.filipecode.icompras.logistica.model.enums.StatusPedido;
import com.filipecode.icompras.logistica.model.enums.model.AtualizacaoEnvioPedido;
import com.filipecode.icompras.logistica.publisher.EnvioPedidoPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EnvioPedidoService {

    private final EnvioPedidoPublisher envioPedidoPublisher;

    public void enviar(Long codigoPedido, String urlNotaFiscal) {
        var codigoRastreio = gerarCodigoRastreio();
        var atualizacaoRepresentation = new AtualizacaoEnvioPedido(
                codigoPedido,
                StatusPedido.ENVIADO,
                codigoRastreio
        );
        envioPedidoPublisher.enviar(atualizacaoRepresentation);
    }

    private String gerarCodigoRastreio() {
        var random = new Random();

        char letra1 = (char) ('A' + random.nextInt(26));
        char letra2 = (char) ('A' + random.nextInt(26));

        int numeros = 100000000 + random.nextInt(900000000);

        return "BR" + letra1 + letra2 + numeros;
    }

}
