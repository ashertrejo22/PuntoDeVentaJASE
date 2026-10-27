package org.uacm.puntodeventa.DB;

import org.uacm.puntodeventa.Clases.Venta;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class VentaABD {
    public static int insertar(Venta venta) throws SQLException {
            String sql = "INSERT INTO Venta (folio, fecha, total_a_pagar, id_usuario, metodo_pago) VALUES (?,?,?,?,?)";

            try (Connection conn = ConexionBD.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, venta.getFolio());

                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                pstmt.setString(2, venta.getFecha_venta().format(formatter)); 

                pstmt.setDouble(3, venta.getTotal());
                pstmt.setInt(4, 1); 
                
                pstmt.setString(5, venta.getMetodoPago());

                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                    else throw new SQLException("No se pudo obtener id_venta");
                }
            }
        }
}