package com.devsu.clientes.infrastructure.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ClienteRequest(
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    String genero,

    @NotNull(message = "La edad es obligatoria")
    @Positive(message = "La edad debe ser positiva")
    Integer edad,

    @NotBlank(message = "La identificación es obligatoria")
    String identificacion,

    String direccion,

    String telefono,

    @NotBlank(message = "El clienteId es obligatorio")
    String clienteId,

    @NotBlank(message = "La contraseña es obligatoria")
    String contrasena,

    Boolean estado
) {}