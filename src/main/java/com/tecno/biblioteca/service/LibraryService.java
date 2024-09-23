package com.tecno.biblioteca.service;

import com.tecno.biblioteca.dao.*;
import com.tecno.biblioteca.model.*;
import java.util.List;
import jakarta.persistence.EntityManager;
import static com.tecno.biblioteca.config.HibernateUtil.*;
import com.tecno.biblioteca.enums.EstadoCuenta;
import com.tecno.biblioteca.enums.EstadoLibro;
import com.tecno.biblioteca.enums.TipoCuenta;

/**
 * @author juan
 */
public class LibraryService {

    EntityManager em = getEntityManager();

    DAOCuenta d_cuenta = new DAOCuenta(em);
    DAOCategoria d_categoria = new DAOCategoria(em);
    DAOLibro d_libro = new DAOLibro(em);

    DAOPrestamo d_prestamo = new DAOPrestamo(em);
    DAODevolucion d_devolucion = new DAODevolucion(em);

    DAODetallePrestamo d_detalle_p = new DAODetallePrestamo(em);
    DAODetalleDevolucion d_detalle_d = new DAODetalleDevolucion(em);

    public void Crear_Cuenta(Cuenta cuenta) {
        d_cuenta.Crear(cuenta);
    }

    public void Actualizar_Cuenta(Cuenta cuenta) {
        d_cuenta.Actualizar(cuenta);
    }

    public void ELiminar_Cuenta(Cuenta cuenta) {
        cuenta.setEstado_cuenta(EstadoCuenta.ELIMINADO);
        d_cuenta.Actualizar(cuenta);
    }

    public Cuenta Encontrar_Cuenta(long id) {
        return d_cuenta.BuscarEntidad(id);
    }

    public List<Cuenta> Encontrar_Cuentas() {
        return d_cuenta.BuscarTodasLasEntidades();
    }

    public void Crear_Categoria(Categoria categoria) {
        d_categoria.Crear(categoria);
    }

    public void Actualizar_Categoria(Categoria categoria) {
        d_categoria.update(categoria);
    }

    public void Eliminar_Categoria(String nombre) {
        d_categoria.Eliminar(nombre);
    }

    public Categoria Encontrar_Categoria_Nombre(String nombre) {
        Categoria categoria = d_categoria.EncontrarPorNombre(nombre);
        return categoria;
    }

    public List<Libro> Encontrar_Libros() {
        return d_libro.findAllEntities();
    }

    public void Actualizar_Libro(Libro libro) {
        d_libro.update(libro);
    }

    public void ELiminar_Libro(Libro libro) {
        libro.setEstado_libro(EstadoLibro.DESCATALOGADO);
        d_libro.update(libro);
    }

    public void Crear_Libro(Libro libro) {
        d_libro.create(libro);
    }

    public List<Categoria> Encontrar_Categorias() {
        return d_categoria.EncontrarTodasEntitidades();
    }
    
    public List<Cuenta> Encontrar_Cuenta_por_Tipo(TipoCuenta tipo_cuenta){
    
        return d_cuenta.EncontrarPorTipoCuenta(tipo_cuenta);
    }
    
    public List<Libro> Encontrar_Libro_por_Disponibilidad(){
       return d_libro.EncontrarPorCantidadDisponible();
    }
    
    public void Crear_Prestamo(Prestamo prestamo){
        d_prestamo.Crear(prestamo);
    }
    
    public Prestamo Encontrar_prestamo(Prestamo prestamo){
        
        return d_prestamo.findEntityById(prestamo.getId_prestamo());
    }
    
    public Prestamo Encontrar_prestamo_por_Usuario(long id){
        
        return d_prestamo.EncontrarPrestamoActivoOMoraPorUsuario(d_cuenta.BuscarEntidad(id));
    }
    
    public void Crear_Devolucion(Devolucion devolucion){
        d_devolucion.create(devolucion);
    }
    
    public boolean exite_cuenta(long id){
       return d_cuenta.ExisteID(id);
    }
}

