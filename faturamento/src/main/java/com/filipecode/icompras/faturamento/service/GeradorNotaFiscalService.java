package com.filipecode.icompras.faturamento.service;

import com.filipecode.icompras.faturamento.model.Pedido;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GeradorNotaFiscalService {

    public void gerar(Pedido pedido) {
        log.info("Gerando nota fiscal para o pedido:{}", pedido.codigo());
        // Lógica para gerar a nota fiscal
    }
}
