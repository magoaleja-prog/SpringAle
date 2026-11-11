package org.example.springale.repository;

import org.example.springale.entity.Rol;
import org.example.springale.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Acceso a datos de usuarios del sistema. */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByActivoTrue();
}
