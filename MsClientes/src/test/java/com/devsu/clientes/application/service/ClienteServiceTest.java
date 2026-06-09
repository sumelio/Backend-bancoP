package com.devsu.clientes.application.service;

import com.devsu.clientes.domain.exception.ClienteAlreadyExistsException;
import com.devsu.clientes.domain.exception.ClienteNotFoundException;
import com.devsu.clientes.domain.model.Cliente;
import com.devsu.clientes.domain.model.event.ClienteEvent;
import com.devsu.clientes.domain.model.event.EventType;
import com.devsu.clientes.domain.port.out.ClienteRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService - Pruebas Unitarias")
class ClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteTest;
    private static final Long CLIENTE_ID_LONG = 1L;
    private static final String CLIENTE_ID = "CLI001";
    private static final String PASSWORD_PLAIN = "1234";
    private static final String PASSWORD_HASHED = "$2a$10$hashedPassword";

    @BeforeEach
    void setUp() {
        clienteTest = new Cliente(
                "Jose Lema",
                "Masculino",
                30,
                "1234567890",
                "Otavalo sn y principal",
                "098254785",
                CLIENTE_ID,
                PASSWORD_PLAIN,
                true
        );
        clienteTest.setId(CLIENTE_ID_LONG);
    }

    // ==================== CREAR CLIENTE ====================

    @Test
    @DisplayName("Debe crear un cliente exitosamente y hashear la contraseña")
    void debeCrearClienteExitosamente() {
        // Given
        when(clienteRepository.existsByClienteId(CLIENTE_ID)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD_PLAIN)).thenReturn(PASSWORD_HASHED);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            c.setId(CLIENTE_ID_LONG);
            return c;
        });

        // When
        Cliente resultado = clienteService.crearCliente(clienteTest);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(CLIENTE_ID_LONG);
        assertThat(resultado.getContrasena()).isEqualTo(PASSWORD_HASHED);

        // Verificar que se hasheó la contraseña
        verify(passwordEncoder).encode(PASSWORD_PLAIN);

        // Verificar que se guardó
        verify(clienteRepository).save(clienteTest);

        // Verificar que se publicó el evento
        ArgumentCaptor<ClienteEvent> eventCaptor = ArgumentCaptor.forClass(ClienteEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        ClienteEvent event = eventCaptor.getValue();
        assertThat(event.clienteId()).isEqualTo(CLIENTE_ID);
        assertThat(event.eventType()).isEqualTo(EventType.CREATED);
        assertThat(event.nombre()).isEqualTo("Jose Lema");
        assertThat(event.estado()).isTrue();
    }

    @Test
    @DisplayName("Debe lanzar ClienteAlreadyExistsException cuando el clienteId ya existe")
    void debeLanzarExcepcionCuandoClienteYaExiste() {
        // Given
        when(clienteRepository.existsByClienteId(CLIENTE_ID)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clienteService.crearCliente(clienteTest))
                .isInstanceOf(ClienteAlreadyExistsException.class)
                .hasMessageContaining(CLIENTE_ID);

        // Verificar que no se intentó guardar
        verify(clienteRepository, never()).save(any());

        // Verificar que no se publicó evento
        verifyNoInteractions(applicationEventPublisher);

        // Verificar que no se intentó hashear contraseña
        verifyNoInteractions(passwordEncoder);
    }

    // ==================== OBTENER CLIENTE POR ID ====================

    @Test
    @DisplayName("Debe obtener un cliente por ID exitosamente")
    void debeObtenerClientePorIdExitosamente() {
        // Given
        when(clienteRepository.findById(CLIENTE_ID_LONG)).thenReturn(Optional.of(clienteTest));

        // When
        Cliente resultado = clienteService.obtenerClientePorId(CLIENTE_ID_LONG);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(CLIENTE_ID_LONG);
        assertThat(resultado.getClienteId()).isEqualTo(CLIENTE_ID);
        assertThat(resultado.getNombre()).isEqualTo("Jose Lema");

        verify(clienteRepository).findById(CLIENTE_ID_LONG);
    }

    @Test
    @DisplayName("Debe lanzar ClienteNotFoundException cuando el cliente no existe")
    void debeLanzarExcepcionCuandoClienteNoExiste() {
        // Given
        when(clienteRepository.findById(CLIENTE_ID_LONG)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clienteService.obtenerClientePorId(CLIENTE_ID_LONG))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessageContaining(CLIENTE_ID_LONG.toString());

        verify(clienteRepository).findById(CLIENTE_ID_LONG);
    }

    // ==================== OBTENER TODOS LOS CLIENTES ====================

    @Test
    @DisplayName("Debe obtener todos los clientes")
    void debeObtenerTodosLosClientes() {
        // Given
        Cliente cliente2 = new Cliente(
                "Marianela Montalvo",
                "Femenino",
                25,
                "0987654321",
                "Amazonas y NNUU",
                "097548965",
                "CLI002",
                PASSWORD_PLAIN,
                true
        );
        cliente2.setId(2L);

        List<Cliente> clientes = Arrays.asList(clienteTest, cliente2);
        when(clienteRepository.findAll()).thenReturn(clientes);

        // When
        List<Cliente> resultado = clienteService.obtenerTodosLosClientes();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getClienteId()).isEqualTo(CLIENTE_ID);
        assertThat(resultado.get(1).getClienteId()).isEqualTo("CLI002");

        verify(clienteRepository).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay clientes")
    void debeRetornarListaVaciaCuandoNoHayClientes() {
        // Given
        when(clienteRepository.findAll()).thenReturn(List.of());

        // When
        List<Cliente> resultado = clienteService.obtenerTodosLosClientes();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();

        verify(clienteRepository).findAll();
    }

    // ==================== ACTUALIZAR CLIENTE ====================

    @Test
    @DisplayName("Debe actualizar un cliente exitosamente sin cambiar contraseña")
    void debeActualizarClienteExitosamente() {
        // Given
        Cliente clienteActualizado = new Cliente(
                "Jose Lema Actualizado",
                "Masculino",
                31,
                "1234567890",
                "Nueva Dirección",
                "099999999",
                CLIENTE_ID,
                null, // No actualizar contraseña
                true
        );

        when(clienteRepository.findById(CLIENTE_ID_LONG)).thenReturn(Optional.of(clienteTest));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Cliente resultado = clienteService.actualizarCliente(CLIENTE_ID_LONG, clienteActualizado);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Jose Lema Actualizado");
        assertThat(resultado.getEdad()).isEqualTo(31);
        assertThat(resultado.getDireccion()).isEqualTo("Nueva Dirección");
        assertThat(resultado.getTelefono()).isEqualTo("099999999");
        assertThat(resultado.getContrasena()).isEqualTo(PASSWORD_PLAIN); // No cambió

        // Verificar que se guardó
        verify(clienteRepository).save(clienteTest);

        // Verificar que se publicó evento UPDATED
        ArgumentCaptor<ClienteEvent> eventCaptor = ArgumentCaptor.forClass(ClienteEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        ClienteEvent event = eventCaptor.getValue();
        assertThat(event.eventType()).isEqualTo(EventType.UPDATED);

        // Verificar que NO se hasheó contraseña
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Debe actualizar un cliente y hashear nueva contraseña")
    void debeActualizarClienteYHashearNuevaContrasena() {
        // Given
        String nuevaPassword = "newPassword123";
        Cliente clienteActualizado = new Cliente(
                "Jose Lema",
                "Masculino",
                30,
                "1234567890",
                "Otavalo sn y principal",
                "098254785",
                CLIENTE_ID,
                nuevaPassword,
                true
        );

        when(clienteRepository.findById(CLIENTE_ID_LONG)).thenReturn(Optional.of(clienteTest));
        when(passwordEncoder.encode(nuevaPassword)).thenReturn(PASSWORD_HASHED);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Cliente resultado = clienteService.actualizarCliente(CLIENTE_ID_LONG, clienteActualizado);

        // Then
        assertThat(resultado.getContrasena()).isEqualTo(PASSWORD_HASHED);

        // Verificar que se hasheó la nueva contraseña
        verify(passwordEncoder).encode(nuevaPassword);

        // Verificar que se publicó evento
        verify(applicationEventPublisher).publishEvent(any(ClienteEvent.class));
    }

    @Test
    @DisplayName("Debe actualizar cliente ignorando contraseña vacía")
    void debeActualizarClienteIgnorandoContrasenaVacia() {
        // Given
        Cliente clienteActualizado = new Cliente(
                "Jose Lema",
                "Masculino",
                30,
                "1234567890",
                "Otavalo sn y principal",
                "098254785",
                CLIENTE_ID,
                "", // Contraseña vacía
                true
        );

        when(clienteRepository.findById(CLIENTE_ID_LONG)).thenReturn(Optional.of(clienteTest));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Cliente resultado = clienteService.actualizarCliente(CLIENTE_ID_LONG, clienteActualizado);

        // Then
        assertThat(resultado.getContrasena()).isEqualTo(PASSWORD_PLAIN); // No cambió

        // Verificar que NO se hasheó contraseña
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Debe lanzar ClienteNotFoundException al actualizar cliente inexistente")
    void debeLanzarExcepcionAlActualizarClienteInexistente() {
        // Given
        when(clienteRepository.findById(CLIENTE_ID_LONG)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clienteService.actualizarCliente(CLIENTE_ID_LONG, clienteTest))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessageContaining(CLIENTE_ID_LONG.toString());

        // Verificar que no se guardó
        verify(clienteRepository, never()).save(any());

        // Verificar que no se publicó evento
        verifyNoInteractions(applicationEventPublisher);
    }

    // ==================== ELIMINAR CLIENTE (SOFT DELETE) ====================

    @Test
    @DisplayName("Debe eliminar (desactivar) un cliente exitosamente")
    void debeEliminarClienteExitosamente() {
        // Given
        when(clienteRepository.findById(CLIENTE_ID_LONG)).thenReturn(Optional.of(clienteTest));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        clienteService.eliminarCliente(CLIENTE_ID_LONG);

        // Then
        assertThat(clienteTest.getEstado()).isFalse(); // Soft delete

        // Verificar que se guardó con estado false
        ArgumentCaptor<Cliente> clienteCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(clienteCaptor.capture());

        Cliente clienteGuardado = clienteCaptor.getValue();
        assertThat(clienteGuardado.getEstado()).isFalse();

        // Verificar que se publicó evento UPDATED
        ArgumentCaptor<ClienteEvent> eventCaptor = ArgumentCaptor.forClass(ClienteEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        ClienteEvent event = eventCaptor.getValue();
        assertThat(event.eventType()).isEqualTo(EventType.UPDATED);
        assertThat(event.estado()).isFalse();
    }

    @Test
    @DisplayName("Debe lanzar ClienteNotFoundException al eliminar cliente inexistente")
    void debeLanzarExcepcionAlEliminarClienteInexistente() {
        // Given
        when(clienteRepository.findById(CLIENTE_ID_LONG)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clienteService.eliminarCliente(CLIENTE_ID_LONG))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessageContaining(CLIENTE_ID_LONG.toString());

        // Verificar que no se guardó
        verify(clienteRepository, never()).save(any());

        // Verificar que no se publicó evento
        verifyNoInteractions(applicationEventPublisher);
    }
}