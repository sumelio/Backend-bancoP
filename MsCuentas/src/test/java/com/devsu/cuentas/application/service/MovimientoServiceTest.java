package com.devsu.cuentas.application.service;

import com.devsu.cuentas.domain.exception.CuentaInactivaException;
import com.devsu.cuentas.domain.exception.CuentaNotFoundException;
import com.devsu.cuentas.domain.exception.MontoInvalidoException;
import com.devsu.cuentas.domain.exception.SaldoNoDisponibleException;
import com.devsu.cuentas.domain.model.Cuenta;
import com.devsu.cuentas.domain.model.Movimiento;
import com.devsu.cuentas.domain.model.TipoCuenta;
import com.devsu.cuentas.domain.model.TipoMovimiento;
import com.devsu.cuentas.domain.port.out.CuentaRepositoryPort;
import com.devsu.cuentas.domain.port.out.MovimientoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovimientoService - Pruebas Unitarias")
class MovimientoServiceTest {

    @Mock
    private CuentaRepositoryPort cuentaRepository;

    @Mock
    private MovimientoRepositoryPort movimientoRepository;

    @InjectMocks
    private MovimientoService movimientoService;

    private Cuenta cuentaActiva;
    private static final String NUMERO_CUENTA = "478758";
    private static final String CLIENT_ID = "CLI001";

    @BeforeEach
    void setUp() {
        cuentaActiva = new Cuenta();
        cuentaActiva.setId(1L);
        cuentaActiva.setNumeroCuenta(NUMERO_CUENTA);
        cuentaActiva.setTipo(TipoCuenta.AHORROS);
        cuentaActiva.setSaldoInicial(new BigDecimal("2000.00"));
        cuentaActiva.setSaldoDisponible(new BigDecimal("2000.00"));
        cuentaActiva.setEstado(true);
        cuentaActiva.setClienteId(CLIENT_ID);
    }


    @Test
    @DisplayName("Debe registrar un depósito exitosamente")
    void debeRegistrarDepositoExitosamente() {
        // Dado
        BigDecimal montoDeposito = new BigDecimal("575.00");
        BigDecimal saldoEsperado = new BigDecimal("2575.00");

        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> {
                    Movimiento mov = invocation.getArgument(0);
                    mov.setId(1L);
                    return mov;
                });

        // Cuando
        Movimiento resultado = movimientoService.registrarMovimiento(
                NUMERO_CUENTA,
                TipoMovimiento.DEPOSITO,
                montoDeposito
        );

