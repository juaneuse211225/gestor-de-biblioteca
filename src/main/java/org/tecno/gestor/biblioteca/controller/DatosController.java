package org.tecno.gestor.biblioteca.controller;

import javafx.event.ActionEvent;
import org.tecno.gestor.biblioteca.enums.TipoCuenta;
import org.tecno.gestor.biblioteca.model.Cuenta;
import org.tecno.gestor.biblioteca.service.LibraryService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DatosController {

    private Cuenta cuenta;

    private String titulo_text;

    private int tipo_ventana;

    @FXML
    private Button bot_cancelar;

    @FXML
    private Button bot_guardar;

    @FXML
    private ComboBox<TipoCuenta> combo_rol;

    @FXML
    private TextField text_apellido;

    @FXML
    private PasswordField text_contra;

    @FXML
    private TextField text_telefono;

    @FXML
    private TextField text_correo;

    @FXML
    private TextField text_id;

    @FXML
    private TextField text_nombre;

    @FXML
    private Label titulo;

    @FXML
    public void initialize() {
        text_id.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                text_id.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        text_telefono.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                text_telefono.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        text_correo.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Si newValue es false, significa que el campo ha perdido el foco
                if (!text_correo.getText().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                    text_correo.setText(""); // Deja el campo en blanco si el correo no es válido
                }
            }
        });

        text_apellido.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZñÑ ]*")) {
                text_apellido.setText(newValue.replaceAll("[^a-zA-ZñÑ ]", "")); // Elimina cualquier carácter que no sea letra
            }
        });

        text_nombre.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZñÑ ]*")) {
                text_nombre.setText(newValue.replaceAll("[^a-zA-ZñÑ ]", "")); // Elimina cualquier carácter que no sea letra
            }
        });

        combo_rol.getItems().addAll(TipoCuenta.BIBLIOTECARIO, TipoCuenta.ADMINISTRADOR);
        combo_rol.setValue(TipoCuenta.BIBLIOTECARIO);
    }

    @FXML
    void CancelAction(ActionEvent event) {
        Stage stage = (Stage) bot_cancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    void GuardarAction(ActionEvent event) {
        LibraryService ls = new LibraryService();

        if (!ComprobarCampos()) {
            switch (titulo_text) {
                case "Registrar usuario" -> {
                    Long id = Long.parseLong(text_id.getText());
                    if (ls.exite_cuenta(id)) {
                        mostrarAlerta("Cuenta existente", "El usuario con N° " + id + " ya existe");
                    } else {
                        ls.Crear_Cuenta(new Cuenta(id, text_nombre.getText(), text_contra.getText(), text_apellido.getText(), text_correo.getText(), text_telefono.getText(), TipoCuenta.USUARIO));
                        limpiar();
                    }
                }
                case "Editar usuario" -> {
                    ls.Actualizar_Cuenta(new Cuenta(Long.parseLong(text_id.getText()), text_nombre.getText(), text_contra.getText(), text_apellido.getText(), text_correo.getText(), text_telefono.getText(), TipoCuenta.USUARIO));
                    Stage stage = getStage();
                    stage.close();
                }
                case "Registrar cuenta" -> {
                    String contraseña = text_contra.getText().trim();
                    if (contraseña.isBlank() || contraseña.length() < 8) {

                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText("Contraseña invalida");
                        alert.setTitle("Contraseña invalida");
                        alert.setContentText("Por favor ingresar una contraseña con al menos 8 carateres");
                        alert.showAndWait();
                    } else {
                        Long id = Long.valueOf(text_id.getText());
                        if(ls.exite_cuenta(id)) {
                            mostrarAlerta("Cuenta existente", "La cuenta con N° " + id + " ya existe");
                        } else {
                            ls.Crear_Cuenta(new Cuenta(Long.parseLong(text_id.getText()), text_nombre.getText(), text_contra.getText(), text_apellido.getText(), text_correo.getText(), text_telefono.getText(), combo_rol.getValue()));
                            limpiar();
                        }
                    }
                }
                case "Editar cuenta" -> {
                    String contraseña = text_contra.getText().trim();
                    if (contraseña.isBlank() || contraseña.length() < 8) {

                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText("Contraseña invalida");
                        alert.setTitle("Contraseña invalida");
                        alert.setContentText("Por favor ingresar una contraseña con al menos 8 carateres");
                        alert.showAndWait();
                    } else {
                        long id = Long.valueOf(text_id.getText());
                        ls.Actualizar_Cuenta(new Cuenta(id, text_nombre.getText(), text_contra.getText(), text_apellido.getText(), text_correo.getText(), text_telefono.getText(), combo_rol.getValue()));
                        Stage stage = getStage();
                        stage.close();
                    }

                }

                default ->
                    throw new AssertionError();
            }
        } else {
            mostrarAlerta("Error", "Todos los campos deben estar llenos.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public boolean ComprobarCampos() {
        if (text_id.getText().isBlank() || text_apellido.getText().isBlank() || text_correo.getText().isBlank() || text_nombre.getText().isBlank() || text_telefono.getText().isBlank()) {
            return true;
        }
        return false;
    }

    public Stage getStage() {
        Stage stage = (Stage) titulo.getScene().getWindow();
        return stage;
    }

    public void limpiar() {
        text_apellido.setText("");
        text_contra.setText("");
        text_correo.setText("");
        text_id.setText("");
        text_nombre.setText("");
        text_telefono.setText("");
        combo_rol.setValue(TipoCuenta.BIBLIOTECARIO);

    }

    public void setTitulo_text(String titulo_text) {
        this.titulo_text = titulo_text;
        if (titulo != null) {
            titulo.setText(titulo_text);
        }
    }

    public String getTitulo_text() {
        return titulo_text;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
        text_apellido.setText(cuenta.getApellido());
        text_contra.setText(cuenta.getContraseña());
        text_correo.setText(cuenta.getCorreo());
        text_id.setText(String.valueOf(cuenta.getId()));
        text_nombre.setText(cuenta.getNombre());
        text_telefono.setText(cuenta.getTelefono());
        combo_rol.setValue(cuenta.getTipo_cuenta());
        text_id.setDisable(true);
    }

    public void setVentana(int ventana) {
        this.tipo_ventana = ventana;
        ajustarComponentesSegunVentana();
    }

    private void ajustarComponentesSegunVentana() {
        if (tipo_ventana == 1) {
            combo_rol.setVisible(false);
            text_contra.setVisible(false);
        } else if (tipo_ventana == 2) {
            combo_rol.setVisible(true);
            text_contra.setVisible(true);
        }
    }

}
