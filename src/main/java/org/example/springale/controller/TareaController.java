package org.example.springale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.springale.dto.AlertaAtrasoResponse;
import org.example.springale.dto.EntregaTareaRequest;
import org.example.springale.dto.EntregaTareaResponse;
import org.example.springale.dto.TareaEstudianteResponse;
import org.example.springale.dto.TareaRequest;
import org.example.springale.dto.TareaResponse;
import org.example.springale.security.SeguridadService;
import org.example.springale.service.TareaService;
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

/** Tareas, entregas, recordatorios y alertas de atraso. */
@RestController
@RequestMapping("/api/tareas")
@Tag(name = "Tareas", description = "Creacion de tareas, entregas y control de atrasos")
public class TareaController {

    private final TareaService service;
    private final SeguridadService seguridad;

    public TareaController(TareaService service, SeguridadService seguridad) {
        this.service = service;
        this.seguridad = seguridad;
    }

    @GetMapping
    @Operation(summary = "Listar tareas")
    public List<TareaResponse> listar(
            @RequestParam(required = false) Long materiaId,
            @RequestParam(required = false) Long profesorId) {
        return service.listar(materiaId, profesorId);
    }

    @GetMapping("/mis-tareas")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Ver mis tareas con su estado individual")
    public List<TareaEstudianteResponse> misTareas() {
        return service.listarParaEstudiante(seguridad.estudianteActual().getId());
    }

    @GetMapping("/estudiante/{estudianteId}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE','PROFESOR','ADMINISTRADOR')")
    @Operation(summary = "Ver tareas de un estudiante")
    public List<TareaEstudianteResponse> tareasDeEstudiante(@PathVariable Long estudianteId) {
        return service.listarParaEstudiante(estudianteId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarea por id")
    public TareaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR')")
    @Operation(summary = "Crear tarea y notificar a los estudiantes inscritos")
    public ResponseEntity<TareaResponse> crear(@Valid @RequestBody TareaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR')")
    @Operation(summary = "Actualizar tarea")
    public TareaResponse actualizar(@PathVariable Long id,
                                    @Valid @RequestBody TareaRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR')")
    @Operation(summary = "Eliminar tarea")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/entrega")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Marcar tarea como completada",
            description = "Otorga puntos si se entrega a tiempo y bonificacion si es anticipada")
    public ResponseEntity<EntregaTareaResponse> entregar(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) EntregaTareaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.marcarEntregada(id, request));
    }

    @GetMapping("/{id}/entregas")
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR')")
    @Operation(summary = "Listar entregas de una tarea")
    public List<EntregaTareaResponse> entregas(@PathVariable Long id) {
        return service.entregasDe(id);
    }

    @GetMapping("/{id}/atrasadas")
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR')")
    @Operation(summary = "Estudiantes atrasados en una tarea")
    public List<AlertaAtrasoResponse> atrasadas(@PathVariable Long id) {
        return service.alertasDeTarea(id);
    }

    @GetMapping("/alertas")
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR')")
    @Operation(summary = "Alerta acumulada de tareas sin entregar",
            description = "El profesor ve que estudiantes acumulan tareas pendientes o atrasadas")
    public List<AlertaAtrasoResponse> alertas(
            @RequestParam(required = false) Long materiaId) {
        return service.alertasAcumuladas(materiaId);
    }
}
