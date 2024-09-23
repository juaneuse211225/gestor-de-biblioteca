package com.tecno.biblioteca.controller;

import com.tecno.biblioteca.model.DetallePrestamo;
import com.tecno.biblioteca.model.Libro;
import com.tecno.biblioteca.model.Prestamo;
import com.tecno.biblioteca.service.LibraryService;
import java.util.List;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CrearDevolucionController {

    private ObservableList<DetallePrestamo> lista;
    private LibraryService ls = new LibraryService();

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
        lista = FXCollections.observableArrayList();
        Column_cantidadPrestada.setCellValueFactory(new PropertyValueFactory<>("ejemplares_prestados"));
        Column_isbn.setCellValueFactory(isbn
                -> new SimpleObjectProperty<>(isbn.getValue().getId_libro().getIsbn()
                ));
        Column_titulo.setCellValueFactory(tit
                -> new SimpleObjectProperty<>(tit.getValue().getId_libro().getTitulo()
                ));
        AgregarSpinner();
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
                    DetallePrestamo detalle = getTableView().getItems().get(getIndex());

                    SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, detalle.getEjemplares_prestados(), 0, 1);
                    spinner.setValueFactory(valueFactory);
                    setGraphic(spinner);

                    spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                        detalle.setEjemplares_prestados(newValue);
                    });
                }
            }
        });
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
        if (!ls.exite_cuenta(id)) {
            System.err.println("El usuario no existe.");
            return;
        }
        
        CargarTabla(id);
        tablaDetalleDevolcion.setDisable(false);
        Descripcion.setDisable(false);
        fecha_final.setDisable(false);
    
}

    @FXML
    void CancelarAction(ActionEvent event) {

    }

    @FXML
    void GuardarAction(ActionEvent event) {
        
    }
    
    public void CargarTabla(long id){
        
        lista.clear();
        lista.addAll( obtenerDetallePrestamo(id));
        tablaDetalleDevolcion.setItems(lista);
        
    }
    
    public List<DetallePrestamo> obtenerDetallePrestamo(Long id){
       Prestamo prestamo = ls.Encontrar_prestamo_por_Usuario(id);
        List<DetallePrestamo> lista = prestamo.getId_detalleprestamo();
        return lista;
    }
}
