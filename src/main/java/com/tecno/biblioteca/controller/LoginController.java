package com.tecno.biblioteca.controller;

import com.jfoenix.controls.JFXSnackbar;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginController {

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private Button Bot_Entrar;

    @FXML
    private PasswordField ContraField;

    @FXML
    private TextField IdentificacionField;

    @FXML
    private Button bot_cerrar;

    @FXML
    private Pane panel_superior;

    @FXML
    private JFXSnackbar snacknotificacion;

    @FXML
    void ActionEntrar(ActionEvent event) throws IOException {
        Escena(event);
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

    public void Escena(ActionEvent event) throws IOException {
        Parent Vista_Main = FXMLLoader.load(getClass().getResource("/fxml/GestorBiblioteca.fxml"));
        Scene SecundaryScene = new Scene(Vista_Main);

        Stage win = (Stage) ((Node) event.getSource()).getScene().getWindow();

        win.setScene(SecundaryScene);
        win.setHeight(650);
        win.setWidth(1200);
        win.centerOnScreen();
        win.show();
    }

    @FXML
    void CerrarAction(ActionEvent event) {
        Stage stage = getStage();
        stage.close();
    }

    public Stage getStage() {
        Stage stage = (Stage) panel_superior.getScene().getWindow();
        return stage;
    }
}
