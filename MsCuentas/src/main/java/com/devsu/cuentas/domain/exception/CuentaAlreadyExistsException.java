package com.devsu.cuentas.domain.exception;

public class CuentaAlreadyExistsException extends RuntimeException {
    public CuentaAlreadyExistsException(String numeroCuenta) {
        super("La cuenta ya existe: " + numeroCuenta);
    }
}