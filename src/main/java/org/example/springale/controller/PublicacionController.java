package org.example.springale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springale.dto.PublicacionRequest;
import org.example.springale.dto.PublicacionResponse;
import org.example.springale.entity.TipoPublicacion;
import org.example.springale.service.PublicacionService;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/publicaciones")
@Tag(name = "Publicaciones", description = "Herramientas colaborativas: anuncios, preguntas y apuntes de los grupos")
public class PublicacionController {

    private final PublicacionService service;

    public PublicacionController(PublicacionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar publicaciones (opcional filtrar por grupo y tipo)")
    public List<PublicacionResponse> listar(
            @RequestParam(required = false) Long grupoId,
            @RequestParam(required = false) TipoPublicacion tipo) {
        return service.listar(grupoId, tipo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar publicacion por id")
    public PublicacionResponse obtener(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/por-grupo/{grupoId}")
    @Operation(summary = "Listar publicaciones de un grupo de estudio")
    public List<PublicacionResponse> porGrupo(@PathVariable Long grupoId) {
        return service.porGrupo(grupoId);
    }

    @GetMapping("/por-autor/{autorId}")
    @Operation(summary = "Listar publicaciones de un estudiante")
    public List<PublicacionResponse> porAutor(@PathVariable Long autorId) {
        return service.porAutor(autorId);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar publicaciones por titulo o contenido")
    public List<PublicacionResponse> buscar(@RequestParam String texto) {
        return service.buscarPorTexto(texto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear publicacion")
    public PublicacionResponse crear(@RequestBody PublicacionRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar publicacion")
    public PublicacionResponse actualizar(@PathVariable Long id, @RequestBody PublicacionRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar publicacion")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
