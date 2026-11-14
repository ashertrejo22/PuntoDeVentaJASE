package org.uacm.puntodeventa.Clases;

public class ProductoCarrito {
    private final int id;
    private final String nombre;
    private final double precio;
    private double cantidad; 
    private final double stock; 

    public ProductoCarrito(Producto p, double cantidad) {
        this.id = p.getID_producto();
        this.nombre = p.getNombre();
        this.precio = p.getPrecio_venta();
        this.cantidad = cantidad;
        this.stock = p.getStock();
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    
    public double getTotal() { return precio * cantidad; }
    public double getStock() { return stock; }
}