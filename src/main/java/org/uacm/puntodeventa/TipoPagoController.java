package org.uacm.puntodeventa;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TipoPagoController implements Initializable {

    @FXML private Label lblTotal;
    @FXML private TextField txtEfectivo;
    @FXML private Label lblCambio;

    private double totalAPagar;
    private double efectivoRecibido = 0.0;
    private double cambioCalculado = 0.0;
    
    private String metodoSeleccionado = null;
    private String numeroRastreoFinal = "N/A";
    private boolean cobroConfirmado = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtEfectivo.setText("");
        lblCambio.setText("Cambio a dar $0.00 MXN");
    }

    // Se llama desde PantallaPrincipalController para pasarle el total
    public void setTotalAPagar(double total) {
        this.totalAPagar = total;
        lblTotal.setText(String.format("Total a pagar $%.2f MXN", total));
    }

    // Acción para los botones numéricos del 0 al 9 y el "."
    @FXML
    private void alPresionarNumero(ActionEvent event) {
        Button botonPresionado = (Button) event.getSource();
        String valorBoton = botonPresionado.getText();
        
        // Evitar múltiples puntos decimales
        if (valorBoton.equals(".") && txtEfectivo.getText().contains(".")) {
            return; 
        }
        
        txtEfectivo.setText(txtEfectivo.getText() + valorBoton);
        calcularCambioEnVivo();
    }

    // Acción para el botón "C"
    @FXML
    private void alPresionarLimpiar(ActionEvent event) {
        txtEfectivo.setText("");
        lblCambio.setText("Cambio a dar $0.00 MXN");
        lblCambio.setTextFill(javafx.scene.paint.Color.valueOf("#cf2e2b"));
    }

    // Acción para el botón "ENTER" (Pago en EFECTIVO)
    @FXML
    private void alPresionarEnter(ActionEvent event) {
        if (!validarEfectivoSuficiente()) {
            return;
        }
        
        this.metodoSeleccionado = "EFECTIVO";
        this.numeroRastreoFinal = "N/A";
        this.cobroConfirmado = true;
        cerrarVentana();
    }

    // Acción para el botón "Débito" (Pago con Tarjeta)
    @FXML
    private void alPresionarDebito(ActionEvent event) {
        confirmarPagoTarjeta("TARJETA DE DÉBITO");
    }

    // Acción para el botón "Crédito" (Pago con Tarjeta)
    @FXML
    private void alPresionarCredito(ActionEvent event) {
        confirmarPagoTarjeta("TARJETA DE CRÉDITO");
    }

    private void calcularCambioEnVivo() {
        try {
            if (txtEfectivo.getText().isEmpty()) {
                lblCambio.setText("Cambio a dar $0.00 MXN");
                lblCambio.setTextFill(javafx.scene.paint.Color.valueOf("#cf2e2b"));
                return;
            }
            
            efectivoRecibido = Double.parseDouble(txtEfectivo.getText());
            cambioCalculado = efectivoRecibido - totalAPagar;
            
            if (cambioCalculado < 0) {
                lblCambio.setText("Falta cobrar: " + String.format("$%.2f MXN", Math.abs(cambioCalculado)));
                lblCambio.setTextFill(javafx.scene.paint.Color.valueOf("#cf2e2b")); // Rojo JASE
            } else {
                lblCambio.setText(String.format("Cambio a dar $%.2f MXN", cambioCalculado));
                lblCambio.setTextFill(javafx.scene.paint.Color.valueOf("#00502b")); // Verde JASE
            }
        } catch (NumberFormatException e) {
            // Ignorar si aún están escribiendo puntos decimales incompletos
        }
    }

    private boolean validarEfectivoSuficiente() {
        if (txtEfectivo.getText().isEmpty()) {
            mostrarAlerta("Pago Incompleto", "Ingrese el monto recibido en efectivo.");
            return false;
        }
        
        try {
            efectivoRecibido = Double.parseDouble(txtEfectivo.getText());
            if (efectivoRecibido < totalAPagar) {
                mostrarAlerta("Saldo Insuficiente", "El efectivo recibido es menor al total a pagar.");
                return false;
            }
            cambioCalculado = efectivoRecibido - totalAPagar;
            return true;
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato Inválido", "Por favor, ingrese un número válido.");
            return false;
        }
    }

    private void confirmarPagoTarjeta(String tipoTarjeta) {
        this.metodoSeleccionado = tipoTarjeta;
        this.efectivoRecibido = totalAPagar; // El cobro es exacto
        this.cambioCalculado = 0.0;
        
        // Genera un rastreo bancario automático para tarjetas
        String fechaRef = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
        int pinAuth = ThreadLocalRandom.current().nextInt(100000, 999999);
        this.numeroRastreoFinal = "TR-" + fechaRef + "-" + pinAuth;
        
        this.cobroConfirmado = true;
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtEfectivo.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Getters para que PantallaPrincipalController recupere los datos finales
    public String getMetodoSeleccionado() {
        return cobroConfirmado ? metodoSeleccionado : null;
    }

    public String getNumeroRastreo() {
        return numeroRastreoFinal;
    }
    
    public double getEfectivoRecibido() {
        return efectivoRecibido;
    }
    
    public double getCambio() {
        return cambioCalculado;
    }
}