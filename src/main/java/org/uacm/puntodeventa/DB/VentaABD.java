package org.uacm.puntodeventa.DB;

import org.uacm.puntodeventa.Clases.Venta;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class VentaABD {
public static int insertar(Venta venta) throws SQLException {
        // 1. Asegúrate de que id_usuario tenga un signo de interrogación (?), NO un 1
        String sql = "INSERT INTO Venta (folio, fecha, total_a_pagar, id_usuario, metodo_pago, num_rastreo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)"; 
                     
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            pstmt.setString(1, venta.getFolio());
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            pstmt.setString(2, venta.getFecha_venta().format(dtf));
            
            pstmt.setDouble(3, venta.getTotalAPagar());
            
            // 2. Extraemos el ID real del objeto Venta
            pstmt.setInt(4, venta.getIdEmpleado()); 
            
            pstmt.setString(5, venta.getMetodoPago());
            pstmt.setString(6, venta.getNumeroRastreo());

            pstmt.executeUpdate();

            // Obtenemos el id_venta generado
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
}