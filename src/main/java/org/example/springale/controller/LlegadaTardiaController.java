package org.example.springale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.springale.dto.LlegadaQrRequest;
import org.example.springale.dto.LlegadaTardiaRequest;
import org.example.springale.dto.LlegadaTardiaResponse;
import org.example.springale.security.SeguridadService;
import org.example.springale.service.LlegadaTardiaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Registro de llegadas tardias.
 * Solo el ADMINISTRADOR registra; el ESTUDIANTE solo consulta su historial.
 */
@RestController
@RequestMapping("/api/llegadas-tardias")
@Tag(name = "Llegadas tardias", description = "Registro manual o por QR y consulta de historial")
public class LlegadaTardiaController {

    private final LlegadaTardiaService service;
    private final SeguridadService seguridad;

    public LlegadaTardiaController(LlegadaTardiaService service, SeguridadService seguridad) {
        this.service = service;
        this.seguridad = seguridad;
    }

    @GetMapping
    @Operation(summary = "Listar llegadas tardias",
            description = "El administrador ve todas; el estudiante solo las propias")
    public List<LlegadaTardiaResponse> listar(
            @RequestParam(required = false) Long estudianteId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.listar(estudianteId, desde, hasta);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar llegada tardia por id")
    public LlegadaTardiaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/historial/{estudianteId}")
    @Operation(summary = "Historial de llegadas tardias de un estudiante")
    public List<LlegadaTardiaResponse> historial(@PathVariable Long estudianteId) {
        return service.historial(estudianteId);
    }

    @GetMapping("/mis-llegadas")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Consultar mis llegadas tardias")
    public List<LlegadaTardiaResponse> misLlegadas() {
        return service.historial(seguridad.estudianteActual().getId());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Registrar llegada tardia manualmente")
    public ResponseEntity<LlegadaTardiaResponse> crear(
            @Valid @RequestBody LlegadaTardiaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PostMapping("/qr")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Registrar llegada tardia por escaneo de QR",
            description = "El codigo puede contener el documento, el id o el formato EST-{id}")
    public ResponseEntity<LlegadaTardiaResponse> registrarQr(
            @Valid @RequestBody LlegadaQrRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.registrarPorQr(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar llegada tardia")
    public LlegadaTardiaResponse actualizar(@PathVariable Long id,
                                            @Valid @RequestBody LlegadaTardiaRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar llegada tardia")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
