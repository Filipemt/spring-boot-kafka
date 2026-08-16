package com.filipecode.icompras.clientes.service;

import com.filipecode.icompras.clientes.controller.dto.ClientePatchRequest;
import com.filipecode.icompras.clientes.controller.dto.ClienteRequest;
import com.filipecode.icompras.clientes.controller.dto.FiltroCliente;
import com.filipecode.icompras.clientes.exception.ClienteJaExistenteException;
import com.filipecode.icompras.clientes.exception.ClienteNaoEncontradoException;
import com.filipecode.icompras.clientes.mapper.ClienteMapper;
import com.filipecode.icompras.clientes.model.Cliente;
import com.filipecode.icompras.clientes.model.valueObject.Cpf;
import com.filipecode.icompras.clientes.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    private static final String CPF_VALIDO = "52998224725";

    @Mock
    private ClienteRepository clienteRepository;

    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteService = new ClienteService(clienteRepository, Mappers.getMapper(ClienteMapper.class));
    }

    @Test
    void salvarDeveNormalizarCpfEVerificarDuplicidade() {
        ClienteRequest request = new ClienteRequest("Fulano", "529.982.247-25",
                "Rua A", "10", "Centro", "fulano@email.com", "11999999999");

        when(clienteRepository.existsByCpf(any(Cpf.class))).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            cliente.setCodigo(1L);
            return cliente;
        });

        var response = clienteService.salvar(request);

        assertThat(response.codigo()).isEqualTo(1L);
        assertThat(response.cpf()).isEqualTo(CPF_VALIDO);
        verify(clienteRepository).save(argThat(cliente -> CPF_VALIDO.equals(cliente.getCpf().valor())));
    }

    @Test
    void salvarDeveLancarExcecaoQuandoCpfJaExistir() {
        ClienteRequest request = new ClienteRequest("Fulano", CPF_VALIDO,
                "Rua A", "10", "Centro", "fulano@email.com", "11999999999");

        when(clienteRepository.existsByCpf(any(Cpf.class))).thenReturn(true);

        assertThatThrownBy(() -> clienteService.salvar(request))
                .isInstanceOf(ClienteJaExistenteException.class);
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void obterPorCodigoDeveRetornarCliente() {
        when(clienteRepository.findByCodigoAndAtivoTrue(1L)).thenReturn(Optional.of(clienteAtivo()));

        var response = clienteService.obterPorCodigo(1L);

        assertThat(response.codigo()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Fulano");
        assertThat(response.cpf()).isEqualTo(CPF_VALIDO);
    }

    @Test
    void obterPorCodigoDeveLancarExcecaoQuandoNaoExistir() {
        when(clienteRepository.findByCodigoAndAtivoTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.obterPorCodigo(1L))
                .isInstanceOf(ClienteNaoEncontradoException.class);
    }

    @Test
    void listarDeveRetornarPaginaDeClientes() {
        when(clienteRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(clienteAtivo())));

        var page = clienteService.listar(new FiltroCliente("Fulano", null, null), PageRequest.of(0, 20));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).nome()).isEqualTo("Fulano");
    }

    @Test
    void atualizarDeveAlterarDadosDoCliente() {
        Cliente cliente = clienteAtivo();
        when(clienteRepository.findByCodigoAndAtivoTrue(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClienteRequest request = new ClienteRequest("Ciclano", CPF_VALIDO,
                "Rua B", "20", "Jardim", "ciclano@email.com", "11988888888");

        var response = clienteService.atualizar(1L, request);

        assertThat(response.nome()).isEqualTo("Ciclano");
        assertThat(response.logradouro()).isEqualTo("Rua B");
        verify(clienteRepository, never()).existsByCpf(any());
        verify(clienteRepository).save(cliente);
    }

    @Test
    void atualizarComCpfDiferenteDeveLancarExcecaoQuandoCpfJaExistir() {
        when(clienteRepository.findByCodigoAndAtivoTrue(1L)).thenReturn(Optional.of(clienteAtivo()));
        when(clienteRepository.existsByCpf(any(Cpf.class))).thenReturn(true);

        ClienteRequest request = new ClienteRequest("Ciclano", "11144477735",
                null, null, null, null, null);

        assertThatThrownBy(() -> clienteService.atualizar(1L, request))
                .isInstanceOf(ClienteJaExistenteException.class);
    }

    @Test
    void atualizarParcialDeveAlterarApenasCamposInformados() {
        Cliente cliente = clienteAtivo();
        when(clienteRepository.findByCodigoAndAtivoTrue(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientePatchRequest request = new ClientePatchRequest(null, null, null, null, null, "novo@email.com", null);

        var response = clienteService.atualizarParcial(1L, request);

        assertThat(response.email()).isEqualTo("novo@email.com");
        assertThat(response.nome()).isEqualTo("Fulano");
        assertThat(response.telefone()).isEqualTo("11999999999");
    }

    @Test
    void deletarDeveRealizarExclusaoLogica() {
        Cliente cliente = clienteAtivo();
        when(clienteRepository.findByCodigoAndAtivoTrue(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        clienteService.deletar(1L);

        assertThat(cliente.getAtivo()).isFalse();
        verify(clienteRepository).save(cliente);
    }

    private Cliente clienteAtivo() {
        Cliente cliente = new Cliente();
        cliente.setCodigo(1L);
        cliente.setNome("Fulano");
        cliente.setCpf(Cpf.of(CPF_VALIDO));
        cliente.setLogradouro("Rua A");
        cliente.setNumero("10");
        cliente.setBairro("Centro");
        cliente.setEmail("fulano@email.com");
        cliente.setTelefone("11999999999");
        cliente.setAtivo(true);
        return cliente;
    }
}
