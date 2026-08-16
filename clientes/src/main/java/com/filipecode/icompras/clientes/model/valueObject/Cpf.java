package com.filipecode.icompras.clientes.model.valueObject;

public record Cpf(String valor) {

    private static final String FORMATACAO = "[.\\-]";

    public Cpf {
        valor = normalizar(valor);
    }

    public static Cpf of(String valor) {
        if (valor == null) {
            return null;
        }
        return new Cpf(valor);
    }

    private static String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        return valor.replaceAll(FORMATACAO, "");
    }
}
