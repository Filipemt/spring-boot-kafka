package com.filipecode.icompras.pedidos.service;

import com.filipecode.icompras.pedidos.model.enums.StatusPedido;
import com.filipecode.icompras.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizacaoStatusPedidoService {

    private final PedidoRepository pedidoRepository;

    public void atualizarStatus(Long codigo, StatusPedido statusPedido, String urlNotaFiscal, String rastreio) {

    }
}
