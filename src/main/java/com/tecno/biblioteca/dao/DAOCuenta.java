package com.tecno.biblioteca.dao;

import com.tecno.biblioteca.enums.EstadoCuenta;
import com.tecno.biblioteca.enums.TipoCuenta;
import java.util.List;

import com.tecno.biblioteca.model.Cuenta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

public class DAOCuenta {

    private EntityManager em;

    public DAOCuenta(EntityManager em) {
        this.em = em;
    }

    /**
     * Crea una nueva instancia
     *
     * @param us nueva instancia
     */
    public void Crear(Cuenta us) {
        if (ExisteID(us.getId())) {
            EntityTransaction tx = null;
            try {
                tx = em.getTransaction();
                tx.begin();
                em.persist(us);
                tx.commit();
            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                throw new RuntimeException("Error al crear el Cuenta", e);
            }
        } else {
            System.out.println("La cuenta ya existe " + us.getId());
        }
    }

    /**
     * Actualiza los datos de la instancia de la entidad
     *
     * @param us Instancia de la entidad
     */
    public void Actualizar(Cuenta us) {
        if (ExisteID(us.getId())) {
            EntityTransaction tx = null;
            try {
                tx = em.getTransaction();
                tx.begin();
                em.merge(us);
                tx.commit();
            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                throw new RuntimeException("Error al actualizar el usuario", e);
            }

        } else {

            System.out.println("La cuenta no existe " + us.getId());

        }
    }

    public void Eliminar(Cuenta cuenta) {

        cuenta.setEstado_cuenta(EstadoCuenta.ELIMINADO);
        Actualizar(cuenta);
    }

    /**
     * Busca la instacia de la entidad que concuerde con el identificador
     *
     * @param id identificador de la instacia
     * @return la instacia de la entidad o null
     */
    public Cuenta BuscarEntidad(long id) {
        Cuenta cuenta = null;

        try {

            cuenta = em.find(Cuenta.class, id);

        } catch (IllegalArgumentException e) {

            System.err.println("Usuario no encontrado");

        }
        return cuenta;
    }

    /**
     * Busca todas las instancias correspondiente a la entidad Cuenta
     *
     * @return una coleccion ordena de tipo {@link Cuenta}
     */
    public List<Cuenta> BuscarTodasLasEntidades() {
        return em.createQuery("SELECT c FROM Cuenta c", Cuenta.class).getResultList();
    }

    /**
     * Comprueba si una instacia existe o no
     *
     * @param id identificador de la entidad
     * @return <code>true</code> si la instancia de la entidad existe o
     * <code>false</code> si no existe
     */
    public boolean ExisteID(long id) {
        try {
            em.getReference(Cuenta.class, id);
            return true;
        } catch (EntityNotFoundException ex) {
            return false;
        }
    }
    
    public List<Cuenta> EncontrarPorTipoCuenta(TipoCuenta tipo_cuenta) {
        TypedQuery<Cuenta> query = em.createQuery("SELECT c FROM Cuenta c WHERE c.tipo_cuenta = :tipo_cuenta", Cuenta.class);
        query.setParameter("tipo_cuenta", tipo_cuenta);
        return query.getResultList();
    }
}
