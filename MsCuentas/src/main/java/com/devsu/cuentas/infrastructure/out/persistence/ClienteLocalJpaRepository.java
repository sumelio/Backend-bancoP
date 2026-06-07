package com.devsu.cuentas.infrastructure.out.persistence;

import com.devsu.cuentas.domain.model.ClienteLocal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteLocalJpaRepository extends JpaRepository<ClienteLocal, String> {

    Optional<ClienteLocal> findByClienteId(String clienteId);
}