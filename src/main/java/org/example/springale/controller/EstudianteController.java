package org.example.springale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.springale.dto.EstudianteRequest;
import org.example.springale.dto.EstudianteResponse;
import org.example.springale.security.SeguridadService;
import org.example.springale.service.EstudianteService;
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

/** CRUD y consultas de estudiantes. */
@RestController
@RequestMapping("/api/estudiantes")
@Tag(name = "Estudiantes", description = "Gestion del perfil academico de estudiantes")
public class EstudianteController {

    private final EstudianteService service;
    private final SeguridadService seguridad;

    public EstudianteController(EstudianteService service, SeguridadService seguridad) {
        this.service = service;
        this.seguridad = seguridad;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR')")
    @Operation(summary = "Listar estudiantes")
    public List<EstudianteResponse> listar() {
        return service.listar();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Consultar mi perfil de estudiante")
    public EstudianteResponse miPerfil() {
        return service.buscar(seguridad.estudianteActual().getId());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar estudiante por id")
    public EstudianteResponse obtener(@PathVariable Long id) {
        seguridad.validarAccesoEstudiante(id);
        return service.buscar(id);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR')")
    @Operation(summary = "Buscar estudiantes por nombre o apellido")
    public List<EstudianteResponse> buscar(@RequestParam String texto) {
        return service.buscarPorTexto(texto);
    }

    @GetMapping("/por-semestre")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR')")
    @Operation(summary = "Buscar estudiantes por semestre minimo")
    public List<EstudianteResponse> porSemestre(@RequestParam Integer minimo) {
        return service.porSemestre(minimo);
    }

    @GetMapping("/por-semillero/{semilleroId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR')")
    @Operation(summary = "Listar estudiantes de un semillero")
    public List<EstudianteResponse> porSemillero(@PathVariable Long semilleroId) {
        return service.porSemillero(semilleroId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear estudiante")
    public ResponseEntity<EstudianteResponse> crear(@Valid @RequestBody EstudianteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar estudiante")
    public EstudianteResponse actualizar(@PathVariable Long id,
                                         @Valid @RequestBody EstudianteRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar estudiante")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
