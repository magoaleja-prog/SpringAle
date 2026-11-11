package org.example.springale.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Solicitud/historial de una tutoria entre estudiantes. Al ser aceptada
 * se habilita el chat interno representado por MensajeTutoria.
 */
@Entity
@Table(name = "tutorias")
public class Tutoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String tema;

    @Column(length = 600)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTutoria estado;

    @Column(nullable = false)
    private LocalDate fechaSolicitud;

    private LocalDate fechaTutoria;

    /** Promedio del aprendiz en la materia antes de la tutoria. */
    private Double notaAntes;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Estudiante estudianteTutor;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "aprendiz_id", nullable = false)
    private Estudiante estudianteAprendiz;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @OneToMany(mappedBy = "tutoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MensajeTutoria> mensajes = new ArrayList<>();

    public Tutoria() {
    }

    public Tutoria(String tema, String descripcion, LocalDate fechaTutoria,
                   Estudiante estudianteTutor, Estudiante estudianteAprendiz, Materia materia) {
        this.tema = tema;
        this.descripcion = descripcion;
        this.fechaTutoria = fechaTutoria;
        this.estudianteTutor = estudianteTutor;
        this.estudianteAprendiz = estudianteAprendiz;
        this.materia = materia;
        this.estado = EstadoTutoria.PENDIENTE;
        this.fechaSolicitud = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoTutoria getEstado() {
        return estado;
    }

    public void setEstado(EstadoTutoria estado) {
        this.estado = estado;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFechaTutoria() {
        return fechaTutoria;
    }

    public void setFechaTutoria(LocalDate fechaTutoria) {
        this.fechaTutoria = fechaTutoria;
    }

    public Double getNotaAntes() {
        return notaAntes;
    }

    public void setNotaAntes(Double notaAntes) {
        this.notaAntes = notaAntes;
    }

    public Estudiante getEstudianteTutor() {
        return estudianteTutor;
    }

    public void setEstudianteTutor(Estudiante estudianteTutor) {
        this.estudianteTutor = estudianteTutor;
    }

    public Estudiante getEstudianteAprendiz() {
        return estudianteAprendiz;
    }

    public void setEstudianteAprendiz(Estudiante estudianteAprendiz) {
        this.estudianteAprendiz = estudianteAprendiz;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public List<MensajeTutoria> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<MensajeTutoria> mensajes) {
        this.mensajes = mensajes;
    }
}
