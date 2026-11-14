/*
 * Archivo: ReporteABD.java
 * Módulo: Reporte de Ventas
 * Propósito: Ejecuta las consultas del reporte de ventas y de los comprobantes.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.uacm.puntodeventa.Clases.ItemReporte;
import org.uacm.puntodeventa.Clases.TipoPago;
import org.uacm.puntodeventa.Clases.Comprobante;
import org.uacm.puntodeventa.Clases.LineaComprobante;
import org.uacm.puntodeventa.Clases.ProductoVendido;

/**
 * Consultas de solo lectura del reporte de ventas. Las fechas se comparan por sus
 * primeros diez caracteres (aaaa-MM-dd) para no depender de que traigan hora.
 */
public final class ReporteABD {

    private static final int LONGITUD_FECHA = 10;
    private static final int PARAMETRO_LIMITE = 3;

    private static final String SQL_VENTAS = "SELECT v.folio, v.fecha, v.total_a_pagar AS total, "
            + "COALESCE(u.nombre, 'Sin asignar') AS cajero, COUNT(d.id_detalle) AS productos, "
            + "COALESCE(v.metodo_pago, 'efectivo') AS metodo FROM Venta v "
            + "LEFT JOIN Usuario u ON v.id_usuario = u.id_usuario "
            + "LEFT JOIN DetalleVenta d ON v.id_venta = d.id_venta "
            + "WHERE substr(v.fecha, 1, 10) BETWEEN ? AND ? GROUP BY v.id_venta";
    private static final String SQL_RECARGAS = "SELECT r.folio, r.fecha, r.monto AS total, "
            + "COALESCE(u.nombre, 'Sin asignar') AS cajero, r.compania FROM Recargas r "
            + "LEFT JOIN Usuario u ON r.id_usuario = u.id_usuario "
            + "WHERE substr(r.fecha, 1, 10) BETWEEN ? AND ?";
    private static final String SQL_MAS_VENDIDOS = "SELECT p.nombre, SUM(d.cantidad) AS unidades, "
            + "SUM(d.cantidad * d.precio_unitario) AS importe FROM DetalleVenta d "
            + "JOIN Venta v ON d.id_venta = v.id_venta JOIN Producto p ON d.id_producto = p.id_producto "
            + "WHERE substr(v.fecha, 1, 10) BETWEEN ? AND ? GROUP BY p.id_producto "
            + "ORDER BY unidades DESC, p.nombre LIMIT ?";
    private static final String SQL_VENTA_POR_FOLIO = "SELECT id_venta, folio, fecha, total_a_pagar, "
            + "COALESCE(metodo_pago, 'efectivo') AS metodo FROM Venta WHERE folio = ?";
    private static final String SQL_LINEAS_VENTA = "SELECT p.nombre, d.cantidad, d.precio_unitario "
            + "FROM DetalleVenta d JOIN Producto p ON d.id_producto = p.id_producto "
            + "WHERE d.id_venta = ? ORDER BY d.id_detalle";
    private static final String SQL_RECARGA_POR_FOLIO = "SELECT folio, fecha, compania, monto, "
            + "numero_telefonico FROM Recargas WHERE folio = ?";

    private ReporteABD() {
    }

