package com.devsu.cuentas.domain.exception;

public class SaldoNoDisponibleException extends RuntimeException {
    public SaldoNoDisponibleException() {
        super("Saldo no disponible");
    }
}