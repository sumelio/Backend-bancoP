package com.devsu.cuentas.domain.port.in;

import com.devsu.cuentas.domain.model.Cuenta;

import java.util.List;

public interface GestionarCuentaUseCase {

    Cuenta crearCuenta(Cuenta cuenta);

    Cuenta obtenerCuentaPorNumero(String numeroCuenta);

    List<Cuenta> obtenerCuentasPorCliente(String clienteId);

    Cuenta actualizarCuenta(String numeroCuenta, Cuenta cuenta);
}