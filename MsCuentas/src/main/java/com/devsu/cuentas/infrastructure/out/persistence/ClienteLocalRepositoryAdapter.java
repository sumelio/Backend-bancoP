package com.devsu.cuentas.infrastructure.out.persistence;

import com.devsu.cuentas.domain.model.ClienteLocal;
import com.devsu.cuentas.domain.port.out.ClienteLocalRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ClienteLocalRepositoryAdapter implements ClienteLocalRepositoryPort {

    private final ClienteLocalJpaRepository jpaRepository;

    public ClienteLocalRepositoryAdapter(ClienteLocalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ClienteLocal save(ClienteLocal clienteLocal) {
        return jpaRepository.save(clienteLocal);
    }

    @Override
    public Optional<ClienteLocal> findByClienteId(String clienteId) {
        return jpaRepository.findByClienteId(clienteId);
    }
}