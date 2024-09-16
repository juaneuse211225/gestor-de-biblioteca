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

    @OneToMany(mappedBy = "id_prestamo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePrestamo> id_detalleprestamo;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_cuenta", unique = true, referencedColumnName = "id")
    private Cuenta id_cuenta;

    @Basic
    @Column(nullable = false)
    private LocalDate fecha_inicio_prestamo;
    @Column(nullable = false)
    private LocalDate fecha_relativa_devolucion;

    @Enumerated(EnumType.STRING)
    private EstadoPrestamo estado_prestamo;

    @OneToOne(mappedBy = "prestamo", optional = true)
    private Devolucion id_devolucion;

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
        this.id_devolucion = null;

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

    public Devolucion getId_devolucion() {
        return id_devolucion;
    }

    public void setId_devolucion(Devolucion id_devolucion) {
        this.id_devolucion = id_devolucion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "Prestamo{" + "id_prestamo=" + id_prestamo + ", fecha_inicio_prestamo=" + fecha_inicio_prestamo + ", fecha_relativa_devolucion=" + fecha_relativa_devolucion + ", estado_prestamo=" + estado_prestamo + ", observaciones=" + observaciones + '}';
    }
    
}
