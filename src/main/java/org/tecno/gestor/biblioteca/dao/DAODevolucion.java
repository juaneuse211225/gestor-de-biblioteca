package org.tecno.gestor.biblioteca.dao;

import java.util.List;

import org.tecno.gestor.biblioteca.model.Devolucion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<Long, Integer> obtenerCantidadDevueltaPorLibro(Long prestamoId) {
      
    String jpql = "SELECT dd.id_libro.isbn, SUM(dd.ejemplares_devueltos) "
                + "FROM Devolucion d "
                + "JOIN d.detalle_devolucion dd "
                + "WHERE d.prestamo.id_prestamo = :prestamoId "
                + "GROUP BY dd.id_libro.isbn";

        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("prestamoId", prestamoId);

        List<Object[]> resultados = query.getResultList();

        // Convertir el resultado a un Map con ID del libro como clave y cantidad devuelta como valor
        return resultados.stream()
                .collect(Collectors.toMap(
                        result -> (Long) result[0], // ID del libro
                        result -> ((Number) result[1]).intValue() // Cantidad devuelta
                ));
    }

}
