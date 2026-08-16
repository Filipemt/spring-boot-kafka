package com.filipecode.icompras.produtos.specification;

import com.filipecode.icompras.produtos.controller.dto.FiltroProduto;
import com.filipecode.icompras.produtos.model.Produto;
import com.filipecode.icompras.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProdutoSpecificationIntegrationTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    void nomeContemDeveRetornarProdutosPorNomeParcial() {
        produtoRepository.save(produtoComNome("Tablet Samsung Galaxy S9"));

        List<Produto> resultado = produtoRepository.findAll(ProdutoSpecification.nomeContem("galaxy s9"));

        assertThat(resultado).singleElement().satisfies(produto ->
                assertThat(produto.getNome()).isEqualTo("Tablet Samsung Galaxy S9"));
    }

    @Test
    void comFiltrosDeveFiltrarPorNome() {
        produtoRepository.save(produtoComNome("Tablet Samsung Galaxy S9"));
        produtoRepository.save(produtoComNome("Fone Bluetooth Pro"));

        List<Produto> resultado = produtoRepository.findAll(
                ProdutoSpecification.comFiltros(new FiltroProduto("tablet samsung")));

        assertThat(resultado).singleElement().satisfies(produto ->
                assertThat(produto.getNome()).isEqualTo("Tablet Samsung Galaxy S9"));
    }

    @Test
    void comFiltrosDeveExcluirProdutosInativos() {
        Produto inativo = produtoComNome("Produto Inativo");
        inativo.desativar();
        produtoRepository.save(inativo);

        List<Produto> resultado = produtoRepository.findAll(
                ProdutoSpecification.comFiltros(new FiltroProduto(null)));

        assertThat(resultado)
                .noneMatch(produto -> produto.getNome().equals("Produto Inativo"));
    }

    private Produto produtoComNome(String nome) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setValorUnitario(new BigDecimal("99.99"));
        produto.setAtivo(true);
        return produto;
    }
}
