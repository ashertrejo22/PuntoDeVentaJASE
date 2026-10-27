package org.uacm.puntodeventa;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CantidadGranelController {

    @FXML
    private TextField txtCantidad;
    private double cantidadFinal = -1.0;

    @FXML
    private void clickAceptar(ActionEvent event) {
        try {
            double valor = Double.parseDouble(txtCantidad.getText().trim());
            if (valor > 0) {
                cantidadFinal = valor;
                cerrarVentana();
            } else {

                mostrarAlertaGramos();
            }
        } catch (NumberFormatException e) {
  
            mostrarAlertaGramos();
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cantidadFinal = -1.0;
        cerrarVentana();
    }

    private void mostrarAlertaGramos() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/pantallas/AlertaGramos.fxml"));
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtCantidad.getScene().getWindow();
        stage.close();
    }

    public double getCantidadFinal() {
        return cantidadFinal;
    }
}
