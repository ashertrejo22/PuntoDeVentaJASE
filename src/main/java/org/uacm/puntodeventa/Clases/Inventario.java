package org.uacm.puntodeventa.Clases;

import java.util.ArrayList;
import java.util.List;

public class Inventario {

    private List<Producto> Productos;
    
    public Inventario() {
        this.Productos = new ArrayList<>();
    }

    /**
     * Busca un producto por ID en el inventario
     *
     * @param id ID del producto a buscar
     * @return Producto encontrado o null si no existe.
     */
    public Producto buscarID(int id) {
        for (Producto p : Productos) {
            if (p.getID_producto() == id) {
                return p;
            }
        }
        return null;
    }

    /**
     * Busca por nombre un producto en inventario
     *
     * @param nombre Nombre del producto a buscar
     * @return Una lista de productos que coinciden.
     */
    public List<Producto> buscarNombre(String nombre) {
        List<Producto> resultados = new ArrayList<>();
        for (Producto p : Productos) {
            if (p.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    /**
     * Elimina un producto por ID
     *
     * @param id identificador de producto a eliminar
     */    
    public void eliminarProductoID(int id) {
        Productos.removeIf(p -> p.getID_producto() == id);
    }

    /**
     * Elimina un producto por nombre
     *
     * @param nombre nombre del producto a eliminar
     */    
    public void eliminarProductoNombre(String nombre) {
        Productos.removeIf(p -> p.getNombre().equalsIgnoreCase(nombre));
    }
    
    /**
     * Modifica un producto por nombre
     * @param id Nuevo ID
     * @param nuevoNombre Nuevo nombre
     * @param nuevoPrecioBase Nuevo precio base
     * @param nuevoStock Nuevo stock
     * @param ruta Nueva ruta de imagen
     * @return Retorna el producto modificado
     */
    public Producto modificarProducto(int id, String nuevoNombre, double nuevoPrecioBase, int nuevoStock, String ruta) {
        for (Producto p : Productos) {
            if (p.getID_producto() == id) {
                p.setNombre(nuevoNombre);
                p.setPrecio_prov(nuevoPrecioBase);
                p.agregarStock(nuevoStock);   // ← Cambiado: minúscula
                p.setRutaImag(ruta);
                return p;
            }
        }
        return null;        
    }
    
}