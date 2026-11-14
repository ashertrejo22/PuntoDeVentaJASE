package org.uacm.puntodeventa.DB;

import org.uacm.puntodeventa.Clases.Recarga;
import java.sql.*;

public class RecargaABD {

    public static void insertar(Recarga r) throws SQLException {
    String sql = "INSERT INTO Recargas (folio, numero_telefonico, compania, monto, fecha, id_usuario) VALUES (?,?,?,?,?,?)";
    try (Connection conn = ConexionBD.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, r.getFolio());
        pstmt.setString(2, r.getNumero());
        pstmt.setString(3, r.getCompania());
        pstmt.setDouble(4, r.getMonto());
        pstmt.setString(5, r.getFecha().toString());
        pstmt.setInt(6, 1);
        pstmt.executeUpdate();
    }
}
}