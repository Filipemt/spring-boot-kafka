package com.filipecode.icompras.clientes.controller;

import com.filipecode.icompras.clientes.controller.dto.ClienteResponse;
import com.filipecode.icompras.clientes.controller.dto.FiltroCliente;
import com.filipecode.icompras.clientes.exception.ClienteJaExistenteException;
import com.filipecode.icompras.clientes.exception.ClienteNaoEncontradoException;
import com.filipecode.icompras.clientes.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    private static final String CPF_VALIDO = "52998224725";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @Test
    void salvarDeveRetornar201() throws Exception {
        when(clienteService.salvar(any())).thenReturn(clienteResponse());

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Fulano",
                                  "cpf": "52998224725",
                                  "logradouro": "Rua A",
                                  "numero": "10",
                                  "bairro": "Centro",
                                  "email": "fulano@email.com",
                                  "telefone": "11999999999"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(1))
                .andExpect(jsonPath("$.nome").value("Fulano"));
    }

    @Test
    void salvarComCpfDuplicadoDeveRetornar409() throws Exception {
        when(clienteService.salvar(any())).thenThrow(new ClienteJaExistenteException(CPF_VALIDO));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Fulano",
                                  "cpf": "52998224725"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.campo").value("cpf"));
    }

    @Test
    void salvarComNomeVazioDeveRetornar400() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "",
                                  "cpf": "52998224725"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.campo").value("nome"));
    }

    @Test
    void salvarComCpfInvalidoDeveRetornar400() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Fulano",
                                  "cpf": "11111111111"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campo").value("cpf"));
    }

    @Test
    void listarDeveRetornarPagina() throws Exception {
        when(clienteService.listar(any(FiltroCliente.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(clienteResponse())));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Fulano"));
    }

    @Test
    void listarComFiltroDeveRepassarFiltroAoServico() throws Exception {
        when(clienteService.listar(any(FiltroCliente.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(clienteResponse())));

        mockMvc.perform(get("/clientes").param("nome", "Fulano").param("cpf", CPF_VALIDO))
                .andExpect(status().isOk());

        ArgumentCaptor<FiltroCliente> captor = ArgumentCaptor.forClass(FiltroCliente.class);
        verify(clienteService).listar(captor.capture(), any(PageRequest.class));
        assertThat(captor.getValue().nome()).isEqualTo("Fulano");
        assertThat(captor.getValue().cpf()).isEqualTo(CPF_VALIDO);
    }

    @Test
    void obterDadosDeveRetornar200() throws Exception {
        when(clienteService.obterPorCodigo(1L)).thenReturn(clienteResponse());

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(1));
    }

    @Test
    void obterDadosInexistenteDeveRetornar404() throws Exception {
        when(clienteService.obterPorCodigo(99L)).thenThrow(new ClienteNaoEncontradoException(99L));

        mockMvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("CLIENTE_NOT_FOUND"));
    }

    @Test
    void atualizarDeveRetornar200() throws Exception {
        when(clienteService.atualizar(any(), any())).thenReturn(clienteResponse());

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Fulano",
                                  "cpf": "52998224725",
                                  "email": "fulano@email.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Fulano"));
    }

    @Test
    void atualizarParcialDeveRetornar200() throws Exception {
        when(clienteService.atualizarParcial(any(), any())).thenReturn(clienteResponse());

        mockMvc.perform(patch("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "novo@email.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("fulano@email.com"));
    }

    @Test
    void deletarDeveRetornar204() throws Exception {
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());
    }

    private ClienteResponse clienteResponse() {
        return new ClienteResponse(1L, "Fulano", CPF_VALIDO,
                "Rua A", "10", "Centro", "fulano@email.com", "11999999999");
    }
}
