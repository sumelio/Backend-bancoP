package com.devsu.cuentas.infrastructure.in.web;

import com.devsu.cuentas.domain.model.Cuenta;
import com.devsu.cuentas.domain.port.in.GestionarCuentaUseCase;
import com.devsu.cuentas.infrastructure.in.web.dto.CuentaRequest;
import com.devsu.cuentas.infrastructure.in.web.dto.CuentaResponse;
import com.devsu.cuentas.infrastructure.in.web.mapper.CuentaMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    private final GestionarCuentaUseCase gestionarCuentaUseCase;

    public CuentaController(GestionarCuentaUseCase gestionarCuentaUseCase) {
        this.gestionarCuentaUseCase = gestionarCuentaUseCase;
    }

    @PostMapping
    public ResponseEntity<CuentaResponse> crearCuenta(@Valid @RequestBody CuentaRequest request) {
        Cuenta cuenta = CuentaMapper.toEntity(request);
        Cuenta cuentaCreada = gestionarCuentaUseCase.crearCuenta(cuenta);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CuentaMapper.toResponse(cuentaCreada));
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> obtenerCuentaPorNumero(@PathVariable String numeroCuenta) {
        Cuenta cuenta = gestionarCuentaUseCase.obtenerCuentaPorNumero(numeroCuenta);
        return ResponseEntity.ok(CuentaMapper.toResponse(cuenta));
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> obtenerCuentasPorCliente(
            @RequestParam String clienteId) {

        List<Cuenta> cuentas = gestionarCuentaUseCase.obtenerCuentasPorCliente(clienteId);

        List<CuentaResponse> response = cuentas.stream()
                .map(CuentaMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> actualizarCuenta(
            @PathVariable String numeroCuenta,
            @Valid @RequestBody CuentaRequest request) {

        Cuenta cuenta = CuentaMapper.toEntity(request);
        Cuenta cuentaActualizada = gestionarCuentaUseCase.actualizarCuenta(numeroCuenta, cuenta);
        return ResponseEntity.ok(CuentaMapper.toResponse(cuentaActualizada));
    }
}