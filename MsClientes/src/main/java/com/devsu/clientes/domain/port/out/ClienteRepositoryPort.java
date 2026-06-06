package com.devsu.clientes.domain.port.out;

import com.devsu.clientes.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(Long id);
    Optional<Cliente> findByClienteId(String clienteId);
    List<Cliente> findAll();
    void deleteById(Long id);
    boolean existsByClienteId(String clienteId);
}