package com.devsu.cuentas.infrastructure.in.web;

import com.devsu.cuentas.domain.model.Movimiento;
import com.devsu.cuentas.domain.port.in.RegistrarMovimientoUseCase;
import com.devsu.cuentas.infrastructure.in.web.dto.MovimientoRequest;
import com.devsu.cuentas.infrastructure.in.web.dto.MovimientoResponse;
import com.devsu.cuentas.infrastructure.in.web.mapper.MovimientoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

    private final RegistrarMovimientoUseCase registrarMovimientoUseCase;

    public MovimientoController(RegistrarMovimientoUseCase registrarMovimientoUseCase) {
        this.registrarMovimientoUseCase = registrarMovimientoUseCase;
    }

    @PostMapping
    public ResponseEntity<MovimientoResponse> registrarMovimiento(
            @Valid @RequestBody MovimientoRequest request) {

        Movimiento movimiento = registrarMovimientoUseCase.registrarMovimiento(
                request.numeroCuenta(),
                request.tipo(),
                request.monto()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body( MovimientoMapper.toResponse(movimiento, request.numeroCuenta()));
    }
}