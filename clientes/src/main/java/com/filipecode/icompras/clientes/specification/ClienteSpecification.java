package com.filipecode.icompras.clientes.specification;

import com.filipecode.icompras.clientes.controller.dto.FiltroCliente;
import com.filipecode.icompras.clientes.model.Cliente;
import com.filipecode.icompras.clientes.model.valueObject.Cpf;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ClienteSpecification {

    private ClienteSpecification() {
    }

    public static Specification<Cliente> comFiltros(FiltroCliente filtro) {
        Specification<Cliente> spec = clienteAtivo();

        if (StringUtils.hasText(filtro.nome())) {
            spec = spec.and(nomeContem(filtro.nome()));
        }
        if (StringUtils.hasText(filtro.cpf())) {
            spec = spec.and(cpfIgual(filtro.cpf()));
        }
        if (StringUtils.hasText(filtro.email())) {
            spec = spec.and(emailContem(filtro.email()));
        }
        return spec;
    }

    public static Specification<Cliente> clienteAtivo() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("ativo"));
    }

    public static Specification<Cliente> nomeContem(String nome) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Cliente> cpfIgual(String cpf) {
        Cpf cpfValor = Cpf.of(cpf);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("cpf"), cpfValor);
    }

    public static Specification<Cliente> emailContem(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }
}
