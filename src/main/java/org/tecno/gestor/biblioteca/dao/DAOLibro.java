package org.tecno.gestor.biblioteca.dao;

import org.tecno.gestor.biblioteca.enums.EstadoLibro;
import org.tecno.gestor.biblioteca.enums.TipoCuenta;
import org.tecno.gestor.biblioteca.model.Cuenta;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

import org.tecno.gestor.biblioteca.model.Libro;
import jakarta.persistence.TypedQuery;

public class DAOLibro {

    @PersistenceContext
    private EntityManager em;

    public DAOLibro() {

    }

    public DAOLibro(EntityManager em) {
        this.em = em;

    }
    
    public boolean ExisteID(long id) {
        Libro libro = em.find(Libro.class, id);
            if(libro != null){
                return true;
            }else{
                return false;
            }
    }

    public void create(Libro lib) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(lib);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear el libro", e);
        }
    }

    public void update(Libro lib) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.merge(lib);
            em.merge(lib);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar el libro", e);
        }
    }

    public Libro findEntityById(long id) {
        Libro libro = em.find(Libro.class, id);
        em.refresh(libro);
        return libro;
    }

    public List<Libro> findAllEntities() {
        List<Libro> libros = em.createQuery("SELECT l FROM Libro l", Libro.class).getResultList();

        // Refrescar cada libro para obtener los datos actualizados desde la base de datos
        for (Libro libro : libros) {
            em.refresh(libro);
        }

        return libros;
    }

    public void delete(long id) {
        Libro lib = findEntityById(id);
        lib.setEstado_libro(EstadoLibro.DESCATALOGADO);
        update(lib);
    }

    public List<Libro> EncontrarPorCantidadDisponible() {
        TypedQuery<Libro> query = em.createQuery("SELECT l FROM Libro l WHERE l.libros_disponibles > 0", Libro.class);
        List<Libro> librosDisponibles = query.getResultList();

        // Refrescar cada libro para asegurarse de que los datos sean los más recientes
        for (Libro libro : librosDisponibles) {
            em.refresh(libro);
        }

        return librosDisponibles;
    }

    public void RefrescarLibros(Libro libro) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.refresh(libro);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al refrescar", e);
        }
    }
    public List<Libro> EncontrarActivos() {
        TypedQuery<Libro> query = em.createQuery("SELECT l FROM Libro l WHERE l.estado_libro = :estado", Libro.class);
        query.setParameter("estado", EstadoLibro.ACTIVO);
        List<Libro> listas = query.getResultList();
        for (Libro libro : listas) {
            em.refresh(libro);

        }

        return listas;
    }
}
