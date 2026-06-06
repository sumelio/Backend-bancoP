package com.devsu.clientes.application.service;

import com.devsu.clientes.domain.model.Cliente;
import com.devsu.clientes.domain.model.ClienteEvent;
import com.devsu.clientes.domain.port.in.GestionarClienteUseCase;
import com.devsu.clientes.domain.port.out.ClienteEventPublisher;
import com.devsu.clientes.domain.port.out.ClienteRepositoryPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService implements GestionarClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;
    private final ClienteEventPublisher eventPublisher;
    private final BCryptPasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepositoryPort clienteRepository,
                          ClienteEventPublisher eventPublisher,
                          BCryptPasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Cliente crearCliente(Cliente cliente) {
        // Validar que el clienteId no exista
        if (clienteRepository.existsByClienteId(cliente.getClienteId())) {
            throw new IllegalArgumentException("El clienteId ya existe: " + cliente.getClienteId());
        }

        // Hashear la contraseña antes de guardar
        String hashedPassword = passwordEncoder.encode(cliente.getContrasena());
        cliente.setContrasena(hashedPassword);

        // Guardar el cliente
        Cliente clienteGuardado = clienteRepository.save(cliente);

        // Publicar evento de cliente creado
        ClienteEvent event = new ClienteEvent(
                clienteGuardado.getClienteId(),
                clienteGuardado.getNombre(),
                clienteGuardado.getEstado(),
                "CREATED",
                ZonedDateTime.now()
        );
        eventPublisher.publish(event);

        return clienteGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerClientePorClienteId(String clienteId) {
        return clienteRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente actualizarCliente(Long id, Cliente cliente) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + id));

        // Actualizar campos de persona
        clienteExistente.setNombre(cliente.getNombre());
        clienteExistente.setGenero(cliente.getGenero());
        clienteExistente.setEdad(cliente.getEdad());
        clienteExistente.setIdentificacion(cliente.getIdentificacion());
        clienteExistente.setDireccion(cliente.getDireccion());
        clienteExistente.setTelefono(cliente.getTelefono());

        // Actualizar campos de cliente
        clienteExistente.setEstado(cliente.getEstado());

        // Si se proporciona una nueva contraseña, hashearla
        if (cliente.getContrasena() != null && !cliente.getContrasena().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(cliente.getContrasena());
            clienteExistente.setContrasena(hashedPassword);
        }

        Cliente clienteActualizado = clienteRepository.save(clienteExistente);

        // Publicar evento de cliente actualizado
        ClienteEvent event = new ClienteEvent(
                clienteActualizado.getClienteId(),
                clienteActualizado.getNombre(),
                clienteActualizado.getEstado(),
                "UPDATED",
                ZonedDateTime.now()
        );
        eventPublisher.publish(event);

        return clienteActualizado;
    }

    @Override
    public void eliminarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + id));

        clienteRepository.deleteById(id);

        // Publicar evento de cliente eliminado
        ClienteEvent event = new ClienteEvent(
                cliente.getClienteId(),
                cliente.getNombre(),
                false,
                "DELETED",
                ZonedDateTime.now()
        );
        eventPublisher.publish(event);
    }
}