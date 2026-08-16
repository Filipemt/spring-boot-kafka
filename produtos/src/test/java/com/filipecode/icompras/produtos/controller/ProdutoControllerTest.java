package com.filipecode.icompras.produtos.controller;

import com.filipecode.icompras.produtos.controller.dto.FiltroProduto;
import com.filipecode.icompras.produtos.controller.dto.ProdutoResponse;
import com.filipecode.icompras.produtos.exception.ProdutoNaoEncontradoException;
import com.filipecode.icompras.produtos.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;

    @Test
    void salvarDeveRetornar201() throws Exception {
        when(produtoService.salvar(any())).thenReturn(produtoResponse());

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Fone Bluetooth",
                                  "valorUnitario": 249.90
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(1))
                .andExpect(jsonPath("$.nome").value("Fone Bluetooth"));
    }

    @Test
    void salvarComNomeVazioDeveRetornar400() throws Exception {
        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "",
                                  "valorUnitario": 249.90
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.campo").value("nome"));
    }

    @Test
    void salvarComValorNuloDeveRetornar400() throws Exception {
        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Fone Bluetooth"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campo").value("valorUnitario"));
    }

    @Test
    void listarDeveRetornarPagina() throws Exception {
        when(produtoService.listar(any(FiltroProduto.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(produtoResponse())));

        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Fone Bluetooth"));
    }

    @Test
    void listarComFiltroDeveRepassarFiltroAoServico() throws Exception {
        when(produtoService.listar(any(FiltroProduto.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(produtoResponse())));

        mockMvc.perform(get("/produtos").param("nome", "Fone"))
                .andExpect(status().isOk());

        ArgumentCaptor<FiltroProduto> captor = ArgumentCaptor.forClass(FiltroProduto.class);
        verify(produtoService).listar(captor.capture(), any(PageRequest.class));
        assertThat(captor.getValue().nome()).isEqualTo("Fone");
    }

    @Test
    void obterDadosDeveRetornar200() throws Exception {
        when(produtoService.obterPorCodigo(1L)).thenReturn(produtoResponse());

        mockMvc.perform(get("/produtos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(1));
    }

    @Test
    void obterDadosInexistenteDeveRetornar404() throws Exception {
        when(produtoService.obterPorCodigo(99L)).thenThrow(new ProdutoNaoEncontradoException(99L));

        mockMvc.perform(get("/produtos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("PRODUTO_NOT_FOUND"));
    }

    @Test
    void atualizarDeveRetornar200() throws Exception {
        when(produtoService.atualizar(any(), any())).thenReturn(produtoResponse());

        mockMvc.perform(put("/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Fone Bluetooth Pro",
                                  "valorUnitario": 299.90
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Fone Bluetooth"));
    }

    @Test
    void atualizarParcialDeveRetornar200() throws Exception {
        when(produtoService.atualizarParcial(any(), any())).thenReturn(produtoResponse());

        mockMvc.perform(patch("/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "valorUnitario": 199.90
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorUnitario").value(249.90));
    }

    @Test
    void deletarDeveRetornar204() throws Exception {
        mockMvc.perform(delete("/produtos/1"))
                .andExpect(status().isNoContent());
    }

    private ProdutoResponse produtoResponse() {
        return new ProdutoResponse(1L, "Fone Bluetooth", new BigDecimal("249.90"));
    }
}
