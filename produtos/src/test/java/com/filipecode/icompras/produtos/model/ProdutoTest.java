package com.filipecode.icompras.produtos.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProdutoTest {

    @Test
    void atualizarDadosDeveAlterarTodosOsCampos() {
        Produto produto = new Produto();

        produto.atualizarDados("Fone Bluetooth Pro", new BigDecimal("299.90"));

        assertThat(produto.getNome()).isEqualTo("Fone Bluetooth Pro");
        assertThat(produto.getValorUnitario()).isEqualByComparingTo("299.90");
    }

    @Test
    void atualizarDadosParciaisDeveAlterarApenasCamposInformados() {
        Produto produto = new Produto();
        produto.atualizarDados("Fone Bluetooth", new BigDecimal("249.90"));

        produto.atualizarDadosParciais(null, new BigDecimal("199.90"));

        assertThat(produto.getNome()).isEqualTo("Fone Bluetooth");
        assertThat(produto.getValorUnitario()).isEqualByComparingTo("199.90");
    }

    @Test
    void desativarDeveMarcarProdutoComoInativo() {
        Produto produto = new Produto();

        produto.desativar();

        assertThat(produto.getAtivo()).isFalse();
    }

    @Test
    void ativoDeveSerTruePorPadrao() {
        Produto produto = new Produto();

        assertThat(produto.getAtivo()).isTrue();
    }
}
