package org.tecno.gestor.biblioteca.controller;

import jakarta.persistence.EntityNotFoundException;
import org.tecno.gestor.biblioteca.enums.EstadoCuenta;
import org.tecno.gestor.biblioteca.enums.EstadoPrestamo;
import org.tecno.gestor.biblioteca.model.Cuenta;
import org.tecno.gestor.biblioteca.model.DetallePrestamo;
import org.tecno.gestor.biblioteca.model.Libro;
import org.tecno.gestor.biblioteca.model.Prestamo;
import org.tecno.gestor.biblioteca.service.LibraryService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
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
import javafx.util.Callback;

public class CrearPrestamoController {

    private LibraryService ls = new LibraryService();

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

        Platform.runLater(() -> {
            Scene scene = tabla_detalleprestamo.getScene();
            if (scene != null) {
                scene.getStylesheets().add(getClass().getResource("/CSS/spinner.css").toExternalForm());
            }
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

        fecha_inicio.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> ov, LocalDate t1, LocalDate t2) {

                //se bloquean todos los dias anteriores a la fecha de creacion mas 8 dias para el datepicker encargado de la fecha final
                fecha_entrega.setDayCellFactory(new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);

                                // Deshabilitar las fechas anteriores a la fecha mínima
                                LocalDate fechaLimite = t2.plusDays(7);
                                if (item.isBefore(fechaLimite)) {
                                    setDisable(true);
                                }
                            }
                        };
                    }
                });
            }
        });

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

        setDesactivar(true);
    }

    @FXML
    void GuardarAction(ActionEvent event) {
        Prestamo presta; 
        presta = crearprestamo();
        if (presta != null) {
            // Crear un nuevo objeto prestamo de manera local
             // Se crea un nuevo préstamo
            
            ls.Crear_Prestamo(presta);  // Guardar el nuevo préstamo
            limpiar();  // Limpiar el formulario
            setDesactivar(true);  // Desactivar los botones

            // Mostrar alerta de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Préstamo registrado");
            alert.setHeaderText("Préstamo registrado");
            alert.setContentText("El préstamo se ha registrado de forma exitosa.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos incompletos");
            alert.setHeaderText("Verifica que los campos no estén vacíos");
            alert.setContentText("Por favor verifica que toda la información esté completa.");
            alert.showAndWait();
        }
    }

    @FXML
    void RegresarAction(ActionEvent event) {
        limpiar();
        setDesactivar(true);
    }

    @FXML
    void agregar_libro_action(ActionEvent event) {
        String id_libro = text_BuscarLibro.getText().trim();
        if (id_libro.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rellenar campos");
            alert.setHeaderText("Rellenar campos");
            alert.setContentText("Ingrese los valores en el campo requerido");
            alert.showAndWait();
        } else {
            long id = Long.valueOf(id_libro);
            Libro libro = ls.Encontrar_Libro(id);
            if (libro != null && libro.getLibros_disponibles() > 0) {
                DetallePrestamo detalle = new DetallePrestamo();
                detalle.setId_libro(libro);
                CargarDatosEnTabla(detalle);
                AgregarSpinner();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Libro no encontrado");
                alert.setHeaderText("Libro no encontrado");
                alert.setContentText("Libro no encontrado o no disponible");
                alert.showAndWait();
            }
        }
        text_BuscarLibro.setText("");
    }

    @FXML
    void buscar_usuario_action(ActionEvent event) {
        String id_usuario = text_BuscarUsuario.getText().trim();
        text_BuscarUsuario.setText("");
        Cuenta cuenta = new Cuenta();

        if (id_usuario.isBlank()) {
            System.out.println("Campo de texto vacío");
            return;
        }

        long id = Long.parseLong(id_usuario);

        try {
            cuenta = ls.Encontrar_Cuenta(id);  // Buscar la cuenta
            ls.refrescarCuenta(cuenta);

        } catch (EntityNotFoundException ex) {
            mostrarAlerta("Usuario no encontrado", "El usuario no fue encontrado en la base de datos.");
            return;
        }

        // Verificar si la cuenta existe y no está eliminada
        if (cuenta == null || cuenta.getEstado_cuenta() == EstadoCuenta.ELIMINADO) {
            mostrarAlerta("Usuario no encontrado", "El usuario fue eliminado o no existe.");
            Nombre.setText("");
            return;
        }

        // Verificar si tiene algún préstamo activo o en mora
        Optional<Prestamo> prestamoActivo = cuenta.getId_prestamo().stream()
                .filter(p -> p.getEstado_prestamo() == EstadoPrestamo.ACTIVO || p.getEstado_prestamo() == EstadoPrestamo.EN_MORA)
                .findFirst();

        if (prestamoActivo.isPresent()) {
            mostrarAlerta("Préstamo activo", "El usuario ya tiene un préstamo activo o en mora. Finalice el préstamo antes de crear uno nuevo.");
            Nombre.setText("");
        } else {
            // Preparar para crear un nuevo préstamo
            setDesactivar(false);
            fecha_inicio.setValue(LocalDate.now());
            Nombre.setText(cuenta.getApellido() + ", " + cuenta.getNombre());

            // Asociar el nuevo préstamo a la cuenta
            this.cuenta = cuenta;

        }
    }
    Cuenta cuenta = new Cuenta();

    public boolean existeLibroEnTabla(DetallePrestamo detalle, TableView<DetallePrestamo> tableView) {
        for (DetallePrestamo d : tableView.getItems()) {
            if (d.getId_libro() == detalle.getId_libro()) {
                return true;
            }
        }
        return false;
    }

    public void quitarLibro(DetallePrestamo detalleSeleccionado) {

        if (detalleSeleccionado != null) {
            tabla_detalleprestamo.getItems().remove(detalleSeleccionado);
        }
    }

    public Prestamo crearprestamo() {
        Prestamo nuevoPrestamo = new Prestamo();  // Se crea un nuevo préstamo local

        if (!(tabla_detalleprestamo.getItems().isEmpty())
                && fecha_entrega.getValue() != null
                && fecha_inicio.getValue() != null
                && !(area_descripsion.getText().trim().isEmpty())) {
            
            nuevoPrestamo.setEstado_prestamo(EstadoPrestamo.ACTIVO);
            nuevoPrestamo.setFecha_inicio_prestamo(fecha_inicio.getValue());
            nuevoPrestamo.setFecha_relativa_devolucion(fecha_entrega.getValue());
            
            nuevoPrestamo.setObservaciones(area_descripsion.getText().trim());
            nuevoPrestamo.setId_cuenta(cuenta);
            procesarDetallesPrestamo(nuevoPrestamo);
            //nuevoPrestamo.setId_detalleprestamo(procesarDetallesPrestamo());
            cuenta.getId_prestamo().add(nuevoPrestamo);  // Asociar el nuevo préstamo a la cuenta
        }

        return nuevoPrestamo;
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
                    spinner.getStyleClass().add("custom-spinner");
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

    public List<DetallePrestamo> procesarDetallesPrestamo(Prestamo prestamo) {
        List<DetallePrestamo> listaDetalles = new ArrayList<>();

        if (prestamo != null) {
            
            // Recorre los ítems de la tabla de detalles de préstamo
            for (DetallePrestamo detalle : tabla_detalleprestamo.getItems()) {
                int cantidadPrestar = detalle.getEjemplares_prestados() <= 1 ? 1 : detalle.getEjemplares_prestados();
                Libro libro = detalle.getId_libro();

                // Crear nuevo detalle de préstamo
                DetallePrestamo nuevoDetalle = new DetallePrestamo();
                nuevoDetalle.setId_libro(libro);
                nuevoDetalle.setEjemplares_prestados(cantidadPrestar); // Asocia el préstamo actual

                // Actualizar libros disponibles
                libro.setLibros_disponibles(libro.getLibros_disponibles() - cantidadPrestar);
                ls.Actualizar_Libro(libro);

                // Añadir el nuevo detalle a la lista
                listaDetalles.add(nuevoDetalle);
            }
            prestamo.setId_detalleprestamo(listaDetalles);
            
            for(DetallePrestamo detalles: listaDetalles){
                detalles.setId_prestamo(prestamo);
            }
             return listaDetalles;
        }
        
        return null;
    }

    public void limpiar() {
        text_BuscarLibro.setText("");
        text_BuscarUsuario.setText("");
        Nombre.setText("");
        area_descripsion.setText("");
        fecha_inicio.getEditor().clear();
        fecha_entrega.getEditor().clear();
        tabla_detalleprestamo.getItems().clear();
    }

    public void setDesactivar(Boolean activar) {
        tabla_detalleprestamo.setDisable(activar);
        bot_agregarlibro.setDisable(activar);
        area_descripsion.setDisable(activar);
        fecha_entrega.setDisable(activar);
        fecha_inicio.setDisable(activar);
        text_BuscarLibro.setDisable(activar);
        bot_guardar.setDisable(activar);
        bot_cancelar.setDisable(activar);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