        // Entonces
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTipo()).isEqualTo(TipoMovimiento.DEPOSITO);
        assertThat(resultado.getValor()).isEqualByComparingTo(montoDeposito);
        assertThat(resultado.getSaldo()).isEqualByComparingTo(saldoEsperado);

        // Verificar que se actualizó el saldo de la cuenta
        assertThat(cuentaActiva.getSaldoDisponible()).isEqualByComparingTo(saldoEsperado);

        // Verificar interacciones
        verify(cuentaRepository).findByNumeroCuentaForUpdate(NUMERO_CUENTA);
        verify(cuentaRepository).save(cuentaActiva);
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    @DisplayName("Debe registrar un retiro exitosamente cuando hay saldo suficiente")
    void debeRegistrarRetiroExitosamente() {
        // Dado
        BigDecimal montoRetiro = new BigDecimal("575.00");
        BigDecimal saldoEsperado = new BigDecimal("1425.00");

        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> {
                    Movimiento mov = invocation.getArgument(0);
                    mov.setId(2L);
                    return mov;
                });

        // Cuando
        Movimiento resultado = movimientoService.registrarMovimiento(
                NUMERO_CUENTA,
                TipoMovimiento.RETIRO,
                montoRetiro
        );

        // Entonces
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTipo()).isEqualTo(TipoMovimiento.RETIRO);
        assertThat(resultado.getValor()).isEqualByComparingTo(montoRetiro.negate());
        assertThat(resultado.getSaldo()).isEqualByComparingTo(saldoEsperado);
        assertThat(cuentaActiva.getSaldoDisponible()).isEqualByComparingTo(saldoEsperado);

        verify(cuentaRepository).findByNumeroCuentaForUpdate(NUMERO_CUENTA);
        verify(cuentaRepository).save(cuentaActiva);
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    @DisplayName("Debe permitir retiro que deje el saldo exactamente en cero")
    void debePermitirRetiroQueDejaElSaldoEnCero() {
        // Dado
        BigDecimal montoRetiro = new BigDecimal("2000.00");
        BigDecimal saldoEsperado = BigDecimal.ZERO;

        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        Movimiento resultado = movimientoService.registrarMovimiento(
                NUMERO_CUENTA,
                TipoMovimiento.RETIRO,
                montoRetiro
        );

        // Entonces
        assertThat(resultado.getSaldo()).isEqualByComparingTo(saldoEsperado);
        assertThat(cuentaActiva.getSaldoDisponible()).isEqualByComparingTo(saldoEsperado);
    }

    // ==================== VALIDACIÓN DE MONTO ====================

    @Test
    @DisplayName("Debe lanzar MontoInvalidoException cuando el monto es nulo")
    void debeLanzarExcepcionCuandoMontoEsNulo() {
        // Cuando & Entonces
        assertThatThrownBy(() ->
                movimientoService.registrarMovimiento(NUMERO_CUENTA, TipoMovimiento.DEPOSITO, null)
        )
                .isInstanceOf(MontoInvalidoException.class)
                .hasMessage("El monto debe ser mayor a cero");

        // Verificar que no se llamó al repositorio
        verifyNoInteractions(cuentaRepository, movimientoRepository);
    }

    @Test
    @DisplayName("Debe lanzar MontoInvalidoException cuando el monto es cero")
    void debeLanzarExcepcionCuandoMontoEsCero() {
        // Cuando & Entonces
        assertThatThrownBy(() ->
                movimientoService.registrarMovimiento(
                        NUMERO_CUENTA,
                        TipoMovimiento.DEPOSITO,
                        BigDecimal.ZERO
                )
        )
                .isInstanceOf(MontoInvalidoException.class)
                .hasMessage("El monto debe ser mayor a cero");

        verifyNoInteractions(cuentaRepository, movimientoRepository);
    }

    @Test
    @DisplayName("Debe lanzar MontoInvalidoException cuando el monto es negativo")
    void debeLanzarExcepcionCuandoMontoEsNegativo() {
        // Cuando & Entonces
        assertThatThrownBy(() ->
                movimientoService.registrarMovimiento(
                        NUMERO_CUENTA,
                        TipoMovimiento.DEPOSITO,
                        new BigDecimal("-100.00")
                )
        )
                .isInstanceOf(MontoInvalidoException.class)
                .hasMessage("El monto debe ser mayor a cero");

        verifyNoInteractions(cuentaRepository, movimientoRepository);
    }

    @Test
    @DisplayName("Debe lanzar MontoInvalidoException cuando el monto tiene más de 2 decimales")
    void debeLanzarExcepcionCuandoMontoTieneMasDeDosDecimales() {
        // Dado
        BigDecimal montoInvalido = new BigDecimal("100.123"); // 3 decimales

        // Cuando & Entonces
        assertThatThrownBy(() ->
                movimientoService.registrarMovimiento(
                        NUMERO_CUENTA,
                        TipoMovimiento.DEPOSITO,
                        montoInvalido
                )
        )
                .isInstanceOf(MontoInvalidoException.class)
                .hasMessage("El monto no puede tener más de 2 decimales");

        verifyNoInteractions(cuentaRepository, movimientoRepository);
    }

    @Test
    @DisplayName("Debe aceptar monto con 2 decimales exactos")
    void debeAceptarMontoConDosDecimales() {
        // Dado
        BigDecimal montoValido = new BigDecimal("100.99"); // 2 decimales

        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Movimiento resultado = movimientoService.registrarMovimiento(
                NUMERO_CUENTA,
                TipoMovimiento.DEPOSITO,
                montoValido
        );

        // Then
        assertThat(resultado).isNotNull();
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    @DisplayName("Debe aceptar monto con 1 decimal")
    void debeAceptarMontoConUnDecimal() {
        // Dado
        BigDecimal montoValido = new BigDecimal("100.5"); // 1 decimal

        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Movimiento resultado = movimientoService.registrarMovimiento(
                NUMERO_CUENTA,
                TipoMovimiento.DEPOSITO,
                montoValido
        );

        // Then
        assertThat(resultado).isNotNull();
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    @DisplayName("Debe aceptar monto sin decimales")
    void debeAceptarMontoSinDecimales() {
        // Dado
        BigDecimal montoValido = new BigDecimal("100"); // 0 decimales

        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        Movimiento resultado = movimientoService.registrarMovimiento(
                NUMERO_CUENTA,
                TipoMovimiento.DEPOSITO,
                montoValido
        );

        // Then
        assertThat(resultado).isNotNull();
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    // ==================== VALIDACIÓN DE CUENTA ====================

    @Test
    @DisplayName("Debe lanzar CuentaNotFoundException cuando la cuenta no existe")
    void debeLanzarExcepcionCuandoCuentaNoExiste() {
        // Given
        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.empty());

        // Cuando & Entonces
        assertThatThrownBy(() ->
                movimientoService.registrarMovimiento(
                        NUMERO_CUENTA,
                        TipoMovimiento.DEPOSITO,
                        new BigDecimal("100.00")
                )
        )
                .isInstanceOf(CuentaNotFoundException.class)
                .hasMessageContaining(NUMERO_CUENTA);

        verify(cuentaRepository).findByNumeroCuentaForUpdate(NUMERO_CUENTA);
        verifyNoInteractions(movimientoRepository);
    }

    @Test
    @DisplayName("Debe lanzar CuentaInactivaException cuando la cuenta está inactiva")
    void debeLanzarExcepcionCuandoCuentaEstaInactiva() {
        // Given
        cuentaActiva.setEstado(false); // Cuenta inactiva

        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.of(cuentaActiva));

        // Cuando & Entonces
        assertThatThrownBy(() ->
                movimientoService.registrarMovimiento(
                        NUMERO_CUENTA,
                        TipoMovimiento.DEPOSITO,
                        new BigDecimal("100.00")
                )
        )
                .isInstanceOf(CuentaInactivaException.class)
                .hasMessageContaining(NUMERO_CUENTA);

        verify(cuentaRepository).findByNumeroCuentaForUpdate(NUMERO_CUENTA);
        verifyNoInteractions(movimientoRepository);
    }

    // ==================== VALIDACIÓN DE SALDO ====================

    @Test
    @DisplayName("Debe lanzar SaldoNoDisponibleException cuando retiro supera el saldo")
    void debeLanzarExcepcionCuandoRetiroSuperaSaldo() {
        // Given
        BigDecimal montoRetiro = new BigDecimal("3000.00"); // Mayor al saldo de 2000

        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.of(cuentaActiva));

        // Cuando & Entonces
        assertThatThrownBy(() ->
                movimientoService.registrarMovimiento(
                        NUMERO_CUENTA,
                        TipoMovimiento.RETIRO,
                        montoRetiro
                )
        )
                .isInstanceOf(SaldoNoDisponibleException.class)
                .hasMessage("Saldo no disponible");

        verify(cuentaRepository).findByNumeroCuentaForUpdate(NUMERO_CUENTA);
        verify(cuentaRepository, never()).save(any());
        verifyNoInteractions(movimientoRepository);
    }

    @Test
    @DisplayName("Debe lanzar SaldoNoDisponibleException cuando retiro deja saldo negativo")
    void debeLanzarExcepcionCuandoRetiroDejaElSaldoNegativo() {
        // Given
        BigDecimal montoRetiro = new BigDecimal("2000.01"); // Excede por 1 centavo

        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.of(cuentaActiva));

        // When & Then
        assertThatThrownBy(() ->
                movimientoService.registrarMovimiento(
                        NUMERO_CUENTA,
                        TipoMovimiento.RETIRO,
                        montoRetiro
                )
        )
                .isInstanceOf(SaldoNoDisponibleException.class);

        verifyNoInteractions(movimientoRepository);
    }

    // ==================== VERIFICACIÓN DE LOCK PESIMISTA ====================

    @Test
    @DisplayName("Debe usar findByNumeroCuentaForUpdate para obtener lock pesimista")
    void debeUsarLockPesimista() {
        // Dado
        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        movimientoService.registrarMovimiento(
                NUMERO_CUENTA,
                TipoMovimiento.DEPOSITO,
                new BigDecimal("100.00")
        );

        // Entonces - Verificar que se usó el método con lock
        verify(cuentaRepository).findByNumeroCuentaForUpdate(NUMERO_CUENTA);
        verify(cuentaRepository, never()).findByNumeroCuenta(anyString());
    }


    @Test
    @DisplayName("Debe guardar depósito con valor positivo")
    void debeGuardarDepositoConValorPositivo() {
        // Dado
        BigDecimal monto = new BigDecimal("575.00");
        ArgumentCaptor<Movimiento> movimientoCaptor = ArgumentCaptor.forClass(Movimiento.class);

        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        movimientoService.registrarMovimiento(
                NUMERO_CUENTA,
                TipoMovimiento.DEPOSITO,
                monto
        );

        // Entonces
        verify(movimientoRepository).save(movimientoCaptor.capture());
        Movimiento movimientoGuardado = movimientoCaptor.getValue();

        assertThat(movimientoGuardado.getValor()).isEqualByComparingTo(monto);
        assertThat(movimientoGuardado.getValor()).isPositive();
    }

    @Test
    @DisplayName("Debe guardar retiro con valor negativo")
    void debeGuardarRetiroConValorNegativo() {
        // Dado
        BigDecimal monto = new BigDecimal("575.00");
        ArgumentCaptor<Movimiento> movimientoCaptor = ArgumentCaptor.forClass(Movimiento.class);

        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        movimientoService.registrarMovimiento(
                NUMERO_CUENTA,
                TipoMovimiento.RETIRO,
                monto
        );

        // Entonces
        verify(movimientoRepository).save(movimientoCaptor.capture());
        Movimiento movimientoGuardado = movimientoCaptor.getValue();

        assertThat(movimientoGuardado.getValor()).isEqualByComparingTo(monto.negate());
        assertThat(movimientoGuardado.getValor()).isNegative();
    }

    // ==================== VERIFICACIÓN DE SNAPSHOT DE SALDO ====================

    @Test
    @DisplayName("Debe guardar snapshot correcto del saldo después del movimiento")
    void debeGuardarSnapshotCorrectoDeSaldo() {
        // Dado
        BigDecimal montoDeposito = new BigDecimal("500.00");
        BigDecimal saldoEsperado = new BigDecimal("2500.00");
        ArgumentCaptor<Movimiento> movimientoCaptor = ArgumentCaptor.forClass(Movimiento.class);

        when(cuentaRepository.findByNumeroCuentaForUpdate(NUMERO_CUENTA))
                .thenReturn(Optional.of(cuentaActiva));
        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando
        movimientoService.registrarMovimiento(
                NUMERO_CUENTA,
                TipoMovimiento.DEPOSITO,
                montoDeposito
        );

        // Entonces
        verify(movimientoRepository).save(movimientoCaptor.capture());
        Movimiento movimientoGuardado = movimientoCaptor.getValue();

        assertThat(movimientoGuardado.getSaldo()).isEqualByComparingTo(saldoEsperado);
    }
}