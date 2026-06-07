package com.devsu.cuentas.domain.exception;

public class ClienteNoEncontradoException extends RuntimeException {
    public ClienteNoEncontradoException(String clienteId) {
        super("Cliente no encontrado en el sistema: " + clienteId);
    }
}