package org.uacm.puntodeventa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.uacm.puntodeventa.Clases.Producto;
import org.uacm.puntodeventa.Clases.Venta;
import org.uacm.puntodeventa.DB.ConexionBD;
import org.uacm.puntodeventa.DB.IniciarBD;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        IniciarBD.inicializar();
            Parent root = FXMLLoader.load(getClass().getResource("/pantallas/Login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setTitle("Iniciar Sesión");
        stage.show();
        try {
    Class.forName("org.sqlite.JDBC");
    try (Connection conn = ConexionBD.getConnection()) {
        // Ejecutar script de creación de tablas (o usar DB Browser manualmente)
    }
} catch (ClassNotFoundException | SQLException e) {
    e.printStackTrace();
}
       
    }

    public static void main(String[] args) {
        launch();
        }
        
}