package com.filipecode.icompras.pedidos.validator;

import com.filipecode.icompras.pedidos.client.ClientesClient;
import com.filipecode.icompras.pedidos.client.ProdutosClient;
import com.filipecode.icompras.pedidos.client.dto.ClienteDTO;
import com.filipecode.icompras.pedidos.client.dto.ProdutoDTO;
import com.filipecode.icompras.pedidos.model.ItemPedido;
import com.filipecode.icompras.pedidos.model.Pedido;
import com.filipecode.icompras.pedidos.model.exception.ValidationException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoValidator {

    private final ClientesClient clientesClient;
    private final ProdutosClient produtosClient;

    public void validar(Pedido pedido) {
        Long codigoCliente = pedido.getCodigoCliente();
        validarCliente(codigoCliente);

        pedido.getItens().forEach(this::validarItem);
    }

    private void validarCliente(Long codigoCliente) {
        try{
            var response = clientesClient.obterDados(codigoCliente);
            ClienteDTO cliente = response.getBody();
            log.info("Cliente de código {} obtido com sucesso: {}", cliente.codigo(), cliente.nome());
        }
        catch (FeignException.NotFound e) {
            log.error("Cliente de código {} não encontrado", codigoCliente);

            throw new ValidationException("codigoCliente", e.getMessage());
        }
    }

    private void validarItem(ItemPedido item) {
        try{
            var response = produtosClient.obterDados(item.getCodigoProduto());
            ProdutoDTO produto = response.getBody();
            log.info("Produto obtido de código: {} obtido com sucesso {}", produto.codigo(), produto.nome());
        }
        catch (FeignException.NotFound e) {
            log.error("Produto de código {} não encontrado", item.getCodigoProduto());

            var mensagemError = "Produto de código " + item.getCodigoProduto() + " não encontrado";
            throw new ValidationException("codigoProduto", mensagemError);
        }
    }

}
