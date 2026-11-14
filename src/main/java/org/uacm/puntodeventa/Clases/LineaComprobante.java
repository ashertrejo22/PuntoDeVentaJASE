/*
 * Archivo: LineaComprobante.java
 * Módulo: Reporte de Ventas
 * Propósito: Representa una partida de un comprobante de transacción.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.Clases;

/**
 * Partida de un comprobante: descripción, cantidad y precio unitario (requisito R5).
 */
public class LineaComprobante {

    private final String descripcion;
    private final double cantidad;
    private final double precioUnitario;

    /**
     * Crea una partida.
     *
     * @param descripcion    producto o servicio cobrado
     * @param cantidad       unidades cobradas
     * @param precioUnitario precio de cada unidad
     */
    public LineaComprobante(String descripcion, double cantidad, double precioUnitario) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    /**
     * Devuelve la descripción de la partida.
     *
     * @return producto o servicio cobrado
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Devuelve las unidades cobradas.
     *
     * @return cantidad
     */
    public double getCantidad() {
        return cantidad;
    }

    /**
     * Devuelve el precio de cada unidad.
     *
     * @return precio unitario en pesos
     */
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Calcula el importe de la partida.
     *
     * @return cantidad por precio unitario
     */
    public double getImporte() {
        return cantidad * precioUnitario;
    }
}
