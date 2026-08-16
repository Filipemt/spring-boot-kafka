package com.filipecode.icompras.produtos.controller;

import com.filipecode.icompras.produtos.controller.dto.FiltroProduto;
import com.filipecode.icompras.produtos.controller.dto.ProdutoPatchRequest;
import com.filipecode.icompras.produtos.controller.dto.ProdutoRequest;
import com.filipecode.icompras.produtos.controller.dto.ProdutoResponse;
import com.filipecode.icompras.produtos.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoResponse> salvar(@Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(produtoService.salvar(request));
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponse>> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(produtoService.listar(new FiltroProduto(nome), pageable));
    }

    @GetMapping("{codigo}")
    public ResponseEntity<ProdutoResponse> obterDados(@PathVariable("codigo") Long codigo) {
        return ResponseEntity.ok(produtoService.obterPorCodigo(codigo));
    }

    @PutMapping("{codigo}")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable("codigo") Long codigo, @Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.atualizar(codigo, request));
    }

    @PatchMapping("{codigo}")
    public ResponseEntity<ProdutoResponse> atualizarParcial(@PathVariable("codigo") Long codigo, @Valid @RequestBody ProdutoPatchRequest request) {
        return ResponseEntity.ok(produtoService.atualizarParcial(codigo, request));
    }

    @DeleteMapping("{codigo}")
    public ResponseEntity<Void> deletar(@PathVariable("codigo") Long codigo) {
        produtoService.deletar(codigo);
        return ResponseEntity.noContent().build();
    }
}
