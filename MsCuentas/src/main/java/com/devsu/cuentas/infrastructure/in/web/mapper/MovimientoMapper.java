package com.devsu.cuentas.infrastructure.in.web.mapper;

import com.devsu.cuentas.domain.model.Movimiento;
import com.devsu.cuentas.infrastructure.in.web.dto.MovimientoResponse;

public class MovimientoMapper {

    private MovimientoMapper() {
        // Utility class
    }

    public static MovimientoResponse toResponse(Movimiento movimiento) {
        return new MovimientoResponse(
                movimiento.getId(),
                movimiento.getFecha(),
                movimiento.getTipo(),
                movimiento.getValor(),
                movimiento.getSaldo(),
                movimiento.getCuenta().getNumeroCuenta()
        );
    }
}