package com.tecno.biblioteca.dao;

import java.util.List;

import com.tecno.biblioteca.model.Prestamo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class DAOPrestamo {

    private EntityManager em;

    public DAOPrestamo(EntityManager em) {
        this.em = em;
    }

    public void Crear(Prestamo prestamo) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(prestamo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear la transaccion", e);
        }

    }

    public void Actualizar(Prestamo prestamo) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.merge(prestamo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar el Transaccion", e);
        }
    }

    public void Eliminar(long id) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            Prestamo prestamo = findEntityById(id);
            if (prestamo != null) {
                em.remove(prestamo);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar La transaccion", e);
        }
    }

    public Prestamo findEntityById(long id) {
        return em.find(Prestamo.class, id);
    }

    public List<Prestamo> findAllEntities() {
        return em.createQuery("SELECT l FROM Prestamo l", Prestamo.class).getResultList();
    }

}
