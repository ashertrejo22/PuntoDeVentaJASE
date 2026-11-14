package org.uacm.puntodeventa;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.uacm.puntodeventa.DB.UsuarioABD;

public class RecuperarController {

    @FXML private TextField txtUsuario;

    @FXML
    private void continuar() {
        try {
            String usuario = txtUsuario.getText().trim();
            if (usuario.isEmpty()) {
                mostrarAlerta("/pantallas/AlertaRecuperar.fxml");
                return;
            }

            // Validar que el usuario existe en la base de datos
            if (!UsuarioABD.existeUsuario(usuario)) {
                mostrarAlerta("/pantallas/AlertaUsuarioIncorrecto.fxml");
                return;
            }

            // Cargar pantalla de preguntas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallas/Preguntas.fxml"));
            Parent root = loader.load();
            PreguntasController controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage actual = (Stage) txtUsuario.getScene().getWindow();
            actual.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void volverLogin(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/pantallas/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            Stage actual = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            actual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}