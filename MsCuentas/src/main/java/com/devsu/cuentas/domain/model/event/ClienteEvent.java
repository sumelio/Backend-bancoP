package com.devsu.cuentas.domain.model.event;

import java.time.Instant;

public record ClienteEvent(
        String clienteId,
        String nombre,
        Boolean estado,
        EventType eventType,
        Instant timestamp
) {
}