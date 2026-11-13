/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.uacm.puntodeventa;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jaasielVC
 */
public class FechaValidaController implements Initializable {

    @FXML
    private Text txtFecha;
    @FXML
    private Button buttonAceptar;
    @FXML
    private Text txtFecha1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    @FXML
         public void cerrarVentana() {
        Stage stage = (Stage) buttonAceptar.getScene().getWindow();
        stage.close();
    }
    
}
