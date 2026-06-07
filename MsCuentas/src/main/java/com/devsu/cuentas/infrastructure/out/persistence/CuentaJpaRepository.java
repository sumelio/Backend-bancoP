package com.devsu.cuentas.infrastructure.out.persistence;

import com.devsu.cuentas.domain.model.Cuenta;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CuentaJpaRepository extends JpaRepository<Cuenta, Long> {

    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    List<Cuenta> findByClienteId(String clienteId);

    boolean existsByNumeroCuenta(String numeroCuenta);

    /**
     * Busca una cuenta por su número de cuenta con bloqueo pesimista (SELECT ... FOR UPDATE).
     *
     * Este método implementa un bloqueo pesimista a nivel de base de datos
     * que previene race conditions cuando múltiples transacciones intentan
     * actualizar el saldo de la misma cuenta simultáneamente.
     *
     * Flujo de bloqueo:
     * 1. Transacción A lee cuenta con este método → fila bloqueada en BD
     * 2. Transacción B intenta leer la misma cuenta → espera
     * 3. Transacción A actualiza saldo y hace commit → libera bloqueo
     * 4. Transacción B obtiene el bloqueo y lee la cuenta actualizada
     *
     * IMPORTANTE:
     * - Debe usarse SOLO dentro de un método @Transactional
     * - El bloqueo se libera al finalizar la transacción (commit/rollback)
     * - Evita lost updates en operaciones concurrentes de depósito/retiro
     *
     * @param numeroCuenta el número de cuenta a bloquear
     * @return Optional con la cuenta bloqueada si existe
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cuenta c WHERE c.numeroCuenta = :numeroCuenta")
    Optional<Cuenta> findByNumeroCuentaForUpdate(@Param("numeroCuenta") String numeroCuenta);
}