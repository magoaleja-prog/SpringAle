package org.example.springale;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Comprueba que los errores de la API se devuelvan siempre como JSON
 * con la estructura uniforme de {@code ApiError}.
 */
@SpringBootTest(properties = "app.open-browser=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiErrorHandlingTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sinTokenDevuelve401ConJsonEstructurado() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.mensaje").value("No autenticado o token invalido"))
                .andExpect(jsonPath("$.ruta").value("/api/usuarios"));
    }

    @Test
    void metodoNoSoportadoDevuelve405() throws Exception {
        mockMvc.perform(patch("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.mensaje").value(containsString("PATCH")))
                .andExpect(jsonPath("$.mensaje").value(containsString("POST")));
    }

    @Test
    void tipoContenidoNoSoportadoDevuelve415() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("hola"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.mensaje").value(containsString("application/json")));
    }

    @Test
    void validacionDevuelve400ConCampos() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"no-es-email\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.campos.email").exists())
                .andExpect(jsonPath("$.campos.password").exists());
    }

    @Test
    void jsonMalformadoDevuelve400() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void formatoNoAceptableDevuelve406Json() throws Exception {
        String token = login("admin@springale.edu", "Admin123*");

        mockMvc.perform(get("/api/materias")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(406))
                .andExpect(jsonPath("$.mensaje").value(containsString("application/json")));
    }

    @Test
    void variableRutaVaciaDevuelve400Json() throws Exception {
        String token = login("ana.torres@springale.edu", "Estudiante123*");

        mockMvc.perform(get("/api/tutorias//mensajes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void endpointErrorDevuelveJsonEstructurado() throws Exception {
        mockMvc.perform(get("/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void credencialesIncorrectasDevuelven400Amigable() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@springale.edu\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("Credenciales incorrectas"));
    }

    @Test
    void rutaInexistenteConTokenDevuelve404() throws Exception {
        String token = login("admin@springale.edu", "Admin123*");

        mockMvc.perform(get("/api/no-existe")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void tokenDeUsuarioDesactivadoDevuelve401() throws Exception {
        String adminToken = login("admin@springale.edu", "Admin123*");
        String email = "desactivado" + System.nanoTime() + "@springale.edu";

        MvcResult creado = mockMvc.perform(post("/api/usuarios")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Usuario",
                                  "apellido": "Desactivado",
                                  "email": "%s",
                                  "password": "Usuario123*",
                                  "rol": "ESTUDIANTE",
                                  "activo": true
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andReturn();

        Number id = JsonPath.read(creado.getResponse().getContentAsString(), "$.id");
        String tokenUsuario = login(email, "Usuario123*");

        mockMvc.perform(delete("/api/usuarios/" + id.longValue())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/notificaciones")
                        .header("Authorization", "Bearer " + tokenUsuario))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    private String login(String email, String password) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        return JsonPath.read(resultado.getResponse().getContentAsString(), "$.token");
    }
}
