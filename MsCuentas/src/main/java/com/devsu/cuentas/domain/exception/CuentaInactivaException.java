package com.devsu.cuentas.domain.exception;

public class CuentaInactivaException extends RuntimeException {

    public CuentaInactivaException(String numeroCuenta) {
        super("La cuenta no está activa: " + numeroCuenta);
    }
}