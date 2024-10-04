package com.tecno.biblioteca.controller;

import com.tecno.biblioteca.model.Libro;
import com.tecno.biblioteca.service.LibraryService;
import java.io.IOException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PanelLibrosController {

    // Servicio para manejar datos de libros
    LibraryService ls = new LibraryService();
    ObservableList<Libro> libros = FXCollections.observableArrayList();
    FilteredList<Libro> FiltroLibros;

    @FXML
    private TableView<Libro> tabla_libros;

    @FXML
    private TableColumn<Libro, String> Column_autor;

    @FXML
    private TableColumn<Libro, String> Column_categoria;

    @FXML
    private TableColumn<Libro, String> Column_disponibles;

    @FXML
    private TableColumn<Libro, Long> Column_isbn;

    @FXML
    private TableColumn<Libro, String> Column_titulo;

    @FXML
    private TableColumn<Libro, String> Column_totales;

    @FXML
    private TableColumn<Libro, String> Column_ubicacion;

    @FXML
    private Button bot_añadir;

    @FXML
    private Button bot_eliminar;

    @FXML
    private ComboBox<String> filtro_combo;

    @FXML
    private TextField text_busqueda;

    Stage modalstage;
    FXMLLoader loader;

    @FXML
    public void initialize() {

        tabla_libros.setRowFactory(tv -> {
            TableRow<Libro> row = new TableRow<>();

            row.setOnMouseClicked(eve -> {
                if (eve.getClickCount() == 2 && !(row.isEmpty())) {
                    Libro libro = row.getItem();
                    handleDobleClick(libro);
                }
            });
            return row;
        });

        // Configurar el combo box
        FiltroLibros = new FilteredList<Libro>(libros, libs -> true);
        text_busqueda.textProperty().addListener((observable, viejo, nuevo) -> aplicarFiltro(nuevo));
        filtro_combo.valueProperty().addListener((observable, viejo, nuevo) -> aplicarFiltro(text_busqueda.getText()));

        filtro_combo.setItems(FXCollections.observableArrayList(
                "isbn", "titulo", "autor", "categoria"
        ));
        filtro_combo.setValue("titulo");

        Column_isbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        Column_titulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        Column_autor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        Column_categoria.setCellValueFactory(celdaNombreCategoria
                //se obtiene el valor del getter Categoria de la clase categoria atravez de Libro 
                -> new SimpleObjectProperty<>(celdaNombreCategoria.getValue().getCategoria().getCategoria()
                ));
        Column_disponibles.setCellValueFactory(new PropertyValueFactory<>("libros_disponibles"));

        Column_totales.setCellValueFactory(new PropertyValueFactory<>("libros_total"));
        Column_ubicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));

        // Cargar datos en la tabla
        CargarDatosEnTabla();
    }

    public void handleDobleClick(Libro librofila) {
        VentanaModal(librofila);
    }

    private void aplicarFiltro(String textoBusqueda) {
        String filtroSeleccionado = filtro_combo.getValue().toLowerCase();

        FiltroLibros.setPredicate(Libro -> {
            // Si el campo de búsqueda está vacío, mostrar todo
            if (textoBusqueda == null || textoBusqueda.isEmpty()) {
                return true;
            }

            // Filtrar según el tipo seleccionado en el ComboBox
            String textoFiltro = textoBusqueda.toLowerCase();

            return switch (filtroSeleccionado) {
                case "autor" ->
                    Libro.getAutor().toLowerCase().contains(textoFiltro);
                case "titulo" ->
                    Libro.getTitulo().toLowerCase().contains(textoFiltro);
                case "estado_libro" ->
                    Libro.getEstado_libro().name().toLowerCase().contains(textoFiltro);
                case "isbn" ->
                    Long.toString(Libro.getIsbn()).contains(textoFiltro);
                case "ubicacion" ->
                    Libro.getUbicacion().toLowerCase().contains(textoFiltro);
                case "categoria" ->
                    Libro.getCategoria().getCategoria().toLowerCase().contains(textoFiltro);
                default ->
                    false;
            };
        });
    }

    @FXML
    void EliminarAction(ActionEvent event) {
        Libro libro = ObtenerSeleccion();
        ls.ELiminar_Libro(libro);
    }

    @FXML
    void añadirAction(ActionEvent event) {
        VentanaModal();
    }

    public void CargarDatosEnTabla() {
        libros.clear();
        libros.addAll(ls.Encontrar_Libros());
        tabla_libros.setItems(FiltroLibros);
    }

    public Libro ObtenerSeleccion() {
        return tabla_libros.getSelectionModel().getSelectedItem();
    }

    public void VentanaModal() {
        try {
            modalstage = (Stage) bot_añadir.getScene().getWindow();
            loader = new FXMLLoader(getClass().getResource("/fxml/crearlibro.fxml"));
            Parent root = loader.load();
            CrearLibroController controller = loader.getController();
            Stage modalStage = new Stage();
            controller.setTitulo_text("Añadir Libro");

            Scene escena = new Scene(root);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(modalstage);
            /*modalStage.setHeight(375);
            modalStage.setWidth(400);*/
            modalStage.sizeToScene();
            modalStage.setScene(escena);
            modalStage.centerOnScreen();
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
        CargarDatosEnTabla();
    }

    public void VentanaModal(Libro libro) {
        try {
            modalstage = (Stage) bot_añadir.getScene().getWindow();
            loader = new FXMLLoader(getClass().getResource("/fxml/crearlibro.fxml"));
            Parent root = loader.load();
            CrearLibroController controller = loader.getController();
            Stage modalStage = new Stage();
            controller.setTitulo_text("Editar libro");
            controller.setLibro(libro);
            Scene escena = new Scene(root);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(modalstage);
            /*modalStage.setHeight(375);
            modalStage.setWidth(400);*/
            modalStage.sizeToScene();
            modalStage.setScene(escena);
            modalStage.centerOnScreen();
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
        CargarDatosEnTabla();
    }
}
