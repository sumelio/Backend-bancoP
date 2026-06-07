package com.devsu.clientes.infrastructure.out.persistence;

import com.devsu.clientes.domain.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteJpaRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByClienteId(String clienteId);
    boolean existsByClienteId(String clienteId);
}