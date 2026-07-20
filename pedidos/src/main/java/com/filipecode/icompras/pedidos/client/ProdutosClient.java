package com.filipecode.icompras.pedidos.client;

import com.filipecode.icompras.pedidos.client.dto.ProdutoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "produtos", url = "${clients.produtos.url}")
public interface ProdutosClient {

    @GetMapping("{codigo}")
    ResponseEntity<ProdutoDTO> obterDados(@PathVariable("codigo") Long codigo);
}
