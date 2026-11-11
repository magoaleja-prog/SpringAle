package org.example.springale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springale.dto.NotificacionResponse;
import org.example.springale.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Notificaciones internas del usuario autenticado. */
@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Recordatorios y avisos internos")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar mis notificaciones")
    public List<NotificacionResponse> listar() {
        return service.listarPropias();
    }

    @GetMapping("/no-leidas")
    @Operation(summary = "Listar mis notificaciones no leidas")
    public List<NotificacionResponse> noLeidas() {
        return service.listarNoLeidas();
    }

    @GetMapping("/no-leidas/count")
    @Operation(summary = "Contar notificaciones no leidas")
    public long contarNoLeidas() {
        return service.contarNoLeidas();
    }

    @PatchMapping("/{id}/leer")
    @Operation(summary = "Marcar una notificacion como leida")
    public NotificacionResponse marcarLeida(@PathVariable Long id) {
        return service.marcarLeida(id);
    }

    @PatchMapping("/leer-todas")
    @Operation(summary = "Marcar todas las notificaciones como leidas")
    public ResponseEntity<Void> marcarTodas() {
        service.marcarTodasLeidas();
        return ResponseEntity.noContent().build();
    }
}
