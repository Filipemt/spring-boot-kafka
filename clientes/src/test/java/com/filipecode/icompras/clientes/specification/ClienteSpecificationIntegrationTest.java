package com.filipecode.icompras.clientes.specification;

import com.filipecode.icompras.clientes.controller.dto.FiltroCliente;
import com.filipecode.icompras.clientes.model.Cliente;
import com.filipecode.icompras.clientes.model.valueObject.Cpf;
import com.filipecode.icompras.clientes.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ClienteSpecificationIntegrationTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void cpfIgualDeveRetornarClienteComCpfNormalizado() {
        clienteRepository.save(clienteComCpf("11122233344", "João da Silva"));

        List<Cliente> resultado = clienteRepository.findAll(ClienteSpecification.cpfIgual("11122233344"));

        assertThat(resultado).singleElement().satisfies(cliente ->
                assertThat(cliente.getNome()).isEqualTo("João da Silva"));
    }

    @Test
    void nomeContemDeveRetornarClientesPorNomeParcial() {
        clienteRepository.save(clienteComCpf("55566677788", "Maria Oliveira Teste"));

        List<Cliente> resultado = clienteRepository.findAll(ClienteSpecification.nomeContem("oliveira teste"));

        assertThat(resultado).singleElement().satisfies(cliente ->
                assertThat(cliente.getNome()).isEqualTo("Maria Oliveira Teste"));
    }

    @Test
    void comFiltrosDeveCombinarNomeECpf() {
        clienteRepository.save(clienteComCpf("11122233344", "João da Silva"));
        clienteRepository.save(clienteComCpf("55566677788", "Maria Oliveira"));

        List<Cliente> resultado = clienteRepository.findAll(
                ClienteSpecification.comFiltros(new FiltroCliente("joão", "111.222.333-44", null)));

        assertThat(resultado).singleElement().satisfies(cliente ->
                assertThat(cliente.getNome()).isEqualTo("João da Silva"));
    }

    @Test
    void comFiltrosDeveExcluirClientesInativos() {
        Cliente inativo = clienteComCpf("99988877766", "Cliente Inativo");
        inativo.desativar();
        clienteRepository.save(inativo);

        List<Cliente> resultado = clienteRepository.findAll(
                ClienteSpecification.comFiltros(new FiltroCliente(null, null, null)));

        assertThat(resultado)
                .noneMatch(cliente -> cliente.getNome().equals("Cliente Inativo"));
    }

    private Cliente clienteComCpf(String cpf, String nome) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setCpf(Cpf.of(cpf));
        cliente.setLogradouro("Rua A");
        cliente.setNumero("10");
        cliente.setBairro("Centro");
        cliente.setEmail(nome.toLowerCase().replace(" ", ".") + "@email.com");
        cliente.setTelefone("11999999999");
        cliente.setAtivo(true);
        return cliente;
    }
}
