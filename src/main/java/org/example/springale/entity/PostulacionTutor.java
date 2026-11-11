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
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

/**
 * Postulacion de un estudiante para ofrecer tutorias de una materia.
 * Solo las postulaciones aprobadas permiten recibir solicitudes.
 */
@Entity
@Table(name = "postulaciones_tutor",
        uniqueConstraints = @UniqueConstraint(columnNames = {"estudiante_id", "materia_id"}))
public class PostulacionTutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String disponibilidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPostulacion estado = EstadoPostulacion.PENDIENTE;

    @Column(nullable = false, updatable = false)
    private LocalDate fechaPostulacion;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    public PostulacionTutor() {
    }

    public PostulacionTutor(String disponibilidad, Estudiante estudiante, Materia materia) {
        this.disponibilidad = disponibilidad;
        this.estudiante = estudiante;
        this.materia = materia;
    }

    @PrePersist
    void asignarFecha() {
        if (fechaPostulacion == null) {
            fechaPostulacion = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public EstadoPostulacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoPostulacion estado) {
        this.estado = estado;
    }

    public LocalDate getFechaPostulacion() {
        return fechaPostulacion;
    }

    public void setFechaPostulacion(LocalDate fechaPostulacion) {
        this.fechaPostulacion = fechaPostulacion;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }
}
