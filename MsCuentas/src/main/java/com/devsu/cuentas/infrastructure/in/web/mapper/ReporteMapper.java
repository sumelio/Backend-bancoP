package com.devsu.cuentas.infrastructure.in.web.mapper;

import com.devsu.cuentas.domain.model.ReporteItem;
import com.devsu.cuentas.infrastructure.in.web.dto.ReporteResponse;

public class ReporteMapper {

    private ReporteMapper() {
        // Utility class
    }

    public static ReporteResponse toResponse(ReporteItem item) {
        return new ReporteResponse(
                item.fecha(),
                item.cliente(),
                item.numeroCuenta(),
                item.tipoCuenta(),
                item.saldoInicial(),
                item.estado(),
                item.movimiento(),
                item.saldoDisponible()
        );
    }
}