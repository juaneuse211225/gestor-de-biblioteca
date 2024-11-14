package org.tecno.gestor.biblioteca.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.tecno.gestor.biblioteca.enums.EstadoPrestamo;
import org.tecno.gestor.biblioteca.model.Cuenta;
import org.tecno.gestor.biblioteca.model.DetalleDevolucion;
import org.tecno.gestor.biblioteca.model.DetallePrestamo;
import org.tecno.gestor.biblioteca.model.Devolucion;
import org.tecno.gestor.biblioteca.model.Libro;
import org.tecno.gestor.biblioteca.model.Prestamo;
import org.tecno.gestor.biblioteca.service.LibraryService;

public class CrearDevolucionController {

    private LibraryService ls = new LibraryService();
    private Prestamo prestamo = new Prestamo();

    @FXML
    private TableColumn<DetallePrestamo, Integer> Column_cantidadDevuelta;

    @FXML
    private TableColumn<DetallePrestamo, Integer> Column_cantidadPrestada;

    @FXML
    private TableColumn<DetallePrestamo, Long> Column_isbn;

    @FXML
    private TableColumn<DetallePrestamo, String> Column_titulo;

    @FXML
    private TextArea Descripcion;

    @FXML
    private Button bot_buscar;

    @FXML
    private Button bot_cancelar;

    @FXML
    private Button bot_guardar;

    @FXML
    private DatePicker fecha_final;

    @FXML
    private TableView<DetallePrestamo> tablaDetalleDevolcion;

    @FXML
    private TextField text_busqueda;

