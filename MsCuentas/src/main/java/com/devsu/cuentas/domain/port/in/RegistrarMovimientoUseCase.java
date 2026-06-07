package com.devsu.cuentas.domain.port.in;

import com.devsu.cuentas.domain.model.Movimiento;
import com.devsu.cuentas.domain.model.TipoMovimiento;

import java.math.BigDecimal;

public interface RegistrarMovimientoUseCase {

    /**
     * Registra un movimiento bancario (depósito o retiro).
     *
     * @param numeroCuenta el número de cuenta
     * @param tipo el tipo de movimiento (DEPOSITO o RETIRO)
     * @param monto el monto del movimiento (siempre positivo)
     * @return el movimiento registrado
     */
    Movimiento registrarMovimiento(String numeroCuenta, TipoMovimiento tipo, BigDecimal monto);
}