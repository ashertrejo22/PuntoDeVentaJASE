package org.uacm.puntodeventa;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AlertaGramosController implements Initializable {

    @FXML
    private Button btnAceptar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    

    @FXML
    private void cerrarVentana(ActionEvent event) {
        if (btnAceptar != null) {
            Stage stage = (Stage) btnAceptar.getScene().getWindow();
            stage.close();
        }
    }
}