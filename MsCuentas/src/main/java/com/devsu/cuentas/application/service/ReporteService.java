package com.devsu.cuentas.application.service;

import com.devsu.cuentas.domain.exception.ClienteNoEncontradoException;
import com.devsu.cuentas.domain.model.ClienteLocal;
import com.devsu.cuentas.domain.model.Cuenta;
import com.devsu.cuentas.domain.model.Movimiento;
import com.devsu.cuentas.domain.model.ReporteItem;
import com.devsu.cuentas.domain.port.in.GenerarReporteUseCase;
import com.devsu.cuentas.domain.port.out.ClienteLocalRepositoryPort;
import com.devsu.cuentas.domain.port.out.MovimientoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;


@Service
@Transactional(readOnly = true)
public class ReporteService implements GenerarReporteUseCase {
    private static final String FORMATO_LOCAL = "dd/MM/yyyy HH:mm:ss";
    private static final ZoneId ZONE_COLOMBIA = ZoneId.of("America/Bogota");

    private final MovimientoRepositoryPort movimientoRepository;
    private final ClienteLocalRepositoryPort clienteLocalRepository;

    public ReporteService(MovimientoRepositoryPort movimientoRepository,
                          ClienteLocalRepositoryPort clienteLocalRepository) {
        this.movimientoRepository = movimientoRepository;
        this.clienteLocalRepository = clienteLocalRepository;
    }

    @Override
    public List<ReporteItem> generar(String clienteId, LocalDate desde, LocalDate hasta) {
        // 1. Convertir LocalDate → Instant UTC (inicio del día 00:00, fin del día 23:59:59.999999999)
        Instant desdeInstant = desde.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant hastaInstant = hasta.atTime(23, 59, 59, 999_999_999)
                .atOffset(ZoneOffset.UTC)
                .toInstant();

        // 2. Traer los movimientos del cliente en el rango de fechas
        List<Movimiento> movimientos = movimientoRepository
                .findByCuentaClienteIdAndFechaBetween(clienteId, desdeInstant, hastaInstant);

        // 3. Obtener el nombre del cliente una sola vez
        ClienteLocal cliente = clienteLocalRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException(clienteId));

        // 4. Mapear cada movimiento a ReporteItem
        String nombreCliente = cliente.getNombre();
        return movimientos.stream()
                .map(movimiento -> toReporteItem(movimiento, nombreCliente))
                .toList();
    }

    private ReporteItem toReporteItem(Movimiento movimiento, String nombreCliente) {
        // Acceder a la cuenta (lazy loading funciona porque estamos en transacción readOnly)
        Cuenta cuenta = movimiento.getCuenta();

        return new ReporteItem(
                this.convertirFechaString(movimiento.getFecha(), FORMATO_LOCAL),
                movimiento.getFecha(),
                nombreCliente,
                cuenta.getNumeroCuenta(),
                cuenta.getTipo(),
                cuenta.getSaldoInicial(),
                cuenta.getEstado(),
                movimiento.getValor(),        // valor con signo: -540, +600
                movimiento.getSaldo()         // snapshot del saldo después del movimiento
        );
    }

    private String convertirFechaString(Instant fecha, String formatoLocal) {
        if (fecha == null) {
            return "";
        }
        Locale localeEspanol = new Locale("es");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatoLocal, localeEspanol)
                .withZone(ZONE_COLOMBIA);

        // Formateamos en zona horaria de Colombia
        return formatter.format(fecha);
    }
}
