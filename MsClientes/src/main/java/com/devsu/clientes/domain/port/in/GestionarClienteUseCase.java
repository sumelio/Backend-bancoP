package com.devsu.clientes.domain.port.in;

import com.devsu.clientes.domain.model.Cliente;

import java.util.List;

public interface GestionarClienteUseCase {
    Cliente crearCliente(Cliente cliente);
    Cliente obtenerClientePorId(Long id);
    List<Cliente> obtenerTodosLosClientes();
    Cliente actualizarCliente(Long id, Cliente cliente);
    void eliminarCliente(Long id);
}