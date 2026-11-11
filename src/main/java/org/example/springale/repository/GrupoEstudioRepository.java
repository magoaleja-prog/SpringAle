package org.example.springale.repository;

import org.example.springale.entity.GrupoEstudio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoEstudioRepository extends JpaRepository<GrupoEstudio, Long> {

    List<GrupoEstudio> findByNombreContainingIgnoreCase(String nombre);

    List<GrupoEstudio> findByTemaContainingIgnoreCase(String tema);

    List<GrupoEstudio> findByMiembrosId(Long estudianteId);
}
