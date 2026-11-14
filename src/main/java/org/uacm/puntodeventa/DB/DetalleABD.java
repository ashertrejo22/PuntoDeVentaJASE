package org.uacm.puntodeventa.DB;

import java.sql.*;
import org.uacm.puntodeventa.Clases.InformacionProductos;
import org.uacm.puntodeventa.Clases.Producto;

public class DetalleABD {

    public static void insertar(int idVenta, int idProducto, double cantidad, double precioUnitario) throws SQLException {
        String sqlDetalle = "INSERT INTO DetalleVenta (id_venta, id_producto, cantidad, precio_unitario) VALUES (?,?,?,?)";
        String sqlStock = "UPDATE Producto SET stock = stock - ? WHERE id_producto = ?";

        try (Connection conn = ConexionBD.getConnection()) {
            
            // 1. Insertar el detalle
            try (PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle)) {
                pstmtDetalle.setInt(1, idVenta);
                pstmtDetalle.setInt(2, idProducto);
                pstmtDetalle.setDouble(3, cantidad);
                pstmtDetalle.setDouble(4, precioUnitario);
                pstmtDetalle.executeUpdate();
            }

            // 2. Descontar en la Base de Datos SQLite
            try (PreparedStatement pstmtStock = conn.prepareStatement(sqlStock)) {
                pstmtStock.setDouble(1, cantidad);
                pstmtStock.setInt(2, idProducto);
                pstmtStock.executeUpdate();
            }

            // 3. ¡ACTUALIZAR LA MEMORIA LOCAL EN VIVO!
            for (Producto p : InformacionProductos.getInstance().getProductos()) {
                if (p.getID_producto() == idProducto) {
                    double nuevoStock = p.getStock() - cantidad;
                    p.setStock(nuevoStock);
                    break;
                }
            }
        }
    }
}