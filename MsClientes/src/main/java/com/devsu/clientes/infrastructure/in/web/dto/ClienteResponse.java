package com.devsu.clientes.infrastructure.in.web.dto;

import java.time.Instant;

public record ClienteResponse(
    Long id,
    String nombre,
    String genero,
    Integer edad,
    String identificacion,
    String direccion,
    String telefono,
    String clienteId,
    Boolean estado,
    Instant fechaCreacion,
    Instant fechaModificacion
) {}