package org.tecno.gestor.biblioteca.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.tecno.gestor.biblioteca.enums.TipoCuenta;
import org.tecno.gestor.biblioteca.model.Cuenta;
import org.tecno.gestor.biblioteca.service.LibraryService;

public class PanelCuentaController {
    
    FilteredList<Cuenta> FiltroDatos;
    private LibraryService ls = new LibraryService();

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
    private TableColumn<Cuenta, Long> column_id;

    @FXML
    private TableColumn<Cuenta, String> column_nombre;

    @FXML
    private TableColumn<Cuenta, String> column_telefono;

    @FXML
    private TableColumn<Cuenta, TipoCuenta> column_tipo;

    @FXML
    private ComboBox<String> combo_filtro;

    @FXML
    private TextField text_busqueda;
    
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
                "nombre", "apellido", "tipo de cuenta", "id"
        ));
        combo_filtro.setValue("nombre");

        FiltroDatos = new FilteredList<>(cuentas, c -> true);
        text_busqueda.textProperty().addListener((observable, viejo, nuevo) -> aplicarFiltro(nuevo));
        combo_filtro.valueProperty().addListener((observable, viejo, nuevo) -> aplicarFiltro(text_busqueda.getText()));
        column_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        column_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        column_apellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        column_correo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        column_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        column_tipo.setCellValueFactory(new PropertyValueFactory<>("tipo_cuenta"));

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
                case "tipo de cuenta" ->
                    Cuenta.getTipo_cuenta().name().toLowerCase().contains(textoFiltro);
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
        if (c1 != null) {
            ls.ELiminar_Cuenta(c1);
            CargarDatosEnTabla();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Eliminar usuario");
            alert.setHeaderText("Eliminar usuario");
            alert.setContentText("Selecciona un usario para eliminarlo");
            alert.showAndWait();
        }
    }

    @FXML
    void GuardarAction(ActionEvent event) {
        VentanaModal();
        CargarDatosEnTabla();
        
    }
    
    public Cuenta ObtenerSeleccion() {
        Cuenta seleccion = TablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccion != null) {
            Cuenta buscar = ls.Encontrar_Cuenta(seleccion.getId());
            return buscar;
        }
        return null;
    }
    
    public void CargarDatosEnTabla() {
        cuentas.clear();
        cuentas.addAll(ls.Encontrar_Admin());
        TablaUsuarios.setItems(FiltroDatos);
    }

    private void handleDobleClick(Cuenta DatosFila) {
        VentanaModal(DatosFila);

    }
    
    public void VentanaModal() {
        Stage gestorStage = (Stage) bot_guardar.getScene().getWindow();
        AdministradorVentanaUsuario adminModal = new AdministradorVentanaUsuario(gestorStage);

        adminModal.mostrarModal("Registrar cuenta", controller -> {
            controller.setTitulo_text("Registrar cuenta");
            controller.setVentana(2);
        });
        CargarDatosEnTabla();
    }

    public void VentanaModal(Cuenta cuenta) {
        Stage gestorStage = (Stage) bot_guardar.getScene().getWindow();
        AdministradorVentanaUsuario adminModal = new AdministradorVentanaUsuario(gestorStage);

        adminModal.mostrarModal("Editar cuenta", controller -> {
            controller.setTitulo_text("Editar cuenta");
            controller.setCuenta(cuenta);
            controller.setVentana(2);
        });
        CargarDatosEnTabla();
    }

}
