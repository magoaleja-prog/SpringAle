package org.example.springale.service;

import org.example.springale.dto.EstudianteResumen;
import org.example.springale.dto.GrupoEstudioRequest;
import org.example.springale.dto.GrupoEstudioResponse;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.GrupoEstudio;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.GrupoEstudioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class GrupoEstudioService {

    private final GrupoEstudioRepository grupos;
    private final EstudianteRepository estudiantes;

    public GrupoEstudioService(GrupoEstudioRepository grupos, EstudianteRepository estudiantes) {
        this.grupos = grupos;
        this.estudiantes = estudiantes;
    }

    public List<GrupoEstudioResponse> listar() {
        return grupos.findAll().stream().map(GrupoEstudioResponse::from).toList();
    }

    public GrupoEstudioResponse buscar(Long id) {
        return GrupoEstudioResponse.from(entidad(id));
    }

    public List<GrupoEstudioResponse> buscarPorNombre(String nombre) {
        return grupos.findByNombreContainingIgnoreCase(nombre).stream().map(GrupoEstudioResponse::from).toList();
    }

    public List<GrupoEstudioResponse> buscarPorTema(String tema) {
        return grupos.findByTemaContainingIgnoreCase(tema).stream().map(GrupoEstudioResponse::from).toList();
    }

    public List<EstudianteResumen> miembros(Long id) {
        return entidad(id).getMiembros().stream()
                .map(EstudianteResumen::from)
                .toList();
    }

    @Transactional
    public GrupoEstudioResponse crear(GrupoEstudioRequest request) {
        GrupoEstudio grupo = new GrupoEstudio();
        grupo.setFechaCreacion(LocalDate.now());
        aplicar(grupo, request);
        grupo.getMiembros().addAll(resolverMiembros(request.miembroIds()));
        return GrupoEstudioResponse.from(grupos.save(grupo));
    }

    @Transactional
    public GrupoEstudioResponse actualizar(Long id, GrupoEstudioRequest request) {
        GrupoEstudio grupo = entidad(id);
        aplicar(grupo, request);
        if (request.miembroIds() != null) {
            grupo.getMiembros().clear();
            grupo.getMiembros().addAll(resolverMiembros(request.miembroIds()));
        }
        return GrupoEstudioResponse.from(grupos.save(grupo));
    }

    @Transactional
    public GrupoEstudioResponse agregarMiembro(Long id, Long estudianteId) {
        GrupoEstudio grupo = entidad(id);
        grupo.getMiembros().add(estudiante(estudianteId));
        return GrupoEstudioResponse.from(grupos.save(grupo));
    }

    @Transactional
    public void quitarMiembro(Long id, Long estudianteId) {
        GrupoEstudio grupo = entidad(id);
        boolean removido = grupo.getMiembros().removeIf(miembro -> miembro.getId().equals(estudianteId));
        if (!removido) {
            throw new NoSuchElementException(
                    "El estudiante con id " + estudianteId + " no pertenece al grupo con id " + id);
        }
    }

    @Transactional
    public void eliminar(Long id) {
        grupos.delete(entidad(id));
    }

    private void aplicar(GrupoEstudio grupo, GrupoEstudioRequest request) {
        if (request.nombre() == null || request.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del grupo de estudio es obligatorio");
        }
        if (request.tema() == null || request.tema().isBlank()) {
            throw new IllegalArgumentException("El tema del grupo de estudio es obligatorio");
        }

        grupo.setNombre(request.nombre().trim());
        grupo.setDescripcion(request.descripcion());
        grupo.setTema(request.tema().trim());
    }

    private Set<Estudiante> resolverMiembros(Set<Long> miembroIds) {
        Set<Estudiante> miembros = new HashSet<>();
        if (miembroIds == null) {
            return miembros;
        }
        for (Long estudianteId : miembroIds) {
            miembros.add(estudiante(estudianteId));
        }
        return miembros;
    }

    private Estudiante estudiante(Long id) {
        return estudiantes.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el estudiante con id " + id));
    }

    private GrupoEstudio entidad(Long id) {
        return grupos.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el grupo de estudio con id " + id));
    }
}
