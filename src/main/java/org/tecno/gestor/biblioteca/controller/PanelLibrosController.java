package org.tecno.gestor.biblioteca.controller;

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
import javafx.scene.control.Alert;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.tecno.gestor.biblioteca.enums.TipoCuenta;
import org.tecno.gestor.biblioteca.model.Libro;
import org.tecno.gestor.biblioteca.service.LibraryService;

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
        if (libro != null) {
            ls.ELiminar_Libro(libro);
            CargarDatosEnTabla();
        } else {
           Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Eliminar libro");
            alert.setHeaderText("Eliminar libro");
            alert.setContentText("Selecciona un Libro para eliminarlo");
            alert.showAndWait();
        }
    }

    @FXML
    void añadirAction(ActionEvent event) {
        VentanaModal();
    }

    public void CargarDatosEnTabla() {
        libros.clear();
        tabla_libros.getItems().clear();
        libros.addAll(ls.Encontrar_Libros());
        tabla_libros.setItems(FiltroLibros);
    }

    public Libro ObtenerSeleccion() {
        Libro seleccion = tabla_libros.getSelectionModel().getSelectedItem();
        if (seleccion != null) {
            return seleccion;
        }
        return null;
    }

    public void VentanaModal() {
        // Obtener la ventana principal
        Stage gestorStage = (Stage) bot_añadir.getScene().getWindow();

        // Crear una instancia de VentanaModalManager
        AdministradorVentanaLibro modalManager = new AdministradorVentanaLibro(gestorStage);

        // Mostrar la ventana modal para añadir un libro
        modalManager.mostrarModal("Registrar Libro", controller -> {
            CrearLibroController libroController = (CrearLibroController) controller;
            libroController.setTitulo_text("Registrar Libro");
        });

        // Cargar los datos actualizados en la tabla después de cerrar la ventana
        CargarDatosEnTabla();
    }

    public void VentanaModal(Libro libro) {
        // Obtener la ventana principal
        Stage gestorStage = (Stage) bot_añadir.getScene().getWindow();

        // Crear una instancia de VentanaModalManager
        AdministradorVentanaLibro modalManager = new AdministradorVentanaLibro(gestorStage);

        // Mostrar la ventana modal para editar un libro
        modalManager.mostrarModal("Editar Libro", controller -> {
            CrearLibroController libroController = (CrearLibroController) controller;
            libroController.setTitulo_text("Editar Libro");
            libroController.setLibro(libro);  // Pasar el libro a editar al controlador
        });

        // Cargar los datos actualizados en la tabla después de cerrar la ventana
        CargarDatosEnTabla();
    }
    TipoCuenta rolUsuarioActual;

    public void setRolUsuarioActual(TipoCuenta rolUsuario) {
        this.rolUsuarioActual = rolUsuario;

    }

}
