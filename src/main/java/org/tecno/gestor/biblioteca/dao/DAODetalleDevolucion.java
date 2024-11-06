package org.tecno.gestor.biblioteca.dao;

import java.util.List;

import org.tecno.gestor.biblioteca.model.DetalleDevolucion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class DAODetalleDevolucion {

    private EntityManager em;

    public DAODetalleDevolucion() {

    }

    public DAODetalleDevolucion(EntityManager em) {
        this.em = em;

    }

    public void create(DetalleDevolucion detalledevolucion) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(detalledevolucion);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear detalledevolucion", e);
        }
    }

    public void update(DetalleDevolucion detalledevolucion) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.merge(detalledevolucion);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar detalledevolucion", e);
        }
    }

    public DetalleDevolucion findEntityById(long id) {
        return em.find(DetalleDevolucion.class, id);
    }

    public List<DetalleDevolucion> findAllEntities() {
        return em.createQuery("SELECT l FROM DetalleDevolucion l", DetalleDevolucion.class).getResultList();
    }

    public void delete(long id) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            DetalleDevolucion detalledevolucion = findEntityById(id);
            if (detalledevolucion != null) {
                em.remove(detalledevolucion);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar detalledevolucion", e);
        }
    }
}
