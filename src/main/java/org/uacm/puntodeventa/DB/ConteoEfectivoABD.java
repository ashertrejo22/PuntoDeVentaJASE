/*
 * Archivo: ConteoEfectivoABD.java
 * Módulo: Corte de Caja
 * Propósito: Guarda y consulta el conteo de billetes y monedas de cada corte.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-11-13
 */

package org.uacm.puntodeventa.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.uacm.puntodeventa.Clases.ConteoEfectivo;
import org.uacm.puntodeventa.Clases.Denominacion;

/**
 * Acceso a la tabla ConteoEfectivo (una fila por denominación contada en un corte).
 */
public final class ConteoEfectivoABD {

    private static final String SQL_BORRAR = "DELETE FROM ConteoEfectivo WHERE id_corte = ?";
    private static final String SQL_INSERTAR = "INSERT INTO ConteoEfectivo (id_corte, denominacion, cantidad) "
            + "VALUES (?,?,?)";
    private static final String SQL_CONSULTAR = "SELECT denominacion, cantidad FROM ConteoEfectivo "
            + "WHERE id_corte = ?";

    private ConteoEfectivoABD() {
    }

    /**
     * Guarda el conteo de un corte, reemplazando el que hubiera. Solo se guardan las denominaciones con piezas.
     *
     * @param idCorte identificador del turno
     * @param conteo  billetes y monedas contados
     * @throws SQLException si falla la base de datos; en ese caso no se guarda nada
     */
    public static void guardar(int idCorte, ConteoEfectivo conteo) throws SQLException {
        try (Connection conexion = ConexionBD.getConnection()) {
            conexion.setAutoCommit(false);
            try {
                borrar(conexion, idCorte);
                insertar(conexion, idCorte, conteo);
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        }
    }

    /**
     * Recupera el conteo de un corte.
     *
     * @param idCorte identificador del turno
     * @return el conteo guardado; vacío si el corte se cerró sin desglose
     * @throws SQLException si falla la consulta o hay una denominación desconocida
     */
    public static ConteoEfectivo obtener(int idCorte) throws SQLException {
        ConteoEfectivo conteo = new ConteoEfectivo();
        try (Connection conexion = ConexionBD.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR)) {
            sentencia.setInt(1, idCorte);
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    conteo.establecerCantidad(leerDenominacion(filas.getString("denominacion")),
                            filas.getInt("cantidad"));
                }
            }
        }
        return conteo;
    }

    private static void borrar(Connection conexion, int idCorte) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_BORRAR)) {
            sentencia.setInt(1, idCorte);
            sentencia.executeUpdate();
        }
    }

    private static void insertar(Connection conexion, int idCorte, ConteoEfectivo conteo) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {
            for (Denominacion denominacion : Denominacion.values()) {
                if (conteo.obtenerCantidad(denominacion) > 0) {
                    ParametrosSql.asignar(sentencia, idCorte, denominacion.name(),
                            conteo.obtenerCantidad(denominacion));
                    sentencia.executeUpdate();
                }
            }
        }
    }

    private static Denominacion leerDenominacion(String nombre) throws SQLException {
        try {
            return Denominacion.valueOf(nombre);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Denominación desconocida en el conteo: " + nombre, e);
        }
    }
}
