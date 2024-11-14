/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tecno.gestor.biblioteca.controller;

import java.io.IOException;
import java.util.function.Consumer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author juan
 */
public class AdministradorVentanaUsuario {

    private Stage gestorStage;
    private FXMLLoader loader;
    private static final String FXML_PATH = "/fxml/datos.fxml";

    public AdministradorVentanaUsuario(Stage gestorStage) {
        this.gestorStage = gestorStage;
    }

    private Stage crearVentanaModal(Parent root, String titulo) {
        Stage modalStage = new Stage();
        Scene escena = new Scene(root);
        modalStage.setTitle(titulo);
        modalStage.initModality(Modality.WINDOW_MODAL);
        modalStage.initOwner(gestorStage);
        modalStage.sizeToScene();
        modalStage.setScene(escena);
        modalStage.centerOnScreen();
        return modalStage;
    }

    public void mostrarModal(String titulo, Consumer<DatosController> configurador) {
        try {
            loader = new FXMLLoader(getClass().getResource(FXML_PATH));
            Parent root = loader.load();
            DatosController controller = loader.getController();
            configurador.accept(controller);
            Stage modalStage = crearVentanaModal(root, titulo);
            modalStage.showAndWait();
        } catch (IOException e) {
        }
    }
}
