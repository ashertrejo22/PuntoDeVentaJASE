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
            "CREATE TABLE IF NOT EXISTS Usuario ("
            + "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "nombre TEXT NOT NULL, "
            + "contrasena TEXT NOT NULL, "
            + "nivel INTEGER DEFAULT 2, "
            + "activo INTEGER DEFAULT 1"
            + ");" +
            "CREATE TABLE IF NOT EXISTS Venta (" +
            "    id_venta INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    folio TEXT NOT NULL UNIQUE," +
            "    fecha DATE NOT NULL," +
            "    total_a_pagar REAL NOT NULL," +
            "    id_usuario INTEGER NOT NULL," +
            "    metodo_pago TEXT," +
            "    num_rastreo TEXT DEFAULT 'N/A'," +
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
            "    num_rastreo TEXT DEFAULT 'N/A'," +
            "    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)" +
            ");" +
            "CREATE TABLE IF NOT EXISTS CorteCaja (" +
            "    id_corte INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    folio TEXT NOT NULL UNIQUE," +
            "    id_usuario INTEGER NOT NULL," +
            "    fecha_apertura TEXT NOT NULL," +
            "    fondo_inicial REAL NOT NULL," +
            "    fecha_cierre TEXT," +
            "    efectivo_esperado REAL," +
            "    efectivo_contado REAL," +
            "    diferencia REAL," +
            "    estado TEXT NOT NULL," +
            "    observaciones TEXT," +
            "    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)" +
            ");" +
            "CREATE TABLE IF NOT EXISTS MovimientoCaja (" +
            "    id_movimiento INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    id_corte INTEGER NOT NULL," +
            "    tipo TEXT NOT NULL," +
            "    monto REAL NOT NULL," +
            "    motivo TEXT," +
            "    fecha TEXT NOT NULL," +
            "    FOREIGN KEY (id_corte) REFERENCES CorteCaja(id_corte)" +
            ");" +
            "CREATE TABLE IF NOT EXISTS ConteoEfectivo (" +
            "    id_corte INTEGER NOT NULL," +
            "    denominacion TEXT NOT NULL," +
            "    cantidad INTEGER NOT NULL," +
            "    PRIMARY KEY (id_corte, denominacion)," +
            "    FOREIGN KEY (id_corte) REFERENCES CorteCaja(id_corte)" +
            ");";

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Cada CREATE TABLE se ejecuta por separado: el driver SQLite solo corre la primera
            // sentencia cuando se le entregan varias juntas en un mismo execute.
            for (String sentencia : sql.split(";")) {
                if (!sentencia.trim().isEmpty()) {
                    stmt.execute(sentencia);
                }
            }
            
            // SEGURIDAD EXTRA: Evita que el programa falle si la BD ya existía sin las nuevas columnas
            try {
                stmt.execute("ALTER TABLE Recargas ADD COLUMN metodo_pago TEXT;");
            } catch (SQLException ex) { }
            
            try {
                stmt.execute("ALTER TABLE Recargas ADD COLUMN num_rastreo TEXT DEFAULT 'N/A';");
            } catch (SQLException ex) { }
            
            try {
                stmt.execute("ALTER TABLE Venta ADD COLUMN num_rastreo TEXT DEFAULT 'N/A';");
            } catch (SQLException ex) { }

            // Corte de Caja: cada venta queda ligada al turno que la cobró
            try {
                stmt.execute("ALTER TABLE Venta ADD COLUMN id_corte INTEGER REFERENCES CorteCaja(id_corte);");
            } catch (SQLException ex) { }

            // Fechas antiguas guardadas como milisegundos: sin convertirlas no aparecerían en los reportes
            for (String tabla : new String[] {"Venta", "Recargas"}) {
                stmt.executeUpdate("UPDATE " + tabla + " SET fecha = date(CAST(fecha AS INTEGER) / 1000, "
                        + "'unixepoch', 'localtime') WHERE fecha NOT LIKE '____-__-__%' "
                        + "AND CAST(fecha AS INTEGER) > 0");
            }

            System.out.println("Tablas creadas y actualizadas correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}