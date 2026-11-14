package org.uacm.puntodeventa.Clases;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author ashje
 */
public class Venta {
    private String folio;
    private LocalDate fecha_venta;
    private List<ProductoCarrito> listaProductos;
    private double total;
    public Venta(List<ProductoCarrito> carrito) {
        this.folio = generarFolio();
        this.fecha_venta = LocalDate.now();
        this.listaProductos = new ArrayList<>(carrito);
        this.total = totalPagar();
    }
    /**
     * Constructor para la venta de varios productos
     */
    public Venta() {
        this.folio = generarFolio();
        this.fecha_venta = LocalDate.now();
        this.listaProductos = new ArrayList<>();
        this.total = 0;
    }
    
    /**
     * Genera un folio de nuestra venta
     * @return Variable con el folio de la venta
     */
    public String generarFolio() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
        }
        return sb.toString();
    }
   
    /**
     * Funcion que calcula el pago total de varios productos
     * @param lista Recibe la lista de productos a cobrar
     * @return Total a pagar por los productos
     */
    public double totalPagar() {
    double suma = 0;
        for (ProductoCarrito item : listaProductos) {
            suma += item.getTotal(); 
        }
    this.total = suma; 
    return suma;
    }
   
/**
     * Funcion que da formato al ticket con ancho fijo
     * @return Cadena con el ticket de venta alineado
     */
    public String generarTicket() {
        StringBuilder ticket = new StringBuilder();

        ticket.append("Folio: ").append(this.folio).append("\n");
        ticket.append("Fecha: ").append(this.fecha_venta).append("\n");
        ticket.append("-------------------------------------\n");

        ticket.append(String.format("%-5s %-18s %12s\n", "CANT", "DESCRIPCION", "IMPORTE"));
        ticket.append("-------------------------------------\n");
        
        ticket.append(mostrarProductos());

        ticket.append("-------------------------------------\n");
        ticket.append(String.format("%-24s $%11.2f\n", "TOTAL A PAGAR:", this.total));
        ticket.append("=====================================\n");
        
        return ticket.toString();
    }
   
    /**
     * Funcion que muestra los productos en formato tabular
     * @return Lista de productos en tipo String
     */
    public String mostrarProductos() {
        StringBuilder sb = new StringBuilder();
        for (ProductoCarrito p : listaProductos) {
            String nombreFormateado = p.getNombre();
            if (nombreFormateado.length() > 17) {
                nombreFormateado = nombreFormateado.substring(0, 15) + "..";
            }
            
            String cantidad = p.getCantidad() + "x";
            sb.append(String.format("%-5s %-18s $%11.2f\n", cantidad, nombreFormateado, p.getTotal()));
        }
        return sb.toString();
    }
   
    /**
     * Agrega los productos al "carrito"
     * @param p Producto a vender
     * @param cantidad Cuantos productos vendera del mismo
     * @return Verdadero o falso si se agrego el producto al "carrito"
     */
    public void agregarProductoCarrito(ProductoCarrito nuevo) {
        for (ProductoCarrito item : listaProductos) {
            if (item.getId() == nuevo.getId()) {
                item.setCantidad(item.getCantidad() + nuevo.getCantidad());
                this.total = totalPagar();
                return;
            }
        }
        listaProductos.add(nuevo);
        this.total = totalPagar();
    }

    public String getFolio() {
        return this.folio;
    }

    public LocalDate getFecha_venta() {
        return this.fecha_venta;
    }

    public double getTotal() {
        return this.total;
    }
    public List<ProductoCarrito> getListaProductos() {
    return this.listaProductos;
    }
    public int getCantidadProductos() {
    int total = 0;
    for (ProductoCarrito p : listaProductos) {
        total += p.getCantidad();
    }
    return total;
    }
}