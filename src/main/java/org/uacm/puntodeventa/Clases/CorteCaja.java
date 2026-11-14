/*
 * Archivo: CorteCaja.java
 * Módulo: Corte de Caja
 * Propósito: Modela un turno de caja con su apertura, arqueo y cierre.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa.Clases;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Turno de caja de un empleado. Se abre con un fondo inicial y se cierra
 * comparando el efectivo esperado contra el efectivo contado físicamente.
 */
public class CorteCaja {

    private static final double TOLERANCIA_CENTAVOS = 0.005;
    private static final int LONGITUD_FOLIO = 6;
    private static final String PREFIJO_FOLIO = "CC-";
    private static final String CARACTERES_FOLIO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final DateTimeFormatter FECHA_FOLIO = DateTimeFormatter.ofPattern("yyyyMMdd");

    private int idCorte;
    private String folio;
    private int idUsuario;
    private LocalDateTime fechaApertura;
    private double fondoInicial;
    private LocalDateTime fechaCierre;
    private double ventasEfectivo;
    private double totalRetiros;
    private double dineroEsperado;
    private double dineroContado;
    private double diferencia;
    private EstadoCorte estado = EstadoCorte.ABIERTO;
    private String observaciones;

    /**
     * Abre un turno nuevo con un folio propio y la fecha y hora actuales (requisito R1).
     *
     * @param idUsuario    identificador del cajero que abre el turno
     * @param fondoInicial efectivo con el que inicia la caja, no negativo
     * @return el turno recién abierto
     * @throws IllegalArgumentException si el fondo inicial es negativo
     */
    public static CorteCaja abrir(int idUsuario, double fondoInicial) {
        if (fondoInicial < 0) {
            throw new IllegalArgumentException("El fondo inicial no puede ser negativo.");
        }
        CorteCaja corte = new CorteCaja();
        corte.idUsuario = idUsuario;
        corte.fondoInicial = Dinero.redondear(fondoInicial);
        corte.fechaApertura = LocalDateTime.now().withNano(0);
        corte.folio = generarFolio();
        return corte;
    }

    /**
     * Calcula el efectivo que debería haber en caja según la regla BR-5:
     * fondo inicial + ventas en efectivo - retiros.
     *
     * @param ventasEfectivo total cobrado en efectivo durante el turno
     * @param totalRetiros   total retirado de la caja durante el turno
     * @return el efectivo que debería haber en caja
     */
    public double calcularEfectivoDisponible(double ventasEfectivo, double totalRetiros) {
        return Dinero.redondear(this.fondoInicial + ventasEfectivo - totalRetiros);
    }

    /**
     * Guarda en el turno las ventas y retiros con los que se calcula el efectivo esperado.
     *
     * @param ventasEfectivo total cobrado en efectivo durante el turno
     * @param totalRetiros   total retirado de la caja durante el turno
     */
    public void aplicarMovimientos(double ventasEfectivo, double totalRetiros) {
        this.ventasEfectivo = Dinero.redondear(ventasEfectivo);
        this.totalRetiros = Dinero.redondear(totalRetiros);
        this.dineroEsperado = calcularEfectivoDisponible(ventasEfectivo, totalRetiros);
    }

    /**
     * Cierra el turno con el efectivo contado y calcula la diferencia contra
     * el esperado (requisitos R3 y regla BR-6).
     *
     * @param dineroContado efectivo contado por el cajero, no negativo
     * @throws IllegalArgumentException si el monto contado es negativo
     * @throws IllegalStateException    si el turno ya estaba cerrado
     */
    public void cerrar(double dineroContado) {
        if (estado == EstadoCorte.CERRADO) {
            throw new IllegalStateException("El turno ya está cerrado.");
        }
        if (dineroContado < 0) {
            throw new IllegalArgumentException("El dinero contado no puede ser negativo.");
        }
        this.dineroContado = Dinero.redondear(dineroContado);
        this.diferencia = Dinero.redondear(this.dineroContado - this.dineroEsperado);
        this.fechaCierre = LocalDateTime.now().withNano(0);
        this.estado = EstadoCorte.CERRADO;
        this.observaciones = clasificarDiferencia().name();
    }

