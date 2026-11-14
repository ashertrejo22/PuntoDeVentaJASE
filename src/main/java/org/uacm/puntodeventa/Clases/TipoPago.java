/*
 * Archivo: TipoPago.java
 * Módulo: Reporte de Ventas
 * Propósito: Define las formas de pago con que se cobra una transacción.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.Clases;

import java.util.Locale;

/**
 * Formas de pago admitidas por el punto de venta.
 */
public enum TipoPago {

    /** Pago en efectivo: es el único que afecta el arqueo de caja. */
    EFECTIVO("efectivo", "Efectivo"),

    /** Pago con tarjeta. */
    TARJETA("tarjeta", "Tarjeta");

    private final String valor;
    private final String etiqueta;

    TipoPago(String valor, String etiqueta) {
        this.valor = valor;
        this.etiqueta = etiqueta;
    }

    /**
     * Devuelve el texto con el que la forma de pago se guarda en la base de datos.
     *
     * @return valor almacenado en la columna metodo_pago
     */
    public String getValor() {
        return valor;
    }

    /**
     * Devuelve el nombre de la forma de pago listo para mostrarse al usuario.
     *
     * @return nombre legible
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Convierte el texto guardado en la base de datos a la forma de pago. Acepta tanto los
     * valores propios ("efectivo", "tarjeta") como los que imprime la pantalla de cobro
     * ("EFECTIVO", "TARJETA DE DÉBITO", "TARJETA DE CRÉDITO"); un texto vacío es efectivo.
     *
     * @param valor texto almacenado, sin importar mayúsculas
     * @return la forma de pago que corresponde al texto
     * @throws IllegalArgumentException si el texto no corresponde a ninguna forma de pago
     */
    public static TipoPago desdeValor(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return EFECTIVO;
        }
        String normalizado = valor.trim().toLowerCase(Locale.ROOT);
        if (normalizado.startsWith(TARJETA.valor)) {
            return TARJETA;
        }
        for (TipoPago tipo : values()) {
            if (tipo.valor.equals(normalizado)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Forma de pago desconocida: " + valor);
    }
}
