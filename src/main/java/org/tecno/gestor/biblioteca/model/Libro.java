package org.tecno.gestor.biblioteca.model;

import org.tecno.gestor.biblioteca.enums.EstadoLibro;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Libro {

    @Id
    @Column(name = "isbn", nullable = false)
    private long isbn;

    @Basic
    private String titulo;
    private String autor;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria", nullable = false, referencedColumnName = "id_categoria")
    private Categoria categoria;

    @OneToMany(mappedBy = "id_libro", cascade = CascadeType.ALL, orphanRemoval = true)
    List<DetallePrestamo> detalle = new ArrayList<DetallePrestamo>();

    @OneToMany(mappedBy = "id_libro", cascade = CascadeType.ALL, orphanRemoval = true)
    List<DetalleDevolucion> detalle_devolucion = new ArrayList<DetalleDevolucion>();

    @Basic
    private int libros_total;
    private int libros_disponibles;
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    private EstadoLibro estado_libro;

    public Libro() {
    }

    public Libro(long isbn, String titulo, String autor, Categoria categoria, int libros_total, String ubicacion) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.libros_total = libros_total;
        this.ubicacion = ubicacion;
        this.libros_disponibles = libros_total;
        this.estado_libro = EstadoLibro.ACTIVO;
    }

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<DetallePrestamo> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<DetallePrestamo> detalle) {
        this.detalle = detalle;
    }

    public List<DetalleDevolucion> getDetalle_devolucion() {
        return detalle_devolucion;
    }

    public void setDetalle_devolucion(List<DetalleDevolucion> detalle_devolucion) {
        this.detalle_devolucion = detalle_devolucion;
    }

    public int getLibros_total() {
        return libros_total;
    }

    public void setLibros_total(int libros_total) {
        this.libros_total = libros_total;
    }

    public int getLibros_disponibles() {
        return libros_disponibles;
    }

    public void setLibros_disponibles(int libros_disponibles) {
        this.libros_disponibles = libros_disponibles;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public EstadoLibro getEstado_libro() {
        return estado_libro;
    }

    public void setEstado_libro(EstadoLibro estado_libro) {
        this.estado_libro = estado_libro;
    }
}
