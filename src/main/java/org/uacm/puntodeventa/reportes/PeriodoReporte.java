/*
 * Archivo: PeriodoReporte.java
 * Módulo: Reporte de Ventas
 * Propósito: Valida y agrupa los criterios de una consulta del reporte.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.reportes;

import java.time.LocalDate;
import org.uacm.puntodeventa.Clases.FiltroTransaccion;

/**
 * Criterios de una consulta del reporte: rango de fechas y tipo de transacción.
 * Al crearse valida la regla de negocio BR-2 (inicio anterior o igual al fin).
 */
public class PeriodoReporte {

    private final LocalDate desde;
    private final LocalDate hasta;
    private final FiltroTransaccion filtro;

    /**
     * Crea y valida el período de una consulta.
     *
     * @param desde  primer día incluido
     * @param hasta  último día incluido
     * @param filtro tipo de transacciones a incluir
     * @throws IllegalArgumentException si falta algún dato o el inicio es posterior al fin
     */
    public PeriodoReporte(LocalDate desde, LocalDate hasta, FiltroTransaccion filtro) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Selecciona la fecha de inicio y la fecha de fin.");
        }
        if (filtro == null) {
            throw new IllegalArgumentException("Selecciona el tipo de transacción.");
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException(
                    "La fecha de inicio debe ser anterior o igual a la fecha de fin.");
        }
        this.desde = desde;
        this.hasta = hasta;
        this.filtro = filtro;
    }

    /**
     * Devuelve el primer día del período.
     *
     * @return fecha de inicio
     */
    public LocalDate getDesde() {
        return desde;
    }

    /**
     * Devuelve el último día del período.
     *
     * @return fecha de fin
     */
    public LocalDate getHasta() {
        return hasta;
    }

    /**
     * Devuelve el tipo de transacciones incluidas.
     *
     * @return filtro de transacciones
     */
    public FiltroTransaccion getFiltro() {
        return filtro;
    }
}
