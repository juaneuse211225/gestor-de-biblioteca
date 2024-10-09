package com.tecno.biblioteca.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import com.tecno.biblioteca.enums.EstadoPrestamo;

@Entity(name = "Prestamo")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_prestamo;

    @OneToMany(mappedBy = "id_prestamo", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    private List<DetallePrestamo> id_detalleprestamo;

    @ManyToOne
    @JoinColumn(name = "id_cuenta", nullable = false, referencedColumnName = "id")
    private Cuenta id_cuenta;

    @Basic
    @Column(nullable = false)
    private LocalDate fecha_inicio_prestamo;
    @Column(nullable = false)
    private LocalDate fecha_relativa_devolucion;

    @Enumerated(EnumType.STRING)
    private EstadoPrestamo estado_prestamo;

    @OneToMany(mappedBy = "prestamo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Devolucion> devolucion;

    @Basic
    private String observaciones;
    
    

    public Prestamo() {
    }

    public Prestamo(List<DetallePrestamo> id_detalleprestamo, Cuenta id_cuenta, LocalDate fecha_inicio_prestamo, LocalDate fecha_relativa_devolucion, String observaciones) {
        this.id_detalleprestamo = id_detalleprestamo;
        this.id_cuenta = id_cuenta;
        this.fecha_inicio_prestamo = fecha_inicio_prestamo;
        this.fecha_relativa_devolucion = fecha_relativa_devolucion;
        this.observaciones = observaciones;
        this.estado_prestamo = EstadoPrestamo.ACTIVO;
        this.devolucion = null;

    }

    public long getId_prestamo() {
        return id_prestamo;
    }

    public void setId_prestamo(long id_prestamo) {
        this.id_prestamo = id_prestamo;
    }

    public List<DetallePrestamo> getId_detalleprestamo() {
        return id_detalleprestamo;
    }

    public void setId_detalleprestamo(List<DetallePrestamo> id_detalleprestamo) {
        this.id_detalleprestamo = id_detalleprestamo;
    }

    public Cuenta getId_cuenta() {
        return id_cuenta;
    }

    public void setId_cuenta(Cuenta id_cuenta) {
        this.id_cuenta = id_cuenta;
    }

    public LocalDate getFecha_inicio_prestamo() {
        return fecha_inicio_prestamo;
    }

    public void setFecha_inicio_prestamo(LocalDate fecha_inicio_prestamo) {
        this.fecha_inicio_prestamo = fecha_inicio_prestamo;
    }

    public LocalDate getFecha_relativa_devolucion() {
        return fecha_relativa_devolucion;
    }

    public void setFecha_relativa_devolucion(LocalDate fecha_relativa_devolucion) {
        this.fecha_relativa_devolucion = fecha_relativa_devolucion;
    }

    public EstadoPrestamo getEstado_prestamo() {
        return estado_prestamo;
    }

    public void setEstado_prestamo(EstadoPrestamo estado_prestamo) {
        this.estado_prestamo = estado_prestamo;
    }

    public List<Devolucion> getDevolucion() {
        return devolucion;
    }

    public void setDevolucion(List<Devolucion> devolucion) {
        this.devolucion = devolucion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return ", fecha de inicio: " + fecha_inicio_prestamo + "- estado del prestamo: " + estado_prestamo + "";
    }
    
}
