/*
 * Archivo: CorteCajaServicio.java
 * Módulo: Corte de Caja
 * Propósito: Aplica las reglas de negocio de apertura, retiros y cierre de turno.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.cortecaja;

import java.sql.SQLException;
import java.util.Optional;
import org.uacm.puntodeventa.Clases.ConteoEfectivo;
import org.uacm.puntodeventa.Clases.CorteCaja;
import org.uacm.puntodeventa.Clases.EstadoCorte;
import org.uacm.puntodeventa.Clases.MovimientoCaja;
import org.uacm.puntodeventa.DB.ConteoEfectivoABD;
import org.uacm.puntodeventa.DB.CorteCajaABD;
import org.uacm.puntodeventa.DB.MovimientoCajaABD;
import org.uacm.puntodeventa.DB.UsuarioABD;
import org.uacm.puntodeventa.reportes.FormatoReporte;

/**
 * Reglas del módulo de Corte de Caja. La pantalla solo lee datos y llama a esta clase,
 * que señala los errores con excepciones para que la pantalla decida cómo mostrarlos.
 */
public class CorteCajaServicio {

    /**
     * Busca el turno abierto de un cajero.
     *
     * @param idUsuario identificador del cajero
     * @return el turno abierto, o vacío si no tiene ninguno
     * @throws SQLException si falla la consulta
     */
    public Optional<CorteCaja> turnoAbierto(int idUsuario) throws SQLException {
        return CorteCajaABD.obtenerTurnoAbierto(idUsuario);
    }

    /**
     * Devuelve el turno abierto de un cajero o rechaza la operación (regla BR-3).
     *
     * @param idUsuario identificador del cajero que quiere registrar una venta
     * @return el turno abierto
     * @throws SQLException          si falla la consulta
     * @throws IllegalStateException si el cajero no tiene un turno abierto
     */
    public CorteCaja exigirTurnoAbierto(int idUsuario) throws SQLException {
        return turnoAbierto(idUsuario).orElseThrow(() -> new IllegalStateException(
                "No hay un turno de caja abierto. Abre tu turno en Corte de Caja antes de registrar ventas."));
    }

    /**
     * Abre un turno nuevo (requisito R1 y regla BR-4).
     *
     * @param idUsuario    identificador del cajero
     * @param fondoInicial efectivo con el que inicia la caja
     * @return el turno abierto y guardado
     * @throws SQLException             si falla la base de datos
     * @throws IllegalStateException    si el cajero ya tiene un turno abierto
     * @throws IllegalArgumentException si el fondo inicial es negativo
     */
    public CorteCaja abrirTurno(int idUsuario, double fondoInicial) throws SQLException {
        Optional<CorteCaja> existente = turnoAbierto(idUsuario);
        if (existente.isPresent()) {
            throw new IllegalStateException("Ya tienes un turno abierto (folio "
                    + existente.get().getFolio() + "). Ciérralo antes de abrir uno nuevo.");
        }
        CorteCaja corte = CorteCaja.abrir(idUsuario, fondoInicial);
        CorteCajaABD.insertar(corte);
        return corte;
    }

    /**
     * Registra un retiro de efectivo en el turno abierto.
     *
     * @param corte  turno abierto
     * @param monto  importe a retirar
     * @param motivo razón del retiro
     * @throws SQLException             si falla la base de datos
     * @throws IllegalStateException    si el turno ya está cerrado
     * @throws IllegalArgumentException si los datos son inválidos o el retiro excede el efectivo en caja
     */
    public void registrarRetiro(CorteCaja corte, double monto, String motivo) throws SQLException {
        exigirAbierto(corte);
        MovimientoCaja retiro = new MovimientoCaja(corte.getIdCorte(), monto, motivo);
        double disponible = efectivoDisponible(corte);
        if (retiro.getMonto() > disponible) {
            throw new IllegalArgumentException("El retiro (" + FormatoReporte.pesos(retiro.getMonto())
                    + ") excede el efectivo disponible en caja (" + FormatoReporte.pesos(disponible) + ").");
        }
        MovimientoCajaABD.insertar(retiro);
    }

    /**
     * Cierra el turno comparando el efectivo contado contra el esperado (requisitos R2 y R3).
     *
     * @param corte         turno abierto
     * @param dineroContado efectivo contado físicamente
     * @throws SQLException             si falla la base de datos
     * @throws IllegalStateException    si el turno ya está cerrado
     * @throws IllegalArgumentException si el monto contado es inválido
     */
    public void cerrarTurno(CorteCaja corte, double dineroContado) throws SQLException {
        prepararCierre(corte, dineroContado);
        CorteCajaABD.actualizarCierre(corte);
    }

    /**
     * Cierra el turno con el conteo de billetes y monedas: el dinero contado es el total del conteo
     * y el desglose queda guardado para el comprobante.
     *
     * @param corte  turno abierto
     * @param conteo billetes y monedas contados físicamente
     * @throws SQLException          si falla la base de datos
     * @throws IllegalStateException si el turno ya está cerrado
     */
    public void cerrarTurno(CorteCaja corte, ConteoEfectivo conteo) throws SQLException {
        prepararCierre(corte, conteo.calcularTotal());
        ConteoEfectivoABD.guardar(corte.getIdCorte(), conteo);
        CorteCajaABD.actualizarCierre(corte);
    }

    /**
     * Genera el comprobante de un turno cerrado (requisito R9).
     *
     * @param corte turno cerrado
     * @return el texto del comprobante
     * @throws SQLException          si falla la consulta del cajero o del conteo
     * @throws IllegalStateException si el turno todavía está abierto
     */
    public String generarComprobante(CorteCaja corte) throws SQLException {
        if (corte.getEstado() != EstadoCorte.CERRADO) {
            throw new IllegalStateException("El comprobante se genera hasta que cierres el turno.");
        }
        return ComprobanteCorte.construir(corte, UsuarioABD.obtenerNombre(corte.getIdUsuario()),
                ConteoEfectivoABD.obtener(corte.getIdCorte()));
    }

    private void prepararCierre(CorteCaja corte, double dineroContado) throws SQLException {
        exigirAbierto(corte);
        corte.aplicarMovimientos(CorteCajaABD.sumarVentasEfectivo(corte.getIdCorte()),
                MovimientoCajaABD.sumarRetiros(corte.getIdCorte()));
        corte.cerrar(dineroContado);
    }

    private double efectivoDisponible(CorteCaja corte) throws SQLException {
        return corte.calcularEfectivoDisponible(CorteCajaABD.sumarVentasEfectivo(corte.getIdCorte()),
                MovimientoCajaABD.sumarRetiros(corte.getIdCorte()));
    }

    private void exigirAbierto(CorteCaja corte) {
        if (corte.getEstado() != EstadoCorte.ABIERTO) {
            throw new IllegalStateException("El turno ya está cerrado.");
        }
    }
}
