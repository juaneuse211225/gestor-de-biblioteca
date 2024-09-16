package com.tecno.biblioteca.controller;

import com.tecno.biblioteca.model.DetallePrestamo;
import com.tecno.biblioteca.model.Libro;
import com.tecno.biblioteca.model.Prestamo;
import com.tecno.biblioteca.service.LibraryService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class CrearPrestamoController {

    LibraryService ls = new LibraryService();

    private Prestamo prestamo;

    @FXML
    private Button bot_cancel;

    @FXML
    private Button bot_guardar;

    @FXML
    private Button bot_siguiente;
    
    @FXML
    private Button bot_atras;

    @FXML
    private StackPane stack;

    @FXML
    private Label titulo;

    AnchorPane datosprestamo1, datosprestamo2;
    DatosPrestamo1Controller controlador1;
    DatosPrestamo2Controller controlador2;

    @FXML
    public void initialize() throws IOException {

        FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/vistas/vistasprestamo/datosprestamo1.fxml"));
        datosprestamo1 = loader1.load();
        controlador1 = loader1.getController();

        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/vistas/vistasprestamo/datosprestamo2.fxml"));
        datosprestamo2 = loader2.load();
        controlador2 = loader2.getController();

        stack.getChildren().addAll(datosprestamo1, datosprestamo2);
        mostrarPanel(stack, datosprestamo1);
    }

    @FXML
    void CancelAction(ActionEvent event) {
        
    }
    
    @FXML
    void RegresarAction(ActionEvent event) {
        this.prestamo = controlador1.getPrestamo();

        bot_guardar.setDisable(true);
        bot_siguiente.setDisable(false);
        bot_atras.setDisable(true);
        
        mostrarPanel(stack, datosprestamo1);
    }

    @FXML
    void GuardarAction(ActionEvent event) {
        // Asignar los detalles del préstamo al objeto prestamo
        prestamo.setId_detalleprestamo(EditarDetalles());
        // Crear el préstamo utilizando el servicio
        ls.Crear_Prestamo(prestamo);

        // Puedes agregar lógica adicional aquí si es necesario
    }

    public List<DetallePrestamo> EditarDetalles() {
        List<DetallePrestamo> lista_detalles = new ArrayList<>();

        if (prestamo != null) {
            // Obtener la lista de detalles de préstamo desde el controlador 2
            for (DetallePrestamo detalle : controlador2.obtenerDetallesPrestamo()) {
                // Asignar el préstamo actual a cada detalle
                detalle.setId_prestamo(prestamo);

                // Actualizar la cantidad disponible del libro
                Libro lib = detalle.getId_libro();
                lib.setLibros_disponibles(lib.getLibros_disponibles() - detalle.getEjemplares_prestados());
                ls.Actualizar_Libro(lib);
                // Agregar el detalle a la lista
                lista_detalles.add(detalle);
            }
        }
        controlador2.CargarDatosEnTabla();
        return lista_detalles;
    }

    @FXML
    void SiguienteAction(ActionEvent event) {
        this.prestamo = controlador1.getPrestamo();

        bot_guardar.setDisable(false);
        bot_siguiente.setDisable(true);
        bot_atras.setDisable(false);
        mostrarPanel(stack, datosprestamo2);

    }

    private void mostrarPanel(StackPane stackPane, AnchorPane PanelAMostrar) {
        for (var child : stackPane.getChildren()) {
            if (child instanceof AnchorPane) {
                child.setVisible(child == PanelAMostrar);
                child.setManaged(child == PanelAMostrar); // Esto asegura que el espacio se ajuste correctamente
            }
        }
    }

}
