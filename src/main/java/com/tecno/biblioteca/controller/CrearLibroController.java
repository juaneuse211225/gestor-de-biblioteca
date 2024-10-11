package com.tecno.biblioteca.controller;

import com.tecno.biblioteca.model.Categoria;
import com.tecno.biblioteca.model.Libro;
import com.tecno.biblioteca.service.LibraryService;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CrearLibroController {

    private Libro libro;
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
    private TextArea text_ubicacion;

    @FXML
    private Label titulo;

    private String titulo1;
    LibraryService ls = new LibraryService();

    @FXML
    public void initialize() {
        
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

    @FXML
    void AñadirAction(ActionEvent event) {
        // Obtener los valores de los campos
        String a2 = text_autor.getText().trim();
        String ejemplaresStr = text_ejemplares_total.getText().trim();
        String idStr = text_isbn.getText().trim();
        String ubi = text_ubicacion.getText().trim();
        String a1 = text_titulo.getText().trim();

        // Comprobar si alguno de los campos está vacío
        if (a1.isEmpty() || a2.isEmpty() || ejemplaresStr.isEmpty() || idStr.isEmpty() || ubi.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos deben estar llenos.");
            return;
        }

        try {
            // Parsear los valores numéricos
            int ejemplares = Integer.parseInt(ejemplaresStr);
            long id = Long.parseLong(idStr);

            // Comprobar qué radio button está seleccionado
            if (group1.getSelectedToggle().equals(radio_new)) {
                String valor = text_nuevacategoria.getText().trim().toUpperCase();

                if (valor.isEmpty()) {
                    mostrarAlerta("Error", "Debe introducir una nueva categoría.");
                    return;
                }

                // Comprobar si la categoría existe
                if (ls.Encontrar_Categoria_Nombre(valor) == null) {
                    Categoria categoria = new Categoria(valor);
                    ls.Crear_Categoria(categoria);
                    ls.Crear_Libro(new Libro(id, a1, a2,categoria, ejemplares, ubi));
                } else {
                    mostrarAlerta("Error", "La categoría ya existe.");
                }

            } else if (group1.getSelectedToggle().equals(radio_cat)) {
                String valor = Categorias.getValue();

                if (valor == null || valor.isEmpty()) {
                    mostrarAlerta("Error", "Debe seleccionar una categoría existente.");
                    return;
                }

                Categoria cat = ls.Encontrar_Categoria_Nombre(valor);
                ls.Crear_Libro(new Libro(id, a1, a2, cat, ejemplares, ubi));
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Los campos de ISBN y ejemplares deben ser numéricos.");
        }
        CargarCategorias();
        Limpiar();
    }

    public void Limpiar() {
        text_autor.setText("");
        text_ejemplares_total.setText("");
        text_isbn.setText("");
        text_nuevacategoria.setText("");
        text_titulo.setText("");
        text_ubicacion.setText("");
        Categorias.setValue("");
    }

// Método para mostrar una alerta
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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
        this.titulo1 = añadir_un_nuevo_Libro;
        if (titulo != null) {
            titulo.setText(titulo1);
        }
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
        text_autor.setText(libro.getAutor());
        text_ejemplares_total.setText(String.valueOf(libro.getLibros_total()));
        text_isbn.setText(String.valueOf(libro.getIsbn()));
        text_nuevacategoria.setText("");
        text_titulo.setText(libro.getTitulo());
        text_ubicacion.setText(libro.getUbicacion());
        Categorias.setValue(libro.getCategoria().getCategoria());
        text_isbn.setDisable(true);
    }

}
