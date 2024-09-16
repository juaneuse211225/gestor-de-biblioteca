package com.tecno.biblioteca.controller;

import com.tecno.biblioteca.model.DetallePrestamo;
import com.tecno.biblioteca.model.Libro;
import com.tecno.biblioteca.service.LibraryService;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class DatosPrestamo2Controller {

    LibraryService ls = new LibraryService();
    ObservableList<Libro> libros = FXCollections.observableArrayList();
    FilteredList<Libro> FiltroLibros;

    @FXML
    private TableColumn<DetallePrestamo, String> Autor;

    @FXML
    private TableColumn<DetallePrestamo, Integer> Cantidad_Prestar;

    @FXML
    private TableColumn<Libro, Integer> Cantidad_disponible;

    @FXML
    private TableColumn<Libro, String> Column_autor;

    @FXML
    private TableColumn<Libro, Long> Column_isbn;

    @FXML
    private TableColumn<Libro, String> Column_titulo;

    @FXML
    private TableColumn<DetallePrestamo, Long> ISBN;

    @FXML
    private TableView<Libro> Tabla_LibrosDisponibles;

    @FXML
    private TableView<DetallePrestamo> Tabla_detallePrestamo;

    @FXML
    private TableColumn<DetallePrestamo, String> Titulo;

    @FXML
    private Button bot_agregar;

    @FXML
    private Button bot_quitar;

    @FXML
    private ComboBox<String> combo_filtro;

    @FXML
    private TextField txt_buscar;

    private Spinner<Integer> spinner = new Spinner<>();

    @FXML
    public void initialize() {
        FiltroLibros = new FilteredList<Libro>(libros, libs -> true);

        FiltroLibros = new FilteredList<Libro>(libros, libs -> true);
        txt_buscar.textProperty().addListener((observable, viejo, nuevo) -> aplicarFiltro(nuevo));
        combo_filtro.valueProperty().addListener((observable, viejo, nuevo) -> aplicarFiltro(txt_buscar.getText()));

        combo_filtro.setItems(FXCollections.observableArrayList(
                "isbn", "titulo", "autor"
        ));
        combo_filtro.setValue("titulo");

        Column_isbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        Column_titulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        Column_autor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        Cantidad_disponible.setCellValueFactory(new PropertyValueFactory<>("libros_disponibles"));
        CargarDatosEnTabla();

        ISBN.setCellValueFactory(celdaISBN
                -> new SimpleObjectProperty<>(celdaISBN.getValue().getId_libro().getIsbn()
                ));
        Titulo.setCellValueFactory(celdaTitulo
                -> new SimpleObjectProperty<>(celdaTitulo.getValue().getId_libro().getTitulo()
                ));
        Autor.setCellValueFactory(celdaAutor
                -> new SimpleObjectProperty<>(celdaAutor.getValue().getId_libro().getAutor()
                ));
        AgregarSpinner();

    }

    public void AgregarSpinner() {
        Cantidad_Prestar.setCellFactory(col -> new TableCell<DetallePrestamo, Integer>() {
            private final Spinner<Integer> spinner = new Spinner<>();

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    DetallePrestamo detalle = getTableView().getItems().get(getIndex());
                    Libro libro = detalle.getId_libro();

                    // Configurar el spinner basado en la disponibilidad del libro
                    SpinnerValueFactory<Integer> valueFactory
                            = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, libro.getLibros_disponibles());
                    spinner.setValueFactory(valueFactory);
                    spinner.getValueFactory().setValue(item != null ? item : 1); // Si hay un valor, usarlo

                    setGraphic(spinner);

                    // Actualizar la cantidad en el DetallePrestamo cada vez que se cambia el valor del spinner
                    spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                        detalle.setEjemplares_prestados(newValue);
                    });
                }
            }
        });
    }

    @FXML
    void AgregarAction(ActionEvent event) {
        Libro libroSeleccionado = ObtenerSeleccion();
        if (libroSeleccionado != null) {
            // Crear una nueva instancia de DetallePrestamo con la información del libro
            DetallePrestamo detalle = new DetallePrestamo();
            detalle.setId_libro(libroSeleccionado);

            // Añadirlo a la tabla de detalle
            Tabla_detallePrestamo.getItems().add(detalle);

            // Remover el libro de la tabla de libros disponibles
            libros.remove(libroSeleccionado);

            // Volver a aplicar el spinner
            AgregarSpinner();
        }
    }

    @FXML
    void QuitarAction(ActionEvent event) {
        DetallePrestamo detalleSeleccionado = Tabla_detallePrestamo.getSelectionModel().getSelectedItem();
        if (detalleSeleccionado != null) {
            // Remover de la tabla de detalle
            Tabla_detallePrestamo.getItems().remove(detalleSeleccionado);

            // Recuperar el libro asociado y devolverlo a la lista de libros disponibles
            Libro libro = detalleSeleccionado.getId_libro();
            libros.add(libro);
        }
    }

    public void CargarDatosEnTabla() {
        libros.clear();
        libros.addAll(ls.Encontrar_Libro_por_Disponibilidad());
        Tabla_LibrosDisponibles.setItems(FiltroLibros);
    }

    public Libro ObtenerSeleccion() {
        return Tabla_LibrosDisponibles.getSelectionModel().getSelectedItem();
    }

    private void aplicarFiltro(String textoBusqueda) {
        String filtroSeleccionado = combo_filtro.getValue() != null ? combo_filtro.getValue().toLowerCase() : "";

        FiltroLibros.setPredicate(libro -> {
            // Si el campo de búsqueda está vacío, mostrar todo
            if (textoBusqueda == null || textoBusqueda.isEmpty()) {
                return true;
            }

            // Filtrar según el tipo seleccionado en el ComboBox
            String textoFiltro = textoBusqueda.toLowerCase();
            return switch (filtroSeleccionado) {
                case "autor" ->
                    libro.getAutor().toLowerCase().contains(textoFiltro);
                case "titulo" ->
                    libro.getTitulo().toLowerCase().contains(textoFiltro);
                case "isbn" ->
                    Long.toString(libro.getIsbn()).contains(textoFiltro);
                default ->
                    false;
            };
        });
    }

    public DatosPrestamo2Controller() {
    }

    public List<DetallePrestamo> obtenerDetallesPrestamo() {
        List<DetallePrestamo> detalles = new ArrayList<>();

        // Recorremos cada fila de la tabla
        for (DetallePrestamo detalle : Tabla_detallePrestamo.getItems()) {
            // Obtenemos el índice de la fila actual
            int indicefila = Tabla_detallePrestamo.getItems().indexOf(detalle);

            // Obtenemos el valor observable de la celda en la columna de cantidad para la fila actual
            TableCell<DetallePrestamo, Integer> cell = (TableCell<DetallePrestamo, Integer>) Cantidad_Prestar.getCellObservableValue(indicefila);

            // Verificamos si la celda tiene un gráfico que es un Spinner
            if (cell != null && cell.getGraphic() instanceof Spinner) {
                Spinner<Integer> spinner = (Spinner<Integer>) cell.getGraphic();

                // Obtenemos el valor del Spinner
                int cantidadPrestar = spinner.getValue();

                // Asignamos este valor a la propiedad correspondiente del DetallePrestamo
                detalle.setEjemplares_prestados(cantidadPrestar);
            }

            // Si el ID del préstamo es nulo, significa que es un nuevo detalle de préstamo
            if (detalle.getId_prestamo() == null) {
                // Crear un nuevo DetallePrestamo con el libro y la cantidad obtenida del Spinner
                Libro lib = detalle.getId_libro();
                DetallePrestamo nuevoDetalle = new DetallePrestamo();
                nuevoDetalle.setId_libro(lib);
                nuevoDetalle.setEjemplares_prestados(detalle.getEjemplares_prestados()); // Este valor ya fue actualizado con el Spinner

                // Añadimos el nuevo detalle a la lista
                detalles.add(nuevoDetalle);
            }
        }

        return detalles;
    }

}
