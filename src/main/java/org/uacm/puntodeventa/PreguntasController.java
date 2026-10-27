package org.uacm.puntodeventa;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.uacm.puntodeventa.Clases.Seguridad;

public class PreguntasController {

    @FXML
    private Label p1, p2, p3;
    @FXML
    private TextField r1, r2, r3;

    private String usuario;

    // recibe el usuario desde Recuperar
    public void setUsuario(String usuario) {
        this.usuario = usuario;

        String user = usuario.trim();

        if (user.equalsIgnoreCase("Administrador")) {
            p1.setText(Seguridad.preguntasAdmin[0]);
            p2.setText(Seguridad.preguntasAdmin[1]);
            p3.setText(Seguridad.preguntasAdmin[2]);
        } else {
            p1.setText(Seguridad.preguntasEmpleado[0]);
            p2.setText(Seguridad.preguntasEmpleado[1]);
            p3.setText(Seguridad.preguntasEmpleado[2]);
        }
    }

    @FXML
    private void verificar() {
        if (r1.getText().trim().isEmpty()) {
            mostrarAlerta();
            return;
        }

        if (r2.getText().trim().isEmpty()) {
            mostrarAlerta();
            return;
        }

        if (r3.getText().trim().isEmpty()) {
            mostrarAlerta();
            return;
        }

        boolean correcto;
        String user = usuario.trim();

        if (user.equalsIgnoreCase("Administrador")) {
            correcto
                    = r1.getText().equals(Seguridad.respuestasAdmin[0])
                    && r2.getText().equals(Seguridad.respuestasAdmin[1])
                    && r3.getText().equals(Seguridad.respuestasAdmin[2]);
        } else {
            correcto
                    = r1.getText().equals(Seguridad.respuestasEmpleado[0])
                    && r2.getText().equals(Seguridad.respuestasEmpleado[1])
                    && r3.getText().equals(Seguridad.respuestasEmpleado[2]);
        }

        if (correcto) {
            mostrarContra();
        } else {
            System.out.println("Respuestas incorrectas");
        }
    }

    private void mostrarContra() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pantallas/NuevaContra.fxml"));

            Parent root = loader.load();

            NuevaContraController controller = loader.getController();

            controller.setUsuario(usuario);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage actual = (Stage) p1.getScene().getWindow();
            actual.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void volverLogin(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/pantallas/Login.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage actual = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            actual.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pantallas/AlertaLlenarCampos.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
