package org.uacm.puntodeventa.Clases;

import java.time.LocalDate;

public class ProductoPerecedero extends Producto {

    private LocalDate fechaCaducidad;
    private String lote;
    public double precioConDescuento = 0;

    public ProductoPerecedero(int id, String nom, String tip, double stock, double precio_pro, LocalDate fech, String lot, String ruta) {
        super(id, nom, tip, stock, precio_pro, ruta);
        this.fechaCaducidad = fech;
        this.lote = lot;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public double getPrecioConDescuento() {
        return precioConDescuento;
    }

    public void setPrecioConDescuento(double precioDes) {
        this.precioConDescuento = precioDes;
    }

    public boolean validarCaducidad() {
        return fechaCaducidad.isBefore(LocalDate.now());
    }

    @Override
    public double getPrecio_venta() {
        double precioNormal = super.getPrecio_venta();
        if (getFechaCaducidad() != null && getFechaCaducidad().isBefore(LocalDate.now().plusDays(7))) {
            double precioRebajado = precioNormal * 0.85;
            return Math.ceil(precioRebajado);
        }

        return precioNormal;
    }

    @Override
    public String mostrarDetalles() {
        return super.mostrarDetalles() + "\nCaducidad: " + this.fechaCaducidad + "\nLote: " + this.lote;
    }
}
