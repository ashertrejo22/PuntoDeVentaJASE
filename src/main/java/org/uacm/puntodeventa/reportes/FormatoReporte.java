/*
 * Archivo: FormatoReporte.java
 * Módulo: Reporte de Ventas
 * Propósito: Da formato de texto a fechas, importes y comprobantes del reporte.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.reportes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.uacm.puntodeventa.Clases.Comprobante;
import org.uacm.puntodeventa.Clases.LineaComprobante;
import org.uacm.puntodeventa.Clases.ProductoVendido;

/**
 * Utilidades de formato compartidas por la pantalla y el comprobante,
 * para que ambos muestren los datos de la misma manera.
 */
public final class FormatoReporte {

    private static final Locale LOCALIDAD = Locale.US;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String SEPARADOR = "------------------------------------";

    private FormatoReporte() {
    }

    /**
     * Da formato de moneda a un importe.
     *
     * @param monto importe en pesos
     * @return el importe con signo de pesos, separador de miles y dos decimales
     */
    public static String pesos(double monto) {
        return String.format(LOCALIDAD, "$%,.2f", monto);
    }

    /**
     * Da formato a una cantidad, sin decimales cuando es entera.
     *
     * @param cantidad unidades
     * @return la cantidad lista para mostrarse
     */
    public static String cantidad(double cantidad) {
        if (cantidad == Math.floor(cantidad)) {
            return String.valueOf((long) cantidad);
        }
        return String.format(LOCALIDAD, "%.3f", cantidad);
    }

    /**
     * Da formato a una fecha para mostrarla al usuario.
     *
     * @param fecha día a mostrar
     * @return la fecha en formato dd/MM/aaaa
     */
    public static String fecha(LocalDate fecha) {
        return fecha.format(FORMATO_FECHA);
    }

    /**
     * Da formato a una fecha con hora para mostrarla al usuario.
     *
     * @param momento fecha y hora a mostrar, puede ser nula
     * @return el texto en formato dd/MM/aaaa HH:mm, o un guion si no hay dato
     */
    public static String fechaHora(LocalDateTime momento) {
        return momento == null ? "-" : momento.format(FORMATO_FECHA_HORA);
    }

    /**
     * Describe el período de una consulta.
     *
     * @param periodo criterios de la consulta
     * @return texto con el formato "dd/MM/aaaa al dd/MM/aaaa"
     */
    public static String periodo(PeriodoReporte periodo) {
        return fecha(periodo.getDesde()) + " al " + fecha(periodo.getHasta());
    }

    /**
     * Da formato a un renglón del listado de productos más vendidos.
     *
     * @param lugar    posición en el listado, empezando en 1
     * @param producto producto del renglón
     * @return texto con lugar, nombre, unidades e importe
     */
    public static String productoVendido(int lugar, ProductoVendido producto) {
        return lugar + ". " + producto.getNombre() + " - " + cantidad(producto.getUnidades())
                + " pzas - " + pesos(producto.getImporte());
    }

    /**
     * Arma el texto del comprobante de una transacción.
     *
     * @param comprobante datos del comprobante
     * @return texto listo para mostrarse en la ventana del ticket
     */
    public static String comprobante(Comprobante comprobante) {
        StringBuilder texto = new StringBuilder();
        texto.append("\n*** ").append(comprobante.getTitulo()).append(" ***");
        texto.append("\nFolio: ").append(comprobante.getFolio());
        texto.append("\nFecha: ").append(fecha(comprobante.getFecha()));
        texto.append("\nForma de pago: ").append(comprobante.getMetodoPago().getEtiqueta());
        texto.append('\n').append(SEPARADOR);
        for (LineaComprobante linea : comprobante.getLineas()) {
            texto.append('\n').append(lineaComprobante(linea));
        }
        texto.append('\n').append(SEPARADOR);
        texto.append("\nTOTAL: ").append(pesos(comprobante.getTotal()));
        return texto.toString();
    }

    private static String lineaComprobante(LineaComprobante linea) {
        return linea.getDescripcion() + "\n    " + cantidad(linea.getCantidad()) + " x "
                + pesos(linea.getPrecioUnitario()) + " = " + pesos(linea.getImporte());
    }
}
