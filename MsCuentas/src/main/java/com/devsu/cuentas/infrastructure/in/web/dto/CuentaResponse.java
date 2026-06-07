package com.devsu.cuentas.infrastructure.in.web.dto;

import com.devsu.cuentas.domain.model.TipoCuenta;

import java.math.BigDecimal;
import java.time.Instant;

public record CuentaResponse(
        Long id,
        String numeroCuenta,
        TipoCuenta tipo,
        BigDecimal saldoInicial,
        BigDecimal saldoDisponible,
        Boolean estado,
        String clienteId,
        Instant fechaCreacion,
        Instant fechaModificacion
) {
}