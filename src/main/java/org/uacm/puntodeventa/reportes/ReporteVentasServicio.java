/*
 * Archivo: ReporteVentasServicio.java
 * Módulo: Reporte de Ventas
 * Propósito: Arma el reporte de un período y busca comprobantes de transacciones.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.reportes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.uacm.puntodeventa.Clases.Comprobante;
import org.uacm.puntodeventa.Clases.FiltroTransaccion;
import org.uacm.puntodeventa.Clases.ItemReporte;
import org.uacm.puntodeventa.Clases.ProductoVendido;
import org.uacm.puntodeventa.DB.ReporteABD;
import org.uacm.puntodeventa.DB.TicketHistoricoABD;

/**
 * Reglas del reporte de ventas: junta ventas y recargas en una sola lista ordenada
 * por fecha (requisito R1) y obtiene los productos más vendidos (requisito R3).
 */
public class ReporteVentasServicio {

    private static final Comparator<ItemReporte> MAS_RECIENTE_PRIMERO =
            Comparator.comparing(ItemReporte::getFecha).thenComparing(ItemReporte::getFolio).reversed();

    /**
     * Consulta las transacciones de un período con sus totales y productos más vendidos.
     *
     * @param periodo         rango de fechas y tipo de transacción, ya validado
     * @param limiteProductos cuántos productos mostrar en el listado de más vendidos
     * @return el reporte del período
     * @throws SQLException             si falla alguna consulta
     * @throws IllegalArgumentException si el límite de productos no es positivo
     */
    public ReporteVentas consultar(PeriodoReporte periodo, int limiteProductos) throws SQLException {
        if (limiteProductos < 1) {
            throw new IllegalArgumentException("El número de productos a mostrar debe ser al menos 1.");
        }
        List<ItemReporte> transacciones = consultarTransacciones(periodo);
        List<ProductoVendido> masVendidos = new ArrayList<>();
        if (periodo.getFiltro().isIncluyeVentas()) {
            masVendidos = ReporteABD.listarMasVendidos(periodo.getDesde(), periodo.getHasta(), limiteProductos);
        }
        return new ReporteVentas(periodo, transacciones, masVendidos);
    }

    /**
     * Busca el comprobante de una transacción por su folio (requisito R5).
     *
     * @param folio folio de la venta o de la recarga
     * @return el comprobante, o vacío si el folio no existe
     * @throws SQLException si falla alguna consulta
     */
    public Optional<Comprobante> buscarComprobante(String folio) throws SQLException {
        Optional<Comprobante> venta = ReporteABD.buscarVenta(folio);
        if (venta.isPresent()) {
            return venta;
        }
        return ReporteABD.buscarRecarga(folio);
    }

    /**
     * Obtiene el texto del ticket de un folio para mostrarlo como copia histórica. Usa el
     * mismo formato que se imprime al cobrar; si la venta no tiene renglones guardados, arma
     * el comprobante resumido del reporte.
     *
     * @param folio folio de la venta o de la recarga
     * @return el texto del ticket, o vacío si el folio no existe
     * @throws SQLException si falla alguna consulta
     */
    public Optional<String> buscarTicket(String folio) throws SQLException {
        Optional<String> historico = TicketHistoricoABD.buscar(folio);
        if (historico.isPresent()) {
            return historico;
        }
        return buscarComprobante(folio).map(FormatoReporte::comprobante);
    }

    private List<ItemReporte> consultarTransacciones(PeriodoReporte periodo) throws SQLException {
        FiltroTransaccion filtro = periodo.getFiltro();
        List<ItemReporte> transacciones = new ArrayList<>();
        if (filtro.isIncluyeVentas()) {
            transacciones.addAll(ReporteABD.listarVentas(periodo.getDesde(), periodo.getHasta()));
        }
        if (filtro.isIncluyeRecargas()) {
            transacciones.addAll(ReporteABD.listarRecargas(periodo.getDesde(), periodo.getHasta()));
        }
        transacciones.sort(MAS_RECIENTE_PRIMERO);
        return transacciones;
    }
}
