package org.tecno.gestor.biblioteca.dao;

import java.util.List;

import org.tecno.gestor.biblioteca.model.Categoria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class DAOCategoria {

    private EntityManager em;

    public DAOCategoria(EntityManager em) {
        this.em = em;
    }

    public void Crear(Categoria categoria) {

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(categoria);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear la categoria", e);
        }

    }

    public void update(Categoria categoria) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.merge(categoria);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar la categoria ", e);
        }
    }

    public void Eliminar(String nombre) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            Categoria categoria = EncontrarPorNombre(nombre);
            if (categoria != null) {
                em.remove(categoria);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar la categoria ", e);
        }
    }

    public Categoria EncontrarPorNombre(String categoria) {
        try{
        TypedQuery<Categoria> query = em.createQuery("SELECT c FROM Categoria c WHERE c.categoria = :categoria", Categoria.class);
        query.setParameter("categoria", categoria);
        return query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public List<Categoria> EncontrarTodasEntitidades() {
        return em.createQuery("SELECT c FROM Categoria c", Categoria.class).getResultList();
    }

}
