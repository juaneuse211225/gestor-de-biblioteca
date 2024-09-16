package com.tecno.biblioteca.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GestorController {

    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private Button GestionPerfiles;

    @FXML
    private StackPane Panel_Stack;

    @FXML
    private Button Perfil;

    @FXML
    private Button bot_PanelUsuario;

    @FXML
    private Button bot_cerrarSecion;

    @FXML
    private Button bot_creardevolucion;

    @FXML
    private Button bot_crearprestamo;

    @FXML
    private Button bot_panelLibros;

    @FXML
    private Button bot_paneldevolucion;

    @FXML
    private Button bot_panelprestamo;

    @FXML
    private Button boton_cerrar;

    @FXML
    private TitledPane devoluciones;

    @FXML
    private TitledPane informes;

    @FXML
    private TitledPane libros;

    @FXML
    private Label panel_superior;

    @FXML
    private TitledPane perfil;

    @FXML
    private TitledPane prestamos;

    @FXML
    private TitledPane usuarios;
    BorderPane panelLibro, panelUsuario, panelcrearPrestamo;

    @FXML
    public void initialize() {
        try {
            panelUsuario = FXMLLoader.load(getClass().getResource("/fxml/vistas/PanelUsuario.fxml"));
            panelLibro = FXMLLoader.load(getClass().getResource("/fxml/vistas/PanelLibros.fxml"));
            panelcrearPrestamo = FXMLLoader.load(getClass().getResource("/fxml/vistas/crearPrestamo.fxml"));
            Panel_Stack.getChildren().addAll(panelUsuario, panelLibro, panelcrearPrestamo);

            mostrarPanel(Panel_Stack, panelUsuario);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void CerrarSesionAction(ActionEvent event) {

    }

    @FXML
    void Cerrar_Action(ActionEvent event) {
        Stage stage = getStage();
        stage.close();
    }

    public Stage getStage() {
        Stage stage = (Stage) Panel_Stack.getScene().getWindow();
        return stage;
    }

    @FXML
    void MoverPressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    void DejarVentana(MouseEvent event) {
        Stage primaryStage = getStage();
        primaryStage.setX(event.getScreenX() - xOffset);
        primaryStage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    void PanelLibrosAction(ActionEvent event) {
        mostrarPanel(Panel_Stack, panelLibro);
    }

    @FXML
    void PanelPerifilesAction(ActionEvent event) {

    }

    @FXML
    void PanelUsuarioAction(ActionEvent event) {
        mostrarPanel(Panel_Stack, panelUsuario);
    }

    private void mostrarPanel(StackPane stackPane, BorderPane PanelAMostrar) {
        for (var child : stackPane.getChildren()) {
            if (child instanceof BorderPane) {
                child.setVisible(child == PanelAMostrar);
                child.setManaged(child == PanelAMostrar); // Esto asegura que el espacio se ajuste correctamente
            }
        }
    }

    @FXML
    void PerfilAction(ActionEvent event) {

    }

    @FXML
    void PanelDevolucionAction(ActionEvent event) {

    }

    @FXML
    void CrearDevolucionAction(ActionEvent event) {

    }

    @FXML
    void CrearPrestamoAction(ActionEvent event) {
        mostrarPanel(Panel_Stack, panelcrearPrestamo);
    }

    @FXML
    void PanelPrestamoAction(ActionEvent event) {

    }

}
