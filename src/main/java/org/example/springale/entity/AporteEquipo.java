package org.example.springale.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Entregable individual de un integrante dentro de un proyecto grupal. */
@Entity
@Table(name = "aportes_equipo")
public class AporteEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAporte tipo;

    @Column(nullable = false, length = 2000)
    private String contenido;

    @Column(length = 500)
    private String comentario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private ProyectoEquipo proyecto;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    public AporteEquipo() {
    }

    public AporteEquipo(TipoAporte tipo, String contenido, String comentario,
                        ProyectoEquipo proyecto, Estudiante estudiante) {
        this.tipo = tipo;
        this.contenido = contenido;
        this.comentario = comentario;
        this.proyecto = proyecto;
        this.estudiante = estudiante;
    }

    @PrePersist
    void asignarFecha() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoAporte getTipo() {
        return tipo;
    }

    public void setTipo(TipoAporte tipo) {
        this.tipo = tipo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public ProyectoEquipo getProyecto() {
        return proyecto;
    }

    public void setProyecto(ProyectoEquipo proyecto) {
        this.proyecto = proyecto;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }
}
