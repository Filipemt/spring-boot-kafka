package com.filipecode.icompras.produtos.service;

import com.filipecode.icompras.produtos.controller.dto.FiltroProduto;
import com.filipecode.icompras.produtos.controller.dto.ProdutoPatchRequest;
import com.filipecode.icompras.produtos.controller.dto.ProdutoRequest;
import com.filipecode.icompras.produtos.controller.dto.ProdutoResponse;
import com.filipecode.icompras.produtos.exception.ProdutoNaoEncontradoException;
import com.filipecode.icompras.produtos.mapper.ProdutoMapper;
import com.filipecode.icompras.produtos.model.Produto;
import com.filipecode.icompras.produtos.repository.ProdutoRepository;
import com.filipecode.icompras.produtos.specification.ProdutoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;

    @Transactional
    public ProdutoResponse salvar(ProdutoRequest request) {
        Produto produto = produtoMapper.toEntity(request);
        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    public ProdutoResponse obterPorCodigo(Long codigo) {
        return produtoMapper.toResponse(buscarProduto(codigo));
    }

    public Page<ProdutoResponse> listar(FiltroProduto filtro, Pageable pageable) {
        return produtoRepository.findAll(ProdutoSpecification.comFiltros(filtro), pageable)
                .map(produtoMapper::toResponse);
    }

    @Transactional
    public ProdutoResponse atualizar(Long codigo, ProdutoRequest request) {
        Produto produto = buscarProduto(codigo);
        produto.atualizarDados(request.nome(), request.valorUnitario());

        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizarParcial(Long codigo, ProdutoPatchRequest request) {
        Produto produto = buscarProduto(codigo);
        produto.atualizarDadosParciais(request.nome(), request.valorUnitario());

        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public void deletar(Long codigo) {
        Produto produto = buscarProduto(codigo);
        produto.desativar();
        produtoRepository.save(produto);
    }

    private Produto buscarProduto(Long codigo) {
        return produtoRepository.findByCodigoAndAtivoTrue(codigo)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(codigo));
    }
}
