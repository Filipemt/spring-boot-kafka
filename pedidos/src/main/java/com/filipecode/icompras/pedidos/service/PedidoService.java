package com.filipecode.icompras.pedidos.service;

import com.filipecode.icompras.pedidos.client.ClientesClient;
import com.filipecode.icompras.pedidos.client.ProdutosClient;
import com.filipecode.icompras.pedidos.client.ServicoBancarioClient;
import com.filipecode.icompras.pedidos.model.DadosPagamento;
import com.filipecode.icompras.pedidos.model.ItemPedido;
import com.filipecode.icompras.pedidos.model.Pedido;
import com.filipecode.icompras.pedidos.model.enums.StatusPedido;
import com.filipecode.icompras.pedidos.model.enums.TipoPagamento;
import com.filipecode.icompras.pedidos.model.exception.ItemNaoEncontradoException;
import com.filipecode.icompras.pedidos.publisher.PagamentoPublisher;
import com.filipecode.icompras.pedidos.repository.ItemPedidoRepository;
import com.filipecode.icompras.pedidos.repository.PedidoRepository;
import com.filipecode.icompras.pedidos.validator.PedidoValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoValidator pedidoValidator;
    private final ServicoBancarioClient servicoBancarioClient;
    private final ClientesClient apiClientes;
    private final ProdutosClient apiProdutos;
    private final PagamentoPublisher pagamentoPublisher;

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
            prepararPublicarPedidoPago(pedido);
        } else {
            pedido.setStatus(StatusPedido.ERRO_PAGAMENTO);
            pedido.setObservacoes(observacoes);
        }

        pedidoRepository.save(pedido);
    }

    private void prepararPublicarPedidoPago(Pedido pedido) {
        pedido.setStatus(StatusPedido.PAGO);
        carregarDadosCliente(pedido);
        carregarItensPedido(pedido);

        pagamentoPublisher.publicar(pedido);
    }

    @Transactional
    public void adicionarNovoPagamento(Long codigoPedido, String dadosCartao, TipoPagamento tipoPagamento) {
        var pedidoEncontrado = pedidoRepository.findById(codigoPedido);

        if (pedidoEncontrado.isEmpty()) {
            log.info("Pedido não encontrado para o código {}", codigoPedido);

            throw new ItemNaoEncontradoException("Pedido não encontrado para o código " + codigoPedido);
        }

        var pedido = pedidoEncontrado.get();

        DadosPagamento dadosPagamento = new DadosPagamento();
        dadosPagamento.setTipoPagamento(tipoPagamento);
        dadosPagamento.setDados(dadosCartao);

        pedido.setDadosPagamento(dadosPagamento);
        pedido.setStatus(StatusPedido.REALIZADO);
        pedido.setObservacoes("Novo pagamento realizado, aguardando processamento!");

        String novaChavePagamento = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(novaChavePagamento);

        pedidoRepository.save(pedido);
    }

    public Optional<Pedido> carregarDadosCompletosPedido(Long codigoPedido) {
        Optional<Pedido> pedido = pedidoRepository.findById(codigoPedido);
        pedido.ifPresent(this::carregarDadosCliente);
        pedido.ifPresent(this::carregarItensPedido);

        return pedido;
    }

    private void carregarDadosCliente(Pedido pedido) {
        Long codigoCliente = pedido.getCodigoCliente();
        var response = apiClientes.obterDados(codigoCliente);
        pedido.setDadosCliente(response.getBody());
    }

    private void carregarItensPedido(Pedido pedido) {
        List<ItemPedido> itensPedido = itemPedidoRepository.findByPedido(pedido);
        pedido.setItens(itensPedido);
        pedido.getItens().forEach(this::carregarDadosProduto);
    }

    private void carregarDadosProduto(ItemPedido itemPedido) {
        Long codigoProduto = itemPedido.getCodigoProduto();

        var response = apiProdutos.obterDados(codigoProduto);
        assert response.getBody() != null;
        itemPedido.setNome(response.getBody().nome());
    }
}