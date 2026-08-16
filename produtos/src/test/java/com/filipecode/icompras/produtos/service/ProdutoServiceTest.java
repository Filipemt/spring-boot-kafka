package com.filipecode.icompras.produtos.service;

import com.filipecode.icompras.produtos.controller.dto.FiltroProduto;
import com.filipecode.icompras.produtos.controller.dto.ProdutoPatchRequest;
import com.filipecode.icompras.produtos.controller.dto.ProdutoRequest;
import com.filipecode.icompras.produtos.exception.ProdutoNaoEncontradoException;
import com.filipecode.icompras.produtos.mapper.ProdutoMapper;
import com.filipecode.icompras.produtos.model.Produto;
import com.filipecode.icompras.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    private ProdutoService produtoService;

    @BeforeEach
    void setUp() {
        produtoService = new ProdutoService(produtoRepository, Mappers.getMapper(ProdutoMapper.class));
    }

    @Test
    void salvarDevePersistirProduto() {
        ProdutoRequest request = new ProdutoRequest("Fone Bluetooth", new BigDecimal("249.90"));

        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto produto = invocation.getArgument(0);
            produto.setCodigo(1L);
            return produto;
        });

        var response = produtoService.salvar(request);

        assertThat(response.codigo()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Fone Bluetooth");
        assertThat(response.valorUnitario()).isEqualByComparingTo("249.90");
    }

    @Test
    void obterPorCodigoDeveRetornarProduto() {
        when(produtoRepository.findByCodigoAndAtivoTrue(1L)).thenReturn(Optional.of(produtoAtivo()));

        var response = produtoService.obterPorCodigo(1L);

        assertThat(response.codigo()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Fone Bluetooth");
        assertThat(response.valorUnitario()).isEqualByComparingTo("249.90");
    }

    @Test
    void obterPorCodigoDeveLancarExcecaoQuandoNaoExistir() {
        when(produtoRepository.findByCodigoAndAtivoTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produtoService.obterPorCodigo(1L))
                .isInstanceOf(ProdutoNaoEncontradoException.class);
    }

    @Test
    void listarDeveRetornarPaginaDeProdutos() {
        when(produtoRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(produtoAtivo())));

        var page = produtoService.listar(new FiltroProduto("Fone"), PageRequest.of(0, 20));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).nome()).isEqualTo("Fone Bluetooth");
    }

    @Test
    void atualizarDeveAlterarDadosDoProduto() {
        Produto produto = produtoAtivo();
        when(produtoRepository.findByCodigoAndAtivoTrue(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProdutoRequest request = new ProdutoRequest("Fone Bluetooth Pro", new BigDecimal("299.90"));

        var response = produtoService.atualizar(1L, request);

        assertThat(response.nome()).isEqualTo("Fone Bluetooth Pro");
        assertThat(response.valorUnitario()).isEqualByComparingTo("299.90");
    }

    @Test
    void atualizarParcialDeveAlterarApenasCamposInformados() {
        Produto produto = produtoAtivo();
        when(produtoRepository.findByCodigoAndAtivoTrue(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProdutoPatchRequest request = new ProdutoPatchRequest(null, new BigDecimal("199.90"));

        var response = produtoService.atualizarParcial(1L, request);

        assertThat(response.valorUnitario()).isEqualByComparingTo("199.90");
        assertThat(response.nome()).isEqualTo("Fone Bluetooth");
    }

    @Test
    void deletarDeveRealizarExclusaoLogica() {
        Produto produto = produtoAtivo();
        when(produtoRepository.findByCodigoAndAtivoTrue(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        produtoService.deletar(1L);

        assertThat(produto.getAtivo()).isFalse();
    }

    private Produto produtoAtivo() {
        Produto produto = new Produto();
        produto.setCodigo(1L);
        produto.setNome("Fone Bluetooth");
        produto.setValorUnitario(new BigDecimal("249.90"));
        produto.setAtivo(true);
        return produto;
    }
}
