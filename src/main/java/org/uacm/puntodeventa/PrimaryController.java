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
    //Pantalla Login
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

                // Obtener nivel e ID del usuario
                int nivelUsuario = obtenerNivelUsuario(usuario);
                int idUsuario = obtenerIdUsuario(usuario); // <-- SE OBTIENE EL ID

                // Se envían los tres datos a la pantalla principal
                abrirSistemaPrincipal(idUsuario, nivelUsuario, usuario);

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

    // NUEVO MÉTODO: Define el ID real según el usuario
    private int obtenerIdUsuario(String usuario) {
        // Idealmente, en el futuro puedes hacer un "UsuarioABD.obtenerId(usuario)" 
        // Por ahora, lo asignamos manualmente según el nombre igual que el nivel
        if ("Administrador".equalsIgnoreCase(usuario)) {
            return 1; 
        } else if ("Empleado".equalsIgnoreCase(usuario)) {
            return 2; // Asegúrate de que el empleado sea el ID 2 en tu base de datos
        }
        return 3;
    }

    private int obtenerNivelUsuario(String usuario) {
        if ("Administrador".equalsIgnoreCase(usuario)) {
            return 1;  // Administrador
        } else if ("Empleado".equalsIgnoreCase(usuario)) {
            return 2;  // Empleado
        }
        return 2;
    }

    // SE ACTUALIZÓ: Ahora recibe y envía idUsuario
    private void abrirSistemaPrincipal(int idUsuario, int nivelUsuario, String nombreUsuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallas/PantallaPrincipal.fxml"));
            Parent root = loader.load();
            PantallaPrincipalController controller = loader.getController();
            
            // SE ACTUALIZÓ: Se pasa el idUsuario a la pantalla principal
            controller.configurarPermisos(nivelUsuario, nombreUsuario, idUsuario);
            
            Stage stage = new Stage();
            stage.setTitle("Sistema de Ventas");
            stage.setScene(new Scene(root, 917, 660));
            stage.setMaximized(true);
            stage.show();
            
            // Cerrar la ventana de Login
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
            
            // Cerrar la ventana de Login
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