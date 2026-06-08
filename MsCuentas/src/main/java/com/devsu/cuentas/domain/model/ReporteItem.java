package com.devsu.cuentas.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record ReporteItem(
    Instant fecha,
    String cliente,
    String numeroCuenta,
    TipoCuenta tipoCuenta,
    BigDecimal saldoInicial,
    Boolean estado,
    BigDecimal movimiento,
    BigDecimal saldoDisponible
) {}