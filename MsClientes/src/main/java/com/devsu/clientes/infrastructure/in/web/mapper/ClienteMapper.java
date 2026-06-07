package com.devsu.clientes.infrastructure.in.web.mapper;

import com.devsu.clientes.domain.model.Cliente;
import com.devsu.clientes.infrastructure.in.web.dto.ClienteRequest;
import com.devsu.clientes.infrastructure.in.web.dto.ClienteResponse;

public class ClienteMapper {

    private ClienteMapper() {
        // Clase utilitaria, no se debe instanciar
    }

    public static Cliente toEntity(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNombre(request.nombre());
        cliente.setGenero(request.genero());
        cliente.setEdad(request.edad());
        cliente.setIdentificacion(request.identificacion());
        cliente.setDireccion(request.direccion());
        cliente.setTelefono(request.telefono());
        cliente.setClienteId(request.clienteId());
        cliente.setContrasena(request.contrasena());
        cliente.setEstado(request.estado());
        return cliente;
    }

    public static Cliente toEntity(Long id, ClienteRequest request) {
        Cliente cliente = toEntity(request);
        cliente.setId(id);
        return cliente;
    }

    public static ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
            cliente.getId(),
            cliente.getNombre(),
            cliente.getGenero(),
            cliente.getEdad(),
            cliente.getIdentificacion(),
            cliente.getDireccion(),
            cliente.getTelefono(),
            cliente.getClienteId(),
            cliente.getEstado(),
            cliente.getFechaCreacion(),
            cliente.getFechaModificacion()
        );
    }
}