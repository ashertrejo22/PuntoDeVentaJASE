/*
 * Archivo: ReporteVentasController.java
 * Módulo: Reporte de Ventas
 * Propósito: Controla la pantalla del reporte de ventas: consulta, comprobante y exportación.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.uacm.puntodeventa.Clases.FiltroTransaccion;
import org.uacm.puntodeventa.Clases.ItemReporte;
import org.uacm.puntodeventa.Clases.ProductoVendido;
import org.uacm.puntodeventa.reportes.FormatoReporte;
import org.uacm.puntodeventa.reportes.PeriodoReporte;
import org.uacm.puntodeventa.reportes.ReporteVentas;
import org.uacm.puntodeventa.reportes.ReporteVentasServicio;
import org.uacm.puntodeventa.reportes.ResumenReporte;

/**
 * Pantalla del reporte de ventas. Solo lee los datos de la pantalla, llama al servicio
 * y muestra el resultado (FX-4); las reglas de negocio viven en {@link ReporteVentasServicio}.
 */
public class ReporteVentasController implements Initializable {

    private static final Logger REGISTRO = Logger.getLogger(ReporteVentasController.class.getName());
    private static final int CLICS_PARA_COMPROBANTE = 2;
    private static final String ALINEACION_DERECHA = "-fx-alignment: CENTER-RIGHT;";

    @FXML
    private DatePicker selectorFechaDesde;
    @FXML
    private DatePicker selectorFechaHasta;
    @FXML
    private ComboBox<FiltroTransaccion> comboTipo;
    @FXML
    private Spinner<Integer> spinnerProductos;
    @FXML
    private TableView<ItemReporte> tablaTransacciones;
    @FXML
    private TableColumn<ItemReporte, String> columnaFolio;
    @FXML
    private TableColumn<ItemReporte, String> columnaFecha;
    @FXML
    private TableColumn<ItemReporte, String> columnaCajero;
    @FXML
    private TableColumn<ItemReporte, String> columnaDetalle;
    @FXML
    private TableColumn<ItemReporte, String> columnaTotal;
    @FXML
    private Label etiquetaTotalVendido;
    @FXML
    private Label etiquetaTransacciones;
    @FXML
    private Label etiquetaTicketPromedio;
    @FXML
    private Label etiquetaFormasPago;
    @FXML
    private Label etiquetaAviso;
    @FXML
    private ListView<String> listaMasVendidos;

    private final ReporteVentasServicio servicio = new ReporteVentasServicio();

    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        columnaFolio.setCellValueFactory(new PropertyValueFactory<>("folio"));
        columnaCajero.setCellValueFactory(new PropertyValueFactory<>("cajero"));
        columnaDetalle.setCellValueFactory(new PropertyValueFactory<>("detalle"));
        columnaFecha.setCellValueFactory(celda -> new ReadOnlyStringWrapper(
                FormatoReporte.fecha(celda.getValue().getFecha())));
        columnaTotal.setCellValueFactory(celda -> new ReadOnlyStringWrapper(
                FormatoReporte.pesos(celda.getValue().getTotal())));
        columnaTotal.setStyle(ALINEACION_DERECHA);
        configurarFiltros();
        configurarDobleClic();
        Platform.runLater(this::consultarPeriodo);
    }

    @FXML
    private void alPresionarConsultar() {
        consultarPeriodo();
    }

    private void configurarFiltros() {
        comboTipo.setItems(FXCollections.observableArrayList(FiltroTransaccion.values()));
        comboTipo.setValue(FiltroTransaccion.AMBAS);
        LocalDate hoy = LocalDate.now();
        selectorFechaDesde.setValue(hoy.withDayOfMonth(1));
        selectorFechaHasta.setValue(hoy);
    }

    private void configurarDobleClic() {
        tablaTransacciones.setOnMouseClicked(evento -> {
            ItemReporte seleccionada = tablaTransacciones.getSelectionModel().getSelectedItem();
            if (evento.getClickCount() == CLICS_PARA_COMPROBANTE && seleccionada != null) {
                mostrarComprobante(seleccionada.getFolio());
            }
        });
    }

    private void consultarPeriodo() {
        try {
            PeriodoReporte periodo = new PeriodoReporte(selectorFechaDesde.getValue(),
                    selectorFechaHasta.getValue(), comboTipo.getValue());
            mostrarReporte(servicio.consultar(periodo, spinnerProductos.getValue()));
        } catch (IllegalArgumentException e) {
            Alertas.aviso("Periodo inválido", e.getMessage());
        } catch (SQLException e) {
            informarErrorBaseDatos("consultar el reporte", e);
        }
    }

    private void mostrarReporte(ReporteVentas reporte) {
        tablaTransacciones.setItems(FXCollections.observableArrayList(reporte.getTransacciones()));
        mostrarResumen(reporte.getResumen());
        mostrarMasVendidos(reporte);
        if (reporte.isVacio()) {
            etiquetaAviso.setText("No se encontraron transacciones en el periodo.");
        } else {
            etiquetaAviso.setText(reporte.getTransacciones().size()
                    + " transacción(es) en el periodo consultado.");
        }
    }

    private void mostrarResumen(ResumenReporte resumen) {
        etiquetaTotalVendido.setText(FormatoReporte.pesos(resumen.getTotal()));
        etiquetaTransacciones.setText(String.valueOf(resumen.getTransacciones()));
        etiquetaTicketPromedio.setText(FormatoReporte.pesos(resumen.getTicketPromedio()));
        etiquetaFormasPago.setText(FormatoReporte.pesos(resumen.getEfectivo()) + " / "
                + FormatoReporte.pesos(resumen.getTarjeta()));
    }

    private void mostrarMasVendidos(ReporteVentas reporte) {
        ObservableList<String> renglones = FXCollections.observableArrayList();
        int lugar = 1;
        for (ProductoVendido producto : reporte.getMasVendidos()) {
            renglones.add(FormatoReporte.productoVendido(lugar, producto));
            lugar++;
        }
        if (renglones.isEmpty()) {
            renglones.add("Sin productos vendidos en el periodo.");
        }
        listaMasVendidos.setItems(renglones);
    }

    private void mostrarComprobante(String folio) {
        try {
            Optional<String> ticket = servicio.buscarTicket(folio);
            if (ticket.isPresent()) {
                abrirVentanaTicket(ticket.get(), folio);
            } else {
                Alertas.aviso("Sin comprobante", "No se encontró el comprobante del folio " + folio);
            }
        } catch (SQLException e) {
            informarErrorBaseDatos("consultar el comprobante " + folio, e);
        }
    }

    private void abrirVentanaTicket(String texto, String folio) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/pantallas/ticket.fxml"));
            Parent raiz = cargador.load();
            TicketController controlador = cargador.getController();
            controlador.setTicketTexto(texto);
            Stage ventana = new Stage();
            ventana.setTitle("Comprobante " + folio);
            ventana.setScene(new Scene(raiz));
            ventana.setResizable(false);
            ventana.show();
        } catch (IOException e) {
            REGISTRO.log(Level.SEVERE, "No se pudo cargar ticket.fxml", e);
            Alertas.error("No se pudo abrir el comprobante", "No se pudo cargar la ventana del ticket.");
        }
    }

    private void informarErrorBaseDatos(String operacion, SQLException error) {
        REGISTRO.log(Level.SEVERE, "Error de base de datos al " + operacion, error);
        Alertas.error("Error de base de datos", "No se pudo " + operacion + ": " + error.getMessage());
    }
}
