/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.uacm.puntodeventa;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
/**
 *
 * @author ashje
 */
public class TipoPagoController {
    
    private String metodoSeleccionado = null;

    @FXML
    private void pagoEfectivo(ActionEvent event) {
        metodoSeleccionado = "EFECTIVO";
        cerrarVentana(event);
    }

    @FXML
    private void pagoTarjeta(ActionEvent event) {
        metodoSeleccionado = "TARJETA";
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }


    public String getMetodoSeleccionado() {
        return metodoSeleccionado;
    }
}
