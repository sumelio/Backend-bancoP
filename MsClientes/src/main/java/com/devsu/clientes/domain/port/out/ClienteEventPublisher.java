package com.devsu.clientes.domain.port.out;

import com.devsu.clientes.domain.model.event.ClienteEvent;

public interface ClienteEventPublisher {
    void publish(ClienteEvent event);
}