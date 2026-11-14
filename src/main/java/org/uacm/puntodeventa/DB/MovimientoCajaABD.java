/*
 * Archivo: MovimientoCajaABD.java
 * Módulo: Corte de Caja
 * Propósito: Guarda y suma los retiros de efectivo de un turno.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.uacm.puntodeventa.Clases.MovimientoCaja;
import org.uacm.puntodeventa.Clases.TipoMovimiento;

/**
 * Acceso a la tabla MovimientoCaja.
 */
public final class MovimientoCajaABD {

    private static final String SQL_INSERTAR =
            "INSERT INTO MovimientoCaja (id_corte, tipo, monto, motivo, fecha) VALUES (?,?,?,?,?)";
    private static final String SQL_SUMAR =
            "SELECT COALESCE(SUM(monto), 0) FROM MovimientoCaja WHERE id_corte = ? AND tipo = ?";

    private MovimientoCajaABD() {
    }

    /**
     * Guarda un movimiento de efectivo.
     *
     * @param movimiento movimiento a guardar
     * @throws SQLException si falla la inserción
     */
    public static void insertar(MovimientoCaja movimiento) throws SQLException {
        try (Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {
            ParametrosSql.asignar(sentencia,
                    movimiento.getIdCorte(),
                    movimiento.getTipo().getValor(),
                    movimiento.getMonto(),
                    movimiento.getMotivo(),
                    movimiento.getFecha().toString());
            sentencia.executeUpdate();
        }
    }

    /**
     * Suma los retiros registrados en un turno.
     *
     * @param idCorte identificador del turno
     * @return total retirado, o cero si no hubo retiros
     * @throws SQLException si falla la consulta
     */
    public static double sumarRetiros(int idCorte) throws SQLException {
        try (Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_SUMAR)) {
            sentencia.setInt(1, idCorte);
            sentencia.setString(2, TipoMovimiento.RETIRO.getValor());
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? resultado.getDouble(1) : 0.0;
            }
        }
    }
}
