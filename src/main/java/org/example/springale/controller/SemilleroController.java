package org.example.springale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springale.dto.EstudianteResponse;
import org.example.springale.dto.SemilleroRequest;
import org.example.springale.dto.SemilleroResponse;
import org.example.springale.service.SemilleroService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/semilleros")
@Tag(name = "Semilleros", description = "CRUD y finders de semilleros")
public class SemilleroController {

    private final SemilleroService service;

    public SemilleroController(SemilleroService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar semilleros")
    public List<SemilleroResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar semillero por id")
    public SemilleroResponse obtener(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar semilleros por nombre")
    public List<SemilleroResponse> buscar(@RequestParam String nombre) {
        return service.buscarPorNombre(nombre);
    }

    @GetMapping("/{id}/estudiantes")
    @Operation(summary = "Listar estudiantes de un semillero")
    public List<EstudianteResponse> estudiantes(@PathVariable Long id) {
        return service.estudiantesDe(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear semillero")
    public SemilleroResponse crear(@RequestBody SemilleroRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar semillero")
    public SemilleroResponse actualizar(@PathVariable Long id, @RequestBody SemilleroRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar semillero")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
