
package org.uacm.puntodeventa.DB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Inicializa las tablas de la base de datos SQLite
 * del sistema de punto de venta.
 *
 * Las tablas se crean únicamente cuando no existen,
 * conservando la información almacenada previamente.
 */
public class IniciarBD {

    /**
     * Crea las tablas necesarias para el funcionamiento
     * del sistema de punto de venta.
     *
     * Utiliza la conexión definida en ConexionBD
     * y ejecuta cada sentencia SQL individualmente.
     */
    public static void inicializar() {

        String sqlProducto =
                "CREATE TABLE IF NOT EXISTS Producto ("
                + "id_producto INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "tipo TEXT NOT NULL,"
                + "stock REAL,"
                + "precio_proveedor REAL NOT NULL,"
                + "precio_venta REAL NOT NULL,"
                + "ruta_imagen TEXT,"
                + "fecha_caducidad TEXT,"
                + "lote TEXT,"
                + "unidad_medida TEXT"
                + ");";

        String sqlUsuario =
                "CREATE TABLE IF NOT EXISTS Usuario ("
                + "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "contrasena TEXT NOT NULL"
                + ");";

        String sqlVenta =
                "CREATE TABLE IF NOT EXISTS Venta ("
                + "id_venta INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "folio TEXT NOT NULL UNIQUE,"
                + "fecha DATE NOT NULL,"
                + "total_a_pagar REAL NOT NULL,"
                + "id_usuario INTEGER NOT NULL,"
                + "FOREIGN KEY (id_usuario) "
                + "REFERENCES Usuario(id_usuario)"
                + ");";

        String sqlDetalleVenta =
                "CREATE TABLE IF NOT EXISTS DetalleVenta ("
                + "id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "cantidad REAL NOT NULL,"
                + "precio_unitario REAL NOT NULL,"
                + "id_producto INTEGER NOT NULL,"
                + "id_venta INTEGER NOT NULL,"
                + "FOREIGN KEY (id_producto) "
                + "REFERENCES Producto(id_producto),"
                + "FOREIGN KEY (id_venta) "
                + "REFERENCES Venta(id_venta)"
                + ");";

        String sqlRecargas =
                "CREATE TABLE IF NOT EXISTS Recargas ("
                + "id_recarga INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "folio TEXT NOT NULL UNIQUE,"
                + "numero_telefonico TEXT NOT NULL,"
                + "compania TEXT NOT NULL,"
                + "monto REAL NOT NULL,"
                + "fecha TEXT NOT NULL,"
                + "id_usuario INTEGER NOT NULL,"
                + "FOREIGN KEY (id_usuario) "
                + "REFERENCES Usuario(id_usuario)"
                + ");";

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sqlProducto);
            stmt.executeUpdate(sqlUsuario);
            stmt.executeUpdate(sqlVenta);
            stmt.executeUpdate(sqlDetalleVenta);
            stmt.executeUpdate(sqlRecargas);

            System.out.println(
                    "Tablas de la base de datos verificadas correctamente."
            );

        } catch (SQLException e) {

            System.err.println(
                    "Error al inicializar la base de datos: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}
