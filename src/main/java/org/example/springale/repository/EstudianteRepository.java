package org.example.springale.repository;

import org.example.springale.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** Acceso a datos de estudiantes. */
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    List<Estudiante> findBySemilleroId(Long semilleroId);

    long countBySemilleroId(Long semilleroId);

    List<Estudiante> findByApellidoContainingIgnoreCase(String apellido);

    Optional<Estudiante> findByEmailIgnoreCase(String email);

    Optional<Estudiante> findByDocumento(String documento);

    Optional<Estudiante> findByUsuarioId(Long usuarioId);

    Optional<Estudiante> findByUsuarioEmailIgnoreCase(String email);

    List<Estudiante> findBySemestreGreaterThanEqual(Integer semestre);

    List<Estudiante> findByMateriasId(Long materiaId);

    @Query("""
            select e from Estudiante e
            where lower(e.nombre) like lower(concat('%', :texto, '%'))
               or lower(e.apellido) like lower(concat('%', :texto, '%'))
            """)
    List<Estudiante> buscarPorNombreOApellido(@Param("texto") String texto);
}
