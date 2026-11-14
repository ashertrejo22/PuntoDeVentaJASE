/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
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
public class AlertaLlenarCamposController implements Initializable {

    @FXML
    private Text txtFecha1;
    @FXML
    private Text txtFecha;
    @FXML
    private Button buttonAceptar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void cerrarVentana(ActionEvent event) {
          if (buttonAceptar != null) {
            Stage stage = (Stage) buttonAceptar.getScene().getWindow();
            stage.close();
        }
    }
    
}
