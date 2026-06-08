package com.devsu.cuentas.application.service;

import com.devsu.cuentas.domain.model.ClienteLocal;
import com.devsu.cuentas.domain.model.event.ClienteEvent;
import com.devsu.cuentas.domain.port.in.SincronizarClienteUseCase;
import com.devsu.cuentas.domain.port.out.ClienteLocalRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SincronizarClienteService implements SincronizarClienteUseCase {

    private final ClienteLocalRepositoryPort repository;

    public SincronizarClienteService(ClienteLocalRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void sincronizar(ClienteEvent event) {
        ClienteLocal clienteLocal = repository.findByClienteId(event.clienteId())
                .orElseGet(ClienteLocal::new);

        clienteLocal.setClienteId(event.clienteId());
        clienteLocal.setNombre(event.nombre());
        clienteLocal.setEstado(event.estado());

        repository.save(clienteLocal);
    }
}