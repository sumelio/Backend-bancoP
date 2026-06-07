package com.devsu.cuentas.infrastructure.out.persistence;

import com.devsu.cuentas.domain.model.Movimiento;
import com.devsu.cuentas.domain.port.out.MovimientoRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public class MovimientoRepositoryAdapter implements MovimientoRepositoryPort {

    private final MovimientoJpaRepository jpaRepository;

    public MovimientoRepositoryAdapter(MovimientoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Movimiento save(Movimiento movimiento) {
        return jpaRepository.save(movimiento);
    }

    @Override
    public List<Movimiento> findByCuentaClienteIdAndFechaBetween(String clienteId, Instant desde, Instant hasta) {
        return jpaRepository.findByCuentaClienteIdAndFechaBetween(clienteId, desde, hasta);
    }
}
