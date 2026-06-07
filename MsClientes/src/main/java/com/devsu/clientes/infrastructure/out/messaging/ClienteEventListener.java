package com.devsu.clientes.infrastructure.out.messaging;

import com.devsu.clientes.domain.model.event.ClienteEvent;
import com.devsu.clientes.domain.port.out.ClienteEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ClienteEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ClienteEventListener.class);

    private final ClienteEventPublisher clienteEventPublisher;

    public ClienteEventListener(ClienteEventPublisher clienteEventPublisher) {
        this.clienteEventPublisher = clienteEventPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleClienteEvent(ClienteEvent event) {
        try {
            logger.info("Publicando evento a RabbitMQ después del commit: {} - {}",
                        event.eventType(), event.clienteId());
            clienteEventPublisher.publish(event);
            logger.info("Evento publicado exitosamente: {} - {}",
                        event.eventType(), event.clienteId());
        } catch (Exception e) {
            logger.error("Error al publicar evento a RabbitMQ: {} - {}. Error: {}",
                         event.eventType(), event.clienteId(), e.getMessage(), e);
            // El evento no se publica pero la transacción ya fue confirmada
            // Considerar implementar un mecanismo de reintento o dead letter queue
        }
    }
}