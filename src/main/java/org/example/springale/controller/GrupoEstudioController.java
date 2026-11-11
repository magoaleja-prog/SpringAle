package org.example.springale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springale.dto.EstudianteResumen;
import org.example.springale.dto.GrupoEstudioRequest;
import org.example.springale.dto.GrupoEstudioResponse;
import org.example.springale.service.GrupoEstudioService;
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
@RequestMapping("/api/grupos-estudio")
@Tag(name = "Grupos de estudio", description = "Herramientas colaborativas: grupos y sus miembros")
public class GrupoEstudioController {

    private final GrupoEstudioService service;

    public GrupoEstudioController(GrupoEstudioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar grupos de estudio")
    public List<GrupoEstudioResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar grupo de estudio por id")
    public GrupoEstudioResponse obtener(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar grupos de estudio por nombre")
    public List<GrupoEstudioResponse> buscar(@RequestParam String nombre) {
        return service.buscarPorNombre(nombre);
    }

    @GetMapping("/por-tema")
    @Operation(summary = "Buscar grupos de estudio por tema")
    public List<GrupoEstudioResponse> porTema(@RequestParam String tema) {
        return service.buscarPorTema(tema);
    }

    @GetMapping("/{id}/miembros")
    @Operation(summary = "Listar los miembros de un grupo de estudio")
    public List<EstudianteResumen> miembros(@PathVariable Long id) {
        return service.miembros(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR','ESTUDIANTE')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear grupo de estudio")
    public GrupoEstudioResponse crear(@RequestBody GrupoEstudioRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR','ESTUDIANTE')")
    @Operation(summary = "Actualizar grupo de estudio")
    public GrupoEstudioResponse actualizar(@PathVariable Long id, @RequestBody GrupoEstudioRequest request) {
        return service.actualizar(id, request);
    }

    @PostMapping("/{id}/miembros/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR','ESTUDIANTE')")
    @Operation(summary = "Agregar un estudiante al grupo de estudio")
    public GrupoEstudioResponse agregarMiembro(@PathVariable Long id, @PathVariable Long estudianteId) {
        return service.agregarMiembro(id, estudianteId);
    }

    @DeleteMapping("/{id}/miembros/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR','ESTUDIANTE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Quitar un estudiante del grupo de estudio")
    public void quitarMiembro(@PathVariable Long id, @PathVariable Long estudianteId) {
        service.quitarMiembro(id, estudianteId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR','ESTUDIANTE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar grupo de estudio")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
