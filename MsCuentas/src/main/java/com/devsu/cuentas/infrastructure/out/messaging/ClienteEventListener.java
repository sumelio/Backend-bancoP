package com.devsu.cuentas.infrastructure.out.messaging;

import com.devsu.cuentas.domain.model.event.ClienteEvent;
import com.devsu.cuentas.domain.port.in.SincronizarClienteUseCase;
import com.devsu.cuentas.infrastructure.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ClienteEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ClienteEventListener.class);

    private final SincronizarClienteUseCase sincronizarClienteUseCase;

    public ClienteEventListener(SincronizarClienteUseCase sincronizarClienteUseCase) {
        this.sincronizarClienteUseCase = sincronizarClienteUseCase;
    }

    @RabbitListener(queues = RabbitConfig.CLIENTE_QUEUE)
    public void onClienteEvent(ClienteEvent event) {
        logger.info("Evento recibido: {} - clienteId={}", event.eventType(), event.clienteId());

        sincronizarClienteUseCase.sincronizar(event);

        logger.info("ClienteLocal sincronizado: clienteId={}", event.clienteId());
    }
}