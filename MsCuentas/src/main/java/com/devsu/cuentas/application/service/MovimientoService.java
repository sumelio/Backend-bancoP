package com.devsu.cuentas.application.service;

import com.devsu.cuentas.domain.exception.CuentaInactivaException;
import com.devsu.cuentas.domain.exception.CuentaNotFoundException;
import com.devsu.cuentas.domain.exception.MontoInvalidoException;
import com.devsu.cuentas.domain.exception.SaldoNoDisponibleException;
import com.devsu.cuentas.domain.model.Cuenta;
import com.devsu.cuentas.domain.model.Movimiento;
import com.devsu.cuentas.domain.model.TipoMovimiento;
import com.devsu.cuentas.domain.port.in.RegistrarMovimientoUseCase;
import com.devsu.cuentas.domain.port.out.CuentaRepositoryPort;
import com.devsu.cuentas.domain.port.out.MovimientoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class MovimientoService implements RegistrarMovimientoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(MovimientoService.class);

    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;

    public MovimientoService(CuentaRepositoryPort cuentaRepository,
                             MovimientoRepositoryPort movimientoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Registra un movimiento bancario (depósito o retiro).
     *
     * @param numeroCuenta el número de cuenta
     * @param tipo Depósito o retiro
     * @param monto el monto del movimiento (siempre positivo)
     * @return el movimiento registrado
     * @throws CuentaNotFoundException si la cuenta no existe
     * @throws CuentaInactivaException si la cuenta está inactiva
     * @throws SaldoNoDisponibleException si el saldo es insuficiente
     */
    @Override
    @Transactional
    public Movimiento registrarMovimiento(String numeroCuenta, TipoMovimiento tipo,  BigDecimal monto) {
        validarMontoPositivo(monto);
        Cuenta cuenta = findCuentaConBloqueo(numeroCuenta);
        validarCuentaActiva(cuenta);
        BigDecimal valor = calcularValorConSigno(tipo, monto);    // tipo → signo
        BigDecimal nuevoSaldo = calcularNuevoSaldo(cuenta, valor);
        validarSaldoSuficiente(nuevoSaldo, numeroCuenta, cuenta.getSaldoDisponible(), valor);
        actualizarSaldoCuenta(cuenta, nuevoSaldo);
        Movimiento movimiento = crearMovimiento(cuenta, tipo, valor,  nuevoSaldo);
        return persistirMovimiento(cuenta, movimiento, numeroCuenta);
    }

    private void validarMontoPositivo(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MontoInvalidoException("El monto debe ser mayor a cero");
        }
        if (monto.scale() > 2) {
            throw new MontoInvalidoException("El monto no puede tener más de 2 decimales");
        }
    }

    private Cuenta findCuentaConBloqueo(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuentaForUpdate(numeroCuenta)
                .orElseThrow(() -> new CuentaNotFoundException(numeroCuenta));
    }

    private void validarCuentaActiva(Cuenta cuenta) {
        if (Boolean.FALSE.equals(cuenta.getEstado())) {
            throw new CuentaInactivaException(cuenta.getNumeroCuenta());
        }
    }

    private BigDecimal calcularNuevoSaldo(Cuenta cuenta, BigDecimal valor) {
        return cuenta.getSaldoDisponible().add(valor);
    }

    private void validarSaldoSuficiente(BigDecimal nuevoSaldo, String numeroCuenta,
                                        BigDecimal saldoActual, BigDecimal valor) {
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            logger.warn("Saldo insuficiente en cuenta {}: disponible={}, intento={}",
                    numeroCuenta, saldoActual, valor);
            throw new SaldoNoDisponibleException();
        }
    }

    private void actualizarSaldoCuenta(Cuenta cuenta, BigDecimal nuevoSaldo) {
        // entidad esta managed. dirty checking la actualizaría sola.
        cuenta.setSaldoDisponible(nuevoSaldo);
    }

    private Movimiento crearMovimiento(Cuenta cuenta, TipoMovimiento tipo, BigDecimal valor, BigDecimal nuevoSaldo) {
        return new Movimiento(tipo, valor, nuevoSaldo, cuenta);
    }
    private BigDecimal calcularValorConSigno(TipoMovimiento tipo, BigDecimal monto) {
        return tipo == TipoMovimiento.RETIRO ? monto.negate() : monto;
    }


    private Movimiento persistirMovimiento(Cuenta cuenta, Movimiento movimiento, String numeroCuenta) {
        // Auque es redundate por el cuenta.setSaldoDisponible(nuevoSaldo);
        // dejo la actualizacion explicita.
        cuentaRepository.save(cuenta);
        Movimiento guardado = movimientoRepository.save(movimiento);
        logMovimientoRegistrado(numeroCuenta, movimiento);
        return guardado;
    }

    private void logMovimientoRegistrado(String numeroCuenta, Movimiento movimiento) {
        logger.info("Movimiento registrado: cuenta={}, tipo={}, valor={}, saldoResultante={}",
                numeroCuenta, movimiento.getTipo(), movimiento.getValor(), movimiento.getSaldo());
    }
}