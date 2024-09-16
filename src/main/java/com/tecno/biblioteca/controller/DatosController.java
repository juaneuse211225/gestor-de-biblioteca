package com.tecno.biblioteca.controller;

import javafx.event.ActionEvent;
import com.tecno.biblioteca.enums.TipoCuenta;
import com.tecno.biblioteca.model.Cuenta;
import com.tecno.biblioteca.service.LibraryService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DatosController {

    private Cuenta cuenta;

    private String titulo_text;

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
    void CancelAction(ActionEvent event) {
        Stage stage = (Stage) bot_cancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    void GuardarAction(ActionEvent event) {
        LibraryService ls = new LibraryService();
        long id = Long.valueOf(text_id.getText());
        if (titulo_text.equals("Añadir usuario")) {
            ls.Crear_Cuenta(new Cuenta(id, text_nombre.getText(), text_contra.getText(), text_apellido.getText(), text_correo.getText(), text_telefono.getText(), combo_rol.getValue()));
            limpiar();
        } else {
            ls.Actualizar_Cuenta(new Cuenta(id, text_nombre.getText(), text_contra.getText(), text_apellido.getText(), text_correo.getText(), text_telefono.getText(), combo_rol.getValue()));
            Stage stage = getStage();
            stage.close();
        }

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
        combo_rol.setValue(TipoCuenta.USUARIO);
    }

    public void setTitulo_text(String titulo_text) {
        this.titulo_text = titulo_text;
        if (titulo != null) {
            titulo.setText(titulo_text);
        }
    }

    public void initialize() {
        combo_rol.getItems().addAll(TipoCuenta.values());
        combo_rol.setValue(TipoCuenta.USUARIO);
    }

    public String getTitulo_text() {
        return titulo_text;
    }

    public DatosController() {
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
        text_contra.setVisible(false);
    }

}
