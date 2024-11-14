package org.tecno.gestor.biblioteca.dao;

import org.tecno.gestor.biblioteca.enums.EstadoPrestamo;
import org.tecno.gestor.biblioteca.model.Categoria;
import org.tecno.gestor.biblioteca.model.Cuenta;
import java.util.List;

import org.tecno.gestor.biblioteca.model.Prestamo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Alert;

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
            TypedQuery<Prestamo> query = em.createQuery("SELECT p FROM Prestamo p WHERE p.id_cuenta = :id_cuenta AND (p.estado_prestamo = 'ACTIVO' OR p.estado_prestamo = 'EN_MORA')", Prestamo.class);
            query.setParameter("id_cuenta", idUsuario);

            Prestamo prestamo = query.getSingleResult();

            // Refrescar el préstamo para asegurarse de que los datos sean los más recientes
            em.refresh(prestamo);

            return prestamo;
        } catch (NoResultException e) {
            return null; // No hay préstamos activos o en mora
        }
    }

    public List<Prestamo> EncontrarPrestamosActivos() {
        try {
            TypedQuery<Prestamo> query = em.createQuery("SELECT p FROM Prestamo p WHERE p.estado_prestamo = 'ACTIVO'", Prestamo.class);
            return query.getResultList();
        } catch (PersistenceException e) {
            System.out.println("algo paso");
            return null;
        }
    }

    public Map<Month, List<Prestamo>> obtenerPrestamosEntreFechas(LocalDate fechaInicial, LocalDate fechaFinal) {
        // Validar las fechas
        if (fechaInicial.isAfter(fechaFinal)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("La fecha inicial no puede ser posterior a la fecha final.");
            alert.setTitle("Error");
            alert.setHeaderText("Verificacion de fechas");
            alert.showAndWait();
            return null;
        }

        // Iniciar una transacción
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();  // Iniciar la transacción

            String jpql = "SELECT p FROM Prestamo p "
                    + "WHERE p.fecha_inicio_prestamo BETWEEN :fechaInicial AND :fechaFinal "
                    + "ORDER BY p.fecha_inicio_prestamo ASC";

            Query query = em.createQuery(jpql);
            query.setParameter("fechaInicial", fechaInicial);
            query.setParameter("fechaFinal", fechaFinal);

            // Ejecutar la consulta y obtener los resultados
            List<Prestamo> prestamos = query.getResultList();
            Map<Month, List<Prestamo>> prestamosPorMes = new HashMap<>();

            // Agrupar préstamos por mes
            for (Prestamo prestamo : prestamos) {
                LocalDate fechaPrestamo = prestamo.getFecha_inicio_prestamo();
                Month mes = fechaPrestamo.getMonth();

                // Agregar préstamo al mapa
                prestamosPorMes.putIfAbsent(mes, new ArrayList<>());
                prestamosPorMes.get(mes).add(prestamo);
            }

            tx.commit();  // Confirmar la transacción

            return prestamosPorMes;

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();  // Hacer rollback en caso de error
            }
            throw e;  // Propagar el error
        }
    }

    public List<Prestamo> EncontrarPrestamoMora() {
        try {
            TypedQuery<Prestamo> query = em.createQuery("SELECT p FROM Prestamo p WHERE p.estado_prestamo = 'EN_MORA'", Prestamo.class);

            return query.getResultList();
        } catch (NoResultException e) {
            return null; // No hay préstamos activos o en mora
        }
    }

    public void RefrescarPrestamo(Prestamo prestamo) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.refresh(prestamo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al refrescar", e);
        }
    }
}
