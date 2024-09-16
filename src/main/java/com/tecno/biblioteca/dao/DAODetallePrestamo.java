package com.tecno.biblioteca.dao;

import java.util.List;

import com.tecno.biblioteca.model.DetallePrestamo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class DAODetallePrestamo {

    private EntityManager em;

    public DAODetallePrestamo() {

    }

    public DAODetallePrestamo(EntityManager em) {
        this.em = em;
    }

    public void create(DetallePrestamo detalleprestamo) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(detalleprestamo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear la transaccion", e);
        }

    }

    public void update(DetallePrestamo detalleprestamo) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.merge(detalleprestamo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar el Transaccion", e);
        }
    }

    public DetallePrestamo findEntityById(long id) {
        return em.find(DetallePrestamo.class, id);
    }

    public List<DetallePrestamo> findAllEntities() {
        return em.createQuery("SELECT l FROM DetallePrestamo l", DetallePrestamo.class).getResultList();
    }

    public void delete(long id) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            DetallePrestamo detalleprestamo = findEntityById(id);
            if (detalleprestamo != null) {
                em.remove(detalleprestamo);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar La transaccion", e);
        }
    }
}
