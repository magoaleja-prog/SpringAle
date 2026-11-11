package org.example.springale.repository;

import org.example.springale.entity.Publicacion;
import org.example.springale.entity.TipoPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    List<Publicacion> findAllByOrderByFechaDesc();

    List<Publicacion> findByGrupoIdOrderByFechaDesc(Long grupoId);

    List<Publicacion> findByAutorIdOrderByFechaDesc(Long autorId);

    List<Publicacion> findByTipoOrderByFechaDesc(TipoPublicacion tipo);

    List<Publicacion> findByGrupoIdAndTipoOrderByFechaDesc(Long grupoId, TipoPublicacion tipo);

    List<Publicacion> findByTituloContainingIgnoreCaseOrContenidoContainingIgnoreCase(String titulo, String contenido);
}
