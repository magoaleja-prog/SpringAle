package org.example.springale.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de OpenAPI/Swagger.
 * El boton Authorize permite pegar el token JWT para probar los endpoints.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SpringAle - Sistema de Gestion Academica Colaborativa",
                version = "3.0",
                description = """
                        Backend REST para la gestion academica con enfoque colaborativo.
                        Incluye usuarios y roles, notas, tareas, llegadas tardias,
                        trabajo en equipo, tutorias entre pares, gamificacion y
                        notificaciones internas.
                        """,
                contact = @Contact(name = "Equipo SpringAle")
        ),
        servers = @Server(url = "/", description = "Servidor local"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Pegue aqui el token obtenido en /auth/login"
)
public class OpenApiConfig {
}
