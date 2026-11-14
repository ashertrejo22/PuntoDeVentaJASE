/*
 * Archivo: ParametrosSql.java
 * Módulo: Base de datos
 * Propósito: Asigna en orden los parámetros de una sentencia SQL preparada.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.DB;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Evita numerar a mano los parámetros de una sentencia: los valores se asignan
 * en el mismo orden en que aparecen los signos de interrogación del SQL.
 */
public final class ParametrosSql {

    private ParametrosSql() {
    }

    /**
     * Asigna los valores a la sentencia, empezando en el primer parámetro.
     *
     * @param sentencia sentencia preparada
     * @param valores   valores en el orden de los parámetros
     * @throws SQLException si algún valor no puede asignarse
     */
    public static void asignar(PreparedStatement sentencia, Object... valores) throws SQLException {
        int posicion = 1;
        for (Object valor : valores) {
            sentencia.setObject(posicion, valor);
            posicion++;
        }
    }
}
