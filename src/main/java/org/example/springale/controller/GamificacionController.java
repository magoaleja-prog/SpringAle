package org.example.springale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.springale.dto.EventoPuntosResponse;
import org.example.springale.dto.OtorgarPuntosRequest;
import org.example.springale.dto.PerfilGamificacionResponse;
import org.example.springale.dto.RankingResponse;
import org.example.springale.service.GamificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Puntos, niveles, logros y tablas de posicion. */
@RestController
@RequestMapping("/api/gamificacion")
@Tag(name = "Gamificacion", description = "Puntos, niveles, logros y rankings")
public class GamificacionController {

    private final GamificacionService service;

    public GamificacionController(GamificacionService service) {
        this.service = service;
    }

    @GetMapping("/ranking")
    @Operation(summary = "Tabla de posicion general")
    public List<RankingResponse> rankingGeneral() {
        return service.rankingGeneral();
    }

    @GetMapping("/ranking/materia/{materiaId}")
    @Operation(summary = "Tabla de posicion por materia")
    public List<RankingResponse> rankingPorMateria(@PathVariable Long materiaId) {
        return service.rankingPorMateria(materiaId);
    }

    @GetMapping("/estudiante/{estudianteId}")
    @Operation(summary = "Perfil de gamificacion: puntos, nivel, logros e historial")
    public PerfilGamificacionResponse perfil(@PathVariable Long estudianteId) {
        return service.perfil(estudianteId);
    }

    @GetMapping("/estudiante/{estudianteId}/historial")
    @Operation(summary = "Historial de puntos de un estudiante")
    public List<EventoPuntosResponse> historial(@PathVariable Long estudianteId) {
        return service.historial(estudianteId);
    }

    @PostMapping("/otorgar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Otorgar puntos manualmente")
    public ResponseEntity<EventoPuntosResponse> otorgar(
            @Valid @RequestBody OtorgarPuntosRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                service.otorgarManual(
                        request.estudianteId(),
                        request.motivo(),
                        request.descripcion(),
                        request.materiaId()
                ));
    }
}
