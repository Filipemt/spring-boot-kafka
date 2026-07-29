package com.filipecode.icompras.faturamento.service;

import com.filipecode.icompras.faturamento.bucket.BucketFile;
import com.filipecode.icompras.faturamento.bucket.BucketService;
import com.filipecode.icompras.faturamento.model.Pedido;
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

    public void gerar(Pedido pedido) {
        log.info("Gerando nota fiscal para o pedido:{}", pedido.codigo());

        try {
            byte[] byteArray = notaFiscalService.gerarNota(pedido);

            String nomeArquivo = String.format("notafiscal_pedido_%d.pdf", pedido.codigo());

            var file = new BucketFile(
                    nomeArquivo, new ByteArrayInputStream(byteArray), MediaType.APPLICATION_PDF, byteArray.length);

            bucketService.upload(file);
        } catch (Exception e) {
            log.info("Erro ao gerar nota fiscal para o pedido:{}", pedido.codigo(), e);
        }
    }
}
