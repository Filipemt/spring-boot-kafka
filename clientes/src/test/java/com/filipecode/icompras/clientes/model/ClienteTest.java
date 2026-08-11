package com.filipecode.icompras.clientes.model;

import com.filipecode.icompras.clientes.model.valueObject.Cpf;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteTest {

    @Test
    void atualizarDadosDeveAlterarTodosOsCampos() {
        Cliente cliente = new Cliente();

        cliente.atualizarDados("Ciclano", Cpf.of("529.982.247-25"), "Rua B", "20",
                "Jardim", "ciclano@email.com", "11988888888");

        assertThat(cliente.getNome()).isEqualTo("Ciclano");
        assertThat(cliente.getCpf()).isEqualTo(Cpf.of("52998224725"));
        assertThat(cliente.getLogradouro()).isEqualTo("Rua B");
        assertThat(cliente.getNumero()).isEqualTo("20");
        assertThat(cliente.getBairro()).isEqualTo("Jardim");
        assertThat(cliente.getEmail()).isEqualTo("ciclano@email.com");
        assertThat(cliente.getTelefone()).isEqualTo("11988888888");
    }

    @Test
    void atualizarDadosParciaisDeveAlterarApenasCamposInformados() {
        Cliente cliente = new Cliente();
        cliente.atualizarDados("Fulano", Cpf.of("52998224725"), "Rua A", "10",
                "Centro", "fulano@email.com", "11999999999");

        cliente.atualizarDadosParciais(null, null, null, null, null, "novo@email.com", null);

        assertThat(cliente.getNome()).isEqualTo("Fulano");
        assertThat(cliente.getCpf()).isEqualTo(Cpf.of("52998224725"));
        assertThat(cliente.getLogradouro()).isEqualTo("Rua A");
        assertThat(cliente.getEmail()).isEqualTo("novo@email.com");
        assertThat(cliente.getTelefone()).isEqualTo("11999999999");
    }

    @Test
    void atualizarDadosParciaisDeveNormalizarCpfQuandoInformado() {
        Cliente cliente = new Cliente();
        cliente.atualizarDados("Fulano", Cpf.of("52998224725"), null, null, null, null, null);

        cliente.atualizarDadosParciais(null, Cpf.of("529.982.247-25"), null, null, null, null, null);

        assertThat(cliente.getCpf()).isEqualTo(Cpf.of("52998224725"));
    }

    @Test
    void desativarDeveMarcarClienteComoInativo() {
        Cliente cliente = new Cliente();

        cliente.desativar();

        assertThat(cliente.getAtivo()).isFalse();
    }

    @Test
    void cpfDeveSerNormalizadoNaCriacao() {
        Cpf cpf = Cpf.of("529.982.247-25");

        assertThat(cpf.valor()).isEqualTo("52998224725");
    }

    @Test
    void cpfNulloDeveContinuarNullo() {
        assertThat(Cpf.of(null)).isNull();
    }
}
