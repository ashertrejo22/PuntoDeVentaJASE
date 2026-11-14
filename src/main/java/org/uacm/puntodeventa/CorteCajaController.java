/*
 * Archivo: CorteCajaController.java
 * Módulo: Corte de Caja
 * Propósito: Controla la pantalla de apertura, retiros, conteo de efectivo y cierre de turno de caja.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.uacm.puntodeventa.Clases.ConteoEfectivo;
import org.uacm.puntodeventa.Clases.CorteCaja;
import org.uacm.puntodeventa.cortecaja.ComprobanteCorte;
import org.uacm.puntodeventa.cortecaja.CorteCajaServicio;
import org.uacm.puntodeventa.reportes.FormatoReporte;

/**
 * Pantalla de Corte de Caja. Solo lee los datos de la pantalla, llama a
 * {@link CorteCajaServicio} y muestra el resultado (FX-4).
 */
public class CorteCajaController {

    private static final Logger REGISTRO = Logger.getLogger(CorteCajaController.class.getName());
    private static final Pattern FORMATO_MONTO = Pattern.compile("\\d+(\\.\\d{1,2})?");
    private static final String TITULO_COMPROBANTE = "Comprobante de Corte de Caja";

    @FXML
    private TextField campoFondoInicial;
    @FXML
    private Button botonAbrirTurno;
    @FXML
    private Label etiquetaEstadoTurno;
    @FXML
    private TextField campoMontoRetiro;
    @FXML
    private TextField campoMotivoRetiro;
    @FXML
    private Button botonRegistrarRetiro;
    @FXML
    private PanelConteoEfectivo panelConteo;
    @FXML
    private Button botonCerrarTurno;
    @FXML
    private Label etiquetaResultadoCierre;
    @FXML
    private Button botonComprobante;

    private final CorteCajaServicio servicio = new CorteCajaServicio();
    private int idUsuarioActual;
    private CorteCaja turnoAbierto;
    private CorteCaja turnoCerrado;

    /**
     * Recibe el usuario en sesión y recupera su turno abierto, si lo hay (excepción E1).
     * Debe llamarse justo después de cargar la pantalla.
     *
     * @param idUsuario identificador del usuario que inició sesión
     */
    public void configurarUsuario(int idUsuario) {
        this.idUsuarioActual = idUsuario;
        cargarTurnoAbierto();
    }

    @FXML
    private void alPresionarAbrirTurno() {
        try {
            double fondo = leerMonto(campoFondoInicial, "fondo inicial");
            turnoAbierto = servicio.abrirTurno(idUsuarioActual, fondo);
            mostrarEstado();
        } catch (IllegalArgumentException | IllegalStateException e) {
            Alertas.aviso("No se pudo abrir el turno", e.getMessage());
        } catch (SQLException e) {
            informarErrorBaseDatos("abrir el turno", e);
        }
    }

    @FXML
    private void alPresionarRegistrarRetiro() {
        try {
            double monto = leerMonto(campoMontoRetiro, "monto del retiro");
            servicio.registrarRetiro(exigirTurno(), monto, campoMotivoRetiro.getText());
            campoMontoRetiro.clear();
            campoMotivoRetiro.clear();
            Alertas.informacion("Retiro registrado", "El retiro se registró en el turno abierto.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            Alertas.aviso("No se pudo registrar el retiro", e.getMessage());
        } catch (SQLException e) {
            informarErrorBaseDatos("registrar el retiro", e);
        }
    }

    @FXML
    private void alPresionarCerrarTurno() {
        try {
            ConteoEfectivo conteo = panelConteo.leerConteo();
            CorteCaja corte = exigirTurno();
            servicio.cerrarTurno(corte, conteo);
            turnoCerrado = corte;
            turnoAbierto = null;
            limpiarCamposDelTurno();
            mostrarEstado();
            etiquetaResultadoCierre.setText("Resultado: " + ComprobanteCorte.describirResultado(corte));
            mostrarComprobante();
        } catch (IllegalArgumentException | IllegalStateException e) {
            Alertas.aviso("No se pudo cerrar el turno", e.getMessage());
        } catch (SQLException e) {
            informarErrorBaseDatos("cerrar el turno", e);
            cargarTurnoAbierto();
        }
    }

    @FXML
    private void alPresionarVerComprobante() {
        mostrarComprobante();
    }

    private void cargarTurnoAbierto() {
        try {
            turnoAbierto = servicio.turnoAbierto(idUsuarioActual).orElse(null);
            mostrarEstado();
        } catch (SQLException e) {
            informarErrorBaseDatos("consultar el turno abierto", e);
        }
    }

    private void mostrarEstado() {
        if (turnoAbierto != null) {
            deshabilitar(campoFondoInicial, botonAbrirTurno);
            habilitar(campoMontoRetiro, campoMotivoRetiro, botonRegistrarRetiro, panelConteo,
                    botonCerrarTurno);
            campoFondoInicial.setText(String.format("%.2f", turnoAbierto.getFondoInicial()));
            etiquetaEstadoTurno.setText("Turno abierto desde "
                    + FormatoReporte.fechaHora(turnoAbierto.getFechaApertura())
                    + ". Folio: " + turnoAbierto.getFolio());
        } else {
            habilitar(campoFondoInicial, botonAbrirTurno);
            deshabilitar(campoMontoRetiro, campoMotivoRetiro, botonRegistrarRetiro, panelConteo,
                    botonCerrarTurno);
            etiquetaEstadoTurno.setText("No hay un turno abierto.");
        }
        botonComprobante.setDisable(turnoCerrado == null);
    }

    private void mostrarComprobante() {
        try {
            if (turnoCerrado == null) {
                throw new IllegalStateException("El comprobante se genera hasta que cierres el turno.");
            }
            Alertas.textoMonoespaciado(TITULO_COMPROBANTE, servicio.generarComprobante(turnoCerrado));
        } catch (IllegalStateException e) {
            Alertas.aviso("Sin comprobante", e.getMessage());
        } catch (SQLException e) {
            informarErrorBaseDatos("generar el comprobante", e);
        }
    }

    private void limpiarCamposDelTurno() {
        campoFondoInicial.clear();
        campoMontoRetiro.clear();
        campoMotivoRetiro.clear();
        panelConteo.limpiar();
    }

    private CorteCaja exigirTurno() {
        if (turnoAbierto == null) {
            throw new IllegalStateException("Primero abre un turno de caja.");
        }
        return turnoAbierto;
    }

    private double leerMonto(TextField campo, String nombre) {
        String texto = campo.getText() == null ? "" : campo.getText().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("Captura el " + nombre + " antes de continuar.");
        }
        if (!FORMATO_MONTO.matcher(texto).matches()) {
            throw new IllegalArgumentException("El " + nombre
                    + " debe ser un monto en pesos con hasta dos decimales, por ejemplo 150 o 150.50.");
        }
        return Double.parseDouble(texto);
    }

    private void habilitar(Node... controles) {
        for (Node control : controles) {
            control.setDisable(false);
        }
    }

    private void deshabilitar(Node... controles) {
        for (Node control : controles) {
            control.setDisable(true);
        }
    }

    private void informarErrorBaseDatos(String operacion, SQLException error) {
        REGISTRO.log(Level.SEVERE, "Error de base de datos al " + operacion, error);
        Alertas.error("Error de base de datos", "No se pudo " + operacion + ": " + error.getMessage());
    }
}
