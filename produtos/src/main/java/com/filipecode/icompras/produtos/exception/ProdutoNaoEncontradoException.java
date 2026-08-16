package com.filipecode.icompras.produtos.exception;

public class ProdutoNaoEncontradoException extends RuntimeException {

    public ProdutoNaoEncontradoException(Long codigo) {
        super("Produto de código " + codigo + " não encontrado");
    }
}
