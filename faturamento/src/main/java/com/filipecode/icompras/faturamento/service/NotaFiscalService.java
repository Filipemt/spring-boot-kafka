package com.filipecode.icompras.faturamento.service;

import com.filipecode.icompras.faturamento.model.Pedido;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * JasperReport - Representa o template compilado do relatório. Não possui dados apenas o layout.
 * JRBeanCollectionDataSource - Representa a fonte de dados do relatório. É uma coleção de beans (objetos Java) que são usados para preencher o relatório.
 * JasperPrint - Representa o relatório já preenchido com os dados. É o resultado da combinação do JasperReport com a fonte de dados.
 * JasperFillManager.fillReport(template compilados, parâmetros, coleção de dados) - Preenche o relatório com os dados.
 * JasperExportManager - Responsável por exportar um JasperPrint para um formato (PDF, HTML, etc); Transforma o JasperPrint em um array de bytes.
 */
@Service
public class NotaFiscalService {

    @Value("classpath:reports/nota-fiscal.jrxml")
    private Resource notaFiscal;
    @Value("classpath:reports/logo.png")
    private Resource logo;

    public byte[] gerarNota(Pedido pedido) {
        try (InputStream inputStream = notaFiscal.getInputStream()) {

            Map<String, Object> params = new HashMap<>();
            params.put("NOME", pedido.cliente().nome());
            params.put("CPF", pedido.cliente().cpf());
            params.put("LOGRADOURO", pedido.cliente().logradouro());
            params.put("NUMERO", pedido.cliente().numero());
            params.put("BAIRRO", pedido.cliente().bairro());
            params.put("EMAIL", pedido.cliente().email());
            params.put("TELEFONE", pedido.cliente().telefone());

            params.put("DATA_PEDIDO", pedido.data());
            params.put("TOTAL_PEDIDO", pedido.total());

            var dataSource = new JRBeanCollectionDataSource(pedido.itens());

            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
