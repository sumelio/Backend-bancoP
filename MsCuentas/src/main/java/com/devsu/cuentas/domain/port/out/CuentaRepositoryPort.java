package com.devsu.cuentas.domain.port.out;

import com.devsu.cuentas.domain.model.Cuenta;

import java.util.List;
import java.util.Optional;

public interface CuentaRepositoryPort {

    Cuenta save(Cuenta cuenta);


    Optional<Cuenta> findById(Long id);

    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    List<Cuenta> findByClienteId(String clienteId);

    List<Cuenta> findAll();

    boolean existsByNumeroCuenta(String numeroCuenta);

    /**
     * Busca una cuenta por su número de cuenta con bloqueo pesimista para actualización.
     * Este método bloquea la fila en la base de datos (SELECT ... FOR UPDATE)
     * para evitar race conditions en actualizaciones concurrentes del saldo.
     *
     * IMPORTANTE: Debe usarse dentro de una transacción (@Transactional).
     *
     * @param numeroCuenta el número de cuenta
     * @return Optional con la cuenta bloqueada si existe
     */
    Optional<Cuenta> findByNumeroCuentaForUpdate(String numeroCuenta);
}