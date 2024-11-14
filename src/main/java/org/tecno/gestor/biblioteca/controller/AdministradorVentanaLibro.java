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
public class AdministradorVentanaLibro {
     private Stage parentStage;

    public AdministradorVentanaLibro(Stage parentStage) {
        this.parentStage = parentStage;
    }

    public void mostrarModal(String titulo, Consumer<Object> configurador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crearlibro2.fxml"));
            Parent root = loader.load();
            Object controller = loader.getController();
            configurador.accept(controller);

            Stage modalStage = new Stage();
            Scene escena = new Scene(root);
            modalStage.setTitle(titulo);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(parentStage);
            modalStage.sizeToScene();
            modalStage.setScene(escena);
            modalStage.centerOnScreen();
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
