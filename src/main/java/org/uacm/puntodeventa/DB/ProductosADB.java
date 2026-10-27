package org.uacm.puntodeventa.DB;

import org.uacm.puntodeventa.Clases.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class ProductosADB {
    public ObservableList<Producto> obtenerTodos() throws SQLException {
        ObservableList<Producto> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_producto, nombre, tipo, stock, precio_proveedor, precio_venta, ruta_imagen, fecha_caducidad, lote, unidad_medida FROM Producto";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id_producto");
                String nombre = rs.getString("nombre");
                String tipo = rs.getString("tipo");
                double stock = rs.getDouble("stock");
                double precioProv = rs.getDouble("precio_proveedor");
                double precioVenta = rs.getDouble("precio_venta");
                String ruta = rs.getString("ruta_imagen");
                String fechaCadStr = rs.getString("fecha_caducidad");
                String lote = rs.getString("lote");
                String unidadMedida = rs.getString("unidad_medida");

                Producto producto;
                if ("Perecedero".equals(tipo)) {
                    LocalDate fechaCad = (fechaCadStr != null) ? LocalDate.parse(fechaCadStr) : null;
                    producto = new ProductoPerecedero(id, nombre, tipo, stock, precioProv, fechaCad, lote, ruta);
                } else if ("Granel".equals(tipo)) {
                    producto = new ProductoGranel(id, nombre, tipo, stock, precioProv, unidadMedida, ruta);
                } else {
                    producto = new Producto(id, nombre, tipo, stock, precioProv, ruta);
                }
                producto.setPrecio_venta(precioVenta);
                lista.add(producto);
            }
        }
        return lista;
    }
    public void insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO Producto (nombre, tipo, stock, precio_proveedor, precio_venta, ruta_imagen, fecha_caducidad, lote, unidad_medida) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getTipo());
            pstmt.setDouble(3, p.getStock());
            pstmt.setDouble(4, p.getPrecio_prov());
            pstmt.setDouble(5, p.getPrecio_venta());
            pstmt.setString(6, p.getRutaImag());
            if (p instanceof ProductoPerecedero) {
                ProductoPerecedero pp = (ProductoPerecedero) p;
                pstmt.setString(7, pp.getFechaCaducidad().toString());
                pstmt.setString(8, pp.getLote());
                pstmt.setString(9, null);
            } else if (p instanceof ProductoGranel) {
                ProductoGranel pg = (ProductoGranel) p;
                pstmt.setString(7, null);
                pstmt.setString(8, null);
                pstmt.setString(9, pg.getUnidadMedida());
            } else {
                pstmt.setString(7, null);
                pstmt.setString(8, null);
                pstmt.setString(9, null);
            }
            pstmt.executeUpdate();
        }
        System.out.println("Insertando producto: " + p.getNombre() + ", tipo: " + p.getTipo());
    }
    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE Producto SET nombre=?, tipo=?, stock=?, precio_proveedor=?, precio_venta=?, ruta_imagen=?, fecha_caducidad=?, lote=?, unidad_medida=? WHERE id_producto=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getTipo());
            pstmt.setDouble(3, p.getStock());
            pstmt.setDouble(4, p.getPrecio_prov());
            pstmt.setDouble(5, p.getPrecio_venta());
            pstmt.setString(6, p.getRutaImag());

            if (p instanceof ProductoPerecedero) {
                ProductoPerecedero pp = (ProductoPerecedero) p;
                pstmt.setString(7, pp.getFechaCaducidad().toString());
                pstmt.setString(8, pp.getLote());
                pstmt.setString(9, null);
            } else if (p instanceof ProductoGranel) {
                ProductoGranel pg = (ProductoGranel) p;
                pstmt.setString(7, null);
                pstmt.setString(8, null);
                pstmt.setString(9, pg.getUnidadMedida());
            } else {
                pstmt.setString(7, null);
                pstmt.setString(8, null);
                pstmt.setString(9, null);
            }
            pstmt.setInt(10, p.getID_producto());
            pstmt.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Producto WHERE id_producto = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public void actualizarStock(int id, double nuevoStock) throws SQLException {
        String sql = "UPDATE Producto SET stock = ? WHERE id_producto = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, nuevoStock);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }
}