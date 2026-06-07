package com.devsu.cuentas.application.service;

import com.devsu.cuentas.domain.exception.ClienteNoEncontradoException;
import com.devsu.cuentas.domain.exception.CuentaAlreadyExistsException;
import com.devsu.cuentas.domain.exception.CuentaNotFoundException;
import com.devsu.cuentas.domain.model.Cuenta;
import com.devsu.cuentas.domain.port.in.GestionarCuentaUseCase;
import com.devsu.cuentas.domain.port.out.ClienteLocalRepositoryPort;
import com.devsu.cuentas.domain.port.out.CuentaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CuentaService implements GestionarCuentaUseCase {

    private final CuentaRepositoryPort cuentaRepository;
    private final ClienteLocalRepositoryPort clienteLocalRepository;

    public CuentaService(CuentaRepositoryPort cuentaRepository,
                         ClienteLocalRepositoryPort clienteLocalRepository) {
        this.cuentaRepository = cuentaRepository;
        this.clienteLocalRepository = clienteLocalRepository;
    }

    @Override
    public Cuenta crearCuenta(Cuenta cuenta) {
        // 1. Validar duplicado de numeroCuenta
        if (cuentaRepository.existsByNumeroCuenta(cuenta.getNumeroCuenta())) {
            throw new CuentaAlreadyExistsException(cuenta.getNumeroCuenta());
        }
        // 2. Validar que el cliente exista en la réplica local
        clienteLocalRepository.findByClienteId(cuenta.getClienteId())
                .orElseThrow(() -> new ClienteNoEncontradoException(cuenta.getClienteId()));
        // 3. saldoDisponible ya = saldoInicial (constructor)
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta obtenerCuentaPorNumero(String numeroCuenta) {
        return findCuentaPorNumero(numeroCuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> obtenerCuentasPorCliente(String clienteId) {
        return cuentaRepository.findByClienteId(clienteId);
    }

    @Override
    public Cuenta actualizarCuenta(String numeroCuenta, Cuenta cuenta) {
        Cuenta cuentaExistente = findCuentaPorNumero(numeroCuenta);
        actualizarCamposCuenta(cuentaExistente, cuenta);
        return cuentaRepository.save(cuentaExistente);
    }

    private Cuenta findCuentaPorNumero(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CuentaNotFoundException(numeroCuenta));
    }

    private void actualizarCamposCuenta(Cuenta cuentaExistente, Cuenta cuenta) {
        if (cuenta.getTipo() != null) {
            cuentaExistente.setTipo(cuenta.getTipo());
        }
        if (cuenta.getEstado() != null) {
            cuentaExistente.setEstado(cuenta.getEstado());
        }
        // Nota: saldoInicial y saldoDisponible no se actualizan directamente
        // El saldoDisponible solo se modifica mediante movimientos
    }
}