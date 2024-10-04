package com.tecno.biblioteca.controller;

import com.tecno.biblioteca.enums.EstadoPrestamo;
import com.tecno.biblioteca.model.Cuenta;
import com.tecno.biblioteca.model.DetalleDevolucion;
import com.tecno.biblioteca.model.DetallePrestamo;
import com.tecno.biblioteca.model.Devolucion;
import com.tecno.biblioteca.model.Libro;
import com.tecno.biblioteca.model.Prestamo;
import com.tecno.biblioteca.service.LibraryService;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CrearDevolucionController {

    private LibraryService ls = new LibraryService();
    private Prestamo prestamo;

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
        FXCollections.observableArrayList();
        Column_cantidadPrestada.setCellValueFactory(new PropertyValueFactory<>("ejemplares_prestados"));
        Column_isbn.setCellValueFactory(isbn
                -> new SimpleObjectProperty<>(isbn.getValue().getId_libro().getIsbn()
                ));
        Column_titulo.setCellValueFactory(tit
                -> new SimpleObjectProperty<>(tit.getValue().getId_libro().getTitulo()
                ));
        AgregarSpinner();
    }

    @FXML
    void BuscarAction(ActionEvent event) {

        // Verificar que el texto no esté vacío
        String inputText = text_busqueda.getText().trim();
        if (inputText.isEmpty()) {
            System.err.println("El campo de búsqueda está vacío. Ingresa un ID de usuario.");
            return;
        }

        long id = Long.parseLong(inputText); // Convertir a long

        // Verificar si el usuario existe
        Cuenta cuenta = ls.Encontrar_Cuenta(id);
        if (cuenta == null) {

            System.err.println("El usuario no existe");
            return;
        } else {
            CargarTabla(id);
            tablaDetalleDevolcion.setDisable(false);
            Descripcion.setDisable(false);
            fecha_final.setDisable(false);
        }

    }

    @FXML
    void CancelarAction(ActionEvent event) {

    }

    @FXML
    void GuardarAction(ActionEvent event) {
        Devolucion devolucion = new Devolucion(fecha_final.getValue(), Descripcion.getText().trim(), prestamo);
        List<DetalleDevolucion> devoluciones = obtenerDetallesDevolucion(devolucion);

        for (DetalleDevolucion detalle : devoluciones) {
            Libro libro = detalle.getId_libro();
            libro.setLibros_disponibles(libro.getLibros_disponibles() + detalle.getEjemplares_devueltos());
            ls.Actualizar_Libro(libro);
        }

        devolucion.setDetalle_devolucion(devoluciones);
        ls.Crear_Devolucion(devolucion);

        prestamo.setId_devolucion(devolucion);

        ls.Actualizar_Prestamo(prestamo);

        // Desasociar el préstamo de la cuenta del usuario
        Cuenta cuenta = prestamo.getId_cuenta();
        ls.Actualizar_Cuenta(cuenta); // Actualizar la cuenta en la base de datos

        System.out.println("Devolución creada, préstamo actualizado y desasociado de la cuenta.");
    }

    public List<DetalleDevolucion> obtenerDetallesDevolucion(Devolucion devolucion) {
        List<DetalleDevolucion> detalles = new ArrayList<>();
        boolean devolucionCompleta = true;

        for (DetallePrestamo detallePrestamo : tablaDetalleDevolcion.getItems()) {
            DetalleDevolucion detalleDevolucion = new DetalleDevolucion();

            // Obtener la cantidad devuelta desde el Spinner (que actualiza DetallePrestamo)
            int cantidadDevuelta = detallePrestamo.getEjemplares_prestados(); // Aquí se debe reflejar lo que el usuario seleccionó en el spinner

            detalleDevolucion.setEjemplares_devueltos(cantidadDevuelta);
            detalleDevolucion.setId_libro(detallePrestamo.getId_libro());
            detalleDevolucion.setId_devolucion(devolucion);

            // Verificar si la cantidad devuelta es menor a la prestada
            if (cantidadDevuelta < detallePrestamo.getEjemplares_prestados()) {
                devolucionCompleta = false;
            }

            // Establecer el estado del préstamo según si es completo o no
            if (devolucionCompleta) {
                prestamo.setEstado_prestamo(EstadoPrestamo.FINALIZADO);
            } else {
                prestamo.setEstado_prestamo(EstadoPrestamo.INCONCLUSO);
            }

            detalles.add(detalleDevolucion);
        }
        return detalles;
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

                    // Crear el Spinner para las devoluciones, con el límite basado en los ejemplares prestados
                    SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, detallePrestamo.getEjemplares_prestados());

                    spinner.setValueFactory(valueFactory);
                    spinner.getValueFactory().setValue(item != null ? item : 0);

                    setGraphic(spinner);

                    // Listener para actualizar el valor devuelto en el DetallePrestamo
                    spinner.valueProperty().addListener((obs, valorViejo, valorNuevo) -> {
                        // Actualizar el campo en la clase DetallePrestamo (por ahora usado para reflejar los devueltos)
                        detallePrestamo.setEjemplares_prestados(valorNuevo);
                    });
                }
            }
        });
    }

    public void CargarTabla(long id) {
        List<DetallePrestamo> lista = obtenerDetallePrestamo(id);

        if (lista == null) {
            return;
        } else {
            for (DetallePrestamo prestamo : lista) {
                tablaDetalleDevolcion.getItems().add(prestamo);
                AgregarSpinner();
            }

        }
    }

    public List<DetallePrestamo> obtenerDetallePrestamo(Long id) {
        prestamo = ls.Encontrar_prestamo_por_Usuario(id);
        if (prestamo != null) {
            List<DetallePrestamo> lista = prestamo.getId_detalleprestamo();
            return lista;
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Prestamo no encontrado");
            alert.setHeaderText("No tiene prestamos activos");
            alert.setContentText("el usuario con identificador: " + id + " no contiene ningun prestamo activo");
            alert.showAndWait();
            return null;
        }
    }
}
