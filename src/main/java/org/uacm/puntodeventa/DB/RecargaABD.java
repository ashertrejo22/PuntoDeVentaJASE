package org.uacm.puntodeventa.DB;

import org.uacm.puntodeventa.Clases.Recarga;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class RecargaABD {

    public static void insertar(Recarga r) throws SQLException {
        // Consulta SQL explícita con los 7 campos de la tabla
        String sql = "INSERT INTO Recargas (folio, numero_telefonico, compania, monto, fecha, id_usuario, metodo_pago) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, r.getFolio());
            pstmt.setString(2, r.getNumero());
            pstmt.setString(3, r.getCompania());
            pstmt.setDouble(4, r.getMonto());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String fechaTexto = r.getFecha().format(formatter);
            pstmt.setString(5, fechaTexto); 
            
            pstmt.setInt(6, 1); // ID del usuario en sesión
            pstmt.setString(7, r.getMetodoPago()); // El método de pago (Efectivo, Débito o Crédito)
            
            pstmt.executeUpdate();
        }
    }
}