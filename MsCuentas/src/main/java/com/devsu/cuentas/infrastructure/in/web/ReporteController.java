package com.devsu.cuentas.infrastructure.in.web;

import com.devsu.cuentas.domain.port.in.GenerarReporteUseCase;
import com.devsu.cuentas.infrastructure.in.web.dto.ReporteResponse;
import com.devsu.cuentas.infrastructure.in.web.mapper.ReporteMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    private final GenerarReporteUseCase generarReporteUseCase;

    public ReporteController(GenerarReporteUseCase generarReporteUseCase) {
        this.generarReporteUseCase = generarReporteUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ReporteResponse>> generarReporte(
            @RequestParam String clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        List<ReporteResponse> reporte = generarReporteUseCase.generar(clienteId, desde, hasta)
                .stream()
                .map(ReporteMapper::toResponse)
                .toList();

        return ResponseEntity.ok(reporte);
    }
}