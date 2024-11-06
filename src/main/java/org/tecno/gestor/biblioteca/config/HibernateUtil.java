package org.tecno.gestor.biblioteca.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManager;

public class HibernateUtil {

    private static final EntityManagerFactory entitymanagerfactory;

    static {
        try {
            entitymanagerfactory = Persistence.createEntityManagerFactory("persistencia-biblioteca");

        } catch (Throwable ex) {
            System.err.println("Inicializar el EntityManagerFactory no es posible. " + ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }

    }

    public static EntityManager getEntityManager() {
        return entitymanagerfactory.createEntityManager();
    }

    public static void cerrar() {
        if (entitymanagerfactory != null) {
            entitymanagerfactory.close();
        }
    }
}
