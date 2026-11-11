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
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "publicaciones")
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String contenido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPublicacion tipo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Estudiante autor;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private GrupoEstudio grupo;

    public Publicacion() {
    }

    public Publicacion(String titulo, String contenido, TipoPublicacion tipo, Estudiante autor, GrupoEstudio grupo) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.tipo = tipo;
        this.autor = autor;
        this.grupo = grupo;
        this.fecha = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public TipoPublicacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoPublicacion tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Estudiante getAutor() {
        return autor;
    }

    public void setAutor(Estudiante autor) {
        this.autor = autor;
    }

    public GrupoEstudio getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoEstudio grupo) {
        this.grupo = grupo;
    }
}
