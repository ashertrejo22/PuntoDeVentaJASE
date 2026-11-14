/*
 * Archivo: ReporteVentas.java
 * Módulo: Reporte de Ventas
 * Propósito: Agrupa el resultado completo de una consulta del reporte.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.reportes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.uacm.puntodeventa.Clases.ItemReporte;
import org.uacm.puntodeventa.Clases.ProductoVendido;

/**
 * Resultado de una consulta: transacciones, resumen y productos más vendidos.
 * Es lo que se muestra en pantalla y lo que se exporta, de modo que ambos coinciden.
 */
public class ReporteVentas {

    private final PeriodoReporte periodo;
    private final List<ItemReporte> transacciones;
    private final List<ProductoVendido> masVendidos;
    private final ResumenReporte resumen;

    /**
     * Crea el reporte y calcula su resumen.
     *
     * @param periodo       criterios con los que se consultó
     * @param transacciones transacciones encontradas
     * @param masVendidos   productos más vendidos del período
     */
    public ReporteVentas(PeriodoReporte periodo, List<ItemReporte> transacciones,
            List<ProductoVendido> masVendidos) {
        this.periodo = periodo;
        this.transacciones = Collections.unmodifiableList(new ArrayList<>(transacciones));
        this.masVendidos = Collections.unmodifiableList(new ArrayList<>(masVendidos));
        this.resumen = new ResumenReporte(this.transacciones);
    }

    /**
     * Devuelve los criterios de la consulta.
     *
     * @return período consultado
     */
    public PeriodoReporte getPeriodo() {
        return periodo;
    }

    /**
     * Devuelve las transacciones encontradas.
     *
     * @return lista de solo lectura, de la más reciente a la más antigua
     */
    public List<ItemReporte> getTransacciones() {
        return transacciones;
    }

    /**
     * Devuelve los productos más vendidos.
     *
     * @return lista de solo lectura, de mayor a menor cantidad vendida
     */
    public List<ProductoVendido> getMasVendidos() {
        return masVendidos;
    }

    /**
     * Devuelve los indicadores del período.
     *
     * @return resumen del reporte
     */
    public ResumenReporte getResumen() {
        return resumen;
    }

    /**
     * Indica si la consulta no encontró transacciones.
     *
     * @return verdadero si no hay transacciones
     */
    public boolean isVacio() {
        return transacciones.isEmpty();
    }
}
