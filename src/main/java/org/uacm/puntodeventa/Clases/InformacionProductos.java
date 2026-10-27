package org.uacm.puntodeventa.Clases;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.uacm.puntodeventa.DB.ProductosADB;
import java.sql.SQLException;
import org.uacm.puntodeventa.DB.ProductosADB;

public class InformacionProductos {
    private static InformacionProductos instancia;
    private ObservableList<Producto> productos;

    private InformacionProductos() {
        try {
            productos = new ProductosADB().obtenerTodos();
        } catch (SQLException e) {
            e.printStackTrace();
            productos = FXCollections.observableArrayList();
        }
    }

    public static InformacionProductos getInstance() {
        if (instancia == null) instancia = new InformacionProductos();
        return instancia;
    }

    public ObservableList<Producto> getProductos() {
        return productos;
    }

    public void agregarProducto(Producto p) {
        try {
            new ProductosADB().insertar(p);
            // Recargar la lista completa para mantener sincronización con IDs generados
            productos.setAll(new ProductosADB().obtenerTodos());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarProducto(Producto p) {
        try {
            new ProductosADB().actualizar(p);
            // Actualizar el objeto en la lista local
            int idx = productos.indexOf(p);
            if (idx >= 0) productos.set(idx, p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarProducto(int id) {
        try {
            new ProductosADB().eliminar(id);
            productos.removeIf(prod -> prod.getID_producto() == id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarStock(int id, double nuevoStock) {
        try {
            new ProductosADB().actualizarStock(id, nuevoStock);
            // Reflejar en la lista local
            for (Producto p : productos) {
                if (p.getID_producto() == id) {
                    p.setStock(nuevoStock);
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}