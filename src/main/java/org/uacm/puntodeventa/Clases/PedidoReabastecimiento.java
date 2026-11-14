/*
 * Clase: PedidoReabastecimiento
 * Versión: 1.0
 * Fecha: 25/10/2026
 * Copyright: Proyecto Sistema de ventas, UACM. 
 */
package org.uacm.puntodeventa.Clases;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Representa un pedido de reabastecimiento de un producto a un
 * proveedor.
 *
 * @author Jaasiel VC
 * @version 1.0, 25/10/2026
 */
public class PedidoReabastecimiento {

    /** Secuencia para generar folios consecutivos. */
    private static final AtomicInteger SECUENCIA =
            new AtomicInteger(1000);

    /** Folio único del pedido. */
    private final int folio;

    /** Identificador del producto a reabastecer. */
    private final int idProducto;

    /** Nombre del producto a reabastecer. */
    private final String nombreProducto;

    /** Proveedor al que se hace el pedido. */
    private final String proveedor;

    /** Cantidad solicitada. */
    private final double cantidad;

    /** Fecha y hora en que se generó el pedido. */
    private final LocalDateTime fecha;

    /** Estado actual del pedido. */
    private String estado;

    /**
     * Crea un pedido con folio nuevo, fecha actual y estado "Generado".
     *
     * @param idProducto identificador del producto
     * @param nombreProducto nombre del producto
     * @param proveedor proveedor del producto
     * @param cantidad cantidad a pedir
     */
    public PedidoReabastecimiento(
            int idProducto,
            String nombreProducto,
            String proveedor,
            double cantidad) {

        this.folio = SECUENCIA.incrementAndGet();
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.proveedor = proveedor;
        this.cantidad = cantidad;
        this.fecha = LocalDateTime.now();
        this.estado = "Generado";
    }

    /**
     * Obtiene el folio del pedido.
     *
     * @return folio del pedido
     */
    public int getFolio() {
        return folio;
    }

    /**
     * Obtiene el identificador del producto.
     *
     * @return identificador del producto
     */
    public int getIdProducto() {
        return idProducto;
    }

    /**
     * Obtiene el nombre del producto.
     *
     * @return nombre del producto
     */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /**
     * Obtiene el proveedor del pedido.
     *
     * @return proveedor del pedido
     */
    public String getProveedor() {
        return proveedor;
    }

    /**
     * Obtiene la cantidad solicitada.
     *
     * @return cantidad solicitada
     */
    public double getCantidad() {
        return cantidad;
    }

    /**
     * Obtiene la fecha y hora de generación del pedido.
     *
     * @return fecha y hora del pedido
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Obtiene la fecha del pedido con formato dd/MM/yyyy HH:mm.
     *
     * @return fecha formateada
     */
    public String getFechaFormateada() {
        return fecha.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        );
    }

    /**
     * Obtiene el estado del pedido.
     *
     * @return estado actual del pedido
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado del pedido.
     *
     * @param estado nuevo estado del pedido
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }
}