package org.uacm.puntodeventa.DB;

import org.uacm.puntodeventa.Clases.Recarga;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class RecargaABD {

    public static void insertar(Recarga r) throws SQLException {
        asegurarColumnaRastreo();

        String sql = "INSERT INTO Recargas (folio, numero_telefonico, compania, monto, fecha, id_usuario, metodo_pago, num_rastreo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, r.getFolio());
            pstmt.setString(2, r.getNumero());
            pstmt.setString(3, r.getCompania());
            pstmt.setDouble(4, r.getMonto());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(5, r.getFecha().format(formatter));

            pstmt.setInt(6, r.getIdEmpleado());
            pstmt.setString(7, r.getMetodoPago());
            pstmt.setString(8, r.getNumeroRastreo());

            pstmt.executeUpdate();
        }
    }

    private static void asegurarColumnaRastreo() {
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE Recargas ADD COLUMN num_rastreo TEXT DEFAULT 'N/A'");
        } catch (SQLException ignored) {
        }
    }
}