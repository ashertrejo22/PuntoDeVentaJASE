/*
 * Archivo: ResumenReporte.java
 * Módulo: Reporte de Ventas
 * Propósito: Calcula los indicadores generales de un conjunto de transacciones.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.reportes;

import java.util.List;
import org.uacm.puntodeventa.Clases.ItemReporte;
import org.uacm.puntodeventa.Clases.TipoPago;

/**
 * Indicadores del período consultado (requisito R2): total vendido, número de
 * transacciones, ticket promedio y desglose por forma de pago.
 */
public class ResumenReporte {

    private final double total;
    private final int transacciones;
    private final double ticketPromedio;
    private final double efectivo;
    private final double tarjeta;

    /**
     * Calcula los indicadores a partir de las transacciones.
     *
     * @param items transacciones del período consultado
     */
    public ResumenReporte(List<ItemReporte> items) {
        double sumaTotal = 0;
        double sumaEfectivo = 0;
        for (ItemReporte item : items) {
            sumaTotal += item.getTotal();
            if (item.getMetodoPago() == TipoPago.EFECTIVO) {
                sumaEfectivo += item.getTotal();
            }
        }
        this.total = sumaTotal;
        this.transacciones = items.size();
        this.ticketPromedio = items.isEmpty() ? 0 : sumaTotal / items.size();
        this.efectivo = sumaEfectivo;
        this.tarjeta = sumaTotal - sumaEfectivo;
    }

    /**
     * Devuelve el monto total vendido.
     *
     * @return total en pesos
     */
    public double getTotal() {
        return total;
    }

    /**
     * Devuelve el número de transacciones.
     *
     * @return cantidad de transacciones
     */
    public int getTransacciones() {
        return transacciones;
    }

    /**
     * Devuelve la venta promedio por transacción.
     *
     * @return ticket promedio en pesos; cero si no hubo transacciones
     */
    public double getTicketPromedio() {
        return ticketPromedio;
    }

    /**
     * Devuelve el monto cobrado en efectivo.
     *
     * @return efectivo en pesos
     */
    public double getEfectivo() {
        return efectivo;
    }

    /**
     * Devuelve el monto cobrado con tarjeta.
     *
     * @return tarjeta en pesos
     */
    public double getTarjeta() {
        return tarjeta;
    }
}
