package com.tecno.biblioteca.controller;

import com.tecno.biblioteca.enums.TipoCuenta;
import com.tecno.biblioteca.model.Cuenta;
import com.tecno.biblioteca.service.LibraryService;

import java.io.IOException;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PanelUsuarioController {

    Stage gestorStage;
    FXMLLoader loader;
    LibraryService ls = new LibraryService();

    ObservableList<Cuenta> cuentas = FXCollections.observableArrayList();

    @FXML
    private TableView<Cuenta> TablaUsuarios;

    @FXML
    private Button bot_eliminar;

    @FXML
    private Button bot_guardar;

    @FXML
    private TableColumn<Cuenta, String> column_apellido;

    @FXML
    private TableColumn<Cuenta, String> column_correo;

    @FXML
    private TableColumn<Cuenta, String> column_id;

    @FXML
    private TableColumn<Cuenta, String> column_nombre;

    @FXML
    private TableColumn<Cuenta, String> column_estado;

    @FXML
    private TableColumn<Cuenta, String> column_telefono;

    @FXML
    private TextField text_busqueda;

    @FXML
    private ComboBox<String> combo_filtro;

    FilteredList<Cuenta> FiltroDatos;

    @FXML
    public void initialize() {

        TablaUsuarios.setRowFactory(tv -> {
            TableRow<Cuenta> row = new TableRow<>();
            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2 && !(row.isEmpty())) {
                    Cuenta DatosFila = row.getItem();

                    handleDobleClick(DatosFila);
                }

            });

            return row;

        });
        combo_filtro.setItems(FXCollections.observableArrayList(
                "nombre", "apellido", "estado_cuenta", "id"
        ));
        combo_filtro.setValue("nombre");

        FiltroDatos = new FilteredList<Cuenta>(cuentas, c -> true);
        text_busqueda.textProperty().addListener((observable, viejo, nuevo) -> aplicarFiltro(nuevo));
        combo_filtro.valueProperty().addListener((observable, viejo, nuevo) -> aplicarFiltro(text_busqueda.getText()));
        column_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        column_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        column_apellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        column_correo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        column_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        column_estado.setCellValueFactory(new PropertyValueFactory<>("estado_cuenta"));

        CargarDatosEnTabla();
    }

    private void aplicarFiltro(String textoBusqueda) {
        String filtroSeleccionado = combo_filtro.getValue().toLowerCase();

        FiltroDatos.setPredicate(Cuenta -> {
            // Si el campo de búsqueda está vacío, mostrar todo
            if (textoBusqueda == null || textoBusqueda.isEmpty()) {
                return true;
            }

            // Filtrar según el tipo seleccionado en el ComboBox
            String textoFiltro = textoBusqueda.toLowerCase();

            return switch (filtroSeleccionado) {
                case "nombre" ->
                    Cuenta.getNombre().toLowerCase().contains(textoFiltro);
                case "apellido" ->
                    Cuenta.getApellido().toLowerCase().contains(textoFiltro);
                case "estado_cuenta" ->
                    Cuenta.getEstado_cuenta().name().toLowerCase().contains(textoFiltro);
                case "id" ->
                    Long.toString(Cuenta.getId()).contains(textoFiltro);
                default ->
                    false;
            };
        });
    }

    @FXML
    void EliminarAction(ActionEvent event) {
        Cuenta c1 = ObtenerSeleccion();
        ls.ELiminar_Cuenta(c1);
        CargarDatosEnTabla();
    }

    @FXML
    void GuardarAction(ActionEvent event) {
        VentanaModal();
        CargarDatosEnTabla();
    }

    ;
    public void VentanaModal() {
        try {
            gestorStage = (Stage) bot_guardar.getScene().getWindow();
            loader = new FXMLLoader(getClass().getResource("/fxml/ventanasmodales/datos.fxml"));
            Parent root = loader.load();
            DatosController controller = loader.getController();
            Stage modalStage = new Stage();
            controller.setTitulo_text("Añadir usuario");

            Scene escena = new Scene(root);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(gestorStage);
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

    public void VentanaModal(Cuenta cuenta) {
        try {
            gestorStage = (Stage) bot_guardar.getScene().getWindow();
            loader = new FXMLLoader(getClass().getResource("/fxml/ventanasmodales/datos.fxml"));
            Parent root = loader.load();
            DatosController controller = loader.getController();
            Stage modalStage = new Stage();
            controller.setTitulo_text("Editar usuario");
            controller.setCuenta(cuenta);
            Scene escena = new Scene(root);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(gestorStage);
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

    public Cuenta ObtenerSeleccion() {
        Cuenta seleccion = TablaUsuarios.getSelectionModel().getSelectedItem();
        Cuenta buscar = ls.Encontrar_Cuenta(seleccion.getId());
        return buscar;

    }

    public void CargarDatosEnTabla() {

        cuentas.clear();
        cuentas.addAll(ls.Encontrar_Cuenta_por_Tipo(TipoCuenta.USUARIO));
        TablaUsuarios.setItems(FiltroDatos);
    }

    private void handleDobleClick(Cuenta DatosFila) {

        VentanaModal(DatosFila);

    }

}
