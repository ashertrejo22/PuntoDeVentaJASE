/*
 * Archivo: ProductoVendido.java
 * Módulo: Reporte de Ventas
 * Propósito: Representa un producto del listado de más vendidos.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.Clases;

/**
 * Producto con las unidades vendidas y el importe generado en un período (requisito R3).
 */
public class ProductoVendido {

    private final String nombre;
    private final double unidades;
    private final double importe;

    /**
     * Crea un renglón del listado de más vendidos.
     *
     * @param nombre   nombre del producto
     * @param unidades cantidad vendida
     * @param importe  importe total generado
     */
    public ProductoVendido(String nombre, double unidades, double importe) {
        this.nombre = nombre;
        this.unidades = unidades;
        this.importe = importe;
    }

    /**
     * Devuelve el nombre del producto.
     *
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve las unidades vendidas.
     *
     * @return cantidad vendida
     */
    public double getUnidades() {
        return unidades;
    }

    /**
     * Devuelve el importe generado.
     *
     * @return importe en pesos
     */
    public double getImporte() {
        return importe;
    }
}
