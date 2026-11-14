package org.uacm.puntodeventa;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class OpcionValidaController implements Initializable {

    @FXML
    private Button buttonAceptar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización
    }    

    @FXML
    private void cerrarVentana(ActionEvent event) {
        if (buttonAceptar != null) {
            Stage stage = (Stage) buttonAceptar.getScene().getWindow();
            stage.close();
        }
    }
}