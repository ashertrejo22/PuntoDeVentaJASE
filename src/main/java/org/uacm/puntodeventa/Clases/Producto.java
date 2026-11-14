
package org.uacm.puntodeventa.Clases;

/**
 * Representa un producto del sistema de punto de venta.
 *
 * Almacena los datos generales, precios, existencias
 * e información necesaria para generar alertas de
 * inventario y pedidos de reabastecimiento.
 *
 * Conserva los constructores y métodos utilizados
 * por la versión del grupo.
 *
 * @version 2.0
 */
public class Producto {

    /** Identificador único del producto. */
    private int ID_producto;

    /** Nombre del producto. */
    private String nombre;

    /** Tipo o categoría del producto. */
    private String tipo;

    /** Existencias actuales del producto. */
    private double stock;

    /** Precio de compra al proveedor. */
    private double precio_prov;

    /** Precio de venta al público. */
    private double precio_venta;

    /** Ruta de la imagen del producto. */
    private String rutaImagen;

    /** Cantidad mínima para activar una alerta. */
    private double stockMinimo = 10.0;

    /** Proveedor asociado al producto. */
    private String proveedor = "Proveedor general";

    /** Cantidad sugerida para reabastecimiento. */
    private double cantidadReabastecimiento = 20.0;

    /**
     * Construye un producto sin imagen.
     *
     * @param id identificador del producto
     * @param nom nombre del producto
     * @param tip tipo del producto
     * @param stock existencias iniciales
     * @param precio_pro precio de compra al proveedor
     */
    public Producto(int id, String nom, String tip,
                    double stock, double precio_pro) {

        this.ID_producto = id;
        this.nombre = nom;
        this.tipo = tip;
        setStock(stock);
        this.precio_prov = precio_pro;
        this.precio_venta = calcularPrecioVenta(precio_pro);
        this.rutaImagen = null;
    }

    /**
     * Construye un producto con imagen.
     *
     * @param id identificador del producto
     * @param nom nombre del producto
     * @param tip tipo del producto
     * @param stock existencias iniciales
     * @param precio_pro precio de compra al proveedor
     * @param ruta ruta de la imagen
     */
    public Producto(int id, String nom, String tip,
                    double stock, double precio_pro,
                    String ruta) {

        this(id, nom, tip, stock, precio_pro);
        this.rutaImagen = ruta;
    }

    /**
     * Calcula el precio de venta agregando un 20 %.
     *
     * @param precio precio de compra
     * @return precio de venta redondeado hacia arriba
     */
    public double calcularPrecioVenta(double precio) {
        return Math.ceil(precio + precio * 0.2);
    }

    /**
     * Descuenta existencias cuando hay stock suficiente.
     *
     * @param cantidad cantidad que se desea descontar
     * @return true si se realizó el descuento; false en otro caso
     */
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

    /**
     * Agrega existencias al producto.
     *
     * @param cantidad cantidad que se agregará
     * @throws IllegalArgumentException si la cantidad no es válida
     */
    public void agregarStock(double cantidad) {

        if (!Double.isFinite(cantidad) || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }

        this.stock += cantidad;
    }

    /**
     * Determina si el producto necesita reabastecimiento.
     *
     * @return true cuando el stock es menor o igual al mínimo
     */
    public boolean necesitaReabastecimiento() {
        return stock <= stockMinimo;
    }

    /**
     * Obtiene el estado actual del inventario.
     *
     * @return Sin existencias, Stock bajo o Stock normal
     */
    public String getEstadoInventario() {

        if (stock <= 0) {
            return "Sin existencias";
        }

        if (necesitaReabastecimiento()) {
            return "Stock bajo";
        }

        return "Stock normal";
    }

    /**
     * Obtiene el identificador del producto.
     *
     * @return identificador
     */
    public int getID_producto() {
        return ID_producto;
    }

    /**
     * Modifica el identificador.
     *
     * @param ID_producto nuevo identificador
     */
    public void setID_producto(int ID_producto) {
        this.ID_producto = ID_producto;
    }

    /**
     * Obtiene el nombre del producto.
     *
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Modifica el nombre del producto.
     *
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el tipo de producto.
     *
     * @return tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Modifica el tipo de producto.
     *
     * @param tipo nuevo tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene las existencias actuales.
     *
     * @return stock disponible
     */
    public double getStock() {
        return stock;
    }

