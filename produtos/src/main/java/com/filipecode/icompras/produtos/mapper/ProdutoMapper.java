package com.filipecode.icompras.produtos.mapper;

import com.filipecode.icompras.produtos.controller.dto.ProdutoRequest;
import com.filipecode.icompras.produtos.controller.dto.ProdutoResponse;
import com.filipecode.icompras.produtos.model.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProdutoMapper {

    Produto toEntity(ProdutoRequest request);

    ProdutoResponse toResponse(Produto produto);
}
