package org.tecno.gestor.biblioteca.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class Informes {

    @FXML
    private Button Bot_Consulta;

    @FXML
    private Button Bot_reporte;

    @FXML
    private TableView<Prestamo> Table_informe;

    @FXML
    private TableColumn<Prestamo, Integer> Column_diasenmora;

    @FXML
    private TableColumn<Prestamo, String> Columna_fechaprestada;

    @FXML
    private TableColumn<Prestamo, String> Columna_fechadevolver;

    //Columna contenedora
    @FXML
    private TableColumn<Prestamo, Cuenta> Columna_usuario;
    //Columnas contenidas
    @FXML
    private TableColumn<Prestamo, String> Columna_correo;

    @FXML
    private TableColumn<Prestamo, String> Columna_nombreusuario;

    @FXML
    private TableColumn<Prestamo, String> Columna_telefono;

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

    LibraryService ls = new LibraryService();
    Map<Month, List<Prestamo>> prestamosPorMes;

    @FXML
    public void initialize() {
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
        cargarPrestamos();
        String rutaArchivo;

        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Guardar reporte");
        File homeDir = new File(System.getProperty("user.home"));
        filechooser.setInitialDirectory(homeDir);

        filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));

        filechooser.setInitialFileName("reporte.pdf");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File archivoSeleccionado = filechooser.showSaveDialog(stage);

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
        GenerarPDF(rutaArchivo);

    }

    @FXML
    void Reporte_Action(ActionEvent event) {
        // Paso 1: Mostrar FileChooser para que el usuario elija dónde guardar el PDF
        FileChooser fileChooser = new FileChooser();
        File homeDir = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(homeDir);
        fileChooser.setTitle("Guardar Reporte");
        fileChooser.setInitialFileName("reporte_de_enmora.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            generarPDFMora(file);
        }
    }

    private void generarPDFMora(File file) {
        // Paso 2: Crear el documento PDF
        Document document = new Document();

        try {
            // Inicializar PdfWriter
            PdfWriter.getInstance(document, new FileOutputStream(file));

            // Abrir el documento
            document.open();

            // Agregar el título
            document.add(new Paragraph("Informe de Préstamos en Mora"));
            document.add(new Paragraph(" "));  // Espacio entre el título y la tabla

            // Paso 3: Crear la tabla PDF con las columnas necesarias
            PdfPTable table = new PdfPTable(6);  // Número de columnas de la tabla
            table.setWidthPercentage(100);  // Ancho de la tabla al 100%
            table.setSpacingBefore(10f);  // Espaciado antes de la tabla
            table.setSpacingAfter(10f);  // Espaciado después de la tabla

            // Definir las cabeceras de la tabla
            table.addCell("ID Préstamo");
            table.addCell("Fecha Préstamo");
            table.addCell("Fecha Devolución Esperada");
            table.addCell("Nombre Usuario");
            table.addCell("Teléfono");
            table.addCell("Correo");

            // Obtener los préstamos de la tabla (TableView)
            for (Prestamo prestamo : Table_informe.getItems()) {
                table.addCell(String.valueOf(prestamo.getId_prestamo()));
                table.addCell(prestamo.getFecha_inicio_prestamo().toString());
                table.addCell(prestamo.getFecha_relativa_devolucion().toString());

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

    @FXML
    void ConsultaAction(ActionEvent event) {
        cargarPrestamos();
    }

    private void cargarPrestamos() {
        // Aquí obtienes la lista de préstamos en mora o lo que quieras cargar
        List<Prestamo> prestamos = ls.EncontrarPrestamosEnmora();  // Supongamos que este método obtiene los préstamos
        if (prestamos == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            
            alert.setHeaderText("No encontrados");
            alert.setContentText("No se encontraron prestamos en mora");
            alert.showAndWait();
            return;
        }
        ObservableList<Prestamo> listaPrestamos = FXCollections.observableArrayList(prestamos);
        Table_informe.setItems(listaPrestamos);
    }

    public void GenerarPDF(String rutaArchivo) {
        Document documento = new Document(PageSize.LETTER);
        try {
            if (prestamosPorMes == null || prestamosPorMes.isEmpty()) {
                System.out.println("No hay préstamos para procesar.");
                return;
            }

            try {
                PdfWriter.getInstance(documento, new FileOutputStream(rutaArchivo));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Informes.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error al crear el archivo PDF.");
                return; // Salir en caso de error
            }

            documento.open();
            boolean contenidoAgregado = false; // Control para verificar si se añade contenido

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

                Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph titulo = new Paragraph(nombreMes, fontTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);

                documento.add(titulo);
                documento.add(new Paragraph("\n"));

                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(95);
                table.setWidths(new float[]{2f, 2.5f, 1f, 1.2f, 2.5f, 2.5f});

                // Añadir cabeceras
                table.addCell("Fecha Inicio");
                table.addCell("Fecha Devolución");
                table.addCell("N° libros");
                table.addCell("N° ejemplar");
                table.addCell("Usuario");
                table.addCell("Estado");

                for (Prestamo p : prestamos) {
                    System.out.println("Procesando préstamo de fecha: " + p.getFecha_inicio_prestamo());

                    int ejemplares = 0;
                    for (DetallePrestamo detalles : p.getId_detalleprestamo()) {
                        ejemplares += detalles.getEjemplares_prestados();
                    }
                    String Cantidad_libros = String.valueOf(p.getId_detalleprestamo().size());

                    LocalDate fechaMasReciente = p.getDevolucion().stream()
                            .map(Devolucion::getFecha_devolucion_real)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    String fechareciente = fechaMasReciente != null ? fechaMasReciente.toString() : "Sin devolución";

                    table.addCell(p.getFecha_inicio_prestamo().toString());
                    table.addCell(fechareciente);
                    table.addCell(Cantidad_libros);
                    table.addCell(String.valueOf(ejemplares));
                    table.addCell(p.getId_cuenta().getNombre());
                    table.addCell(p.getEstado_prestamo().toString());

                    contenidoAgregado = true; // Indicar que se ha añadido contenido
                }

                // Añadir el título y la tabla al documento
                documento.add(table);
                documento.add(new Paragraph("\n"));

                System.out.println("Documento generado correctamente para el mes de: " + nombreMes);
            }

            if (!contenidoAgregado) {
                System.out.println("No se ha añadido contenido al documento.");
                documento.add(new Paragraph("No hay datos disponibles para generar el informe."));
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            if (documento.isOpen()) {
                documento.close(); // Cerrar solo si está abierto
            }
        }
    }

}
