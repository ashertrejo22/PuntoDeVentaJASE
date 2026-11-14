package org.uacm.puntodeventa.DB;

import org.uacm.puntodeventa.Clases.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class ProductosADB {
    
    public ObservableList<Producto> obtenerTodos() throws SQLException {
        ObservableList<Producto> lista = FXCollections.observableArrayList();
        // --- SE AGREGÓ 'categoria' AL SELECT ---
        String sql = "SELECT id_producto, nombre, tipo, stock, precio_proveedor, precio_venta, ruta_imagen, fecha_caducidad, lote, unidad_medida, categoria FROM Producto";
        
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
                
                // --- SE RECUPERA LA CATEGORÍA ---
                String categoria = rs.getString("categoria");
                if (categoria == null) categoria = "General"; // Valor por defecto si está vacío

                Producto producto;
                if ("Perecedero".equals(tipo)) {
                    LocalDate fechaCad = (fechaCadStr != null) ? LocalDate.parse(fechaCadStr) : null;
                    // Se pasa la categoría al constructor
                    producto = new ProductoPerecedero(id, nombre, tipo, stock, precioProv, fechaCad, lote, ruta, categoria);
                } else if ("Granel".equals(tipo)) {
                    // Se pasa la categoría al constructor
                    producto = new ProductoGranel(id, nombre, tipo, stock, precioProv, unidadMedida, ruta, categoria);
                } else {
                    // Se pasa la categoría al constructor
                    producto = new Producto(id, nombre, tipo, stock, precioProv, ruta, categoria);
                }
                
                producto.setPrecio_venta(precioVenta);
                lista.add(producto);
            }
        }
        return lista;
    }

    public void insertar(Producto p) throws SQLException {
        // --- SE AGREGÓ 'categoria' Y UN '?' EXTRA AL INSERT ---
        String sql = "INSERT INTO Producto (nombre, tipo, stock, precio_proveedor, precio_venta, ruta_imagen, fecha_caducidad, lote, unidad_medida, categoria) VALUES (?,?,?,?,?,?,?,?,?,?)";
        
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
            
            // --- SE INSERTA LA CATEGORÍA EN LA BD ---
            pstmt.setString(10, p.getCategoria());
            
            pstmt.executeUpdate();
        }
        System.out.println("Insertando producto: " + p.getNombre() + ", tipo: " + p.getTipo() + ", categoria: " + p.getCategoria());
    }

    public void actualizar(Producto p) throws SQLException {
        // --- SE AGREGÓ 'categoria=?' AL UPDATE ---
        String sql = "UPDATE Producto SET nombre=?, tipo=?, stock=?, precio_proveedor=?, precio_venta=?, ruta_imagen=?, fecha_caducidad=?, lote=?, unidad_medida=?, categoria=? WHERE id_producto=?";
        
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
            
            // --- SE ACTUALIZA LA CATEGORÍA EN LA BD ---
            pstmt.setString(10, p.getCategoria());
            
            // El ID ahora es el parámetro 11 porque agregamos la categoría antes
            pstmt.setInt(11, p.getID_producto());
            
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