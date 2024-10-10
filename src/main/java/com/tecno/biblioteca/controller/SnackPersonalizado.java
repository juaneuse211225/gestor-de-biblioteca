/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tecno.biblioteca.controller;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 *
 * @author juan
 */
public class SnackPersonalizado {

    private JFXSnackbar snacknotificacion;
    private Pane panel;  // Panel donde estará asignado el Snackbar
    private double width;  // Ancho personalizado del Snackbar

    // Constructor que inicializa el Snackbar con el panel y ancho
    public SnackPersonalizado(Pane panel, double width) {
        this.panel = panel;
        this.width = width;

        // Crear el JFXSnackbar y asignarlo al panel
        snacknotificacion = new JFXSnackbar(panel);
        snacknotificacion.setPrefWidth(width);  // Ajustar ancho del Snackbar

        // Añadir CSS si es necesario
        String css1 = getClass().getResource("/CSS/bar.css").toExternalForm();
        snacknotificacion.getStylesheets().add(css1);

        // Mover el Snackbar fuera de la pantalla (arriba)
        snacknotificacion.setTranslateY(-50); // Ajusta la posición hacia arriba según la altura estimada
    }

    // Método para mostrar el Snackbar con animación desde arriba
    public void mostrarSnackbar(String texto, int duracionMillis) {
        // Crear el evento del Snackbar con el texto proporcionado
        snacknotificacion.fireEvent(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout(texto)));

        // Crear una animación para mover el Snackbar desde arriba hacia abajo
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.millis(300)); // Duración de la animación
        transition.setNode(snacknotificacion);
        transition.setFromY(-50); // Empieza fuera de la vista (arriba)
        transition.setToY(0); // Llega a la posición de visualización
        transition.play(); // Iniciar la animación

        // Ocultar el Snackbar después del tiempo especificado
        PauseTransition pause = new PauseTransition(Duration.millis(duracionMillis));
        pause.setOnFinished(event -> {
            // Animación para ocultar el Snackbar subiéndolo de nuevo
            TranslateTransition hideTransition = new TranslateTransition();
            hideTransition.setDuration(Duration.millis(300)); // Duración de la animación de salida
            hideTransition.setNode(snacknotificacion);
            hideTransition.setFromY(0); // Desde la posición visible
            hideTransition.setToY(-50); // Subir fuera de la vista nuevamente
            hideTransition.play();
        });
        pause.play();
    }
}
