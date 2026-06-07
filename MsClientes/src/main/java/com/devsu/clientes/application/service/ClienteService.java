package com.devsu.clientes.application.service;

import com.devsu.clientes.domain.exception.ClienteAlreadyExistsException;
import com.devsu.clientes.domain.exception.ClienteNotFoundException;
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
        validateClienteDoesNotExist(cliente.getClienteId());
        hashClientePassword(cliente);

        Cliente clienteGuardado = clienteRepository.save(cliente);
        publishClienteEvent(clienteGuardado, EventType.CREATED);

        return clienteGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente actualizarCliente(Long id, Cliente cliente) {
        Cliente clienteExistente = findClienteById(id);
        updateClienteFields(clienteExistente, cliente);
        updatePasswordIfProvided(clienteExistente, cliente);

        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        publishClienteEvent(clienteActualizado, EventType.UPDATED);

        return clienteActualizado;
    }

    @Override
    public void eliminarCliente(Long id) {
        Cliente cliente = findClienteById(id);
        deactivateCliente(cliente);
        publishClienteEvent(cliente, EventType.UPDATED);
    }

    // ==================== Métodos privados ====================

    private void validateClienteDoesNotExist(String clienteId) {
        if (clienteRepository.existsByClienteId(clienteId)) {
            throw new ClienteAlreadyExistsException(clienteId);
        }
    }

    private void hashClientePassword(Cliente cliente) {
        String hashedPassword = passwordEncoder.encode(cliente.getContrasena());
        cliente.setContrasena(hashedPassword);
    }

    private Cliente findClienteById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
    }

    private void updateClienteFields(Cliente target, Cliente source) {
        target.setNombre(source.getNombre());
        target.setGenero(source.getGenero());
        target.setEdad(source.getEdad());
        target.setDireccion(source.getDireccion());
        target.setTelefono(source.getTelefono());
        target.setEstado(source.getEstado());
    }

    private void updatePasswordIfProvided(Cliente target, Cliente source) {
        if (source.getContrasena() != null && !source.getContrasena().isEmpty()) {
            hashClientePassword(source);
            target.setContrasena(source.getContrasena());
        }
    }

    private void deactivateCliente(Cliente cliente) {
        cliente.setEstado(false);
        clienteRepository.save(cliente);
    }

    private void publishClienteEvent(Cliente cliente, EventType eventType) {
        ClienteEvent event = new ClienteEvent(
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getEstado(),
                eventType,
                Instant.now()
        );
        applicationEventPublisher.publishEvent(event);
    }
}