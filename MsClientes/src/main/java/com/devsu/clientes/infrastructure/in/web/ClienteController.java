package com.devsu.clientes.infrastructure.in.web;

import com.devsu.clientes.domain.model.Cliente;
import com.devsu.clientes.domain.port.in.GestionarClienteUseCase;
import com.devsu.clientes.infrastructure.in.web.dto.ClienteRequest;
import com.devsu.clientes.infrastructure.in.web.dto.ClienteResponse;
import com.devsu.clientes.infrastructure.in.web.mapper.ClienteMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final GestionarClienteUseCase gestionarClienteUseCase;

    public ClienteController(GestionarClienteUseCase gestionarClienteUseCase) {
        this.gestionarClienteUseCase = gestionarClienteUseCase;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crearCliente(@Valid @RequestBody ClienteRequest request) {
        Cliente cliente = ClienteMapper.toEntity(request);
        Cliente clienteCreado = gestionarClienteUseCase.crearCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteMapper.toResponse(clienteCreado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerClientePorId(@PathVariable Long id) {
        Cliente cliente = gestionarClienteUseCase.obtenerClientePorId(id);
        return ResponseEntity.ok(ClienteMapper.toResponse(cliente));
    }


    @GetMapping
    public ResponseEntity<List<ClienteResponse>> obtenerTodosLosClientes() {
        List<ClienteResponse> clientes = gestionarClienteUseCase.obtenerTodosLosClientes()
                .stream()
                .map(ClienteMapper::toResponse)
                .toList();
        return ResponseEntity.ok(clientes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        Cliente cliente = ClienteMapper.toEntity(id, request);
        Cliente clienteActualizado = gestionarClienteUseCase.actualizarCliente(id, cliente);
        return ResponseEntity.ok(ClienteMapper.toResponse(clienteActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        gestionarClienteUseCase.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}