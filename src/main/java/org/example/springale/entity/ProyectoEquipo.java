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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Proyecto de trabajo en equipo. El profesor define el lider, los
 * integrantes y la fecha limite. Cada miembro sube su aporte individual.
 */
@Entity
@Table(name = "proyectos_equipo")
public class ProyectoEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 1500)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaLimite;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProyecto estado = EstadoProyecto.PENDIENTE;

    private Double calificacion;

    @Column(length = 1000)
    private String comentarioProfesor;

    private LocalDateTime fechaEntregaFinal;

    @Column(length = 2000)
    private String entregaFinalContenido;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "profesor_id", nullable = false)
    private Usuario profesor;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "lider_id", nullable = false)
    private Estudiante lider;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "proyecto_miembros",
            joinColumns = @JoinColumn(name = "proyecto_id"),
            inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    private Set<Estudiante> miembros = new HashSet<>();

    @OneToMany(mappedBy = "proyecto")
    private List<AporteEquipo> aportes = new ArrayList<>();

    public ProyectoEquipo() {
    }

    public ProyectoEquipo(String nombre, String descripcion, LocalDateTime fechaLimite,
                          Materia materia, Usuario profesor, Estudiante lider) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.materia = materia;
        this.profesor = profesor;
        this.lider = lider;
    }

    @PrePersist
    void asignarFechaCreacion() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public EstadoProyecto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProyecto estado) {
        this.estado = estado;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentarioProfesor() {
        return comentarioProfesor;
    }

    public void setComentarioProfesor(String comentarioProfesor) {
        this.comentarioProfesor = comentarioProfesor;
    }

    public LocalDateTime getFechaEntregaFinal() {
        return fechaEntregaFinal;
    }

    public void setFechaEntregaFinal(LocalDateTime fechaEntregaFinal) {
        this.fechaEntregaFinal = fechaEntregaFinal;
    }

    public String getEntregaFinalContenido() {
        return entregaFinalContenido;
    }

    public void setEntregaFinalContenido(String entregaFinalContenido) {
        this.entregaFinalContenido = entregaFinalContenido;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Usuario getProfesor() {
        return profesor;
    }

    public void setProfesor(Usuario profesor) {
        this.profesor = profesor;
    }

    public Estudiante getLider() {
        return lider;
    }

    public void setLider(Estudiante lider) {
        this.lider = lider;
    }

    public Set<Estudiante> getMiembros() {
        return miembros;
    }

    public void setMiembros(Set<Estudiante> miembros) {
        this.miembros = miembros;
    }

    public List<AporteEquipo> getAportes() {
        return aportes;
    }

    public void setAportes(List<AporteEquipo> aportes) {
        this.aportes = aportes;
    }
}
