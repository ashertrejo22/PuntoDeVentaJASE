/*
 * Archivo: TipoMovimiento.java
 * Módulo: Corte de Caja
 * Propósito: Define los tipos de movimiento de dinero dentro de un turno.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.Clases;

/**
 * Tipos de movimiento de efectivo que se registran durante un turno.
 */
public enum TipoMovimiento {

    /** Salida de efectivo de la caja (regla de negocio BR-5). */
    RETIRO("retiro");

    private final String valor;

    TipoMovimiento(String valor) {
        this.valor = valor;
    }

    /**
     * Devuelve el texto con el que el tipo se guarda en la base de datos.
     *
     * @return valor almacenado en la columna tipo
     */
    public String getValor() {
        return valor;
    }
}
