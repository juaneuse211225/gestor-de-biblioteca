package org.tecno.gestor.biblioteca.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.tecno.gestor.biblioteca.enums.EstadoPrestamo;
import org.tecno.gestor.biblioteca.model.Cuenta;
import org.tecno.gestor.biblioteca.model.DetallePrestamo;
import org.tecno.gestor.biblioteca.model.Devolucion;
import org.tecno.gestor.biblioteca.model.Prestamo;
import org.tecno.gestor.biblioteca.service.LibraryService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import org.tecno.gestor.biblioteca.model.Libro;

public class Informes {

    LibraryService ls = new LibraryService();
    Map<Month, List<Prestamo>> prestamosPorMes;
    ObservableList<Prestamo> listaPrestamos = FXCollections.observableArrayList();

    @FXML
    private Button Bot_Consulta;

    @FXML
    private Button Bot_reporte;

    @FXML
    private Button Bot_Consulta1;

    @FXML
    private TableColumn<Libro, String> Column_autor;

    @FXML
    private TableColumn<Libro, String> Column_categoria;

    @FXML
    private TableColumn<Prestamo, Integer> Column_diasenmora;

    @FXML
    private TableColumn<Libro, String> Column_disponibles;

    @FXML
    private TableColumn<Libro, String> Column_estado;

    @FXML
    private TableColumn<Libro, Long> Column_isbn;

    @FXML
    private TableColumn<Prestamo, Long> Column_num_prestamo;

    @FXML
    private TableColumn<Libro, String> Column_titutlo;

    @FXML
    private TableColumn<Libro, String> Column_total;

    @FXML
    private TableColumn<Libro, String> Column_ubicacion;

    @FXML
    private TableColumn<Prestamo, String> Columna_correo;

    @FXML
    private TableColumn<Prestamo, String> Columna_fechadevolver;

    @FXML
    private TableColumn<Prestamo, String> Columna_fechaprestada;

    @FXML
    private TableColumn<Prestamo, String> Columna_nombreusuario;

    @FXML
    private TableColumn<Prestamo, String> Columna_telefono;

    @FXML
    private TableColumn<Prestamo, Cuenta> Columna_usuario;

    @FXML
    private BarChart<String, Number> Grafico;

    @FXML
    private Tab Informe_pendientes;

    @FXML
    private Tab Informe_prestamos;

    @FXML
    private Pane Panel_pendientes;

    @FXML
    private Pane Panel_prestamos;

    @FXML
    private TableView<Prestamo> Table_informe;

    @FXML
    private Tab _informe_inventario;

    @FXML
    private Button buscar;

    @FXML
    private CategoryAxis ejeX;

    @FXML
    private NumberAxis ejeY;

    @FXML
    private DatePicker fecha_final;

    @FXML
    private DatePicker fecha_inicial;

    @FXML
    private Button guardar;

    @FXML
    private Pane panel_inventario;

    @FXML
    private Button reporte_inventario;

    @FXML
    private TableView<Libro> tabla_inventario;

