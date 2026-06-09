package com.devsu.cuentas.domain.port.in;

import com.devsu.cuentas.domain.model.ReporteItem;

import java.time.LocalDate;
import java.util.List;

public interface GenerarReporteUseCase {
    List<ReporteItem> generar(String clienteId, LocalDate desde, LocalDate hasta);
}