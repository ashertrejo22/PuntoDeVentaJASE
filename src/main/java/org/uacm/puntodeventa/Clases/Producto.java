package org.uacm.puntodeventa.Clases;

import java.time.LocalDate;

public class Producto {
    private int ID_producto;
    private String nombre;
    private String tipo;
    private double stock;
    private double precio_prov;
    private double precio_venta;
    private String rutaImagen;
    
    public Producto(int id, String nom, String tip, double stock, double precio_pro) {
        this.ID_producto = id;
        this.nombre = nom;
        this.tipo = tip;
        this.stock = stock;
        this.precio_prov = precio_pro;
        this.precio_venta = calcularPrecioVenta(precio_pro);
        this.rutaImagen = null;
    }

    public Producto(int id, String nom, String tip, double stock, double precio_pro, 
                    String ruta) {
        this(id, nom, tip, stock, precio_pro);
        this.precio_venta = calcularPrecioVenta(precio_pro);
        this.rutaImagen = ruta;
    }

    public double calcularPrecioVenta(double precio) {
        return Math.ceil(precio + precio * 0.2);
    }

    public boolean reducirStock(double cantidad) {
        if (this.stock >= cantidad) {
            this.stock -= cantidad;
            return true;
        }
        return false;
    }
    public int getID_producto() {
        return ID_producto;
    }

    public void setID_producto(int ID_producto) {
        this.ID_producto = ID_producto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getStock() {
        return stock;
    }
    
        public void setStock(double stock) {
        this.stock = stock;
    }

    public void agregarStock(double stock) {
        this.stock += stock;
    }

    public double getPrecio_prov() {
        return precio_prov;
    }

    public void setPrecio_prov(double precio_prov) {
        this.precio_prov = precio_prov;
        this.precio_venta = calcularPrecioVenta(precio_prov);
    }

    public double getPrecio_venta() {
        return precio_venta;
    }

    public void setPrecio_venta(double precio_venta) {
        this.precio_venta = precio_venta;
    }
    

    public String getRutaImag() {
        return rutaImagen;
    }

    public void setRutaImag(String ruta) {
        this.rutaImagen = ruta;
    }

    public String mostrarDetalles() {
        String datos = "\nID:" + this.ID_producto +
                       "\nNombre:" + this.nombre +
                       "\nTipo:" + this.tipo +
                       "\nStock:" + this.stock +
                       "\nPrecio proveedor:" + this.precio_prov +
                       "\nPrecio venta:" + this.precio_venta;
        return datos;
    }
}