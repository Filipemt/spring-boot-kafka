package com.filipecode.icompras.clientes.repository;

import com.filipecode.icompras.clientes.model.Cliente;
import com.filipecode.icompras.clientes.model.valueObject.Cpf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {

    Optional<Cliente> findByCodigoAndAtivoTrue(Long codigo);

    boolean existsByCpf(Cpf cpf);
}
