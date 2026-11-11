package org.example.springale;

import org.example.springale.entity.Rol;
import org.example.springale.entity.Usuario;
import org.example.springale.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "app.open-browser=false")
@ActiveProfiles("test")
class SpringAleApplicationTests {

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
    }

    @Test
    void cargaLosDatosSemillaYLaClaveDelAdministrador() {
        Usuario administrador = usuarios.findByEmailIgnoreCase("admin@springale.edu")
                .orElseThrow(() -> new AssertionError("No se creo el administrador semilla"));

        assertEquals(Rol.ADMINISTRADOR, administrador.getRol());
        assertTrue(administrador.isActivo());
        assertTrue(passwordEncoder.matches("Admin123*", administrador.getPassword()));
    }
}
