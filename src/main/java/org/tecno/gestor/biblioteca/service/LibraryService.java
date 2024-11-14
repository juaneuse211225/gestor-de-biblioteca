package org.tecno.gestor.biblioteca.service;

import org.tecno.gestor.biblioteca.model.Cuenta;
import org.tecno.gestor.biblioteca.model.Categoria;
import org.tecno.gestor.biblioteca.model.Devolucion;
import org.tecno.gestor.biblioteca.model.Prestamo;
import org.tecno.gestor.biblioteca.model.Libro;
import org.tecno.gestor.biblioteca.dao.DAODevolucion;
import org.tecno.gestor.biblioteca.dao.DAOCategoria;
import org.tecno.gestor.biblioteca.dao.DAOPrestamo;
import org.tecno.gestor.biblioteca.dao.DAODetalleDevolucion;
import org.tecno.gestor.biblioteca.dao.DAOLibro;
import org.tecno.gestor.biblioteca.dao.DAOCuenta;
import org.tecno.gestor.biblioteca.dao.DAODetallePrestamo;
import java.util.List;
import jakarta.persistence.EntityManager;
import static org.tecno.gestor.biblioteca.config.HibernateUtil.*;
import org.tecno.gestor.biblioteca.enums.EstadoCuenta;
import org.tecno.gestor.biblioteca.enums.EstadoLibro;
import org.tecno.gestor.biblioteca.enums.TipoCuenta;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

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
    
    public List<Libro> Encontrar_Activos() {
        return d_libro.EncontrarActivos();
    }

    public void Actualizar_Libro(Libro libro) {
        d_libro.update(libro);
    }

    public void ELiminar_Libro(Libro libro) {
        libro.setEstado_libro(EstadoLibro.DESCATALOGADO);
        libro.setLibros_disponibles(0);
        libro.setLibros_total(0);
        d_libro.update(libro);
    }

    public void Crear_Libro(Libro libro) {
        d_libro.create(libro);
    }

    public List<Categoria> Encontrar_Categorias() {
        return d_categoria.EncontrarTodasEntitidades();
    }
    
    public List<Cuenta> Encontrar_Usuarios(){
    
        return d_cuenta.EncontrarUsuarios();
    }
    
    public List<Cuenta> Encontrar_Admin(){
    
        return d_cuenta.EncontrarCuentas();
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
    
    public boolean exite_libro(long id){
       return d_libro.ExisteID(id);
    }

    public void Actualizar_Prestamo(Prestamo prestamo) {
        d_prestamo.Actualizar(prestamo);
    }
    
    public Libro Encontrar_Libro(Long id){
        return d_libro.findEntityById(id);
    }
    
    public Map<Long, Integer> ObtenerCantidadDevueltaPorLibro(Long prestamoId){
        return d_devolucion.obtenerCantidadDevueltaPorLibro(prestamoId);
    }
    
    public List<Prestamo> EncontrarPrestamosActivos(){
        return d_prestamo.EncontrarPrestamosActivos();
    }
    public List<Prestamo> EncontrarPrestamosEnmora(){
        return d_prestamo.EncontrarPrestamoMora();
    }
    
    public Map<Month, List<Prestamo>> obtenerPrestamosEntreFechas(LocalDate fechainicio, LocalDate fechafinal){
        return d_prestamo.obtenerPrestamosEntreFechas(fechainicio, fechafinal);
    }
    
    public void refrescarLibro(Libro libro){
        d_libro.RefrescarLibros(libro);
    }
    public void refrescarCuenta(Cuenta cuenta){
        d_cuenta.RefrescarCuenta(cuenta);
    }
    
    public void refrescarPrestamos(Prestamo prestamo){
        d_prestamo.RefrescarPrestamo(prestamo);
    }
}

