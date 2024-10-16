package com.tecno.biblioteca.controller;

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
import com.tecno.biblioteca.enums.EstadoPrestamo;
import com.tecno.biblioteca.model.Cuenta;
import com.tecno.biblioteca.model.DetallePrestamo;
import com.tecno.biblioteca.model.Devolucion;
import com.tecno.biblioteca.model.Prestamo;
import com.tecno.biblioteca.service.LibraryService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
            System.out.println("Por favor, selecciona ambas fechas.");
        }
    }

    @FXML
    void GuardarAction(ActionEvent event) throws FileNotFoundException {
        String rutaArchivo;

        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Guardar archivo");

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
            return;
        }

        GenerarPDF(rutaArchivo);

    }

    @FXML
    void Reporte_Action(ActionEvent event) {
        
    }

    @FXML
    void ConsultaAction(ActionEvent event) {
        // Cargar datos en la tabla
        cargarPrestamos();
    }

    public void GenerarPDF(String rutaArchivo) {
        Document documento = new Document(PageSize.LETTER);
        try {
            if (prestamosPorMes == null || prestamosPorMes.isEmpty()) {
                return;
            }

            try {
                PdfWriter.getInstance(documento, new FileOutputStream(rutaArchivo));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Informes.class.getName()).log(Level.SEVERE, null, ex);
            }
            documento.open();

            for (Map.Entry<Month, List<Prestamo>> entry1 : prestamosPorMes.entrySet()) {
                Month mes = entry1.getKey();
                List<Prestamo> prestamos = entry1.getValue();

                String nombreMes = mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

                Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph titulo = new Paragraph(nombreMes, fontTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER); // Centrar el título

                PdfPTable table = new PdfPTable(6);

                table.setWidthPercentage(95); // La tabla ocupa el 100% del ancho de la página
                table.setWidths(new float[]{2f, 2.5f, 1f, 1.2f, 2.5f, 2.5f});

                table.addCell("Fecha Inicio");
                table.addCell("Fecha Devolucion");
                table.addCell("N° libros");
                table.addCell("N° ejemplar");
                table.addCell("Usuario");
                table.addCell("Estado");

                for (Prestamo p : prestamos) {

                    int ejemplares = 0;
                    for (DetallePrestamo detalles : p.getId_detalleprestamo()) {
                        ejemplares += detalles.getEjemplares_prestados();
                    }
                    String Cantidad_libros = String.valueOf(p.getId_detalleprestamo().size());

                    LocalDate fechaMasReciente = p.getDevolucion().stream()
                            .map(Devolucion::getFecha_devolucion_real) // Obtener las fechas
                            .max(Comparator.naturalOrder()) // Comparar para obtener la más reciente
                            .orElse(null);

                    String fechareciente = fechaMasReciente != null ? fechaMasReciente.toString() : "Sin devolucion";

                    table.addCell(p.getFecha_inicio_prestamo().toString());
                    table.addCell(fechareciente);
                    table.addCell(Cantidad_libros);
                    table.addCell(String.valueOf(ejemplares));
                    table.addCell(p.getId_cuenta().getNombre());
                    table.addCell(p.getEstado_prestamo().toString());

                }
                documento.add(titulo);
                documento.add(new Paragraph("\n"));
                documento.add(table);
                documento.add(new Paragraph("\n"));

            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            documento.close();
        }
    }

    private void cargarPrestamos() {
        // Aquí obtienes la lista de préstamos en mora o lo que quieras cargar
        List<Prestamo> prestamos = ls.EncontrarPrestamosEnmora();  // Supongamos que este método obtiene los préstamos
        if (prestamos == null) {
            System.out.println("No se encontrarion prestamos en mora");
            return;
        }
        ObservableList<Prestamo> listaPrestamos = FXCollections.observableArrayList(prestamos);
        Table_informe.setItems(listaPrestamos);
    }
}
