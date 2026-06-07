package com.devsu.cuentas.infrastructure.in.web.dto;

import com.devsu.cuentas.domain.model.TipoMovimiento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MovimientoRequest(
        @NotNull(message = "El tipo de movimiento es obligatorio")
        TipoMovimiento tipo,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a cero")
        BigDecimal monto
) {
}