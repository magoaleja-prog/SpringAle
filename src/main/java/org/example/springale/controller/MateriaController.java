package org.example.springale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.springale.dto.EstudianteResumen;
import org.example.springale.dto.MateriaRequest;
import org.example.springale.dto.MateriaResponse;
import org.example.springale.service.MateriaService;
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

/** CRUD de materias e inscripciones de estudiantes. */
@RestController
@RequestMapping("/api/materias")
@Tag(name = "Materias", description = "Materias, profesores responsables e inscripciones")
public class MateriaController {

    private final MateriaService service;

    public MateriaController(MateriaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar materias")
    public List<MateriaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar materia por id")
    public MateriaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/{id}/estudiantes")
    @Operation(summary = "Listar estudiantes inscritos en una materia")
    public List<EstudianteResumen> estudiantes(@PathVariable Long id) {
        return service.estudiantesDe(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR')")
    @Operation(summary = "Crear materia")
    public ResponseEntity<MateriaResponse> crear(@Valid @RequestBody MateriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR')")
    @Operation(summary = "Actualizar materia")
    public MateriaResponse actualizar(@PathVariable Long id,
                                      @Valid @RequestBody MateriaRequest request) {
        return service.actualizar(id, request);
    }

    @PostMapping("/{id}/estudiantes/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR')")
    @Operation(summary = "Inscribir estudiante en la materia")
    public MateriaResponse inscribir(@PathVariable Long id, @PathVariable Long estudianteId) {
        return service.inscribir(id, estudianteId);
    }

    @DeleteMapping("/{id}/estudiantes/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR')")
    @Operation(summary = "Retirar estudiante de la materia")
    public MateriaResponse retirar(@PathVariable Long id, @PathVariable Long estudianteId) {
        return service.retirar(id, estudianteId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar materia")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
