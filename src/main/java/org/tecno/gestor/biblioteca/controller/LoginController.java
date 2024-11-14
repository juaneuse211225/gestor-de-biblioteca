package org.tecno.gestor.biblioteca.controller;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.JFXSnackbarLayout;
import org.tecno.gestor.biblioteca.config.HibernateUtil;
import org.tecno.gestor.biblioteca.enums.TipoCuenta;
import org.tecno.gestor.biblioteca.model.Cuenta;
import org.tecno.gestor.biblioteca.service.LibraryService;
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
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.tecno.gestor.biblioteca.enums.EstadoCuenta;

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
    public void initialize() {
        // Restringir a solo números en IdentificacionField
        IdentificacionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                IdentificacionField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    void ActionEntrar(ActionEvent event) throws IOException {
        String css1 = getClass().getResource("/CSS/bar.css").toExternalForm();
        snacknotificacion = new JFXSnackbar(panel1);
        snacknotificacion.setPrefWidth(322);
        snacknotificacion.getStylesheets().add(css1);
        String texto1 = IdentificacionField.getText().trim();
        String texto2 = ContraField.getText().trim();
        if (!(texto1.isEmpty()) || !(texto2.isEmpty())) {
            Long id = Long.valueOf(texto1);

            int tipoCuenta = autenticacion(texto2, id);

            if (tipoCuenta == 2) {
                Escena(event, TipoCuenta.ADMINISTRADOR);
            } else if (tipoCuenta == 1) {
                Escena(event, TipoCuenta.BIBLIOTECARIO);
            } else {
                snacknotificacion.fireEvent(new SnackbarEvent(new JFXSnackbarLayout("Id o contraseña erroneos o inexistente")));
            }
        } else {
            snacknotificacion.fireEvent(new SnackbarEvent(new JFXSnackbarLayout("Campo vacios, ingrese los datos")));
        }

    }
    
    public int autenticacion(String contraseña, Long id) {
        Cuenta cuenta = ls.Encontrar_Cuenta(id);
        if (cuenta != null && cuenta.getContraseña().equals(contraseña) && (cuenta.getEstado_cuenta() == EstadoCuenta.ACTIVO)) {

            if (cuenta.getTipo_cuenta().equals(TipoCuenta.ADMINISTRADOR)) {
                return 2;
            } else if (cuenta.getTipo_cuenta().equals(TipoCuenta.BIBLIOTECARIO)) {
                return 1;
            }
        }
        return 0;
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

    public void Escena(ActionEvent event, TipoCuenta tipoCuenta) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestorBiblioteca.fxml"));
        Parent Vista_Main = loader.load();
        Stage stage = new Stage();
        // Obtener el controlador de la nueva escena
        GestorController controller = loader.getController();

        // Pasar el tipo de cuenta al nuevo controlador
        Scene SecundaryScene = new Scene(Vista_Main);
        Stage win = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(SecundaryScene);
        stage.setHeight(650);
        stage.setWidth(1200);
        stage.centerOnScreen();
        stage.setTitle("Gestor de biblioteca");
        Image a = new Image(getClass().getResource("/img/image832.png").toString());
        stage.getIcons().add(a);
        stage.initStyle(StageStyle.UNDECORATED);
        win.close();
        stage.show();
        
        controller.inicializar(tipoCuenta);
    }

    @FXML
    void CerrarAction(ActionEvent event) {
        HibernateUtil.cerrar();
        Stage stage = getStage();
        stage.close();
    }

    public Stage getStage() {
        Stage stage = (Stage) panel_superior.getScene().getWindow();
        return stage;
    }
}
