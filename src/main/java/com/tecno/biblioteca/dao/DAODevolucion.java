package com.tecno.biblioteca.dao;

import java.util.List;

import com.tecno.biblioteca.model.Devolucion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class DAODevolucion {

    private EntityManager em;

    public DAODevolucion() {

    }

    public DAODevolucion(EntityManager em) {
        this.em = em;

    }

    public void create(Devolucion devolucion) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(devolucion);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear la devolucion", e);
        }
    }

    public void update(Devolucion devolucion) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.merge(devolucion);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar la devolucion", e);
        }
    }

    public Devolucion findEntityById(long id) {
        return em.find(Devolucion.class, id);
    }

    public List<Devolucion> findAllEntities() {
        return em.createQuery("SELECT d FROM Devolucion d", Devolucion.class).getResultList();
    }

    public void delete(int id) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            Devolucion devolucion = findEntityById(id);
            if (devolucion != null) {
                em.remove(devolucion);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar la devolucion ", e);
        }
    }
}
