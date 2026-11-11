package org.example.springale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.springale.dto.PeriodoRequest;
import org.example.springale.dto.PeriodoResponse;
import org.example.springale.service.PeriodoService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** CRUD de periodos academicos. */
@RestController
@RequestMapping("/api/periodos")
@Tag(name = "Periodos", description = "Periodos academicos para agrupar calificaciones")
public class PeriodoController {

    private final PeriodoService service;

    public PeriodoController(PeriodoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar periodos")
    public List<PeriodoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar periodos activos")
    public List<PeriodoResponse> activos() {
        return service.activos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar periodo por id")
    public PeriodoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear periodo")
    public ResponseEntity<PeriodoResponse> crear(@Valid @RequestBody PeriodoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar periodo")
    public PeriodoResponse actualizar(@PathVariable Long id,
                                      @Valid @RequestBody PeriodoRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar periodo")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
