package org.example.springale.service;

import org.example.springale.dto.PublicacionRequest;
import org.example.springale.dto.PublicacionResponse;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.GrupoEstudio;
import org.example.springale.entity.Publicacion;
import org.example.springale.entity.TipoPublicacion;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.GrupoEstudioRepository;
import org.example.springale.repository.PublicacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class PublicacionService {

    private final PublicacionRepository publicaciones;
    private final GrupoEstudioRepository grupos;
    private final EstudianteRepository estudiantes;

    public PublicacionService(PublicacionRepository publicaciones,
                              GrupoEstudioRepository grupos,
                              EstudianteRepository estudiantes) {
        this.publicaciones = publicaciones;
        this.grupos = grupos;
        this.estudiantes = estudiantes;
    }

    public List<PublicacionResponse> listar(Long grupoId, TipoPublicacion tipo) {
        List<Publicacion> resultado;
        if (grupoId != null && tipo != null) {
            resultado = publicaciones.findByGrupoIdAndTipoOrderByFechaDesc(grupoId, tipo);
        } else if (grupoId != null) {
            resultado = publicaciones.findByGrupoIdOrderByFechaDesc(grupoId);
        } else if (tipo != null) {
            resultado = publicaciones.findByTipoOrderByFechaDesc(tipo);
        } else {
            resultado = publicaciones.findAllByOrderByFechaDesc();
        }
        return resultado.stream().map(PublicacionResponse::from).toList();
    }

    public PublicacionResponse buscar(Long id) {
        return PublicacionResponse.from(entidad(id));
    }

    public List<PublicacionResponse> porGrupo(Long grupoId) {
        return publicaciones.findByGrupoIdOrderByFechaDesc(grupoId).stream()
                .map(PublicacionResponse::from)
                .toList();
    }

    public List<PublicacionResponse> porAutor(Long autorId) {
        return publicaciones.findByAutorIdOrderByFechaDesc(autorId).stream()
                .map(PublicacionResponse::from)
                .toList();
    }

    public List<PublicacionResponse> buscarPorTexto(String texto) {
        return publicaciones.findByTituloContainingIgnoreCaseOrContenidoContainingIgnoreCase(texto, texto).stream()
                .map(PublicacionResponse::from)
                .toList();
    }

    @Transactional
    public PublicacionResponse crear(PublicacionRequest request) {
        Publicacion publicacion = new Publicacion();
        publicacion.setFecha(LocalDateTime.now());
        aplicar(publicacion, request);
        return PublicacionResponse.from(publicaciones.save(publicacion));
    }

    @Transactional
    public PublicacionResponse actualizar(Long id, PublicacionRequest request) {
        Publicacion publicacion = entidad(id);
        aplicar(publicacion, request);
        return PublicacionResponse.from(publicaciones.save(publicacion));
    }

    @Transactional
    public void eliminar(Long id) {
        publicaciones.delete(entidad(id));
    }

    private void aplicar(Publicacion publicacion, PublicacionRequest request) {
        if (request.titulo() == null || request.titulo().isBlank()) {
            throw new IllegalArgumentException("El titulo de la publicacion es obligatorio");
        }
        if (request.contenido() == null || request.contenido().isBlank()) {
            throw new IllegalArgumentException("El contenido de la publicacion es obligatorio");
        }
        if (request.autorId() == null) {
            throw new IllegalArgumentException("El autor de la publicacion es obligatorio");
        }
        if (request.grupoId() == null) {
            throw new IllegalArgumentException("El grupo de la publicacion es obligatorio");
        }

        Estudiante autor = estudiantes.findById(request.autorId())
                .orElseThrow(() -> new NoSuchElementException(
                        "No existe el estudiante con id " + request.autorId()));
        GrupoEstudio grupo = grupos.findById(request.grupoId())
                .orElseThrow(() -> new NoSuchElementException(
                        "No existe el grupo de estudio con id " + request.grupoId()));

        if (!grupo.getMiembros().contains(autor)) {
            throw new IllegalArgumentException("El autor debe pertenecer al grupo de estudio");
        }

        publicacion.setTitulo(request.titulo().trim());
        publicacion.setContenido(request.contenido().trim());
        publicacion.setTipo(request.tipo() == null ? TipoPublicacion.ANUNCIO : request.tipo());
        publicacion.setAutor(autor);
        publicacion.setGrupo(grupo);
    }

    private Publicacion entidad(Long id) {
        return publicaciones.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe la publicacion con id " + id));
    }
}
