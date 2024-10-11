package com.tecno.biblioteca.dao;

import com.tecno.biblioteca.enums.EstadoPrestamo;
import com.tecno.biblioteca.model.Categoria;
import com.tecno.biblioteca.model.Cuenta;
import java.util.List;

import com.tecno.biblioteca.model.Prestamo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

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

    public Prestamo EncontrarPrestamoActivoOMoraPorUsuario(Cuenta idUsuario) {
        try {
            TypedQuery<Prestamo> query = em.createQuery("SELECT p FROM Prestamo p WHERE p.id_cuenta = :id_cuenta AND (p.estado_prestamo = 'ACTIVO' OR p.estado_prestamo = 'MORA')", Prestamo.class);

            query.setParameter("id_cuenta", idUsuario);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // No hay préstamos activos o en mora
        }
    }
    
    public List<Prestamo> EncontrarPrestamosActivos(){
        try{
          TypedQuery<Prestamo> query = em.createQuery("SELECT p FROM Prestamo p WHERE p.estado_prestamo = 'ACTIVO'", Prestamo.class);
          return query.getResultList();
        }catch(PersistenceException e){
            System.out.println("algo paso");
            return null;
        }
    }
}
