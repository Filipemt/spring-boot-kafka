package com.filipecode.icompras.produtos.repository;

import com.filipecode.icompras.produtos.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {

    Optional<Produto> findByCodigoAndAtivoTrue(Long codigo);
}
