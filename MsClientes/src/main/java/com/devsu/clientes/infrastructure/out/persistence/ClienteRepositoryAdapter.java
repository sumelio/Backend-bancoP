package com.devsu.clientes.infrastructure.out.persistence;

import com.devsu.clientes.domain.model.Cliente;
import com.devsu.clientes.domain.port.out.ClienteRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpaRepository;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cliente save(Cliente cliente) {
        return jpaRepository.save(cliente);
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Cliente> findByClienteId(String clienteId) {
        return jpaRepository.findByClienteId(clienteId);
    }

    @Override
    public List<Cliente> findAll() {
        return jpaRepository.findAllByOrderByIdAsc();
    }

    @Override
    public boolean existsByClienteId(String clienteId) {
        return jpaRepository.existsByClienteId(clienteId);
    }
}