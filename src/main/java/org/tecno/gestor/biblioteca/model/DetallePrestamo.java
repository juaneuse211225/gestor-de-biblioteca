package org.tecno.gestor.biblioteca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity(name = "Detalleprestamo")
public class DetallePrestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "id_prestamo", nullable = false)
    private Prestamo id_prestamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro", nullable = false)
    private Libro id_libro;

    @Column(nullable = false)
    private int ejemplares_prestados;
    
    @Transient
    private int ejemplares_devolver;

    public DetallePrestamo() {
    }

    public DetallePrestamo(Prestamo id_prestamo, Libro id_libro, int ejemplares_prestados) {
        this.id_prestamo = id_prestamo;
        this.id_libro = id_libro;
        this.ejemplares_prestados = ejemplares_prestados;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Prestamo getId_prestamo() {
        return id_prestamo;
    }

    public void setId_prestamo(Prestamo id_prestamo) {
        this.id_prestamo = id_prestamo;
    }

    public Libro getId_libro() {
        return id_libro;
    }

    public void setId_libro(Libro id_libro) {
        this.id_libro = id_libro;
    }

    public int getEjemplares_prestados() {
        return ejemplares_prestados;
    }

    public void setEjemplares_prestados(int ejemplares_prestados) {
        this.ejemplares_prestados = ejemplares_prestados;
    }

    public void setEjemplares_devolver(int ejemplares_devolver) {
        this.ejemplares_devolver = ejemplares_devolver;
    }

    public int getEjemplares_devolver() {
        return ejemplares_devolver;
    }
}
