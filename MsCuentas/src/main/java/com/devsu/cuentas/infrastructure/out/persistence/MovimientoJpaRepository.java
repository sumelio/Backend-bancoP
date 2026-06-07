package com.devsu.cuentas.infrastructure.out.persistence;

import com.devsu.cuentas.domain.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface MovimientoJpaRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByCuentaClienteIdAndFechaBetween(String clienteId, Instant desde, Instant hasta);
}