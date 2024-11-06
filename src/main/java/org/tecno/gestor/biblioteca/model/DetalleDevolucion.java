package org.tecno.gestor.biblioteca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Detalledevolucion")
public class DetalleDevolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "id_devolucion", nullable = false)
    private Devolucion id_devolucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro", nullable = false)
    private Libro id_libro;

    @Basic
    private int ejemplares_devueltos;

    public DetalleDevolucion() {
    }

    public DetalleDevolucion(Devolucion id_devolucion, Libro id_libro, int ejemplares_devueltos) {
        this.id_devolucion = id_devolucion;
        this.id_libro = id_libro;
        this.ejemplares_devueltos = ejemplares_devueltos;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Devolucion getId_devolucion() {
        return id_devolucion;
    }

    public void setId_devolucion(Devolucion id_devolucion) {
        this.id_devolucion = id_devolucion;
    }

    public Libro getId_libro() {
        return id_libro;
    }

    public void setId_libro(Libro id_libro) {
        this.id_libro = id_libro;
    }

    public int getEjemplares_devueltos() {
        return ejemplares_devueltos;
    }

    public void setEjemplares_devueltos(int ejemplares_devueltos) {
        this.ejemplares_devueltos = ejemplares_devueltos;
    }
}
