package org.example.springale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.springale.dto.AporteEquipoRequest;
import org.example.springale.dto.AporteEquipoResponse;
import org.example.springale.dto.CalificarProyectoRequest;
import org.example.springale.dto.EntregaFinalRequest;
import org.example.springale.dto.ProyectoConsolidadoResponse;
import org.example.springale.dto.ProyectoEquipoRequest;
import org.example.springale.dto.ProyectoEquipoResponse;
import org.example.springale.service.ProyectoEquipoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Proyectos de trabajo en equipo, aportes, consolidado y entrega final. */
@RestController
@RequestMapping("/api/proyectos")
@Tag(name = "Trabajo en equipo", description = "Proyectos grupales, aportes y entrega final")
public class ProyectoEquipoController {

    private final ProyectoEquipoService service;

    public ProyectoEquipoController(ProyectoEquipoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar proyectos segun el rol autenticado")
    public List<ProyectoEquipoResponse> listar(
            @RequestParam(required = false) Long materiaId) {
        return service.listar(materiaId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar proyecto por id")
    public ProyectoEquipoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/{id}/consolidado")
    @Operation(summary = "Vista consolidada con todos los aportes del equipo")
    public ProyectoConsolidadoResponse consolidado(@PathVariable Long id) {
        return service.consolidado(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR')")
    @Operation(summary = "Crear proyecto grupal y asignar lider")
    public ResponseEntity<ProyectoEquipoResponse> crear(
            @Valid @RequestBody ProyectoEquipoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PostMapping("/{id}/aportes")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Subir mi aporte individual al proyecto")
    public ResponseEntity<AporteEquipoResponse> aportar(
            @PathVariable Long id,
            @Valid @RequestBody AporteEquipoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.agregarAporte(id, request));
    }

    @PostMapping("/{id}/entrega-final")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "El lider realiza la entrega final del equipo")
    public ProyectoEquipoResponse entregaFinal(
            @PathVariable Long id,
            @Valid @RequestBody EntregaFinalRequest request) {
        return service.entregaFinal(id, request);
    }

    @PatchMapping("/{id}/calificar")
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR')")
    @Operation(summary = "Calificar el trabajo completo y dejar comentario")
    public ProyectoEquipoResponse calificar(
            @PathVariable Long id,
            @Valid @RequestBody CalificarProyectoRequest request) {
        return service.calificar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESOR','ADMINISTRADOR')")
    @Operation(summary = "Eliminar proyecto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