    @FXML
    public void initialize() {
        Column_num_prestamo.setCellValueFactory(new PropertyValueFactory<>("id_prestamo"));
        Columna_fechaprestada.setCellValueFactory(new PropertyValueFactory<>("fecha_inicio_prestamo"));
        Columna_fechadevolver.setCellValueFactory(new PropertyValueFactory<>("fecha_relativa_devolucion"));
        Column_diasenmora.setCellValueFactory(prestamo -> {
            LocalDate fechaDevolucionRelativa = prestamo.getValue().getFecha_relativa_devolucion();
            LocalDate fechaActual = LocalDate.now();

            long diasEnMora = ChronoUnit.DAYS.between(fechaDevolucionRelativa, fechaActual);

            int resultado = (int) (diasEnMora > 0 ? diasEnMora : 0);
            System.out.println(diasEnMora + ":" + resultado);

            return new SimpleObjectProperty<>(resultado);
        });

        Columna_usuario.setCellValueFactory(prestamo
                -> new SimpleObjectProperty(prestamo.getValue().getDevolucion())); // Cambiar según lo que deseas mostrar

        // Configurar subcolumnas para acceder a propiedades de Cuenta
        Columna_correo.setCellValueFactory(cuenta
                -> new SimpleStringProperty(cuenta.getValue().getId_cuenta().getCorreo()));

        Columna_nombreusuario.setCellValueFactory(cuenta
                -> new SimpleStringProperty(cuenta.getValue().getId_cuenta().getNombre()));

        Columna_telefono.setCellValueFactory(cuenta
                -> new SimpleStringProperty(cuenta.getValue().getId_cuenta().getTelefono()));

        Column_isbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        Column_titutlo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        Column_autor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        Column_categoria.setCellValueFactory(celdaNombreCategoria
                //se obtiene el valor del getter Categoria de la clase categoria atravez de Libro 
                -> new SimpleObjectProperty<>(celdaNombreCategoria.getValue().getCategoria().getCategoria()
                ));
        Column_disponibles.setCellValueFactory(new PropertyValueFactory<>("libros_disponibles"));

        Column_total.setCellValueFactory(new PropertyValueFactory<>("libros_total"));
        Column_ubicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        Column_estado.setCellValueFactory(new PropertyValueFactory<>("estado_libro"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Cantidad de Préstamos");
        yAxis.setTickUnit(1);  // Establece la unidad de los ticks en 1 para mostrar solo números enteros.
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return String.format("%.0f", number);  // Formatea el número como entero.
            }

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        });

    }

    @FXML
    void BuscarAction(ActionEvent event) {
        XYChart.Series<String, Number> prestamosDevueltos = new XYChart.Series<>();

        XYChart.Series<String, Number> prestamosHechos = new XYChart.Series<>();
        Grafico.getData().clear();
        prestamosDevueltos.setName("Prestamos devueltos");
        prestamosHechos.setName("Prestamos hechos");

        LocalDate fechaInicial = fecha_inicial.getValue();
        LocalDate fechaFinal = fecha_final.getValue();

        if (fechaInicial != null && fechaFinal != null) {
            prestamosPorMes = ls.obtenerPrestamosEntreFechas(fechaInicial, fechaFinal);

            // Procesar los resultados
            for (Map.Entry<Month, List<Prestamo>> entry : prestamosPorMes.entrySet()) {
                Month mes = entry.getKey();
                List<Prestamo> prestamos = entry.getValue();

                int contadorTotal = 0; // Contador para total de préstamos
                int prestamosFinalizados = 0; // Contador para préstamos finalizados

                // Contar préstamos
                for (Prestamo p : prestamos) {
                    contadorTotal++; // Aumentar contador total
                    // Comprobar si el préstamo está finalizado
                    if (p.getEstado_prestamo() == EstadoPrestamo.FINALIZADO) { // Asegúrate de que getEstado() devuelva el estado correcto
                        prestamosFinalizados++; // Aumentar contador de préstamos finalizados
                    }
                }

                String nombreMes = mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

                prestamosHechos.getData().add(new XYChart.Data<>(nombreMes, contadorTotal));
                prestamosDevueltos.getData().add(new XYChart.Data<>(nombreMes, prestamosFinalizados));
            }
            Grafico.getData().addAll(prestamosHechos, prestamosDevueltos);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ingresa las fechas");
            alert.setHeaderText("ingresa el rango de fechas");
            alert.setContentText("Por favor ingresar las fechas en los campos");
        }
    }

    @FXML
    void GuardarAction(ActionEvent event) throws FileNotFoundException {
        BuscarAction(event);
        String rutaArchivo;
        File archivoSeleccionado = seleccionarDirectorio("Guardar informe de Prestamos", "Informe de prestamos.pdf", getStage(event));

        if (archivoSeleccionado != null) {
            rutaArchivo = archivoSeleccionado.getAbsolutePath();

            if (!rutaArchivo.endsWith(".pdf")) {
                rutaArchivo += ".pdf";
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al generar el reporte");
            alert.setContentText("Error al guardar o al generar el reporte");
            alert.showAndWait();
            return;
        }
        System.out.println(rutaArchivo);
        GenerarPDF(archivoSeleccionado);

    }

    @FXML
    void Reporte_Action(ActionEvent event) {
        // Paso 1: Mostrar FileChooser para que el usuario elija dónde guardar el PDF

        File file = seleccionarDirectorio("Guardar informe de mora", "Informe de En mora.pdf", getStage(event));
        if (file != null) {
            generarPDFMora(file);
        }
    }

    public Stage getStage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        return stage;
    }

    public File seleccionarDirectorio(String titulo, String nombre_archivo, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        File homeDir = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(homeDir);
        fileChooser.setTitle(titulo);
        fileChooser.setInitialFileName(nombre_archivo);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(stage);
        return file;
    }

    @FXML
    void ConsultaAction(ActionEvent event) {
        cargarPrestamos();
    }
    ObservableList<Libro> listalibros = FXCollections.observableArrayList();

    @FXML
    void reporte_inventario_action(ActionEvent event) {
        File file = seleccionarDirectorio("Guardar informe de inventario", "Informe de inventario.pdf", getStage(event));
        if (file != null) {
            GenerarPDFInventario(file);
        }
    }

    @FXML
    void ConsultainventarioAction(ActionEvent event) {
        List<Libro> libros = ls.Encontrar_Libros();
        if (libros.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);

            alert.setHeaderText("No encontrados");
            alert.setContentText("No se encontraron libros");
            alert.showAndWait();

        } else {

            listalibros.clear();
            listalibros.addAll(libros);
            tabla_inventario.setItems(listalibros);
        }
    }

    private void cargarPrestamos() {
        // Aquí obtienes la lista de préstamos en mora o lo que quieras cargar

        List<Prestamo> prestamos = ls.EncontrarPrestamosEnmora();  // Supongamos que este método obtiene los préstamos
        if (prestamos.isEmpty()) {

            listaPrestamos.clear();
            listaPrestamos.addAll(prestamos);
            Table_informe.setItems(listaPrestamos);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setHeaderText("No encontrados");
            alert.setContentText("No se encontraron prestamos en mora");
            alert.showAndWait();

        } else {
            listaPrestamos.clear();
            listaPrestamos.addAll(prestamos);
            Table_informe.setItems(listaPrestamos);
        }
    }

    public void GenerarPDF(File file) {
        Document documento = new Document(PageSize.LETTER);

        try {
            if (prestamosPorMes == null || prestamosPorMes.isEmpty()) {
                System.out.println("No hay préstamos para procesar.");
                return;
            }

            try {
                PdfWriter.getInstance(documento, new FileOutputStream(file));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Informes.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error al crear el archivo PDF.");
                return; // Salir en caso de error
            }

            documento.open();
            boolean contenidoAgregado = false; // Control para verificar si se añade contenido
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.DARK_GRAY);
            Paragraph titulo = new Paragraph("Informe de Prestamos", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            documento.add(new Paragraph("\n"));
            for (Map.Entry<Month, List<Prestamo>> entry1 : prestamosPorMes.entrySet()) {
                Month mes = entry1.getKey();
                List<Prestamo> prestamos = entry1.getValue();

                if (prestamos == null || prestamos.isEmpty()) {
                    System.out.println("No hay préstamos para el mes de: " + mes);
                    continue;
                }

                // Imprimir el mes que se está procesando
                String nombreMes = mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                System.out.println("Procesando préstamos para el mes de: " + nombreMes);

                Font fonthead = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.GRAY);
                Paragraph head = new Paragraph(nombreMes, fonthead);
                head.setAlignment(Element.ALIGN_LEFT);

                documento.add(head);
                documento.add(new Paragraph("\n"));
                documento.add(new Paragraph("\n"));

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2f, 2.5f, 4f, 2.5f, 2.5f});

                // Añadir cabeceras
                table.addCell("Fecha Inicio");
                table.addCell("Fecha Devolución");
                table.addCell("Libros prestados");

                table.addCell("Usuario");
                table.addCell("Estado");

                for (Prestamo p : prestamos) {
                    System.out.println("Procesando préstamo de fecha: " + p.getFecha_inicio_prestamo());

                    int ejemplares = 0;
                    for (DetallePrestamo detalles : p.getId_detalleprestamo()) {
                        ejemplares += detalles.getEjemplares_prestados();
                    }
                    String libros = "";
                    for (DetallePrestamo detalles : p.getId_detalleprestamo()) {
                        Libro libro = detalles.getId_libro();
                        libros += libro.getTitulo() + ", (" + detalles.getEjemplares_prestados() + "); \n";
                    }

                    LocalDate fechaMasReciente = p.getDevolucion().stream()
                            .map(Devolucion::getFecha_devolucion_real)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    String fechareciente = fechaMasReciente != null ? fechaMasReciente.toString() : "Sin devolución";

                    table.addCell(p.getFecha_inicio_prestamo().toString());
                    table.addCell(fechareciente);
                    table.addCell(libros);
                    table.addCell(p.getId_cuenta().getNombre() + " " + p.getId_cuenta().getApellido());
                    table.addCell(p.getEstado_prestamo().toString());

                    contenidoAgregado = true; // Indicar que se ha añadido contenido
                }

                // Añadir el título y la tabla al documento
                documento.add(table);

                System.out.println("Documento generado correctamente para el mes de: " + nombreMes);
            }

            if (!contenidoAgregado) {
                System.out.println("No se ha añadido contenido al documento.");
                documento.add(new Paragraph("No hay datos disponibles para generar el informe."));
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reporte exitoso");
            alert.setHeaderText("Reporte exitoso");
            alert.setContentText("Reporte creado exitosamente en " + file.toString());
            alert.showAndWait();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            if (documento.isOpen()) {
                documento.close(); // Cerrar solo si está abierto
            }
        }
    }

    public void GenerarPDFInventario(File file) {
        Document documento = new Document(PageSize.LETTER);
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(file));
            documento.open();

            Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.DARK_GRAY);
            Paragraph titulo = new Paragraph("Informe de inventario", fuente);
            titulo.setAlignment(Element.ALIGN_CENTER);

            documento.add(titulo);
            documento.add(new Paragraph(Chunk.NEWLINE));

            PdfPTable tabla = new PdfPTable(7);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2.3f, 2.5f, 2.5f, 2f, 1f, 2.3f, 2.7f});

            tabla.addCell("ISBN");
            tabla.addCell("Titulo");
            tabla.addCell("Autor");
            tabla.addCell("Categoria");
            tabla.addCell("Disp.");
            tabla.addCell("Estado");
            tabla.addCell("Ubicacion");

            for (Libro libro : tabla_inventario.getItems()) {
                String isbn = String.valueOf(libro.getIsbn());
                tabla.addCell(isbn);
                tabla.addCell(libro.getTitulo());
                tabla.addCell(libro.getAutor());
                tabla.addCell(libro.getCategoria().getCategoria());
                tabla.addCell(String.valueOf(libro.getLibros_disponibles()));
                tabla.addCell(libro.getEstado_libro().toString());
                tabla.addCell(libro.getUbicacion());
            }

            documento.add(tabla);
            documento.add(new Paragraph(Chunk.NEWLINE));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reporte exitoso");
            alert.setHeaderText("Reporte exitoso");
            alert.setContentText("Reporte creado exitosamente en " + file.toString());
            alert.showAndWait();

        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }
    }

    private void generarPDFMora(File file) {
        Document document = new Document(PageSize.LETTER);
        try {
            // Inicializar PdfWriter
            PdfWriter.getInstance(document, new FileOutputStream(file));

            // Abrir el documento
            document.open();

            Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.DARK_GRAY);
            Paragraph titulo = new Paragraph("Informe de prestamos en Mora", fuente);
            titulo.setAlignment(Element.ALIGN_LEFT);
            document.add(titulo);
            document.add(new Paragraph("\n"));

            // Paso 3: Crear la tabla PDF con las columnas necesarias
            PdfPTable table = new PdfPTable(7);  // Número de columnas de la tabla
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 2f, 2f, 1f, 3f, 2.5f, 3f});

            // Definir las cabeceras de la tabla
            table.addCell("ID Préstamo");
            table.addCell("Fecha Préstamo");
            table.addCell("Fecha Devolución Esperada");
            table.addCell("Dias en mora");
            table.addCell("Nombre Usuario");
            table.addCell("Teléfono");
            table.addCell("Correo");

            // Obtener los préstamos de la tabla (TableView)
            for (Prestamo prestamo : Table_informe.getItems()) {
                table.addCell(String.valueOf(prestamo.getId_prestamo()));
                table.addCell(prestamo.getFecha_inicio_prestamo().toString());
                table.addCell(prestamo.getFecha_relativa_devolucion().toString());

                LocalDate fechaDevolucionRelativa = prestamo.getFecha_relativa_devolucion();
                LocalDate fechaActual = LocalDate.now();

                long diasEnMora = ChronoUnit.DAYS.between(fechaDevolucionRelativa, fechaActual);

                int resultado = (int) (diasEnMora > 0 ? diasEnMora : 0);
                table.addCell(String.valueOf(resultado));
                // Información del usuario
                String nombreUsuario = prestamo.getId_cuenta().getNombre();
                String telefonoUsuario = prestamo.getId_cuenta().getTelefono();
                String correoUsuario = prestamo.getId_cuenta().getCorreo();

                table.addCell(nombreUsuario);
                table.addCell(telefonoUsuario);
                table.addCell(correoUsuario);
            }

            // Agregar la tabla al documento
            document.add(table);

            // Cerrar el documento
            document.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reporte exitoso");
            alert.setHeaderText("Reporte exitoso");
            alert.setContentText("Reporte creado exitosamente en " + file.toString());
            alert.showAndWait();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

}
