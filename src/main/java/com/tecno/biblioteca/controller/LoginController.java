package com.tecno.biblioteca.controller;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.JFXSnackbarLayout;
import com.tecno.biblioteca.enums.TipoCuenta;
import com.tecno.biblioteca.model.Cuenta;
import com.tecno.biblioteca.service.LibraryService;

import java.io.IOException;
import java.net.URL;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginController {

    private double xOffset = 0;
    private double yOffset = 0;

    LibraryService ls = new LibraryService();

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
    private AnchorPane panel1;

    @FXML
    void ActionEntrar(ActionEvent event) throws IOException {
        String css1 = getClass().getResource("/CSS/bar.css").toExternalForm();
        snacknotificacion = new JFXSnackbar(panel1);
        snacknotificacion.setPrefWidth(322);
        snacknotificacion.getStylesheets().add(css1);
        
        String texto1 = ContraField.getText().trim();
        String texto2 = IdentificacionField.getText().trim();
        
        if (!(texto1.isEmpty()) || !(texto2.isEmpty())) {
            Long id = Long.valueOf(texto2);
            
            if (autenticacion(texto1, id)) {
                Escena(event);
            } else {

                snacknotificacion.fireEvent(new SnackbarEvent(new JFXSnackbarLayout("Id o contraseña erroneos o inexistente")));

            }
        } else {
            snacknotificacion.fireEvent(new SnackbarEvent(new JFXSnackbarLayout("Campo vacios, ingrese los datos")));

        }

    }

    public boolean autenticacion(String contraseña, Long id) {
        Cuenta cuenta = ls.Encontrar_Cuenta(id);
        if (cuenta != null && cuenta.getContraseña().equals(contraseña) && !(cuenta.getTipo_cuenta().equals(TipoCuenta.USUARIO))) {
            return true;
        }
        return false;
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
