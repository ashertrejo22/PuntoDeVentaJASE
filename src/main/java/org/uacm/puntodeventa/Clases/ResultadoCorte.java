/*
 * Archivo: ResultadoCorte.java
 * Módulo: Corte de Caja
 * Propósito: Clasifica la diferencia entre el efectivo esperado y el contado.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.Clases;

/**
 * Resultado del arqueo de caja (regla de negocio BR-6).
 */
public enum ResultadoCorte {

    /** El efectivo contado coincide con el esperado. */
    CUADRADO,

    /** Hay menos efectivo del esperado. */
    FALTANTE,

    /** Hay más efectivo del esperado. */
    SOBRANTE
}
