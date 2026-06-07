package com.devsu.clientes.application.service;

import com.devsu.clientes.domain.model.Cliente;
import com.devsu.clientes.domain.model.event.ClienteEvent;
import com.devsu.clientes.domain.model.event.EventType;
import com.devsu.clientes.domain.port.in.GestionarClienteUseCase;
import com.devsu.clientes.domain.port.out.ClienteRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService implements GestionarClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepositoryPort clienteRepository,
                          ApplicationEventPublisher applicationEventPublisher,
                          PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.applicationEventPublisher = applicationEventPublisher;
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
                EventType.CREATED,
                Instant.now()
        );
        applicationEventPublisher.publishEvent(event);

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
                EventType.UPDATED,
                Instant.now()
        );
        applicationEventPublisher.publishEvent(event);

        return clienteActualizado;
    }

    @Override
    public void eliminarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + id));

        cliente.setEstado(false);
        clienteRepository.save(cliente);

        // Publicar evento de cliente eliminado (como UPDATED con estado=false)
        ClienteEvent event = new ClienteEvent(
                cliente.getClienteId(),
                cliente.getNombre(),
                false,
                EventType.UPDATED,
                Instant.now()
        );
        applicationEventPublisher.publishEvent(event);
    }
}