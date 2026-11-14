/*
 * Archivo: ComprobanteCorte.java
 * Módulo: Corte de Caja
 * Propósito: Da formato al comprobante de corte de caja (reporte JASE-RPT-1).
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.cortecaja;

import org.uacm.puntodeventa.Clases.ConteoEfectivo;
import org.uacm.puntodeventa.Clases.CorteCaja;
import org.uacm.puntodeventa.Clases.Denominacion;
import org.uacm.puntodeventa.Clases.ResultadoCorte;
import org.uacm.puntodeventa.reportes.FormatoReporte;

/**
 * Arma el texto del comprobante que se entrega al administrador al cerrar el turno.
 * Resalta la diferencia cuando es distinta de cero (regla BR-6).
 */
public final class ComprobanteCorte {

    private static final String SEPARADOR = "------------------------------------";

    private ComprobanteCorte() {
    }

    /**
     * Construye el comprobante de un turno cerrado.
     *
     * @param corte        turno cerrado
     * @param nombreCajero nombre del empleado responsable
     * @param conteo       billetes y monedas contados; si está vacío no se imprime el desglose
     * @return el texto del comprobante
     */
    public static String construir(CorteCaja corte, String nombreCajero, ConteoEfectivo conteo) {
        StringBuilder texto = new StringBuilder();
        texto.append("COMPROBANTE DE CORTE DE CAJA\nTienda JASE\n").append(SEPARADOR);
        texto.append("\nFolio:    ").append(corte.getFolio());
        texto.append("\nCajero:   ").append(nombreCajero);
        texto.append("\nApertura: ").append(FormatoReporte.fechaHora(corte.getFechaApertura()));
        texto.append("\nCierre:   ").append(FormatoReporte.fechaHora(corte.getFechaCierre()));
        texto.append('\n').append(SEPARADOR);
        texto.append(renglon("Fondo inicial", corte.getFondoInicial()));
        texto.append(renglon("+ Ventas en efectivo", corte.getVentasEfectivo()));
        texto.append(renglon("- Retiros", corte.getTotalRetiros()));
        texto.append(renglon("= Efectivo esperado", corte.getDineroEsperado()));
        texto.append(renglon("Efectivo contado", corte.getDineroContado()));
        texto.append(desglosar(conteo));
        texto.append('\n').append(SEPARADOR);
        texto.append("\nRESULTADO: ").append(describirResultado(corte));
        return texto.toString();
    }

    /**
     * Describe el resultado del arqueo en texto.
     *
     * @param corte turno cerrado
     * @return "CUADRADO", o "FALTANTE de $x" / "SOBRANTE de $x" con una nota de seguimiento
     */
    public static String describirResultado(CorteCaja corte) {
        ResultadoCorte resultado = corte.clasificarDiferencia();
        if (resultado == ResultadoCorte.CUADRADO) {
            return "CUADRADO";
        }
        return resultado + " de " + FormatoReporte.pesos(Math.abs(corte.getDiferencia()))
                + "\n*** Diferencia para seguimiento del administrador ***";
    }

    private static String desglosar(ConteoEfectivo conteo) {
        if (conteo.estaVacio()) {
            return "";
        }
        StringBuilder texto = new StringBuilder("\n").append(SEPARADOR).append("\nDESGLOSE DEL EFECTIVO CONTADO");
        for (Denominacion.Tipo tipo : Denominacion.Tipo.values()) {
            texto.append(desglosarSeccion(conteo, tipo));
        }
        return texto.toString();
    }

    private static String desglosarSeccion(ConteoEfectivo conteo, Denominacion.Tipo tipo) {
        StringBuilder texto = new StringBuilder();
        for (Denominacion denominacion : Denominacion.values()) {
            if (denominacion.getTipo() == tipo && conteo.obtenerCantidad(denominacion) > 0) {
                texto.append(String.format("%n  %-8s x %-5d %14s", denominacion.getEtiqueta(),
                        conteo.obtenerCantidad(denominacion),
                        FormatoReporte.pesos(conteo.calcularSubtotal(denominacion))));
            }
        }
        String titulo = tipo == Denominacion.Tipo.BILLETE ? "Subtotal billetes" : "Subtotal monedas";
        return texto.append(renglon(titulo, conteo.calcularSubtotal(tipo))).toString();
    }

    private static String renglon(String concepto, double monto) {
        return String.format("%n%-22s %14s", concepto, FormatoReporte.pesos(monto));
    }
}
