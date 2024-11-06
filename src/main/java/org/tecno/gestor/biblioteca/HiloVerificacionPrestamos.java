package org.tecno.gestor.biblioteca;


import java.time.LocalDate;
import java.util.List;
import org.tecno.gestor.biblioteca.enums.EstadoPrestamo;
import org.tecno.gestor.biblioteca.model.Prestamo;
import org.tecno.gestor.biblioteca.service.LibraryService;

/**
 *
 * @author juan
 */
public class HiloVerificacionPrestamos implements Runnable {

    @Override
    public void run() {
        LibraryService ls = new LibraryService();

        while (!Thread.currentThread().isInterrupted()) {  // COntinuar solo si el hilo no ha sido interrupido
            List<Prestamo> prestamos = ls.EncontrarPrestamosActivos();

            if (prestamos != null && !prestamos.isEmpty()) {
                for (Prestamo prestamo : prestamos) {
                    if (prestamo.getFecha_relativa_devolucion().isBefore(LocalDate.now())) {
                        prestamo.setEstado_prestamo(EstadoPrestamo.EN_MORA);
                        ls.Actualizar_Prestamo(prestamo);
                    }
                }
            } else {
                System.out.println("No hay préstamos activos. Se volverá a comprobar en 30 minutos.");
            }

            // Espera 5 minutos antes de volver a comprobar
            try {
                Thread.sleep(30 * 60 * 1000);  // 5 minutos en milisegundos
            } catch (InterruptedException e) {
                System.err.println("Hilo interrumpido: " + e.getMessage());
                break;  // Salir del bucle si el hilo es interrumpido
            }
        }
    }

}
