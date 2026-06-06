package com.devsu.clientes.domain.port.in;

import com.devsu.clientes.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface GestionarClienteUseCase {
    Cliente crearCliente(Cliente cliente);
    Optional<Cliente> obtenerClientePorId(Long id);
    Optional<Cliente> obtenerClientePorClienteId(String clienteId);
    List<Cliente> obtenerTodosLosClientes();
    Cliente actualizarCliente(Long id, Cliente cliente);
    void eliminarCliente(Long id);
}