/*
 * Clase: AlertasInventario
 * Versión: 1.0
 * Fecha: 25/10/2026
 * Copyright: Proyecto Sistema de ventas, UACM. 
 */
package org.uacm.puntodeventa.Clases;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Utilidades para detectar productos con alerta de inventario y
 * generar pedidos de reabastecimiento.
 *
 * @author Jaasiel VC
 * @version 1.0, 25/10/2026
 */
public final class AlertasInventario {

    private AlertasInventario() {
    }

    /**
     * Obtiene los productos que necesitan reabastecimiento.
     *
     * @param productos lista de productos a revisar; puede ser null
     * @return lista con los productos en alerta, vacía si no hay ninguno
     */
    public static ObservableList<Producto> obtenerProductosConAlerta(
            ObservableList<Producto> productos) {

        ObservableList<Producto> alertas =
                FXCollections.observableArrayList();

        if (productos == null) {
            return alertas;
        }

        for (Producto producto : productos) {
            if (producto != null
                    && producto.necesitaReabastecimiento()) {
                alertas.add(producto);
            }
        }

        return alertas;
    }

    /**
     * Genera un pedido de reabastecimiento para un producto.
     *
     * @param producto producto a reabastecer
     * @return pedido generado con los datos del producto
     * @throws IllegalArgumentException si el producto es null, no
     *         necesita reabastecimiento, no tiene proveedor o la
     *         cantidad a pedir no es mayor que cero
     */
    public static PedidoReabastecimiento generarPedido(
            Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException(
                    "Selecciona un producto."
            );
        }

        if (!producto.necesitaReabastecimiento()) {
            throw new IllegalArgumentException(
                    "El producto no necesita reabastecimiento."
            );
        }

        if (producto.getProveedor() == null
                || producto.getProveedor().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El producto no tiene proveedor configurado."
            );
        }

        if (producto.getCantidadReabastecimiento() <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad a pedir debe ser mayor que cero."
            );
        }

        return new PedidoReabastecimiento(
                producto.getID_producto(),
                producto.getNombre(),
                producto.getProveedor(),
                producto.getCantidadReabastecimiento()
        );
    }
}