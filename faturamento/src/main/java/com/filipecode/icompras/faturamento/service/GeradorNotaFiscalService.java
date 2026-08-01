package com.filipecode.icompras.faturamento.service;

import com.filipecode.icompras.faturamento.bucket.BucketFile;
import com.filipecode.icompras.faturamento.bucket.BucketService;
import com.filipecode.icompras.faturamento.model.Pedido;
import com.filipecode.icompras.faturamento.publisher.FaturamentoPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeradorNotaFiscalService {

    private final NotaFiscalService notaFiscalService;
    private final BucketService bucketService;
    private final FaturamentoPublisher faturamentoPublisher;

    public void gerar(Pedido pedido) {
        log.info("Gerando nota fiscal para o pedido:{}", pedido.codigo());

        try {
            byte[] byteArray = notaFiscalService.gerarNota(pedido);

            String nomeArquivo = String.format("notafiscal_pedido_%d.pdf", pedido.codigo());

            var file = new BucketFile(
                    nomeArquivo, new ByteArrayInputStream(byteArray), MediaType.APPLICATION_PDF, byteArray.length);

            bucketService.upload(file);

            faturamentoPublisher.publicar(pedido, bucketService.getUrl(nomeArquivo));

            log.info("Nota fiscal gerada com sucesso para o pedido: {}", pedido.codigo());
        } catch (Exception e) {
            log.info("Erro ao gerar nota fiscal para o pedido:{}", pedido.codigo(), e);
        }
    }
}