    @FXML
    private Label titulo;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Scene scene = tablaDetalleDevolcion.getScene();
            if (scene != null) {
                scene.getStylesheets().add(getClass().getResource("/CSS/spinner.css").toExternalForm());
            }
        });
        text_busqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                text_busqueda.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        Column_cantidadPrestada.setCellValueFactory(new PropertyValueFactory<>("ejemplares_prestados"));
        Column_isbn.setCellValueFactory(isbn
                -> new SimpleObjectProperty<>(isbn.getValue().getId_libro().getIsbn()
                ));
        Column_titulo.setCellValueFactory(tit
                -> new SimpleObjectProperty<>(tit.getValue().getId_libro().getTitulo()
                ));
        setDesactivar(true);
    }

    @FXML
    void BuscarAction(ActionEvent event) {

        if (text_busqueda.getText().trim().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ingrese el N° de identificacion");
            alert.setHeaderText("ingresar valores en el campo de texto");
            alert.setContentText("Por favor ingresar en el campo de texto un N° de identificacion");
            alert.showAndWait();
            return;
        }

        String id = text_busqueda.getText().trim();

        tablaDetalleDevolcion.getItems().clear();

        Cuenta cuenta = ls.Encontrar_Cuenta(Long.parseLong(id));
        ls.refrescarCuenta(cuenta);
        if (cuenta == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cuenta no encontrada");
            alert.setHeaderText("El usuario no encontrado o inexistente");
            alert.setContentText("el Usaurio con N° " + id + " no existe");
            alert.showAndWait();
            return;
        }

        prestamo = mostrarDialogo(cuenta);
        if (prestamo == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Selecciona un prestamo");
            alert.setHeaderText("Selecciona un prestamo");
            alert.setContentText("Por favor seleccionar un prestamo de la lista");
            alert.showAndWait();

            return;
        }

        CargarTabla(prestamo);

        fecha_final.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        // Deshabilitar las fechas de inicio del prestamo
                        if (item.isBefore(prestamo.getFecha_inicio_prestamo())) {
                            setDisable(true);
                        }
                    }
                };
            }
        });

        fecha_final.setValue(LocalDate.now());
        setDesactivar(false);

    }

    public void setDesactivar(boolean activado) {
        tablaDetalleDevolcion.setDisable(activado);
        fecha_final.setDisable(activado);
        Descripcion.setDisable(activado);
        bot_guardar.setDisable(activado);
        bot_cancelar.setDisable(activado);

    }

    public void CargarTabla(Prestamo prestamo) {
        List<DetallePrestamo> detallesprestamo = prestamo.getId_detalleprestamo();
        if (prestamo.getEstado_prestamo() == EstadoPrestamo.INCONCLUSO) {

            for (DetallePrestamo detalleprestamo : detallesprestamo) {

                tablaDetalleDevolcion.getItems().add(detalleprestamo);

            }

            AgregarSpinner(prestamo);

        } else {
            for (DetallePrestamo detalleprestamo : detallesprestamo) {
                tablaDetalleDevolcion.getItems().add(detalleprestamo);
            }

            AgregarSpinner();
        }
    }

    @FXML
    void CancelarAction(ActionEvent event) {
        limpiar();
        setDesactivar(true);
    }

    public void limpiar() {
        text_busqueda.setText("");
        Descripcion.setText("");
        fecha_final.getEditor().clear();
        prestamo.equals(new Prestamo());
        tablaDetalleDevolcion.getItems().clear();
    }

    private Prestamo mostrarDialogo(Cuenta cuenta) {

        Dialog<Prestamo> dialog = new Dialog<>();
        dialog.setTitle("Elegir préstamo");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/CSS/dialog.css").toExternalForm());
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType seleccionarButtonType = new ButtonType("Seleccionar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelarButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(seleccionarButtonType, cancelarButtonType);

        Button seleccionarButton = (Button) dialog.getDialogPane().lookupButton(seleccionarButtonType);
        seleccionarButton.setId("boton");

        Button cancelarButton = (Button) dialog.getDialogPane().lookupButton(cancelarButtonType);
        cancelarButton.setId("boton");

        ListView<Prestamo> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        dialog.setWidth(600); // Ancho del diálogo
        dialog.setHeight(300);
        // Filtrar los préstamos basados en su estado
        if (cuenta.getId_prestamo() != null) {
            List<Prestamo> prestamosFiltrados = cuenta.getId_prestamo().stream()
                    .filter(prestamo -> prestamo.getEstado_prestamo() == EstadoPrestamo.EN_MORA
                    || prestamo.getEstado_prestamo() == EstadoPrestamo.ACTIVO
                    || prestamo.getEstado_prestamo() == EstadoPrestamo.INCONCLUSO)
                    .sorted(Comparator.comparingInt(prestamo -> {
                        if (prestamo.getEstado_prestamo() == EstadoPrestamo.EN_MORA) {
                            return 1;
                        }
                        if (prestamo.getEstado_prestamo() == EstadoPrestamo.ACTIVO) {
                            return 2;
                        }
                        if (prestamo.getEstado_prestamo() == EstadoPrestamo.INCONCLUSO) {
                            return 3;
                        }
                        return 4;
                    }))
                    .collect(Collectors.toList());

            listView.getItems().addAll(prestamosFiltrados);
        }

        // Mostrar el contenido del `toString` en el ListView
        listView.setCellFactory(param -> new ListCell<Prestamo>() {
            @Override
            protected void updateItem(Prestamo prestamo, boolean empty) {
                super.updateItem(prestamo, empty);
                if (empty || prestamo == null) {
                    setText(null);
                } else {
                    setText(prestamo.toString());  // Utiliza el método toString() de la entidad Prestamo
                }
            }
        });

        // Agregar un evento de doble clic para seleccionar el préstamo
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Prestamo seleccionado = listView.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    dialog.setResult(seleccionado);  // Establecer el préstamo seleccionado como el resultado
                    dialog.close();  // Cerrar el diálogo
                }
            }
        });

        VBox contenido = new VBox(10);
        contenido.getChildren().add(listView);
        contenido.setStyle("-fx-padding: 10px;");
        dialog.getDialogPane().setContent(contenido);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == seleccionarButtonType) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Optional<Prestamo> resultado = dialog.showAndWait();
        return resultado.orElse(null);
    }

    public void AgregarSpinner() {
        Column_cantidadDevuelta.setCellFactory(col -> new TableCell<DetallePrestamo, Integer>() {
            private final Spinner<Integer> spinner = new Spinner<>();

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    DetallePrestamo detallePrestamo = tablaDetalleDevolcion.getItems().get(getIndex());

                    SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, detallePrestamo.getEjemplares_prestados());

                    spinner.setValueFactory(valueFactory);
                    spinner.getValueFactory().setValue(item != null ? item : 0);
                    spinner.getStyleClass().add("custom-spinner");
                    setGraphic(spinner);

                    spinner.valueProperty().addListener((obs, valorViejo, valorNuevo) -> {

                        detallePrestamo.setEjemplares_devolver(valorNuevo);
                    });
                }
            }
        });
    }

    public void AgregarSpinner(Prestamo prestamo) {
        Column_cantidadDevuelta.setCellFactory(col -> new TableCell<DetallePrestamo, Integer>() {
            private final Spinner<Integer> spinner = new Spinner<>();

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Map<Long, Integer> mapa = ls.ObtenerCantidadDevueltaPorLibro(prestamo.getId_prestamo());

                    DetallePrestamo detallePrestamo = tablaDetalleDevolcion.getItems().get(getIndex());

                    Long idLibro = detallePrestamo.getId_libro().getIsbn();
                    int cantidadPrestada = detallePrestamo.getEjemplares_prestados();
                    int cantidadDevuelta = mapa.getOrDefault(idLibro, 0);

                    int cantidadFaltante = cantidadPrestada - cantidadDevuelta;
                    if (cantidadFaltante < 0) {
                        cantidadFaltante = 0;
                    }

                    SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, cantidadFaltante);
                    spinner.setValueFactory(valueFactory);
                    spinner.getValueFactory().setValue(item != null ? item : 0);
                    spinner.getStyleClass().add("custom-spinner");
                    setGraphic(spinner);

                    spinner.valueProperty().addListener((obs, valorViejo, valorNuevo) -> {
                        detallePrestamo.setEjemplares_devolver(valorNuevo);
                    });
                }
            }
        });
    }

    @FXML
    void GuardarAction(ActionEvent event) {
        if (fecha_final.getValue() == null || Descripcion.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ingresa los campos");
            alert.setHeaderText("Ingresa los campos");
            alert.setContentText("Por favor ingresar valores validos a los campos");
            alert.showAndWait();
            return;
        }

        Devolucion devolucion = new Devolucion(fecha_final.getValue(), Descripcion.getText().trim(), prestamo);
        List<DetalleDevolucion> devoluciones = generarDetallesDevolucion(devolucion);

        for (DetalleDevolucion detalle : devoluciones) {
            Libro libro = detalle.getId_libro();
            ls.refrescarLibro(libro);
            libro.setLibros_disponibles(libro.getLibros_disponibles() + detalle.getEjemplares_devueltos());
            ls.Actualizar_Libro(libro);
        }

        devolucion.setDetalle_devolucion(devoluciones);
        ls.Crear_Devolucion(devolucion);

        prestamo.getDevolucion().add(devolucion);

        ls.Actualizar_Prestamo(prestamo);

        Cuenta cuenta = prestamo.getId_cuenta();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Devolucion exitosa");
        alert.setHeaderText("Devolucion exitosa");
        alert.setContentText("La devolucion fue guardada con exito");
        alert.showAndWait();
        limpiar();
        setDesactivar(true);
    }

    public List<DetalleDevolucion> generarDetallesDevolucion(Devolucion devolucion) {
        List<DetalleDevolucion> detalles = new ArrayList<>();
        boolean devolucionCompleta = true;

        for (DetallePrestamo detallePrestamo : tablaDetalleDevolcion.getItems()) {
            DetalleDevolucion detalleDevolucion = new DetalleDevolucion();
            int cantidadPrestada = detallePrestamo.getEjemplares_prestados();
            long idLibro = detallePrestamo.getId_libro().getIsbn();

            int cantidadDevueltaPreviamente = 0;

            for (Devolucion devolucionPrevia : prestamo.getDevolucion()) {
                for (DetalleDevolucion detalleDevolucionPrevio : devolucionPrevia.getDetalle_devolucion()) {
                    if (detalleDevolucionPrevio.getId_libro().getIsbn() == idLibro) {
                        cantidadDevueltaPreviamente += detalleDevolucionPrevio.getEjemplares_devueltos();
                    }
                }
            }

            int cantidadDevueltaAhora = detallePrestamo.getEjemplares_devolver();
            int totalDevuelto = cantidadDevueltaPreviamente + cantidadDevueltaAhora;

            detalleDevolucion.setEjemplares_devueltos(cantidadDevueltaAhora);
            detalleDevolucion.setId_libro(detallePrestamo.getId_libro());
            detalleDevolucion.setId_devolucion(devolucion);

            if (totalDevuelto < cantidadPrestada) {
                devolucionCompleta = false;
            }

            detalles.add(detalleDevolucion);
        }

        if (devolucionCompleta) {
            prestamo.setEstado_prestamo(EstadoPrestamo.FINALIZADO);
        } else {
            prestamo.setEstado_prestamo(EstadoPrestamo.INCONCLUSO);
        }

        return detalles;
    }

}
