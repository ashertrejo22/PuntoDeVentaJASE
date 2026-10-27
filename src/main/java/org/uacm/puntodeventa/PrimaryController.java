package org.uacm.puntodeventa;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.uacm.puntodeventa.DB.UsuarioABD;

import java.io.IOException;
import java.sql.SQLException;

public class PrimaryController {

    @FXML private Button btnIniciarSesion;
    @FXML private TextField TxtFieldUsuario;
    @FXML private PasswordField PasswordField;
    @FXML private Label lblErrores;
    @FXML private Label lblUsuario;
    @FXML private Label lblContra;

    @FXML
    public void initialize() {
        btnIniciarSesion.setOnAction(event -> {
            try {
                String usuario = TxtFieldUsuario.getText().trim();
                String contra = PasswordField.getText().trim();

                if (usuario.isEmpty() || contra.isEmpty()) {
                    throw new IllegalArgumentException("Los campos no pueden estar vacíos.");
                }

                // Validar credenciales contra la base de datos
                boolean credencialesValidas = UsuarioABD.validarCredenciales(usuario, contra);
                if (!credencialesValidas) {
                    throw new SecurityException("¡Usuario o contraseña incorrectos!");
                }

                // Obtener el nivel del usuario (necesitas una columna "nivel" en Usuario)
                // Si no tienes nivel, asigna 1 = Administrador, 2 = Empleado según el nombre
                int nivelUsuario = obtenerNivelUsuario(usuario);

                abrirSistemaPrincipal(nivelUsuario, usuario);

            } catch (IllegalArgumentException | SecurityException e) {
                lblErrores.setStyle("-fx-text-fill: red;");
                lblErrores.setText(e.getMessage());
            } catch (SQLException e) {
                lblErrores.setStyle("-fx-text-fill: red;");
                lblErrores.setText("Error de conexión con la base de datos");
                e.printStackTrace();
            } catch (Exception e) {
                lblErrores.setStyle("-fx-text-fill: red;");
                lblErrores.setText("Ocurrió un error inesperado en el inicio de sesión.");
                e.printStackTrace();
            }
        });
    }

    private int obtenerNivelUsuario(String usuario) {
        // Temporal: define niveles según el nombre del usuario
        // Puedes guardar el nivel en la base de datos (columna "nivel") y leerlo aquí.
        if ("Administrador".equalsIgnoreCase(usuario)) {
            return 1;  // Administrador
        } else if ("Empleado".equalsIgnoreCase(usuario)) {
            return 2;  // Empleado
        }
        // Si hay otros usuarios, por defecto empleado
        return 2;
    }

    private void abrirSistemaPrincipal(int nivelUsuario, String nombreUsuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallas/PantallaPrincipal.fxml"));
            Parent root = loader.load();
            PantallaPrincipalController controller = loader.getController();
            controller.configurarPermisos(nivelUsuario, nombreUsuario);
            Stage stage = new Stage();
            stage.setTitle("Sistema de Ventas");
            stage.setScene(new Scene(root, 917, 660));
            stage.show();
            Stage actual = (Stage) btnIniciarSesion.getScene().getWindow();
            actual.close();
        } catch (IOException e) {
            mostrarAlertaError("Error de Sistema", "No se pudo cargar la pantalla principal.");
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirRecuperacion() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/pantallas/Recuperar.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            Stage actual = (Stage) btnIniciarSesion.getScene().getWindow();
            actual.close();
        } catch (IOException e) {
            mostrarAlertaError("Error de Sistema", "No se pudo cargar la pantalla de recuperación.");
            e.printStackTrace();
        }
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}