/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.uacm.puntodeventa.Clases;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author laloa
 */
public class ProductoPanaderia extends Producto{
    public ProductoPanaderia(int id, String nom, String tip, double stock, double precio_pro, String ruta, String rutaImagenSeleccionada) {
        super(id, nom, tip, stock, precio_pro, ruta);
    }
    @Override
    public String mostrarDetalles() {
        return super.mostrarDetalles() + "\nProducto de Panadería (Descuento después de las 19:00)";
    }
    
    @Override
    public double getPrecio_venta() {
        LocalTime horaLimite = LocalTime.of(19, 0);
        double precioNormal = super.getPrecio_venta();
        if (LocalTime.now().isAfter(horaLimite)) {
            double precioRebajado = precioNormal * 0.85;
            return precioRebajado;
        }

        return precioNormal;
    }
}