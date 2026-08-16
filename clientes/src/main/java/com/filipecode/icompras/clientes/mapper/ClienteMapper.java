package com.filipecode.icompras.clientes.mapper;

import com.filipecode.icompras.clientes.controller.dto.ClienteRequest;
import com.filipecode.icompras.clientes.controller.dto.ClienteResponse;
import com.filipecode.icompras.clientes.model.Cliente;
import com.filipecode.icompras.clientes.model.valueObject.Cpf;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClienteMapper {

    Cliente toEntity(ClienteRequest request);

    ClienteResponse toResponse(Cliente cliente);

    default Cpf toCpf(String cpf) {
        return Cpf.of(cpf);
    }

    default String toCpfString(Cpf cpf) {
        return cpf == null ? null : cpf.valor();
    }
}
