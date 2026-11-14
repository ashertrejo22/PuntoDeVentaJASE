/*
 * Archivo: EstadoCorte.java
 * Módulo: Corte de Caja
 * Propósito: Define los estados posibles de un turno de caja.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.Clases;

/**
 * Estados en los que puede encontrarse un turno de caja.
 */
public enum EstadoCorte {

    /** Turno en curso: admite ventas y retiros. */
    ABIERTO("abierto"),

    /** Turno terminado con su arqueo registrado. */
    CERRADO("cerrado");

    private final String valor;

    EstadoCorte(String valor) {
        this.valor = valor;
    }

    /**
     * Devuelve el texto con el que el estado se guarda en la base de datos.
     *
     * @return valor almacenado en la columna estado
     */
    public String getValor() {
        return valor;
    }

    /**
     * Convierte el texto guardado en la base de datos al estado correspondiente.
     *
     * @param valor texto almacenado en la columna estado
     * @return el estado que corresponde al texto
     * @throws IllegalArgumentException si el texto no corresponde a ningún estado
     */
    public static EstadoCorte desdeValor(String valor) {
        for (EstadoCorte estado : values()) {
            if (estado.valor.equals(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de corte desconocido: " + valor);
    }
}
