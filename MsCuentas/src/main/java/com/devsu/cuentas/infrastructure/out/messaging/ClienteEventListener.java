package com.devsu.cuentas.infrastructure.in.messaging;

import com.devsu.cuentas.domain.model.ClienteLocal;
import com.devsu.cuentas.domain.model.event.ClienteEvent;
import com.devsu.cuentas.domain.port.out.ClienteLocalRepositoryPort;
import com.devsu.cuentas.infrastructure.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;   // ← import

@Component
public class ClienteEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ClienteEventListener.class);

    private final ClienteLocalRepositoryPort clienteLocalRepository;

    public ClienteEventListener(ClienteLocalRepositoryPort clienteLocalRepository) {
        this.clienteLocalRepository = clienteLocalRepository;
    }

    @RabbitListener(queues = RabbitConfig.CLIENTE_QUEUE)
    @Transactional
    public void onClienteEvent(ClienteEvent event) {
        logger.info("Evento recibido: {} - clienteId={}", event.eventType(), event.clienteId());

        ClienteLocal clienteLocal = clienteLocalRepository.findByClienteId(event.clienteId())
                .orElseGet(ClienteLocal::new);

        clienteLocal.setClienteId(event.clienteId());
        clienteLocal.setNombre(event.nombre());
        clienteLocal.setEstado(event.estado());

        clienteLocalRepository.save(clienteLocal);

        logger.info("ClienteLocal sincronizado: clienteId={}", event.clienteId());
    }
}