
package org.uacm.puntodeventa.DB;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Administra la conexión a la base de datos SQLite
 * del sistema de punto de venta.
 *
 * Utiliza una ruta relativa al directorio del proyecto
 * para evitar depender de la computadora de un integrante.
 */
public class ConexionBD {

    /** Ubicación de la base de datos dentro del proyecto. */
    private static final Path RUTA_BD =
            Paths.get("BD", "BDConstruccion.db")
                    .toAbsolutePath()
                    .normalize();

    /** URL de conexión a SQLite. */
    private static final String URL =
            "jdbc:sqlite:" + RUTA_BD;

    /** Conexión compartida con la base de datos. */
    private static Connection connection = null;

    /**
     * Obtiene una conexión activa con la base de datos.
     *
     * @return conexión SQLite
     * @throws SQLException si el archivo no existe
     *                      o no se puede establecer la conexión
     */
    public static Connection getConnection() throws SQLException {

        if (!Files.isRegularFile(RUTA_BD)) {
            throw new SQLException(
                    "No se encontró la base de datos en: " + RUTA_BD
            );
        }

        if (connection == null || connection.isClosed()) {

            connection = DriverManager.getConnection(URL);

            // Activar integridad referencial de SQLite.
            try (var stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        }

        return connection;
    }

    /**
     * Cierra la conexión activa con la base de datos.
     */
    public static void closeConnection() {

        if (connection != null) {

            try {
                connection.close();
                connection = null;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
