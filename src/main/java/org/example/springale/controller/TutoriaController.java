package org.example.springale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.springale.dto.CambiarEstadoTutoriaRequest;
import org.example.springale.dto.MensajeTutoriaRequest;
import org.example.springale.dto.MensajeTutoriaResponse;
import org.example.springale.dto.PostulacionTutorRequest;
import org.example.springale.dto.PostulacionTutorResponse;
import org.example.springale.dto.TutoriaRequest;
import org.example.springale.dto.TutoriaResponse;
import org.example.springale.entity.EstadoPostulacion;
import org.example.springale.entity.EstadoTutoria;
import org.example.springale.service.TutoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Tutorias entre estudiantes: postulaciones, solicitudes, estados y chat. */
@RestController
@RequestMapping("/api/tutorias")
@Tag(name = "Tutorias", description = "Tutores entre pares, solicitudes, chat e historial")
public class TutoriaController {

    private final TutoriaService service;

    public TutoriaController(TutoriaService service) {
        this.service = service;
    }

    @PostMapping("/postulaciones")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Postularme como tutor de una materia",
            description = "Requiere promedio minimo de 3.8 en la materia")
    public ResponseEntity<PostulacionTutorResponse> postular(
            @Valid @RequestBody PostulacionTutorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.postular(request));
    }

    @GetMapping("/postulaciones")
    @Operation(summary = "Listar postulaciones (propias o todas segun el rol)")
    public List<PostulacionTutorResponse> postulaciones(
            @RequestParam(required = false) EstadoPostulacion estado) {
        return service.listarPostulaciones(estado);
    }

    @PatchMapping("/postulaciones/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Aprobar o rechazar una postulacion de tutor")
    public PostulacionTutorResponse cambiarEstadoPostulacion(
            @PathVariable Long id,
            @RequestParam EstadoPostulacion estado) {
        return service.cambiarEstadoPostulacion(id, estado);
    }

    @GetMapping("/tutores")
    @Operation(summary = "Listar tutores aprobados de una materia")
    public List<PostulacionTutorResponse> tutores(@RequestParam Long materiaId) {
        return service.tutoresPorMateria(materiaId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Solicitar una tutoria")
    public ResponseEntity<TutoriaResponse> solicitar(
            @Valid @RequestBody TutoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.solicitar(request));
    }

    @GetMapping
    @Operation(summary = "Listar tutorias segun el rol")
    public List<TutoriaResponse> listar(
            @RequestParam(required = false) EstadoTutoria estado) {
        return service.listar(estado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tutoria por id")
    public TutoriaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/tutor/{tutorId}")
    @Operation(summary = "Tutorias de un tutor")
    public List<TutoriaResponse> porTutor(
            @PathVariable Long tutorId,
            @RequestParam(required = false) EstadoTutoria estado) {
        return service.porTutor(tutorId, estado);
    }

    @GetMapping("/aprendiz/{aprendizId}")
    @Operation(summary = "Tutorias de un aprendiz")
    public List<TutoriaResponse> porAprendiz(
            @PathVariable Long aprendizId,
            @RequestParam(required = false) EstadoTutoria estado) {
        return service.porAprendiz(aprendizId, estado);
    }

    @GetMapping("/historial/{estudianteId}")
    @Operation(summary = "Historial de tutorias completadas")
    public List<TutoriaResponse> historial(@PathVariable Long estudianteId) {
        return service.historial(estudianteId);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Aceptar, rechazar, completar o cancelar una tutoria")
    public TutoriaResponse cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoTutoriaRequest request) {
        return service.cambiarEstado(id, request);
    }

    @GetMapping("/{id}/mensajes")
    @Operation(summary = "Listar mensajes del chat de una tutoria aceptada")
    public List<MensajeTutoriaResponse> mensajes(@PathVariable Long id) {
        return service.listarMensajes(id);
    }

    @PostMapping("/{id}/mensajes")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Enviar mensaje en el chat de una tutoria")
    public ResponseEntity<MensajeTutoriaResponse> enviarMensaje(
            @PathVariable Long id,
            @Valid @RequestBody MensajeTutoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.enviarMensaje(id, request));
    }
}
