/*
 * Archivo: ItemReporte.java
 * Módulo: Reporte de Ventas
 * Propósito: Representa un renglón del reporte: una venta o una recarga.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.Clases;

import java.time.LocalDate;

/**
 * Renglón del reporte de ventas. Representa indistintamente una venta de
 * productos o una recarga telefónica, para mostrar ambas en una sola tabla
 * (requisito R1).
 */
public class ItemReporte {

    private final String folio;
    private final LocalDate fecha;
    private final double total;
    private String cajero = "";
    private String detalle = "";
    private TipoPago metodoPago = TipoPago.EFECTIVO;

    /**
     * Crea un renglón con sus datos obligatorios.
     *
     * @param folio folio de la transacción
     * @param fecha día en que se realizó
     * @param total importe cobrado
     */
    public ItemReporte(String folio, LocalDate fecha, double total) {
        this.folio = folio;
        this.fecha = fecha;
        this.total = total;
    }

    /**
     * Asigna los datos descriptivos del renglón.
     *
     * @param cajero  nombre del usuario que realizó la transacción
     * @param detalle número de productos, o compañía en el caso de una recarga
     * @return el mismo renglón, para encadenar llamadas
     */
    public ItemReporte conDescripcion(String cajero, String detalle) {
        this.cajero = cajero;
        this.detalle = detalle;
        return this;
    }

    /**
     * Asigna la forma de pago con la que se cobró.
     *
     * @param metodoPago forma de pago
     * @return el mismo renglón, para encadenar llamadas
     */
    public ItemReporte conMetodoPago(TipoPago metodoPago) {
        this.metodoPago = metodoPago;
        return this;
    }

    /**
     * Devuelve el folio de la transacción.
     *
     * @return folio
     */
    public String getFolio() {
        return folio;
    }

    /**
     * Devuelve el día de la transacción.
     *
     * @return fecha
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Devuelve el importe cobrado.
     *
     * @return total en pesos
     */
    public double getTotal() {
        return total;
    }

    /**
     * Devuelve el nombre del cajero.
     *
     * @return nombre del usuario que realizó la transacción
     */
    public String getCajero() {
        return cajero;
    }

    /**
     * Devuelve el detalle de la transacción.
     *
     * @return número de productos o compañía de la recarga
     */
    public String getDetalle() {
        return detalle;
    }

    /**
     * Devuelve la forma de pago.
     *
     * @return forma de pago con la que se cobró
     */
    public TipoPago getMetodoPago() {
        return metodoPago;
    }
}
