package org.uacm.puntodeventa;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class TicketController {

    @FXML
    private Button btnCerrar;
    @FXML
    private TextArea txtAreaTicket;

    @FXML
    public void initialize() {
        btnCerrar.setOnAction(event -> {
            Stage stage = (Stage) btnCerrar.getScene().getWindow();
            stage.close();
        });
    }

    public void setTicketTexto(String texto) {
        if (txtAreaTicket != null) {
            txtAreaTicket.setText(texto);
            txtAreaTicket.setEditable(false);
        }
    }
}
