package org.tecno.gestor.biblioteca.model;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "Devolucion")
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_devolucion;

    private LocalDate fecha_devolucion_real;

    @Basic
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "prestamo", nullable = false)
    private Prestamo prestamo;

    @OneToMany(mappedBy = "id_devolucion", cascade = CascadeType.ALL)
    private List<DetalleDevolucion> detalle_devolucion = new ArrayList<>();

    public Devolucion() {
    }

    public Devolucion(LocalDate fecha_devolucion_real, String observaciones, Prestamo prestamo) {
        this.fecha_devolucion_real = fecha_devolucion_real;
        this.observaciones = observaciones;
        this.prestamo = prestamo;
        this.detalle_devolucion = null;
    }

    public long getId_devolucion() {
        return id_devolucion;
    }

    public void setId_devolucion(long id_devolucion) {
        this.id_devolucion = id_devolucion;
    }

    public LocalDate getFecha_devolucion_real() {
        return fecha_devolucion_real;
    }

    public void setFecha_devolucion_real(LocalDate fecha_devolucion_real) {
        this.fecha_devolucion_real = fecha_devolucion_real;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public List<DetalleDevolucion> getDetalle_devolucion() {
        return detalle_devolucion;
    }

    public void setDetalle_devolucion(List<DetalleDevolucion> detalle_devolucion) {
        this.detalle_devolucion = detalle_devolucion;
    }

}
