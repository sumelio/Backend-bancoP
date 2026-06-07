package com.devsu.clientes.domain.exception;

public class ClienteAlreadyExistsException extends RuntimeException {

    public ClienteAlreadyExistsException(String clienteId) {
        super("El cliente ya existe con clienteId: " + clienteId);
    }
}
