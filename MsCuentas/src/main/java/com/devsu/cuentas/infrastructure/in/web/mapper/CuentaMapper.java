package com.devsu.cuentas.infrastructure.in.web.mapper;

import com.devsu.cuentas.domain.model.Cuenta;
import com.devsu.cuentas.infrastructure.in.web.dto.CuentaRequest;
import com.devsu.cuentas.infrastructure.in.web.dto.CuentaResponse;

public class CuentaMapper {

    private CuentaMapper() {
        // Utility class
    }

    public static Cuenta toEntity(CuentaRequest request) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(request.numeroCuenta());
        cuenta.setTipo(request.tipo());
        cuenta.setSaldoInicial(request.saldoInicial());
        cuenta.setSaldoDisponible(request.saldoInicial());
        cuenta.setEstado(request.estado() != null ? request.estado() : true);
        cuenta.setClienteId(request.clienteId());
        return cuenta;
    }


    public static CuentaResponse toResponse(Cuenta cuenta) {
        return new CuentaResponse(
                cuenta.getId(),
                cuenta.getNumeroCuenta(),
                cuenta.getTipo(),
                cuenta.getSaldoInicial(),
                cuenta.getSaldoDisponible(),
                cuenta.getEstado(),
                cuenta.getClienteId(),
                cuenta.getFechaCreacion(),
                cuenta.getFechaModificacion()
        );
    }
}