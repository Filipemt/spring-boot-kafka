package com.filipecode.icompras.produtos.controller;

import com.filipecode.icompras.produtos.model.Produto;
import com.filipecode.icompras.produtos.service.ProdutoService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService service;

    @PostMapping
    public ResponseEntity<Produto> salvar(@RequestBody Produto produto) {
        service.salvar(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @GetMapping("{codigo}")
    public ResponseEntity<Produto> obterDados(@PathVariable("codigo") Long codigo ){
        return service
                .obterPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
