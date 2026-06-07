package com.devsu.cuentas.infrastructure.in.web.dto;

import com.devsu.cuentas.domain.model.TipoMovimiento;

import java.math.BigDecimal;
import java.time.Instant;

public record MovimientoResponse(
        Long id,
        String numeroCuenta,
        TipoMovimiento tipo,
        BigDecimal valor,
        BigDecimal saldo,
        Instant fecha
) {
}