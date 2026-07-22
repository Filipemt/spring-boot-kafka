package com.filipecode.icompras.pedidos.service;

import com.filipecode.icompras.pedidos.client.ServicoBancarioClient;
import com.filipecode.icompras.pedidos.model.Pedido;
import com.filipecode.icompras.pedidos.model.enums.StatusPedido;
import com.filipecode.icompras.pedidos.repository.ItemPedidoRepository;
import com.filipecode.icompras.pedidos.repository.PedidoRepository;
import com.filipecode.icompras.pedidos.validator.PedidoValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoValidator pedidoValidator;
    private final ServicoBancarioClient servicoBancarioClient;

    @Transactional
    public Pedido criarPedido(Pedido pedido) {
        pedidoValidator.validar(pedido);
        realizarPersistencia(pedido);

        enviarSolicitacaoPagamento(pedido);

        return pedido;
    }

    private void realizarPersistencia(Pedido pedido) {
        pedidoRepository.save(pedido);
        itemPedidoRepository.saveAll(pedido.getItens());
    }

    private void enviarSolicitacaoPagamento(Pedido pedido) {
        String chavePagamento = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(chavePagamento);
    }

    public void atualizarStatusPagamento(Long codigoPedido, String chavePagamento, boolean sucesso, String observacoes) {
        Optional<Pedido> pedidoEncontrado = pedidoRepository.findByCodigoAndChavePagamento(codigoPedido, chavePagamento);

        if (pedidoEncontrado.isEmpty()) {
            var mensagem = String.format("Pedido não encontrado para o código %d e chave de pagamento %s",
                    codigoPedido, chavePagamento);

            log.info(mensagem);
            return;
        }

        Pedido pedido = pedidoEncontrado.get();
        if (sucesso) {
            pedido.setStatus(StatusPedido.PAGO);
        }
        else {
            pedido.setStatus(StatusPedido.ERRO_PAGAMENTO);
            pedido.setObservacoes(observacoes);
        }

        pedidoRepository.save(pedido);
    }
}
