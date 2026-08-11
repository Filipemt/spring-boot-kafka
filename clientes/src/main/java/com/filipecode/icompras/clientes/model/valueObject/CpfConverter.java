package com.filipecode.icompras.clientes.model.valueObject;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CpfConverter implements AttributeConverter<Cpf, String> {

    @Override
    public String convertToDatabaseColumn(Cpf cpf) {
        return cpf == null ? null : cpf.valor();
    }

    @Override
    public Cpf convertToEntityAttribute(String valor) {
        return valor == null ? null : Cpf.of(valor);
    }
}
