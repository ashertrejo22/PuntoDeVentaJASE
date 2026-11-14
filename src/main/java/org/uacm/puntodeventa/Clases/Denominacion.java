/*
 * Archivo: Denominacion.java
 * Módulo: Corte de Caja
 * Propósito: Define los billetes y monedas que se cuentan al cerrar un turno.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-11-13
 */

package org.uacm.puntodeventa.Clases;

/**
 * Billetes y monedas de curso legal que el cajero cuenta en el corte de caja (MET-12).
 * El valor se guarda en centavos para sumar sin errores de redondeo.
 */
public enum Denominacion {
    BILLETE_20("$20", 2000, Tipo.BILLETE),
    BILLETE_50("$50", 5000, Tipo.BILLETE),
    BILLETE_100("$100", 10000, Tipo.BILLETE),
    BILLETE_200("$200", 20000, Tipo.BILLETE),
    BILLETE_500("$500", 50000, Tipo.BILLETE),
    BILLETE_1000("$1,000", 100000, Tipo.BILLETE),
    MONEDA_50_CENTAVOS("$0.50", 50, Tipo.MONEDA),
    MONEDA_1("$1", 100, Tipo.MONEDA),
    MONEDA_2("$2", 200, Tipo.MONEDA),
    MONEDA_5("$5", 500, Tipo.MONEDA),
    MONEDA_10("$10", 1000, Tipo.MONEDA),
    MONEDA_20("$20", 2000, Tipo.MONEDA);

    /** Sección del conteo a la que pertenece una denominación. */
    public enum Tipo {
        /** Papel moneda. */
        BILLETE,
        /** Moneda metálica. */
        MONEDA
    }

    private final String etiqueta;
    private final int valorCentavos;
    private final Tipo tipo;

    Denominacion(String etiqueta, int valorCentavos, Tipo tipo) {
        this.etiqueta = etiqueta;
        this.valorCentavos = valorCentavos;
        this.tipo = tipo;
    }

    /**
     * Devuelve el texto que se muestra al cajero.
     *
     * @return valor con signo de pesos, por ejemplo "$500"
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Devuelve el valor de una pieza.
     *
     * @return valor en centavos
     */
    public int getValorCentavos() {
        return valorCentavos;
    }

    /**
     * Indica si es billete o moneda.
     *
     * @return la sección del conteo
     */
    public Tipo getTipo() {
        return tipo;
    }
}
