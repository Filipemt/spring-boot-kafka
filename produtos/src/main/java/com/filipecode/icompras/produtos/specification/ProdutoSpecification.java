package com.filipecode.icompras.produtos.specification;

import com.filipecode.icompras.produtos.controller.dto.FiltroProduto;
import com.filipecode.icompras.produtos.model.Produto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ProdutoSpecification {

    private ProdutoSpecification() {
    }

    public static Specification<Produto> comFiltros(FiltroProduto filtro) {
        Specification<Produto> spec = produtoAtivo();

        if (StringUtils.hasText(filtro.nome())) {
            spec = spec.and(nomeContem(filtro.nome()));
        }
        return spec;
    }

    public static Specification<Produto> produtoAtivo() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("ativo"));
    }

    public static Specification<Produto> nomeContem(String nome) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }
}
