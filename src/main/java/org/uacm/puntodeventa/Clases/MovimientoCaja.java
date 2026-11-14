/*
 * Archivo: MovimientoCaja.java
 * Módulo: Corte de Caja
 * Propósito: Representa un retiro de efectivo realizado durante un turno.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.Clases;

import java.time.LocalDateTime;

/**
 * Movimiento de efectivo de un turno de caja. Hoy solo existen retiros, que
 * restan del efectivo esperado según la regla de negocio BR-5.
 */
public class MovimientoCaja {

    private final int idCorte;
    private final TipoMovimiento tipo;
    private final double monto;
    private final String motivo;
    private final LocalDateTime fecha;

    /**
     * Crea un retiro de efectivo con la fecha y hora actuales.
     *
     * @param idCorte identificador del turno al que pertenece el retiro
     * @param monto   importe retirado, mayor que cero
     * @param motivo  razón del retiro, obligatoria
     * @throws IllegalArgumentException si el monto no es positivo o falta el motivo
     */
    public MovimientoCaja(int idCorte, double monto, String motivo) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto del retiro debe ser mayor que cero.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Captura el motivo del retiro.");
        }
        this.idCorte = idCorte;
        this.tipo = TipoMovimiento.RETIRO;
        this.monto = Dinero.redondear(monto);
        this.motivo = motivo.trim();
        this.fecha = LocalDateTime.now().withNano(0);
    }

    /**
     * Devuelve el turno al que pertenece el movimiento.
     *
     * @return identificador del turno
     */
    public int getIdCorte() {
        return idCorte;
    }

    /**
     * Devuelve el tipo de movimiento.
     *
     * @return tipo de movimiento
     */
    public TipoMovimiento getTipo() {
        return tipo;
    }

    /**
     * Devuelve el importe del movimiento.
     *
     * @return importe en pesos
     */
    public double getMonto() {
        return monto;
    }

    /**
     * Devuelve la razón del movimiento.
     *
     * @return motivo capturado por el cajero
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Devuelve el momento en que se registró el movimiento.
     *
     * @return fecha y hora del movimiento
     */
    public LocalDateTime getFecha() {
        return fecha;
    }
}
