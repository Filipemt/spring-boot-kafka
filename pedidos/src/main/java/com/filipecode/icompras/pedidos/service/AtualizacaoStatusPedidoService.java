package com.filipecode.icompras.pedidos.service;

import com.filipecode.icompras.pedidos.model.enums.StatusPedido;
import com.filipecode.icompras.pedidos.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizacaoStatusPedidoService {

    private final PedidoRepository pedidoRepository;

    @Transactional
    public void atualizarStatus(Long codigo, StatusPedido statusPedido, String urlNotaFiscal, String rastreio) {
        // Por conta da annotation @Transactional não é necessário pedidoRepository.save()
        pedidoRepository.findById(codigo).ifPresent(pedido -> {
            pedido.setStatus(statusPedido);
            pedido.setUrlNotaFiscal(urlNotaFiscal);
            pedido.setCodigoRastreio(rastreio);
        });
    }
}
