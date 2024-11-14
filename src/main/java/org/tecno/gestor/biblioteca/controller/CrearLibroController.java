package org.tecno.gestor.biblioteca.controller;

import org.tecno.gestor.biblioteca.model.Categoria;
import org.tecno.gestor.biblioteca.model.Libro;
import org.tecno.gestor.biblioteca.service.LibraryService;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CrearLibroController {

    @FXML
    private Pane CategoriaExistente;

    @FXML
    private ComboBox<String> Categorias;

    @FXML
    private Pane NuevaCategoria;

    @FXML
    private Button bot_añadir;

    @FXML
    private Button bot_cancelar;

    @FXML
    private ToggleGroup group1;

    @FXML
    private RadioButton radio_cat;

    @FXML
    private RadioButton radio_new;

    @FXML
    private Spinner<String> spin_estanteria;

    @FXML
    private Spinner<String> spin_pasillo;

    @FXML
    private Spinner<Integer> spin_seccion;

    @FXML
    private TextField text_autor;

    @FXML
    private TextField text_ejemplares_total;

    @FXML
    private TextField text_isbn;

    @FXML
    private TextField text_nuevacategoria;

    @FXML
    private TextField text_titulo;

    @FXML
    private Label titulo;

    LibraryService ls = new LibraryService();

    @FXML
    public void initialize() {

        SpinnerValueFactory<String> estanteriaFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(
                FXCollections.observableArrayList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L")
        );
        spin_estanteria.setValueFactory(estanteriaFactory);

        SpinnerValueFactory<String> pasilloFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(
                FXCollections.observableArrayList("I", "II", "III", "IV", "V", "VI")
        );
        spin_pasillo.setValueFactory(pasilloFactory);

        SpinnerValueFactory<Integer> seccionFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 1);
        spin_seccion.setValueFactory(seccionFactory);

        text_isbn.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                text_isbn.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        text_ejemplares_total.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                text_ejemplares_total.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        CargarCategorias();

    }

    public void CargarCategorias() {
        List<Categoria> cat = ls.Encontrar_Categorias();
        Categorias.getItems().clear();
        for (Categoria c1 : cat) {
            Categorias.getItems().addAll(c1.getCategoria());
        }
    }

    @FXML
    void AncelarAction(ActionEvent event) {
        Stage stage = (Stage) bot_cancelar.getScene().getWindow();
        stage.close();
    }

    public String obtenerMensajeUbicacion() {
        String pasillo = spin_pasillo.getValue();
        String estanteria = spin_estanteria.getValue();
        Integer seccion = spin_seccion.getValue();

        return "pasillo: " + pasillo + " - estanteria: " + estanteria + " - seccion: " + seccion;
    }

    public void establecerUbicacionDesdeTexto(String ubicacion) {

        String[] partes = ubicacion.split(" - ");

        // Extraer el valor de cada parte
        String pasilloTexto = partes[0].replace("pasillo: ", "").trim();
        String estanteriaTexto = partes[1].replace("estanteria: ", "").trim();
        String seccionTexto = partes[2].replace("seccion: ", "").trim();

        Integer seccionValor = Integer.parseInt(seccionTexto);

        spin_pasillo.getValueFactory().setValue(pasilloTexto);
        spin_estanteria.getValueFactory().setValue(estanteriaTexto);
        spin_seccion.getValueFactory().setValue(seccionValor);
    }

    @FXML
    void AñadirAction(ActionEvent event) {
        // Obtener los valores de los campos
        String tituloLibro = text_titulo.getText().trim();
        String autor = text_autor.getText().trim();
        String ejemplaresStr = text_ejemplares_total.getText().trim();
        String isbnStr = text_isbn.getText().trim();
        String ubicacion = obtenerMensajeUbicacion();

        // Validar campos vacíos
        if (tituloLibro.isEmpty() || autor.isEmpty() || ejemplaresStr.isEmpty() || isbnStr.isEmpty() || ubicacion.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos deben estar llenos.", Alert.AlertType.WARNING);
            return;
        }

        int ejemplares;
        long isbn;

        try {
            ejemplares = Integer.parseInt(ejemplaresStr);
            isbn = Long.parseLong(isbnStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El ISBN y los ejemplares deben ser números válidos.", Alert.AlertType.WARNING);
            return;
        }

        // Obtener la categoría seleccionada o crear una nueva
        Categoria categoria = obtenerCategoria();
        if (categoria == null) {
            return; // El método `obtenerCategoria` ya muestra alertas si hay errores
        }

        // Determinar la acción en función del texto del Label `titulo`
        if (titulo.getText().equals("Registrar Libro")) {
            // Validar existencia del libro antes de añadirlo
            if (ls.exite_libro(isbn)) {
                mostrarAlerta("Libro existente", "El libro con ISBN " + isbn + " ya existe.", Alert.AlertType.WARNING);
                return;
            }

            // Crear nuevo libro
            Libro nuevoLibro = new Libro(isbn, tituloLibro, autor, categoria, ejemplares, ubicacion);
            ls.Crear_Libro(nuevoLibro);
            mostrarAlerta("Éxito", "Libro añadido exitosamente.", Alert.AlertType.INFORMATION);

        } else if (titulo.getText().equals("Editar Libro")) {
            // Aquí obtendrás el libro actual de la fuente de datos según el ISBN (ya cargado en `setLibro`)
            Libro libroExistente = ls.Encontrar_Libro(isbn);  // O recupera el libro de donde se almacenó al cargar
            if (libroExistente != null) {
                actualizarLibro(libroExistente, tituloLibro, autor, categoria, ejemplares, ubicacion);
                mostrarAlerta("Éxito", "Libro actualizado exitosamente.", Alert.AlertType.INFORMATION);
                Stage stage = getStage();
                stage.close(); // Cierra la ventana después de la edición
            } else {
                mostrarAlerta("Error", "No se encontró el libro a editar.", Alert.AlertType.ERROR);
            }
        }

        // Recargar las categorías y limpiar los campos
        CargarCategorias();
        Limpiar();
    }

    private Categoria obtenerCategoria() {
        if (group1.getSelectedToggle().equals(radio_new)) {
            String nuevaCategoria = text_nuevacategoria.getText().trim().toUpperCase();

            if (nuevaCategoria.isBlank()) {
                mostrarAlerta("Error", "Debe introducir una nueva categoría.", Alert.AlertType.ERROR);
                return null;
            }

            Categoria categoriaExistente = ls.Encontrar_Categoria_Nombre(nuevaCategoria);
            if (categoriaExistente == null) {
                // Crear nueva categoría si no existe
                Categoria nuevaCat = new Categoria(nuevaCategoria);
                ls.Crear_Categoria(nuevaCat);
                return nuevaCat;
            }
            return categoriaExistente;
        } else if (group1.getSelectedToggle().equals(radio_cat)) {
            String categoriaSeleccionada = Categorias.getValue();

            if (categoriaSeleccionada == null || categoriaSeleccionada.isEmpty()) {
                mostrarAlerta("Error", "Debe seleccionar una categoría existente.", Alert.AlertType.ERROR);
                return null;
            }

            return ls.Encontrar_Categoria_Nombre(categoriaSeleccionada);
        }

        return null;
    }

    private void actualizarLibro(Libro libro, String titulo, String autor, Categoria categoria, int ejemplares, String ubicacion) {
        // Actualizar campos del libro
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setCategoria(categoria);
        libro.setUbicacion(ubicacion);

        // Actualizar cantidad de ejemplares
        int ejemplaresActuales = libro.getLibros_total();
        int diferencia = ejemplares - ejemplaresActuales;
        libro.setLibros_total(ejemplares);
        libro.setLibros_disponibles(libro.getLibros_disponibles() + diferencia);

        // Actualizar en el sistema
        ls.Actualizar_Libro(libro);
    }

    public Stage getStage() {
        Stage stage = (Stage) titulo.getScene().getWindow();
        return stage;
    }

    public void Limpiar() {
        text_autor.setText("");
        text_ejemplares_total.setText("");
        text_isbn.setText("");
        text_nuevacategoria.setText("");
        text_titulo.setText("");
        restablecerSpinners();
        radio_cat.setSelected(true);
        Categorias.setValue("");

    }

    public void restablecerSpinners() {
        spin_pasillo.getValueFactory().setValue("I");

        spin_estanteria.getValueFactory().setValue("A");

        spin_seccion.getValueFactory().setValue(1);
    }

// Método para mostrar una alerta
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    void CategoriaAction(ActionEvent event) {
        CategoriaExistente.setDisable(false);
        NuevaCategoria.setDisable(true);
    }

    @FXML
    void NuevaCategoriaAction(ActionEvent event) {
        CategoriaExistente.setDisable(true);
        NuevaCategoria.setDisable(false);
    }

    public void setTitulo_text(String añadir_un_nuevo_Libro) {

        titulo.setText(añadir_un_nuevo_Libro);

    }

    public void setLibro(Libro libro) {
        // Establecer los valores del libro en los campos de texto
        text_autor.setText(libro.getAutor());
        text_ejemplares_total.setText(String.valueOf(libro.getLibros_total()));
        text_isbn.setText(String.valueOf(libro.getIsbn()));
        text_titulo.setText(libro.getTitulo());
        establecerUbicacionDesdeTexto(libro.getUbicacion());

        // Seleccionar la categoría correspondiente en el ComboBox
        Categorias.setValue(libro.getCategoria().getCategoria());

        // Deshabilitar el campo ISBN para que no pueda ser modificado
        text_isbn.setDisable(true);
    }

}
