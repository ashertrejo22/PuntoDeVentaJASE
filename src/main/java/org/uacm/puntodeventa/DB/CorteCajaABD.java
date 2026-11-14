/*
 * Archivo: CorteCajaABD.java
 * Módulo: Corte de Caja
 * Propósito: Guarda, consulta y cierra los turnos de caja.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.DB;

import java.util.Locale;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;
import org.uacm.puntodeventa.Clases.CorteCaja;
import org.uacm.puntodeventa.Clases.EstadoCorte;
import org.uacm.puntodeventa.Clases.TipoPago;

/**
 * Acceso a la tabla CorteCaja.
 */
public final class CorteCajaABD {

    private static final String SQL_INSERTAR = "INSERT INTO CorteCaja "
            + "(folio, id_usuario, fecha_apertura, fondo_inicial, estado) VALUES (?,?,?,?,?)";
    private static final String SQL_ABIERTO = "SELECT id_corte, folio, id_usuario, fecha_apertura, "
            + "fondo_inicial FROM CorteCaja WHERE id_usuario = ? AND estado = ?";
    private static final String SQL_CIERRE = "UPDATE CorteCaja SET fecha_cierre = ?, efectivo_esperado = ?, "
            + "efectivo_contado = ?, diferencia = ?, estado = ?, observaciones = ? WHERE id_corte = ?";
    private static final String SQL_VENTAS_EFECTIVO = "SELECT COALESCE(SUM(total_a_pagar), 0) FROM Venta "
            + "WHERE id_corte = ? AND UPPER(COALESCE(metodo_pago, 'EFECTIVO')) = ?";

    private CorteCajaABD() {
    }

    /**
     * Inserta la apertura de un turno y asigna al objeto el id generado.
     *
     * @param corte turno recién abierto
     * @throws SQLException si falla la inserción
     */
    public static void insertar(CorteCaja corte) throws SQLException {
        try (Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR,
                        Statement.RETURN_GENERATED_KEYS)) {
            ParametrosSql.asignar(sentencia,
                    corte.getFolio(),
                    corte.getIdUsuario(),
                    corte.getFechaApertura().toString(),
                    corte.getFondoInicial(),
                    EstadoCorte.ABIERTO.getValor());
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (!claves.next()) {
                    throw new SQLException("No se pudo obtener el id_corte generado.");
                }
                corte.setIdCorte(claves.getInt(1));
            }
        }
    }

    /**
     * Busca el turno que el usuario tiene abierto.
     *
     * @param idUsuario identificador del cajero
     * @return el turno abierto, o vacío si no tiene ninguno
     * @throws SQLException si falla la consulta
     */
    public static Optional<CorteCaja> obtenerTurnoAbierto(int idUsuario) throws SQLException {
        try (Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_ABIERTO)) {
            sentencia.setInt(1, idUsuario);
            sentencia.setString(2, EstadoCorte.ABIERTO.getValor());
            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() ? Optional.of(leerTurno(fila)) : Optional.empty();
            }
        }
    }

    /**
     * Guarda los datos del cierre de un turno.
     *
     * @param corte turno ya cerrado en memoria
     * @throws SQLException si falla la actualización o no existe el turno
     */
    public static void actualizarCierre(CorteCaja corte) throws SQLException {
        try (Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_CIERRE)) {
            ParametrosSql.asignar(sentencia,
                    corte.getFechaCierre().toString(),
                    corte.getDineroEsperado(),
                    corte.getDineroContado(),
                    corte.getDiferencia(),
                    EstadoCorte.CERRADO.getValor(),
                    corte.getObservaciones(),
                    corte.getIdCorte());
            if (sentencia.executeUpdate() == 0) {
                throw new SQLException("No existe el corte con id " + corte.getIdCorte());
            }
        }
    }

    /**
     * Suma las ventas cobradas en efectivo durante un turno.
     *
     * @param idCorte identificador del turno
     * @return total vendido en efectivo, o cero si no hubo ventas
     * @throws SQLException si falla la consulta
     */
    public static double sumarVentasEfectivo(int idCorte) throws SQLException {
        try (Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_VENTAS_EFECTIVO)) {
            sentencia.setInt(1, idCorte);
            sentencia.setString(2, TipoPago.EFECTIVO.getValor().toUpperCase(Locale.ROOT));
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? resultado.getDouble(1) : 0.0;
            }
        }
    }

    private static CorteCaja leerTurno(ResultSet fila) throws SQLException {
        CorteCaja corte = new CorteCaja();
        corte.setIdCorte(fila.getInt("id_corte"));
        corte.setFolio(fila.getString("folio"));
        corte.setIdUsuario(fila.getInt("id_usuario"));
        corte.setFechaApertura(LocalDateTime.parse(fila.getString("fecha_apertura")));
        corte.setFondoInicial(fila.getDouble("fondo_inicial"));
        return corte;
    }
}
