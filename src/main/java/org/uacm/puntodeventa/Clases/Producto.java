package org.uacm.puntodeventa.Clases;

/**
 * Representa un producto del sistema de punto de venta.
 * Fusiona los atributos base del equipo con el módulo de
 * alertas de inventario y reabastecimiento.
 *
 * @version 2.0 (Integrada)
 */
public class Producto {

    // --- ATRIBUTOS GENERALES ---
    private int ID_producto;
    private String nombre;
    private String tipo;
    private double stock;
    private double precio_prov;
    private double precio_venta;
    private String rutaImagen;
    private String categoria;

    // --- ATRIBUTOS DE INVENTARIO Y ALERTAS ---
    private double stockMinimo = 10.0;
    private String proveedor = "Proveedor general";
    private double cantidadReabastecimiento = 20.0;

    /**
     * Constructor principal unificado requerido por controladores y clases hijas.
     */
    public Producto(int id, String nom, String tip, double stock, double precio_pro, String ruta, String categoria) {
        this.ID_producto = id;
        this.nombre = nom;
        this.tipo = tip;
        setStock(stock); // Usa la validación de seguridad
        this.precio_prov = precio_pro;
        this.precio_venta = calcularPrecioVenta(precio_pro);
        this.rutaImagen = ruta;
        this.categoria = categoria;
    }

    public double calcularPrecioVenta(double precio) {
        return Math.ceil(precio + precio * 0.2);
    }

    public boolean reducirStock(double cantidad) {
        if (cantidad <= 0) {
            return false;
        }
        if (this.stock >= cantidad) {
            this.stock -= cantidad;
            return true;
        }
        return false;
    }

    public void agregarStock(double cantidad) {
        if (!Double.isFinite(cantidad) || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        this.stock += cantidad;
    }

    // --- MÉTODOS DE ALERTAS ---

    public boolean necesitaReabastecimiento() {
        return stock <= stockMinimo;
    }

    public String getEstadoInventario() {
        if (stock <= 0) {
            return "Sin existencias";
        }
        if (necesitaReabastecimiento()) {
            return "Stock bajo";
        }
        return "Stock normal";
    }

    // --- GETTERS Y SETTERS GENERALES ---

    public int getID_producto() { return ID_producto; }
    public void setID_producto(int ID_producto) { this.ID_producto = ID_producto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getStock() { return stock; }
    
    public void setStock(double stock) {
        if (!Double.isFinite(stock) || stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        this.stock = stock;
    }

    public double getPrecio_prov() { return precio_prov; }
    
    public void setPrecio_prov(double precio_prov) {
        this.precio_prov = precio_prov;
        this.precio_venta = calcularPrecioVenta(precio_prov);
    }

    public double getPrecio_venta() { return precio_venta; }
    public void setPrecio_venta(double precio_venta) { this.precio_venta = precio_venta; }

    public String getRutaImag() { return rutaImagen; }
    public void setRutaImag(String ruta) { this.rutaImagen = ruta; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    // --- GETTERS Y SETTERS DE ALERTAS ---

    public double getStockMinimo() { return stockMinimo; }
    
    public void setStockMinimo(double stockMinimo) {
        if (!Double.isFinite(stockMinimo) || stockMinimo < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo.");
        }
        this.stockMinimo = stockMinimo;
    }

    public String getProveedor() { return proveedor; }
    
    public void setProveedor(String proveedor) {
        if (proveedor == null || proveedor.trim().isEmpty()) {
            throw new IllegalArgumentException("El proveedor no puede estar vacío.");
        }
        this.proveedor = proveedor.trim();
    }

    public double getCantidadReabastecimiento() { return cantidadReabastecimiento; }
    
    public void setCantidadReabastecimiento(double cantidadReabastecimiento) {
        if (!Double.isFinite(cantidadReabastecimiento) || cantidadReabastecimiento <= 0) {
            throw new IllegalArgumentException("La cantidad de reabastecimiento debe ser mayor que cero.");
        }
        this.cantidadReabastecimiento = cantidadReabastecimiento;
    }

    public String mostrarDetalles() {
        return "\nID: " + ID_producto
             + "\nNombre: " + nombre
             + "\nTipo: " + tipo
             + "\nCategoría: " + categoria
             + "\nStock: " + stock
             + "\nPrecio proveedor: " + precio_prov
             + "\nPrecio venta: " + precio_venta
             + "\nStock mínimo: " + stockMinimo
             + "\nProveedor: " + proveedor
             + "\nCantidad de reabastecimiento: " + cantidadReabastecimiento
             + "\nEstado: " + getEstadoInventario();
    }
}