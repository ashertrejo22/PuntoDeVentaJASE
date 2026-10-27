package org.uacm.puntodeventa;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TipoPagoController {

    @FXML private Label lblTotal;
    @FXML private TextField txtEfectivo;
    @FXML private Label lblCambio;

    private double totalAPagar = 0.0;
    private String metodoSeleccionado = null; 

    @FXML
    public void initialize() {
        txtEfectivo.textProperty().addListener((obs, oldVal, newVal) -> {
            calcularCambio();
        });
    }

    public void setTotalAPagar(double total) {
        this.totalAPagar = total;
        this.lblTotal.setText(String.format("Total a pagar $%.2f MXN", totalAPagar));
        this.lblCambio.setText("Cambio a dar $0.00 MXN");
    }

    @FXML
    private void alPresionarNumero(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String valorBoton = btn.getText();
        txtEfectivo.appendText(valorBoton);
    }

    @FXML
    private void alPresionarLimpiar(ActionEvent event) {
        txtEfectivo.clear();
    }

    @FXML
    private void alPresionarEnter(ActionEvent event) {
        try {
            String textoIngresado = txtEfectivo.getText().trim();
            if (textoIngresado.isEmpty()) {
                mostrarAlerta("Ingrese el monto recibido en efectivo.");
                return;
            }

            double entregado = Double.parseDouble(textoIngresado);
            if (entregado < totalAPagar) {
                mostrarAlerta("Falta dinero. El monto es menor al total a pagar.");
                return;
            }

            double cambio = entregado - totalAPagar;
            this.metodoSeleccionado = String.format("EFECTIVO\nRecibido: $%.2f MXN\nCambio: $%.2f MXN", 
                                                    entregado, cambio);
            cerrarVentana();
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Ingrese una cantidad numérica válida.");
        }
    }

    @FXML
    private void alPresionarDebito(ActionEvent event) {
        this.metodoSeleccionado = "TARJETA - DÉBITO";
        cerrarVentana();
    }

    @FXML
    private void alPresionarCredito(ActionEvent event) {
        this.metodoSeleccionado = "TARJETA - CRÉDITO";
        cerrarVentana();
    }

    private void calcularCambio() {
        try {
            String texto = txtEfectivo.getText().trim();
            if (texto.isEmpty()) {
                lblCambio.setText("Cambio a dar $0.00 MXN");
                return;
            }
            
            double entregado = Double.parseDouble(texto);
            double diferencia = entregado - totalAPagar;
            
            if (diferencia >= 0) {
                lblCambio.setText(String.format("Cambio a dar $%.2f MXN", diferencia));
            } else {
                lblCambio.setText("Falta dinero...");
            }
        } catch (NumberFormatException e) {
            lblCambio.setText("Monto inválido");
        }
    }

    public String getMetodoSeleccionado() {
        return metodoSeleccionado;
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atención");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) lblTotal.getScene().getWindow();
        stage.close();
    }
}