    /**
     * Establece las existencias del producto.
     *
     * @param stock nuevas existencias
     * @throws IllegalArgumentException si el valor es negativo
     *                                  o no es finito
     */
    public void setStock(double stock) {

        if (!Double.isFinite(stock) || stock < 0) {
            throw new IllegalArgumentException(
                    "El stock no puede ser negativo.");
        }

        this.stock = stock;
    }

    /**
     * Obtiene el precio de compra.
     *
     * @return precio del proveedor
     */
    public double getPrecio_prov() {
        return precio_prov;
    }

    /**
     * Modifica el precio del proveedor y recalcula
     * automáticamente el precio de venta.
     *
     * @param precio_prov nuevo precio del proveedor
     */
    public void setPrecio_prov(double precio_prov) {
        this.precio_prov = precio_prov;
        this.precio_venta = calcularPrecioVenta(precio_prov);
    }

    /**
     * Obtiene el precio de venta.
     *
     * @return precio de venta
     */
    public double getPrecio_venta() {
        return precio_venta;
    }

    /**
     * Modifica directamente el precio de venta.
     * Se conserva por compatibilidad con la versión del grupo.
     *
     * @param precio_venta nuevo precio de venta
     */
    public void setPrecio_venta(double precio_venta) {
        this.precio_venta = precio_venta;
    }

    /**
     * Obtiene la ruta de la imagen.
     *
     * @return ruta de imagen
     */
    public String getRutaImag() {
        return rutaImagen;
    }

    /**
     * Modifica la ruta de la imagen.
     *
     * @param ruta nueva ruta
     */
    public void setRutaImag(String ruta) {
        this.rutaImagen = ruta;
    }

    /**
     * Obtiene el stock mínimo.
     *
     * @return cantidad mínima permitida
     */
    public double getStockMinimo() {
        return stockMinimo;
    }

    /**
     * Modifica el stock mínimo.
     *
     * @param stockMinimo nuevo mínimo
     * @throws IllegalArgumentException si es negativo o no es finito
     */
    public void setStockMinimo(double stockMinimo) {

        if (!Double.isFinite(stockMinimo) || stockMinimo < 0) {
            throw new IllegalArgumentException(
                    "El stock mínimo no puede ser negativo.");
        }

        this.stockMinimo = stockMinimo;
    }

    /**
     * Obtiene el proveedor del producto.
     *
     * @return nombre del proveedor
     */
    public String getProveedor() {
        return proveedor;
    }

    /**
     * Modifica el proveedor.
     *
     * @param proveedor nombre del proveedor
     * @throws IllegalArgumentException si está vacío o es nulo
     */
    public void setProveedor(String proveedor) {

        if (proveedor == null || proveedor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El proveedor no puede estar vacío.");
        }

        this.proveedor = proveedor.trim();
    }

    /**
     * Obtiene la cantidad sugerida para reabastecimiento.
     *
     * @return cantidad a solicitar
     */
    public double getCantidadReabastecimiento() {
        return cantidadReabastecimiento;
    }

    /**
     * Modifica la cantidad de reabastecimiento.
     *
     * @param cantidadReabastecimiento cantidad a solicitar
     * @throws IllegalArgumentException si no es positiva o finita
     */
    public void setCantidadReabastecimiento(
            double cantidadReabastecimiento) {

        if (!Double.isFinite(cantidadReabastecimiento)
                || cantidadReabastecimiento <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad de reabastecimiento debe ser mayor que cero.");
        }

        this.cantidadReabastecimiento =
                cantidadReabastecimiento;
    }

    /**
     * Genera un texto con la información del producto.
     *
     * @return descripción detallada del producto
     */
    public String mostrarDetalles() {

        return "\nID: " + ID_producto
                + "\nNombre: " + nombre
                + "\nTipo: " + tipo
                + "\nStock: " + stock
                + "\nPrecio proveedor: " + precio_prov
                + "\nPrecio venta: " + precio_venta
                + "\nStock mínimo: " + stockMinimo
                + "\nProveedor: " + proveedor
                + "\nCantidad de reabastecimiento: "
                + cantidadReabastecimiento
                + "\nEstado: " + getEstadoInventario();
    }
}
