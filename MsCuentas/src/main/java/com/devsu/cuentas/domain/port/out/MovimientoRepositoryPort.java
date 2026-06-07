package com.devsu.cuentas.domain.port.out;

import com.devsu.cuentas.domain.model.Movimiento;

import java.time.Instant;
import java.util.List;

public interface MovimientoRepositoryPort {
    Movimiento save(Movimiento movimiento);
    List<Movimiento> findByCuentaClienteIdAndFechaBetween(String clienteId, Instant desde, Instant hasta);
}