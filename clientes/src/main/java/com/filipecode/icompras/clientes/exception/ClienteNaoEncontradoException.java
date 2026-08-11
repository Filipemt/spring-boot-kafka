package com.filipecode.icompras.clientes.exception;

public class ClienteNaoEncontradoException extends RuntimeException {

    public ClienteNaoEncontradoException(Long codigo) {
        super("Cliente de código " + codigo + " não encontrado");
    }
}
