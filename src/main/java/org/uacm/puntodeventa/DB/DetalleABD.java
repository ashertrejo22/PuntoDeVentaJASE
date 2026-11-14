package org.uacm.puntodeventa.DB;

import java.sql.*;

public class DetalleABD {

    public static void insertar(int idVenta, int idProducto, double cantidad, double precioUnitario) throws SQLException {
        String sql = "INSERT INTO DetalleVenta (id_venta, id_producto, cantidad, precio_unitario) VALUES (?,?,?,?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idVenta);
            pstmt.setInt(2, idProducto);
            pstmt.setDouble(3, cantidad);
            pstmt.setDouble(4, precioUnitario);
            pstmt.executeUpdate();
        }
    }
}