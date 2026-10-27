package org.uacm.puntodeventa.DB;

import org.uacm.puntodeventa.Clases.Venta;
import java.sql.*;

public class VentaABD {

    public static int insertar(Venta venta) throws SQLException {
    String sql = "INSERT INTO Venta (folio, fecha, total_a_pagar, id_usuario) VALUES (?,?,?,?)";
    try (Connection conn = ConexionBD.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        pstmt.setString(1, venta.getFolio());
        pstmt.setString(2, venta.getFecha_venta().toString()); 
        pstmt.setDouble(3, venta.getTotal());
        pstmt.setInt(4, 1);
        System.out.println("Total a insertar en BD: " + venta.getTotal());
        pstmt.executeUpdate();
        try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) return rs.getInt(1);
            else throw new SQLException("No se pudo obtener id_venta");
        }
    }
    }
}