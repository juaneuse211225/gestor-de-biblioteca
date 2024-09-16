package com.tecno.biblioteca.dao;

import com.tecno.biblioteca.enums.EstadoLibro;
import com.tecno.biblioteca.enums.TipoCuenta;
import com.tecno.biblioteca.model.Cuenta;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;

import java.util.List;

import com.tecno.biblioteca.model.Libro;
import jakarta.persistence.TypedQuery;

public class DAOLibro {

    @PersistenceContext
    private EntityManager em;

    public DAOLibro() {

    }

    public DAOLibro(EntityManager em) {
        this.em = em;

    }

    public void create(Libro lib) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            System.out.println(em.isOpen());
            System.out.println(em.contains(lib.getCategoria()));
            System.out.println(em.contains(lib));
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
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar el libro", e);
        }
    }

    public Libro findEntityById(long id) {
        return em.find(Libro.class, id);
    }

    public List<Libro> findAllEntities() {
        return em.createQuery("SELECT l FROM Libro l", Libro.class).getResultList();
    }

    public void delete(long id) {
        Libro lib = findEntityById(id);
        lib.setEstado_libro(EstadoLibro.DESCATALOGADO);
        update(lib);
    }
    
    public List<Libro> EncontrarPorCantidadDisponible() {
        TypedQuery<Libro> query = em.createQuery("SELECT l FROM Libro l WHERE l.libros_disponibles > 0", Libro.class);
        return query.getResultList();
    }
}
