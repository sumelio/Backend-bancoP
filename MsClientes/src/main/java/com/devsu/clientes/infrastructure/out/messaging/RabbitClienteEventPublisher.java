package com.devsu.clientes.infrastructure.out.messaging;

import com.devsu.clientes.domain.model.event.ClienteEvent;
import com.devsu.clientes.domain.port.out.ClienteEventPublisher;
import com.devsu.clientes.infrastructure.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitClienteEventPublisher implements ClienteEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RabbitClienteEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitClienteEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(ClienteEvent event) {
        String routingKey = "cliente." + event.eventType().name().toLowerCase();
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.CLIENTE_EXCHANGE, routingKey, event);
            logger.info("Evento publicado a RabbitMQ: {} routingKey={}", event.clienteId(), routingKey);
        } catch (Exception e) {
            logger.error("Error publicando evento para clienteId={}: {}", event.clienteId(), e.getMessage(), e);
            // aquí, en producción, iría el respaldo (outbox / reintento)
            // Podria captura el AmqpException
        }
    }
}