    /**
     * Clasifica la diferencia del arqueo como cuadrado, faltante o sobrante.
     *
     * @return el resultado del arqueo
     */
    public ResultadoCorte clasificarDiferencia() {
        if (Math.abs(diferencia) < TOLERANCIA_CENTAVOS) {
            return ResultadoCorte.CUADRADO;
        }
        return diferencia < 0 ? ResultadoCorte.FALTANTE : ResultadoCorte.SOBRANTE;
    }

    private static String generarFolio() {
        StringBuilder sufijo = new StringBuilder();
        for (int i = 0; i < LONGITUD_FOLIO; i++) {
            int posicion = ThreadLocalRandom.current().nextInt(CARACTERES_FOLIO.length());
            sufijo.append(CARACTERES_FOLIO.charAt(posicion));
        }
        return PREFIJO_FOLIO + LocalDateTime.now().format(FECHA_FOLIO) + "-" + sufijo;
    }

    /**
     * Devuelve el identificador del turno en la base de datos.
     *
     * @return identificador del turno
     */
    public int getIdCorte() {
        return idCorte;
    }

    /**
     * Asigna el identificador del turno generado por la base de datos.
     *
     * @param idCorte identificador del turno
     */
    public void setIdCorte(int idCorte) {
        this.idCorte = idCorte;
    }

    /**
     * Devuelve el folio del turno.
     *
     * @return folio del corte de caja
     */
    public String getFolio() {
        return folio;
    }

    /**
     * Asigna el folio recuperado de la base de datos.
     *
     * @param folio folio del corte de caja
     */
    public void setFolio(String folio) {
        this.folio = folio;
    }

    /**
     * Devuelve el cajero dueño del turno.
     *
     * @return identificador del usuario
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Asigna el cajero recuperado de la base de datos.
     *
     * @param idUsuario identificador del usuario
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Devuelve el momento de apertura.
     *
     * @return fecha y hora de apertura
     */
    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    /**
     * Asigna el momento de apertura recuperado de la base de datos.
     *
     * @param fechaApertura fecha y hora de apertura
     */
    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    /**
     * Devuelve el fondo con el que inició la caja.
     *
     * @return fondo inicial en pesos
     */
    public double getFondoInicial() {
        return fondoInicial;
    }

    /**
     * Asigna el fondo inicial recuperado de la base de datos.
     *
     * @param fondoInicial fondo inicial en pesos
     */
    public void setFondoInicial(double fondoInicial) {
        this.fondoInicial = fondoInicial;
    }

    /**
     * Devuelve el momento de cierre.
     *
     * @return fecha y hora de cierre, o null mientras el turno siga abierto
     */
    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    /**
     * Devuelve las ventas en efectivo consideradas en el último cálculo.
     *
     * @return ventas en efectivo en pesos
     */
    public double getVentasEfectivo() {
        return ventasEfectivo;
    }

    /**
     * Devuelve los retiros considerados en el último cálculo.
     *
     * @return retiros en pesos
     */
    public double getTotalRetiros() {
        return totalRetiros;
    }

    /**
     * Devuelve el efectivo que debía haber en caja.
     *
     * @return efectivo esperado en pesos
     */
    public double getDineroEsperado() {
        return dineroEsperado;
    }

    /**
     * Devuelve el efectivo contado al cierre.
     *
     * @return efectivo contado en pesos
     */
    public double getDineroContado() {
        return dineroContado;
    }

    /**
     * Devuelve la diferencia entre lo contado y lo esperado.
     *
     * @return diferencia en pesos; negativa si falta dinero
     */
    public double getDiferencia() {
        return diferencia;
    }

    /**
     * Devuelve el estado del turno.
     *
     * @return estado actual
     */
    public EstadoCorte getEstado() {
        return estado;
    }

    /**
     * Devuelve la clasificación registrada al cerrar el turno.
     *
     * @return CUADRADO, FALTANTE o SOBRANTE; null mientras el turno siga abierto
     */
    public String getObservaciones() {
        return observaciones;
    }
}
