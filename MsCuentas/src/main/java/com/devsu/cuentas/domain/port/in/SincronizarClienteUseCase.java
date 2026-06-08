package com.devsu.cuentas.domain.port.in;

import com.devsu.cuentas.domain.model.event.ClienteEvent;

public interface SincronizarClienteUseCase {
    void sincronizar(ClienteEvent event);
}