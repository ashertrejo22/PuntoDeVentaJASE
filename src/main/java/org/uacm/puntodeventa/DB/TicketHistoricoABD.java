/*
 * Archivo: TicketHistoricoABD.java
 * Módulo: Reporte de Ventas
 * Propósito: Reconstruye el ticket de una venta o recarga ya guardada, con el mismo
 *            formato que se imprime al cobrar (cajero, SON, método de pago y rastreo).
 * Autor: Juan Arturo González Gómez        Fecha: 2026-11-13
 */

package org.uacm.puntodeventa.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.uacm.puntodeventa.Clases.ConvierteNumeroLetra;
import org.uacm.puntodeventa.Clases.Producto;
import org.uacm.puntodeventa.Clases.ProductoCarrito;
import org.uacm.puntodeventa.Clases.Venta;

/**
 * Consultas de solo lectura que arman la copia histórica de un ticket.
 */
public final class TicketHistoricoABD {

    private static final String SEPARADOR_DOBLE = "========================================\n";
    private static final String SEPARADOR_SIMPLE = "----------------------------------------\n";
    private static final String LEYENDA_COPIA = "  *** COPIA DE TICKET (HISTÓRICO) ***\n";
    private static final String CAJERO_GENERAL = "Cajero General";
    private static final String METODO_EFECTIVO = "EFECTIVO";
    private static final String SIN_RASTREO = "N/A";
    private static final String FORMATO_REIMPRESION = "dd/MM/yyyy HH:mm:ss";

    private static final String SQL_VENTA = "SELECT v.total_a_pagar, v.metodo_pago, v.num_rastreo, "
            + "d.cantidad, d.precio_unitario, p.id_producto, p.nombre, u.id_usuario, "
            + "u.nombre AS nombre_empleado FROM Venta v "
            + "JOIN DetalleVenta d ON v.id_venta = d.id_venta "
            + "JOIN Producto p ON d.id_producto = p.id_producto "
            + "LEFT JOIN Usuario u ON v.id_usuario = u.id_usuario WHERE v.folio = ? ORDER BY d.id_detalle";
    private static final String SQL_RECARGA = "SELECT r.folio, r.fecha, r.compania, r.monto, "
            + "r.numero_telefonico, r.metodo_pago, r.num_rastreo, r.id_usuario, "
            + "u.nombre AS nombre_empleado FROM Recargas r "
            + "LEFT JOIN Usuario u ON r.id_usuario = u.id_usuario WHERE r.folio = ?";

    private TicketHistoricoABD() {
    }

    /**
     * Busca el folio entre las ventas y, si no está, entre las recargas.
     *
     * @param folio folio impreso en el ticket
     * @return el texto del ticket, o vacío si el folio no existe o no tiene renglones
     * @throws SQLException si falla la consulta
     */
    public static Optional<String> buscar(String folio) throws SQLException {
        try (Connection conexion = ConexionBD.getConnection()) {
            Optional<String> venta = buscarVenta(conexion, folio);
            return venta.isPresent() ? venta : buscarRecarga(conexion, folio);
        }
    }

    private static Optional<String> buscarVenta(Connection conexion, String folio) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_VENTA)) {
            sentencia.setString(1, folio);
            try (ResultSet fila = sentencia.executeQuery()) {
                if (!fila.next()) {
                    return Optional.empty();
                }
                double total = fila.getDouble("total_a_pagar");
                String metodo = fila.getString("metodo_pago");
                String rastreo = fila.getString("num_rastreo");
                String cajero = fila.getString("nombre_empleado");
                int idEmpleado = fila.getInt("id_usuario");
                List<ProductoCarrito> productos = new ArrayList<>();
                do {
                    productos.add(leerRenglon(fila));
                } while (fila.next());
                Venta copia = new Venta(productos, total, total, 0.0, metodo, rastreo, idEmpleado,
                        cajero == null ? CAJERO_GENERAL : cajero);
                return Optional.of(copia.generarTicket() + pieDeCopia());
            }
        }
    }

    private static ProductoCarrito leerRenglon(ResultSet fila) throws SQLException {
        Producto producto = new Producto(fila.getInt("id_producto"), fila.getString("nombre"), "General",
                0.0, fila.getDouble("precio_unitario"), "", "");
        return new ProductoCarrito(producto, fila.getDouble("cantidad"));
    }

    private static Optional<String> buscarRecarga(Connection conexion, String folio) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_RECARGA)) {
            sentencia.setString(1, folio);
            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() ? Optional.of(armarRecarga(fila)) : Optional.empty();
            }
        }
    }

    private static String armarRecarga(ResultSet fila) throws SQLException {
        String folio = fila.getString("folio");
        double monto = fila.getDouble("monto");
        String cajero = fila.getString("nombre_empleado") != null ? fila.getString("nombre_empleado")
                : CAJERO_GENERAL;
        int idEmpleado = fila.getInt("id_usuario") > 0 ? fila.getInt("id_usuario") : 1;
        String metodo = fila.getString("metodo_pago");
        if (metodo == null || metodo.isEmpty()) {
            metodo = METODO_EFECTIVO;
        }
        StringBuilder texto = new StringBuilder();
        texto.append(SEPARADOR_DOBLE)
                .append("        ABARROTES JASE S.A. DE C.V.\n")
                .append("             RFC: JASE230038LH0\n")
                .append("    Prol. San Isidro 151, San Lorenzo\n")
                .append("         Tezonco, Iztapalapa, CDMX\n")
                .append(SEPARADOR_DOBLE)
                .append("    *** COMPROBANTE DE RECARGA ***\n")
                .append(SEPARADOR_SIMPLE)
                .append("Folio: ").append(folio).append('\n')
                .append("Fecha Venta: ").append(fila.getString("fecha")).append('\n')
                .append("Cajero: ").append(cajero).append('\n')
                .append("No. Empleado: ").append(idEmpleado).append('\n')
                .append("Compañía: ").append(fila.getString("compania")).append('\n')
                .append("Teléfono: ").append(fila.getString("numero_telefonico")).append('\n')
                .append(SEPARADOR_SIMPLE)
                .append(String.format("%-25s %s%n", "MONTO RECARGADO:", String.format("$%.2f MXN", monto)))
                .append("SON: ").append(ConvierteNumeroLetra.convertir(monto)).append('\n')
                .append(SEPARADOR_SIMPLE)
                .append("Método de Pago:\n").append(metodo).append('\n');
        if (!METODO_EFECTIVO.equalsIgnoreCase(metodo)) {
            texto.append("No. Rastreo: ").append(rastreoAMostrar(fila.getString("num_rastreo"), folio))
                    .append('\n');
        }
        return texto.append(pieDeCopia()).toString();
    }

    private static String rastreoAMostrar(String guardado, String folio) {
        boolean hayRastreo = guardado != null && !guardado.isEmpty() && !SIN_RASTREO.equalsIgnoreCase(guardado);
        return hayRastreo ? guardado : "TR-" + folio.replace("REC-", "");
    }

    private static String pieDeCopia() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMATO_REIMPRESION));
        return SEPARADOR_DOBLE + LEYENDA_COPIA + "  Reimpreso el: " + fecha + "\n" + SEPARADOR_DOBLE;
    }
}
