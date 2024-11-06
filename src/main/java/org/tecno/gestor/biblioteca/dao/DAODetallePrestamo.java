package org.tecno.gestor.biblioteca.dao;

import java.util.List;

import org.tecno.gestor.biblioteca.model.DetallePrestamo;
import org.tecno.gestor.biblioteca.model.Prestamo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

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

    public List<DetallePrestamo> EncontrarPorNombre(Prestamo prestamo) {

        TypedQuery<DetallePrestamo> query = em.createQuery("SELECT dp FROM DetallePrestamo dp WHERE dp.id_prestamo = :id_prestamo", DetallePrestamo.class);
        query.setParameter("id_prestamo", prestamo);
        return query.getResultList();
    }
}
