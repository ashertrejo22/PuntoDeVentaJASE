/*
 * Archivo: Dinero.java
 * Módulo: Corte de Caja
 * Propósito: Centraliza el redondeo de importes a centavos.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.Clases;

/**
 * Utilidad para manejar importes en pesos con precisión de centavos.
 */
public final class Dinero {

    private static final double CENTAVOS_POR_PESO = 100.0;

    private Dinero() {
    }

    /**
     * Redondea un importe a dos decimales.
     *
     * @param importe importe en pesos
     * @return el importe redondeado a centavos
     * @throws IllegalArgumentException si el importe no es un número finito
     */
    public static double redondear(double importe) {
        if (!Double.isFinite(importe)) {
            throw new IllegalArgumentException("El importe debe ser un número válido.");
        }
        return Math.round(importe * CENTAVOS_POR_PESO) / CENTAVOS_POR_PESO;
    }
}
