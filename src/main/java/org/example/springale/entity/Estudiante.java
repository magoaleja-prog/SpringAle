package org.example.springale.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Perfil academico del estudiante. Se relaciona uno a uno con un Usuario
 * con rol ESTUDIANTE y conserva la informacion propia de su vida escolar.
 */
@Entity
@Table(name = "estudiantes")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 30)
    private String documento;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String apellido;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false)
    private Integer semestre;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semillero_id")
    private Semillero semillero;

    @ManyToMany(mappedBy = "estudiantes", fetch = FetchType.LAZY)
    private Set<Materia> materias = new HashSet<>();

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Calificacion> calificaciones = new ArrayList<>();

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Publicacion> publicaciones = new ArrayList<>();

    @OneToMany(mappedBy = "estudianteTutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tutoria> tutoriasComoTutor = new ArrayList<>();

    @OneToMany(mappedBy = "estudianteAprendiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tutoria> tutoriasComoAprendiz = new ArrayList<>();

    public Estudiante() {
    }

    public Estudiante(String documento, String nombre, String apellido, String email,
                      Integer semestre, Usuario usuario, Semillero semillero) {
        this.documento = documento;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.semestre = semestre;
        this.usuario = usuario;
        this.semillero = semillero;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Semillero getSemillero() {
        return semillero;
    }

    public void setSemillero(Semillero semillero) {
        this.semillero = semillero;
    }

    public Set<Materia> getMaterias() {
        return materias;
    }

    public void setMaterias(Set<Materia> materias) {
        this.materias = materias;
    }

    public List<Calificacion> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(List<Calificacion> calificaciones) {
        this.calificaciones = calificaciones;
    }

    public List<Publicacion> getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(List<Publicacion> publicaciones) {
        this.publicaciones = publicaciones;
    }

    public List<Tutoria> getTutoriasComoTutor() {
        return tutoriasComoTutor;
    }

    public void setTutoriasComoTutor(List<Tutoria> tutoriasComoTutor) {
        this.tutoriasComoTutor = tutoriasComoTutor;
    }

    public List<Tutoria> getTutoriasComoAprendiz() {
        return tutoriasComoAprendiz;
    }

    public void setTutoriasComoAprendiz(List<Tutoria> tutoriasComoAprendiz) {
        this.tutoriasComoAprendiz = tutoriasComoAprendiz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Estudiante estudiante)) return false;
        return id != null && id.equals(estudiante.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
