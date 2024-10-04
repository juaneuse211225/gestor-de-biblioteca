package com.tecno.biblioteca.controller;

import com.tecno.biblioteca.enums.EstadoPrestamo;
import com.tecno.biblioteca.model.Cuenta;
import com.tecno.biblioteca.model.DetallePrestamo;
import com.tecno.biblioteca.model.Libro;
import com.tecno.biblioteca.model.Prestamo;
import com.tecno.biblioteca.service.LibraryService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CrearPrestamofinalController {

    LibraryService ls = new LibraryService();

    private Prestamo prestamo = new Prestamo();

    @FXML
    private TableColumn<DetallePrestamo, Integer> Column_ejemplares;

    @FXML
    private TableColumn<DetallePrestamo, Long> Column_isbn;

    @FXML
    private TableColumn<DetallePrestamo, String> Column_titulo;

    @FXML
    private TextArea area_descripsion;

    @FXML
    private Button bot_agregarlibro;

    @FXML
    private Button bot_agregarusuario;

    @FXML
    private Button bot_cancelar;

    @FXML
    private Button bot_guardar;

    @FXML
    private DatePicker fecha_entrega;

    @FXML
    private DatePicker fecha_inicio;

    @FXML
    private TableView<DetallePrestamo> tabla_detalleprestamo;

    @FXML
    private TextField text_BuscarLibro;

    @FXML
    private TextField text_BuscarUsuario;

    @FXML
    private Label titulo;

    @FXML
    private Label Nombre;

    @FXML
    public void initialize() {
        fecha_inicio.setValue(LocalDate.now());
        tabla_detalleprestamo.setRowFactory(tv -> {
            TableRow<DetallePrestamo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2 && !(row.isEmpty())) {
                    DetallePrestamo detalle = row.getItem();

                    quitarLibro(detalle);
                }

            });
            return row;
        });

        text_BuscarLibro.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                text_BuscarLibro.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        text_BuscarUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                text_BuscarUsuario.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        Column_isbn.setCellValueFactory(celda_isbn
                -> new SimpleObjectProperty<>(celda_isbn.getValue().getId_libro().getIsbn()
                ));
        Column_titulo.setCellValueFactory(celda_titulo
                -> new SimpleObjectProperty<>(celda_titulo.getValue().getId_libro().getTitulo()
                ));
    }

    @FXML
    void GuardarAction(ActionEvent event) {
        if (prestamo != null && !(area_descripsion.getText().isBlank())) {

            ls.Crear_Prestamo(crearprestamo());
            limpiar();
        }

    }

    @FXML
    void RegresarAction(ActionEvent event) {
        limpiar();
    }

    @FXML
    void agregar_libro_action(ActionEvent event) {
        String id_libro = text_BuscarLibro.getText().trim();
        if (id_libro.isBlank()) {
            System.out.println("el campo esta vacio");
        } else {
            long id = Long.valueOf(id_libro);
            Libro libro = ls.Encontrar_Libro(id);
            if (libro != null && libro.getLibros_disponibles() > 0) {
                DetallePrestamo detalle = new DetallePrestamo();
                detalle.setId_libro(libro);
                CargarDatosEnTabla(detalle);
                AgregarSpinner();
            } else {
                System.err.println("Libro no encontrado o no disponible");
            }
        }
        text_BuscarLibro.setText("");
    }

    public boolean existeLibroEnTabla(DetallePrestamo detalle, TableView<DetallePrestamo> tableView) {
        for (DetallePrestamo d : tableView.getItems()) {
            if (d.getId_libro() == detalle.getId_libro()) {
                return true;
            }
        }
        return false;
    }

    @FXML
    void buscar_usuario_action(ActionEvent event) {
        String id_usuario = text_BuscarUsuario.getText().trim();

        if (id_usuario.isBlank()) {
            System.out.println("Campo de texto vacío");
        } else {
            long id = Long.valueOf(id_usuario);

            // Buscar la cuenta por ID
            Cuenta cuenta = ls.Encontrar_Cuenta(id);

            if (cuenta == null) {
                System.err.println("El usuario no fue encontrado");
                Nombre.setText("");
                return;
            }

            // Verificar si tiene algún préstamo activo o en mora
            Optional<Prestamo> op = cuenta.getId_prestamo().stream()
                    .filter(p -> p.getEstado_prestamo() == EstadoPrestamo.ACTIVO || p.getEstado_prestamo() == EstadoPrestamo.EN_MORA)
                    .findFirst();

            // Si no tiene préstamos activos, permitir crear uno nuevo
            if (op.isEmpty()) {
                Nombre.setText(cuenta.getApellido() + ", " + cuenta.getNombre());

                prestamo.setId_cuenta(cuenta);
                cuenta.getId_prestamo().add(prestamo);  // Asociar el nuevo préstamo a la cuenta
            } else {
                // Si tiene un préstamo activo o en mora, mostrar un mensaje de error
                System.err.println("El usuario ya tiene un préstamo activo o en mora. Finalice el préstamo antes de crear uno nuevo.");
                Nombre.setText("");
            }
        }
    }

    public void quitarLibro(DetallePrestamo detalleSeleccionado) {

        if (detalleSeleccionado != null) {
            tabla_detalleprestamo.getItems().remove(detalleSeleccionado);
        }
    }

    public Prestamo crearprestamo() {
        if (!(tabla_detalleprestamo.getItems().isEmpty()) && fecha_entrega.getValue() != null && fecha_inicio.getValue() != null && !(area_descripsion.getText().trim().isEmpty())) {
            prestamo.setEstado_prestamo(EstadoPrestamo.ACTIVO);
            prestamo.setFecha_inicio_prestamo(fecha_inicio.getValue());
            prestamo.setFecha_relativa_devolucion(fecha_entrega.getValue());
            prestamo.setId_detalleprestamo(EditarDetalles());
            prestamo.setId_devolucion(null);
            prestamo.setObservaciones(area_descripsion.getText().trim());

        }
        return prestamo;
    }

    public void CargarDatosEnTabla(DetallePrestamo detalle) {
        if (existeLibroEnTabla(detalle, tabla_detalleprestamo)) {
            System.out.println("el libro ya esta presente en la tabla");
        } else {
            tabla_detalleprestamo.getItems().add(detalle);
        }
    }

    public void AgregarSpinner() {
        Column_ejemplares.setCellFactory(col -> new TableCell<DetallePrestamo, Integer>() {
            private final Spinner<Integer> spinner = new Spinner<>();

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    DetallePrestamo detalle = getTableView().getItems().get(getIndex());
                    Libro libro = detalle.getId_libro();

                    // Configurar el Spinner con el rango de valores (mínimo 1, máximo libros disponibles)
                    SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, libro.getLibros_disponibles());
                    spinner.setValueFactory(valueFactory);
                    spinner.getValueFactory().setValue(detalle.getEjemplares_prestados());

                    setGraphic(spinner);

                    // Listener para asegurar que el valor no sea menor que 1
                    spinner.valueProperty().addListener((obs, valorViejo, valorNuevo) -> {
                        if (valorNuevo < 1) {
                            spinner.getValueFactory().setValue(1); // Forzar que el valor mínimo sea 1
                        } else {
                            detalle.setEjemplares_prestados(valorNuevo); // Actualizar la cantidad prestada
                        }
                    });
                }
            }
        });
    }

    public List<DetallePrestamo> obtenerDetallesPrestamo() {
        List<DetallePrestamo> detalles = new ArrayList<>();
        for (DetallePrestamo detalle : tabla_detalleprestamo.getItems()) {
            int cantidadPrestar = detalle.getEjemplares_prestados();

            // Aquí ya no es necesario el operador ternario porque lo controlamos en el spinner
            detalle.setEjemplares_prestados(cantidadPrestar);

            // Crear un nuevo detalle de préstamo para cada libro
            Libro lib = detalle.getId_libro();
            DetallePrestamo nuevoDetalle = new DetallePrestamo();
            nuevoDetalle.setId_libro(lib);
            nuevoDetalle.setEjemplares_prestados(detalle.getEjemplares_prestados());
            nuevoDetalle.setId_prestamo(prestamo);

            detalles.add(nuevoDetalle);
        }
        return detalles;
    }

    public List<DetallePrestamo> EditarDetalles() {
        List<DetallePrestamo> lista_detalles = new ArrayList<>();

        if (prestamo != null) {

            for (DetallePrestamo detalle : obtenerDetallesPrestamo()) {
                // Asignar el préstamo actual a cada detalle
                detalle.setId_prestamo(prestamo);

                // Actualizar la cantidad disponible del libro
                Libro lib = detalle.getId_libro();
                lib.setLibros_disponibles(lib.getLibros_disponibles() - detalle.getEjemplares_prestados());
                ls.Actualizar_Libro(lib);
                // Agregar el detalle a la lista
                lista_detalles.add(detalle);
            }
        }

        return lista_detalles;
    }

    public void limpiar() {
        text_BuscarLibro.setText("");
        text_BuscarUsuario.setText("");
        Nombre.setText("");
        area_descripsion.setText("");
        fecha_entrega.setValue(null);
        fecha_inicio.setValue(null);
        prestamo.equals(new Prestamo());
        tabla_detalleprestamo.getItems().clear();
    }

}
