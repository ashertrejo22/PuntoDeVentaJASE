/*
 * Archivo: FiltroTransaccion.java
 * Módulo: Reporte de Ventas
 * Propósito: Define qué transacciones incluye una consulta del reporte.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.Clases;

/**
 * Tipos de transacción que puede incluir el reporte de ventas (requisito R1).
 */
public enum FiltroTransaccion {

    /** Ventas de productos y recargas. */
    AMBAS("Ambas", true, true),

    /** Solo ventas de productos. */
    SOLO_VENTAS("Solo ventas", true, false),

    /** Solo recargas telefónicas. */
    SOLO_RECARGAS("Solo recargas", false, true);

    private final String etiqueta;
    private final boolean incluyeVentas;
    private final boolean incluyeRecargas;

    FiltroTransaccion(String etiqueta, boolean incluyeVentas, boolean incluyeRecargas) {
        this.etiqueta = etiqueta;
        this.incluyeVentas = incluyeVentas;
        this.incluyeRecargas = incluyeRecargas;
    }

    /**
     * Indica si el filtro incluye las ventas de productos.
     *
     * @return verdadero si se deben consultar las ventas
     */
    public boolean isIncluyeVentas() {
        return incluyeVentas;
    }

    /**
     * Indica si el filtro incluye las recargas telefónicas.
     *
     * @return verdadero si se deben consultar las recargas
     */
    public boolean isIncluyeRecargas() {
        return incluyeRecargas;
    }

    /**
     * Devuelve la etiqueta que se muestra en la lista desplegable.
     *
     * @return nombre legible del filtro
     */
    @Override
    public String toString() {
        return etiqueta;
    }
}
