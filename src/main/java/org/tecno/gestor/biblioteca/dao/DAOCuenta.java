package org.tecno.gestor.biblioteca.dao;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.tecno.gestor.biblioteca.enums.EstadoCuenta;
import org.tecno.gestor.biblioteca.enums.TipoCuenta;
import org.tecno.gestor.biblioteca.model.Cuenta;

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
        Cuenta cuenta2 = em.find(Cuenta.class, id);

        if (cuenta2 == null) {
            throw new EntityNotFoundException("Cuenta no encontrada con ID: " + id);
        }

        
        return cuenta2;
    }

    /**
     * Busca todas las instancias correspondiente a la entidad Cuenta
     *
     * @return una coleccion ordena de tipo {@link Cuenta}
     */
    public List<Cuenta> BuscarTodasLasEntidades() {
        List<Cuenta> cuentas = em.createQuery("SELECT c FROM Cuenta c", Cuenta.class).getResultList();

        // Refrescar cada entidad para asegurar que los datos estén actualizados
        for (Cuenta cuenta : cuentas) {
            em.refresh(cuenta);
        }

        return cuentas;
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
        List<Cuenta> listas = query.getResultList();
        for (Cuenta cuenta : listas) {
            em.refresh(cuenta);

        }

        return listas;
    }

    public void RefrescarCuenta(Cuenta cuenta) {
        EntityTransaction tx = null;
        try {
            if (!em.getTransaction().isActive()) {
                tx = em.getTransaction();
                tx.begin();
            }
            em.refresh(cuenta);
            if (tx != null) {
                tx.commit();
            }
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al refrescar la cuenta", e);
        }
    }

}
