package com.filipecode.icompras.clientes.exception;

public class ClienteJaExistenteException extends RuntimeException {

    public ClienteJaExistenteException(String cpf) {
        super("Já existe cliente cadastrado com o CPF " + cpf);
    }
}
