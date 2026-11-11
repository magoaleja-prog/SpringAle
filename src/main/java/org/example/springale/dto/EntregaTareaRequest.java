package org.example.springale.dto;

import jakarta.validation.constraints.Size;

/** Comentario opcional que envia el estudiante al entregar una tarea. */
public record EntregaTareaRequest(
        @Size(max = 500) String comentario
) {
}
