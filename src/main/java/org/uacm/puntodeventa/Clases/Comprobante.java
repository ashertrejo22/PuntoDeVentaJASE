/*
 * Archivo: Comprobante.java
 * Módulo: Reporte de Ventas
 * Propósito: Agrupa los datos del comprobante de una venta o de una recarga.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.Clases;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Datos del comprobante de una transacción (requisito R5): folio, fecha, partidas,
 * forma de pago y total cobrado.
 */
public class Comprobante {

    private final String titulo;
    private final String folio;
    private final LocalDate fecha;
    private final List<LineaComprobante> lineas = new ArrayList<>();
    private TipoPago metodoPago = TipoPago.EFECTIVO;
    private double total;

    /**
     * Crea un comprobante sin partidas.
     *
     * @param titulo encabezado, por ejemplo "TICKET VENTA"
     * @param folio  folio de la transacción
     * @param fecha  día de la transacción
     */
    public Comprobante(String titulo, String folio, LocalDate fecha) {
        this.titulo = titulo;
        this.folio = folio;
        this.fecha = fecha;
    }

    /**
     * Agrega una partida al comprobante.
     *
     * @param linea partida a agregar
     */
    public void agregarLinea(LineaComprobante linea) {
        lineas.add(linea);
    }

    /**
     * Registra la forma de pago y el total cobrado.
     *
     * @param metodoPago forma de pago
     * @param total      total cobrado en pesos
     */
    public void registrarCobro(TipoPago metodoPago, double total) {
        this.metodoPago = metodoPago;
        this.total = total;
    }

    /**
     * Devuelve el encabezado del comprobante.
     *
     * @return título
     */
    public String getTitulo() {
        return titulo;
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
     * Devuelve las partidas del comprobante.
     *
     * @return lista de solo lectura
     */
    public List<LineaComprobante> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    /**
     * Devuelve la forma de pago.
     *
     * @return forma de pago
     */
    public TipoPago getMetodoPago() {
        return metodoPago;
    }

    /**
     * Devuelve el total cobrado.
     *
     * @return total en pesos
     */
    public double getTotal() {
        return total;
    }
}