    /**
     * Lista las ventas de productos de un período.
     *
     * @param desde primer día incluido
     * @param hasta último día incluido
     * @return las ventas encontradas, sin un orden garantizado
     * @throws SQLException si falla la consulta
     */
    public static List<ItemReporte> listarVentas(LocalDate desde, LocalDate hasta) throws SQLException {
        List<ItemReporte> ventas = new ArrayList<>();
        try (Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_VENTAS)) {
            asignarPeriodo(sentencia, desde, hasta);
            try (ResultSet fila = sentencia.executeQuery()) {
                while (fila.next()) {
                    ventas.add(leerVenta(fila));
                }
            }
        }
        return ventas;
    }

    /**
     * Lista las recargas telefónicas de un período.
     *
     * @param desde primer día incluido
     * @param hasta último día incluido
     * @return las recargas encontradas, sin un orden garantizado
     * @throws SQLException si falla la consulta
     */
    public static List<ItemReporte> listarRecargas(LocalDate desde, LocalDate hasta) throws SQLException {
        List<ItemReporte> recargas = new ArrayList<>();
        try (Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_RECARGAS)) {
            asignarPeriodo(sentencia, desde, hasta);
            try (ResultSet fila = sentencia.executeQuery()) {
                while (fila.next()) {
                    recargas.add(leerRecarga(fila));
                }
            }
        }
        return recargas;
    }

    /**
     * Lista los productos más vendidos de un período, de mayor a menor cantidad.
     *
     * @param desde  primer día incluido
     * @param hasta  último día incluido
     * @param limite cuántos productos devolver como máximo
     * @return los productos más vendidos
     * @throws SQLException si falla la consulta
     */
    public static List<ProductoVendido> listarMasVendidos(LocalDate desde, LocalDate hasta, int limite)
            throws SQLException {
        List<ProductoVendido> productos = new ArrayList<>();
        try (Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_MAS_VENDIDOS)) {
            asignarPeriodo(sentencia, desde, hasta);
            sentencia.setInt(PARAMETRO_LIMITE, limite);
            try (ResultSet fila = sentencia.executeQuery()) {
                while (fila.next()) {
                    productos.add(new ProductoVendido(fila.getString("nombre"),
                            fila.getDouble("unidades"), fila.getDouble("importe")));
                }
            }
        }
        return productos;
    }

    /**
     * Busca el comprobante de una venta de productos.
     *
     * @param folio folio de la venta
     * @return el comprobante, o vacío si el folio no es de una venta
     * @throws SQLException si falla la consulta
     */
    public static Optional<Comprobante> buscarVenta(String folio) throws SQLException {
        try (Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_VENTA_POR_FOLIO)) {
            sentencia.setString(1, folio);
            try (ResultSet fila = sentencia.executeQuery()) {
                if (!fila.next()) {
                    return Optional.empty();
                }
                return Optional.of(armarComprobanteVenta(conexion, fila));
            }
        }
    }

    /**
     * Busca el comprobante de una recarga telefónica.
     *
     * @param folio folio de la recarga
     * @return el comprobante, o vacío si el folio no es de una recarga
     * @throws SQLException si falla la consulta
     */
    public static Optional<Comprobante> buscarRecarga(String folio) throws SQLException {
        try (Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_RECARGA_POR_FOLIO)) {
            sentencia.setString(1, folio);
            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() ? Optional.of(armarComprobanteRecarga(fila)) : Optional.empty();
            }
        }
    }

    private static void asignarPeriodo(PreparedStatement sentencia, LocalDate desde, LocalDate hasta)
            throws SQLException {
        sentencia.setString(1, desde.toString());
        sentencia.setString(2, hasta.toString());
    }

    private static ItemReporte leerVenta(ResultSet fila) throws SQLException {
        String detalle = fila.getInt("productos") + " producto(s)";
        return new ItemReporte(fila.getString("folio"), leerFecha(fila), fila.getDouble("total"))
                .conDescripcion(fila.getString("cajero"), detalle)
                .conMetodoPago(TipoPago.desdeValor(fila.getString("metodo")));
    }

    private static ItemReporte leerRecarga(ResultSet fila) throws SQLException {
        String detalle = "Recarga " + fila.getString("compania");
        return new ItemReporte(fila.getString("folio"), leerFecha(fila), fila.getDouble("total"))
                .conDescripcion(fila.getString("cajero"), detalle);
    }

    private static Comprobante armarComprobanteVenta(Connection conexion, ResultSet venta)
            throws SQLException {
        Comprobante comprobante = new Comprobante("TICKET VENTA", venta.getString("folio"), leerFecha(venta));
        comprobante.registrarCobro(TipoPago.desdeValor(venta.getString("metodo")),
                venta.getDouble("total_a_pagar"));
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_LINEAS_VENTA)) {
            sentencia.setInt(1, venta.getInt("id_venta"));
            try (ResultSet lineas = sentencia.executeQuery()) {
                while (lineas.next()) {
                    comprobante.agregarLinea(new LineaComprobante(lineas.getString("nombre"),
                            lineas.getDouble("cantidad"), lineas.getDouble("precio_unitario")));
                }
            }
        }
        return comprobante;
    }

    private static Comprobante armarComprobanteRecarga(ResultSet recarga) throws SQLException {
        Comprobante comprobante = new Comprobante("COMPROBANTE RECARGA", recarga.getString("folio"),
                leerFecha(recarga));
        String descripcion = "Recarga " + recarga.getString("compania") + " al "
                + recarga.getString("numero_telefonico");
        comprobante.agregarLinea(new LineaComprobante(descripcion, 1, recarga.getDouble("monto")));
        comprobante.registrarCobro(TipoPago.EFECTIVO, recarga.getDouble("monto"));
        return comprobante;
    }

    private static LocalDate leerFecha(ResultSet fila) throws SQLException {
        String texto = fila.getString("fecha");
        if (texto == null || texto.length() < LONGITUD_FECHA) {
            throw new SQLException("Fecha inválida en el folio " + fila.getString("folio") + ": " + texto);
        }
        try {
            return LocalDate.parse(texto.substring(0, LONGITUD_FECHA));
        } catch (DateTimeParseException e) {
            throw new SQLException("Fecha inválida en el folio " + fila.getString("folio") + ": " + texto, e);
        }
    }
}
