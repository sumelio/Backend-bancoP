package com.devsu.cuentas.domain.port.in;

import com.devsu.cuentas.domain.model.Movimiento;
import com.devsu.cuentas.domain.model.TipoMovimiento;

import java.math.BigDecimal;

public interface RegistrarMovimientoUseCase {

    Movimiento registrarMovimiento(String numeroCuenta, TipoMovimiento tipo, BigDecimal monto);
}