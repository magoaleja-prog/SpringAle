package org.example.springale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.springale.dto.CalificacionRequest;
import org.example.springale.dto.CalificacionResponse;
import org.example.springale.dto.RendimientoResponse;
import org.example.springale.service.CalificacionService;
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

import java.util.List;

/** CRUD de notas, historial y rendimiento academico. */
@RestController
@RequestMapping("/api/calificaciones")
@Tag(name = "Calificaciones", description = "Notas por materia y periodo, historial y rendimiento")
public class CalificacionController {

    private final CalificacionService service;

    public CalificacionController(CalificacionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar calificaciones",
            description = "Un estudiante solo ve las propias; un profesor puede filtrar por materia")
    public List<CalificacionResponse> listar(
            @RequestParam(required = false) Long estudianteId,
            @RequestParam(required = false) Long materiaId) {
        return service.listar(estudianteId, materiaId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar calificacion por id")
    public CalificacionResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/estudiante/{estudianteId}/historial")
    @Operation(summary = "Historial de notas de un estudiante por materia")
    public List<CalificacionResponse> historial(
            @PathVariable Long estudianteId,
            @RequestParam(required = false) Long materiaId) {
        return service.historial(estudianteId, materiaId);
    }

    @GetMapping("/estudiante/{estudianteId}/rendimiento")
    @Operation(summary = "Rendimiento academico y promedio por materia")
    public RendimientoResponse rendimiento(@PathVariable Long estudianteId) {
        return service.rendimiento(estudianteId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR')")
    @Operation(summary = "Registrar calificacion")
    public ResponseEntity<CalificacionResponse> crear(
            @Valid @RequestBody CalificacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR')")
    @Operation(summary = "Actualizar calificacion")
    public CalificacionResponse actualizar(@PathVariable Long id,
                                           @Valid @RequestBody CalificacionRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR')")
    @Operation(summary = "Eliminar calificacion")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
