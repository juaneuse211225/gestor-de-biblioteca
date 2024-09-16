package com.tecno.biblioteca.controller;

import com.tecno.biblioteca.model.Cuenta;
import com.tecno.biblioteca.model.Prestamo;
import com.tecno.biblioteca.service.LibraryService;
import java.io.IOException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DatosPrestamo1Controller {

    LibraryService ls = new LibraryService();
    ObservableList<Cuenta> ListaCuenta = FXCollections.observableArrayList();
    FilteredList<Cuenta> FiltrarLista;

    @FXML
    private Button bot_crear_usuario;

    @FXML
    private TextArea descripcion;

    @FXML
    private DatePicker fecha_inicial;

    @FXML
    private DatePicker fecha_relativa;

    @FXML
    private ListView<Cuenta> lista;

    @FXML
    private ComboBox<String> combo_filtro;

    @FXML
    private TextField text_buscar;

    public DatosPrestamo1Controller() {
    }

    private Prestamo prestamo;

    @FXML
    public void initialize() {
        FiltrarLista = new FilteredList<>(ListaCuenta, c -> true);
        text_buscar.textProperty().addListener((observable, viejo, nuevo) -> aplicarFiltro(nuevo));
        combo_filtro.valueProperty().addListener((observable, viejo, nuevo) -> aplicarFiltro(text_buscar.getText()));
        combo_filtro.setItems(FXCollections.observableArrayList(
                "id", "nombre", "apellido"
        ));
        combo_filtro.setValue("id");
        lista.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        CargarDatosEnLista();
        fecha_inicial.setValue(LocalDate.now());
    }

    @FXML
    void CrearAction(ActionEvent event) {
        VentanaModal();
    }

    Stage stage;
    FXMLLoader loader;

    public void VentanaModal() {
        try {
            stage = (Stage) bot_crear_usuario.getScene().getWindow();
            loader = new FXMLLoader(getClass().getResource("/fxml/ventanasmodales/datos.fxml"));
            Parent root = loader.load();
            DatosController controller = loader.getController();
            Stage modalStage = new Stage();
            controller.setTitulo_text("Añadir usuario");

            Scene escena = new Scene(root);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(stage);
            /*modalStage.setHeight(375);
            modalStage.setWidth(400);*/
            modalStage.sizeToScene();
            modalStage.setScene(escena);
            modalStage.centerOnScreen();
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CargarDatosEnLista() {
        ListaCuenta.clear();
        ListaCuenta.addAll(ls.Encontrar_Cuentas());
        lista.setItems(FiltrarLista);
    }

    private void aplicarFiltro(String textoBusqueda) {
        String filtroSeleccionado = combo_filtro.getValue().toLowerCase();

        FiltrarLista.setPredicate(c -> {
            // Si el campo de búsqueda está vacío, mostrar todo
            if (textoBusqueda == null || textoBusqueda.isEmpty()) {
                return true;
            }

            // Filtrar según el tipo seleccionado en el ComboBox
            String textoFiltro = textoBusqueda.toLowerCase();

            return switch (filtroSeleccionado) {
                case "id" ->
                    Long.valueOf(c.getId()).toString().toLowerCase().contains(textoFiltro);
                case "nombre" ->
                    c.getNombre().toLowerCase().contains(textoFiltro);
                case "apellido" ->
                    c.getApellido().toLowerCase().contains(textoFiltro);
                default ->
                    false;
            };
        });
    }

    public Prestamo getPrestamo() {

        Cuenta cuen = obtenerCuentaSeleccionada();

        Prestamo prestamo = new Prestamo(null, null, fecha_inicial.getValue(), fecha_relativa.getValue(), descripcion.getText().trim());
        cuen.setId_prestamo(prestamo);
        prestamo.setId_cuenta(cuen);
        return prestamo;
    }

    public Cuenta obtenerCuentaSeleccionada() {
        return lista.getSelectionModel().getSelectedItem();
    }

}
