package org.tecno.gestor.biblioteca.model;

import jakarta.persistence.*;
import java.util.ArrayList;

import org.tecno.gestor.biblioteca.enums.EstadoCuenta;
import org.tecno.gestor.biblioteca.enums.TipoCuenta;
import java.util.List;

@Entity
@Table(name = "Cuenta")
public class Cuenta {

    @Id
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = true)
    private String contraseña;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String correo;

    @Column(nullable = false)
    private String telefono;

    @OneToMany(mappedBy = "id_cuenta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Prestamo> id_prestamo = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta", nullable = false)
    private TipoCuenta tipo_cuenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cuenta", nullable = false)
    private EstadoCuenta estado_cuenta;

    public Cuenta() {
    }

    public Cuenta(long id, String nombre, String contraseña, String apellido, String correo, String telefono, TipoCuenta tipo_cuenta) {
        this.id = id;
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.tipo_cuenta = tipo_cuenta;
        this.estado_cuenta = EstadoCuenta.ACTIVO;
        this.id_prestamo = null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<Prestamo> getId_prestamo() {
        return id_prestamo;
    }

    public void setId_prestamo(List<Prestamo> id_prestamo) {
        this.id_prestamo = id_prestamo;
    }

    

    public TipoCuenta getTipo_cuenta() {
        return tipo_cuenta;
    }

    public void setTipo_cuenta(TipoCuenta tipo_cuenta) {
        this.tipo_cuenta = tipo_cuenta;
    }

    public EstadoCuenta getEstado_cuenta() {
        return estado_cuenta;
    }

    public void setEstado_cuenta(EstadoCuenta estado_cuenta) {
        this.estado_cuenta = estado_cuenta;
    }

    @Override
    public String toString() {
        return  '{'+ id + " - " + nombre + " " + apellido + '}';
    }
    
}
