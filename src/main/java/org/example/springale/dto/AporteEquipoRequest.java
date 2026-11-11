package org.example.springale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.springale.entity.TipoAporte;

/** Entregable individual que sube un miembro del equipo. */
public record AporteEquipoRequest(
        @NotNull(message = "El tipo de aporte es obligatorio") TipoAporte tipo,
        @NotBlank(message = "El contenido es obligatorio") @Size(max = 2000) String contenido,
        @Size(max = 500) String comentario
) {
}
