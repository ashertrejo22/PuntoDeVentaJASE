package org.uacm.puntodeventa;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.uacm.puntodeventa.DB.UsuarioABD;

public class NuevaContraController {

    @FXML private PasswordField txtNuevaContra;
    @FXML private PasswordField txtConfirmarContra;
    @FXML private Label lblError;

    private String usuario;

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @FXML
    private void guardarContra() {
        String contra1 = txtNuevaContra.getText();
        String contra2 = txtConfirmarContra.getText();

        // Validaciones
        if (contra1.isEmpty() || contra2.isEmpty()) {
            lblError.setText("Completa todos los campos");
            return;
        }
        if (contra1.length() < 6) {
            lblError.setText("Debe tener mínimo 6 caracteres");
            return;
        }
        boolean tieneNumero = contra1.matches(".*\\d.*");
        boolean tieneLetra = contra1.matches(".*[a-zA-Z].*");
        if (!tieneNumero || !tieneLetra) {
            lblError.setText("Debe contener letras y números");
            return;
        }
        if (!contra1.equals(contra2)) {
            lblError.setText("Las contraseñas no coinciden");
            return;
        }

        lblError.setText("");

        // Guardar nueva contraseña en la base de datos
        try {
            UsuarioABD.actualizarContrasena(usuario, contra1);
            // Mostrar mensaje de éxito
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Contraseña actualizada");
            alerta.setHeaderText(null);
            alerta.setContentText("Tu contraseña fue actualizada correctamente.");
            alerta.showAndWait();
            volverLogin();
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Error al actualizar la contraseña");
        }
    }

    private void volverLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/pantallas/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            Stage actual = (Stage) txtNuevaContra.getScene().getWindow();
            actual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}