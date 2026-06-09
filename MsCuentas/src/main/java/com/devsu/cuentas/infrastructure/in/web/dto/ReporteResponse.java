package com.devsu.cuentas.infrastructure.in.web.dto;

import com.devsu.cuentas.domain.model.TipoCuenta;

import java.math.BigDecimal;
import java.time.Instant;

public record ReporteResponse(
        String fecha,
        Instant fechaInstante,
        String cliente,           // nombre, del cliente_local
        String numeroCuenta,
        TipoCuenta tipoCuenta,
        BigDecimal saldoInicial,
        Boolean estado,
        BigDecimal movimiento,    // valor con signo: -540, 600
        BigDecimal saldoDisponible
) {
}