package org.uacm.puntodeventa.DB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class IniciarBD {
    public static void inicializar() {
        String sql = 
            "CREATE TABLE IF NOT EXISTS Producto (" +
            "    id_producto INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    nombre TEXT NOT NULL," +
            "    tipo TEXT NOT NULL," +
            "    stock REAL," +
            "    precio_proveedor REAL NOT NULL," +
            "    precio_venta REAL NOT NULL," +
            "    ruta_imagen TEXT," +
            "    fecha_caducidad TEXT," +
            "    lote TEXT," +
            "    unidad_medida TEXT" +
            ");" +
            "CREATE TABLE IF NOT EXISTS Usuario (" +
            "    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    nombre TEXT NOT NULL," +
            "    contrasena TEXT NOT NULL" +
            ");" +
            "CREATE TABLE IF NOT EXISTS Venta (" +
            "    id_venta INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    folio TEXT NOT NULL UNIQUE," +
            "    fecha DATE NOT NULL," +
            "    total_a_pagar REAL NOT NULL," +
            "    id_usuario INTEGER NOT NULL," +
            "    metodo_pago TEXT," +
            "    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)" +
            ");" +
            "CREATE TABLE IF NOT EXISTS DetalleVenta (" +
            "    id_detalle INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    cantidad REAL NOT NULL," +
            "    precio_unitario REAL NOT NULL," +
            "    id_producto INTEGER NOT NULL," +
            "    id_venta INTEGER NOT NULL," +
            "    FOREIGN KEY (id_producto) REFERENCES Producto(id_producto)," +
            "    FOREIGN KEY (id_venta) REFERENCES Venta(id_venta)" +
            ");" +
            "CREATE TABLE IF NOT EXISTS Recargas (" +
            "    id_recarga INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    folio TEXT NOT NULL UNIQUE," +
            "    numero_telefonico TEXT NOT NULL," +
            "    compania TEXT NOT NULL," +
            "    monto REAL NOT NULL," +
            "    fecha TEXT NOT NULL," +
            "    id_usuario INTEGER NOT NULL," +
            "    metodo_pago TEXT," +
            "    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)" +
            ");";

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql); 
            
            // SEGURIDAD EXTRA: Si la tabla Recargas ya existía de antes sin la columna, se la añadimos al vuelo
            try {
                stmt.execute("ALTER TABLE Recargas ADD COLUMN metodo_pago TEXT;");
            } catch (SQLException ex) {
                // Si la columna ya existe, SQLite lanzará una excepción que ignoramos de manera segura
            }

            System.out.println("Tablas creadas y actualizadas correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}