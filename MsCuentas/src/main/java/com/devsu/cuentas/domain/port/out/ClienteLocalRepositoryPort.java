package com.devsu.cuentas.domain.port.out;

import com.devsu.cuentas.domain.model.ClienteLocal;

import java.util.Optional;

public interface ClienteLocalRepositoryPort {
    ClienteLocal save(ClienteLocal clienteLocal);

    Optional<ClienteLocal> findByClienteId(String clienteId);
}