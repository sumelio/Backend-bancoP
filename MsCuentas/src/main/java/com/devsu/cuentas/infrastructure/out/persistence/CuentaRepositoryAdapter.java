package com.devsu.cuentas.infrastructure.out.persistence;

import com.devsu.cuentas.domain.model.Cuenta;
import com.devsu.cuentas.domain.port.out.CuentaRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository jpaRepository;

    public CuentaRepositoryAdapter(CuentaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cuenta save(Cuenta cuenta) {
        return jpaRepository.save(cuenta);
    }

    @Override
    public Optional<Cuenta> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
        return jpaRepository.findByNumeroCuenta(numeroCuenta);
    }

    @Override
    public List<Cuenta> findByClienteId(String clienteId) {
        return jpaRepository.findByClienteId(clienteId);
    }

    @Override
    public List<Cuenta> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public boolean existsByNumeroCuenta(String numeroCuenta) {
        return jpaRepository.existsByNumeroCuenta(numeroCuenta);
    }

    @Override
    public Optional<Cuenta> findByNumeroCuentaForUpdate(String numeroCuenta) {
        return jpaRepository.findByNumeroCuentaForUpdate(numeroCuenta);
    }
}