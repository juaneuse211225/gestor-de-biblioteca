package org.tecno.gestor.biblioteca.controller;

import java.io.IOException;
import org.tecno.gestor.biblioteca.config.HibernateUtil;
import org.tecno.gestor.biblioteca.enums.TipoCuenta;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GestorController {

    private PanelLibrosController panelLibrocontroll;
    private PanelUsuarioController panelUsuarioController;
    private double xOffset = 0;
    private double yOffset = 0;
    private BorderPane panelLibro, panelCuenta, panelUsuario, panelcrearPrestamo, panelcrearDevolucion, panelinformes;
    private FXMLLoader loader, loader2;
    private TipoCuenta rolUsuarioActual;

    @FXML
    private StackPane Panel_Stack;

    @FXML
    private Button bot_PanelCuenta;

    @FXML
    private Button bot_PanelUsuario;

    @FXML
    private Button bot_cerrarSecion;

    @FXML
    private Button bot_creardevolucion;

    @FXML
    private Button bot_crearprestamo;

    @FXML
    private Button bot_panelInformes;

    @FXML
    private Button bot_panelLibros;

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
    private TitledPane prestamos;

    @FXML
    private TitledPane usuarios;

    @FXML
    public void initialize() {
        try {
            loader2 = new FXMLLoader(getClass().getResource("/fxml/PanelUsuario.fxml"));
            panelUsuario = loader2.load();
            panelUsuarioController = loader2.getController();

            panelcrearPrestamo = FXMLLoader.load(getClass().getResource("/fxml/crearprestamo.fxml"));

            loader = new FXMLLoader(getClass().getResource("/fxml/PanelLibros.fxml"));
            panelLibro = loader.load();
            panelLibrocontroll = loader.getController();
            panelCuenta = FXMLLoader.load(getClass().getResource("/fxml/PanelCuenta.fxml"));
            panelcrearDevolucion = FXMLLoader.load(getClass().getResource("/fxml/panelDevolucion.fxml"));
            panelinformes = FXMLLoader.load(getClass().getResource("/fxml/informe.fxml"));

            Panel_Stack.getChildren().addAll(panelCuenta, panelUsuario, panelLibro, panelcrearPrestamo, panelinformes, panelcrearDevolucion);

            mostrarPanel(Panel_Stack, panelUsuario);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void CerrarSesionAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("¿Estas seguro?");
        alert.setHeaderText("¿Estas seguro?");
        alert.setContentText("Quieres salir del gestor?");
        Optional<ButtonType> resultado = alert.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            Stage win = (Stage) ((Node) event.getSource()).getScene().getWindow();
            win.close();
        } else {

        }
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
    void Cerrar_Action(ActionEvent event) {
        HibernateUtil.cerrar();
        Stage stage = getStage();
        stage.close();
    }

    @FXML
    void PanelLibrosAction(ActionEvent event) {
        mostrarPanel(Panel_Stack, panelLibro);
        panelLibrocontroll.CargarDatosEnTabla();
    }

    @FXML
    void PanelUsuarioAction(ActionEvent event) {
        mostrarPanel(Panel_Stack, panelUsuario);
    }

    @FXML
    void CrearDevolucionAction(ActionEvent event) {
        mostrarPanel(Panel_Stack, panelcrearDevolucion);
    }

    @FXML
    void CrearPrestamoAction(ActionEvent event) {
        mostrarPanel(Panel_Stack, panelcrearPrestamo);
    }

    @FXML
    void InformesAction(ActionEvent event) {
        mostrarPanel(Panel_Stack, panelinformes);
    }

    @FXML
    void PanelCuentaAction(ActionEvent event) {
        mostrarPanel(Panel_Stack, panelCuenta);
    }

    public void inicializar(TipoCuenta tipoCuenta) {
        this.rolUsuarioActual = tipoCuenta;

        if (tipoCuenta == TipoCuenta.BIBLIOTECARIO) {
            informes.setVisible(false);
            bot_PanelCuenta.setVisible(false);
            bot_PanelCuenta.setManaged(false);
        } else if (tipoCuenta == TipoCuenta.ADMINISTRADOR) {
            informes.setVisible(true);
        }
    }

    private void mostrarPanel(StackPane stackPane, BorderPane PanelAMostrar) {
        for (var child : stackPane.getChildren()) {
            if (child instanceof BorderPane) {
                child.setVisible(child == PanelAMostrar);
                child.setManaged(child == PanelAMostrar);
            }
        }
    }

    public Stage getStage() {
        Stage stage = (Stage) Panel_Stack.getScene().getWindow();
        return stage;
    }

}
