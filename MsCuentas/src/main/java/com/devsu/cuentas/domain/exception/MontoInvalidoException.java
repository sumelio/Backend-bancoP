package com.devsu.cuentas.domain.exception;

public class MontoInvalidoException extends RuntimeException {

    public MontoInvalidoException(String validacion) {
        super(validacion);
    }
}