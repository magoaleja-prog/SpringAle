package org.example.springale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Registro por escaneo de QR. El codigo puede ser el documento del
 * estudiante, su id numerico o el formato EST-{id}.
 */
public record LlegadaQrRequest(
        @NotBlank(message = "El codigo QR es obligatorio") @Size(max = 50) String codigoQr,
        @Size(max = 500) String observacion
) {
}
