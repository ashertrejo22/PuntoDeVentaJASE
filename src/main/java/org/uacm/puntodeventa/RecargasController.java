package org.uacm.puntodeventa;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.uacm.puntodeventa.Clases.Recarga;
import org.uacm.puntodeventa.DB.RecargaABD;

public class RecargasController implements Initializable {

    @FXML private ComboBox<String> comboBoxCompañia;
    @FXML private TextField txtNumero;
    @FXML private Button btnAceptar;
    @FXML private Button btnCancelar;
    @FXML private ComboBox<String> comboBoxMonto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboBoxCompañia.getItems().addAll("Telcel", "Movistar", "Unefon", "AT&T");
        comboBoxMonto.getItems().addAll("$10", "$20", "$30", "$50", "$100", "$150", "$200", "$300");
    }

    @FXML
    private void aceptar(ActionEvent event) {
        try {
            String numero = txtNumero.getText().trim();
            String companiaSeleccionada = comboBoxCompañia.getValue();
            String montoSeleccionado = comboBoxMonto.getValue();

            if (numero.isEmpty() || companiaSeleccionada == null || montoSeleccionado == null) {
                mostrarMensaje("/pantallas/AlertaCampos.fxml");
                return;
            }

            if (!numero.matches("\\d{10}")) {
                mostrarMensaje("/pantallas/AlertaNumeros.fxml");
                return;
            }

            double monto = 0;
            try {
                monto = Double.parseDouble(montoSeleccionado.replace("$", ""));
            } catch (NumberFormatException ex) {
                mostrarMensaje("/pantallas/AlertaMonto.fxml");
                return;
            }

            // Crear objeto Recarga (genera folio automáticamente)
            Recarga nuevaRecarga = new Recarga(numero, companiaSeleccionada, monto);

            // Guardar en base de datos
            RecargaABD.insertar(nuevaRecarga);

            // Mostrar comprobante
            abrirComprobanteRecarga(nuevaRecarga.generarComprobante());

            // Cerrar ventana actual
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error del Sistema", "Ocurrió un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirComprobanteRecarga(String comprobanteTexto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallas/ticket.fxml"));
            Parent root = loader.load();
            TicketController controller = loader.getController();
            controller.setTicketTexto(comprobanteTexto);
            Stage stage = new Stage();
            stage.setTitle("Comprobante de Recarga");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo generar la vista del comprobante.");
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarMensaje(String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        Stage stage = (Stage) txtNumero.getScene().getWindow();
        stage.close();
    }
}