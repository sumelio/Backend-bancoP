package com.devsu.cuentas.infrastructure.in.web.mapper;

import com.devsu.cuentas.domain.model.Movimiento;
import com.devsu.cuentas.infrastructure.in.web.dto.MovimientoResponse;

public class MovimientoMapper {

    private MovimientoMapper() {
    }

    public static MovimientoResponse toResponse(Movimiento movimiento, String numeroCuenta) {
        return new MovimientoResponse(
                movimiento.getId(),
                numeroCuenta,
                movimiento.getTipo(),
                movimiento.getValor(),
                movimiento.getSaldo(),
                movimiento.getFecha()
        );
    }
}