package org.uacm.puntodeventa.Clases;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Venta {
    private String folio;
    private LocalDateTime fecha_venta;
    private List<ProductoCarrito> listaProductos;
    private double total;
    private String tipoPago;

    public Venta(List<ProductoCarrito> carrito, String metodoPago) {
        this.folio = generarFolio();
        this.fecha_venta = LocalDateTime.now();
        this.listaProductos = new ArrayList<>(carrito);
        this.total = totalPagar();
        this.tipoPago = (metodoPago != null && !metodoPago.isEmpty()) ? metodoPago : "EFECTIVO";
    }
    
    public Venta() {
        this.folio = generarFolio();
        this.fecha_venta = LocalDateTime.now();
        this.listaProductos = new ArrayList<>();
        this.total = 0;
        this.tipoPago = "EFECTIVO"; 
    }
    
    public String generarFolio() {
        String fechaParte = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomParte = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 4; i++) {
            randomParte.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
        }
        return "VTA-" + fechaParte + "-" + randomParte.toString();
    }
   
    public double totalPagar() {
        double suma = 0;
        for (ProductoCarrito item : listaProductos) {
            suma += item.getTotal(); 
        }
        this.total = suma; 
        return suma;
    }
   
    public String generarTicket() {
        StringBuilder ticket = new StringBuilder();

        ticket.append("--------------------------------------\n");
        ticket.append("      ABARROTES JASE S.A. DE C.V.\n");
        ticket.append("          RFC: JASE230038LH0\n");
        ticket.append("  Prol. San Isidro 151, San Lorenzo\n");
        ticket.append("   Tezonco, Iztapalapa, CDMX, 09790 \n");
        ticket.append("--------------------------------------\n");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        ticket.append("Fecha: ").append(this.fecha_venta.format(dtf)).append("\n");
        ticket.append("Folio: ").append(this.folio).append("\n");
        
        ticket.append("--------------------------------------\n");
        ticket.append(String.format("%-11s %-8s %-5s %-8s\n", "ARTICULO", "CATEG", "CANT", "IMPORTE"));
        
        ticket.append(mostrarProductos());

        ticket.append("--------------------------------------\n");
        String totalFormateado = String.format("$%.2f MXN", this.total);
        ticket.append(String.format("%-25s %s\n", "TOTAL PAGADO:", totalFormateado));
        
        // Muestra de manera dinámica el método real (Efectivo con cambio o Tarjeta)
        ticket.append("Método de Pago:\n").append(this.tipoPago).append("\n");
        
        ticket.append("--------------------------------------\n");
        ticket.append("       ¡Gracias por su compra!\n");
        
        return ticket.toString();
    }

    public String mostrarProductos() {
        StringBuilder sb = new StringBuilder();
        for (ProductoCarrito p : listaProductos) {
            Producto original = InformacionProductos.getInstance().getProductos().stream()
                    .filter(prod -> prod.getID_producto() == p.getId())
                    .findFirst()
                    .orElse(null);
                    
            String categoriaReal = (original != null && original.getCategoria() != null) ? original.getCategoria() : "General";
            String nombreFormateado = p.getNombre().length() > 10 ? p.getNombre().substring(0, 9) + "." : p.getNombre();
            String catCorta = categoriaReal.length() > 7 ? categoriaReal.substring(0, 7) : categoriaReal;
            
            String cantidad = String.valueOf(p.getCantidad());
            if (cantidad.endsWith(".0")) {
                cantidad = cantidad.replace(".0", "");
            }
            
            sb.append(String.format("%-11s %-8s %-5s $%8.2f\n", nombreFormateado, catCorta, cantidad, p.getTotal()));
        }
        return sb.toString();
    }

    public String getFolio() { return this.folio; }
    public LocalDateTime getFecha_venta() { return this.fecha_venta; }
    public double getTotal() { return this.total; }
    public List<ProductoCarrito> getListaProductos() { return this.listaProductos; }
    public String getMetodoPago() { return this.tipoPago; }
    
    public int getCantidadProductos() {
        int totalProd = 0;
        for (ProductoCarrito p : listaProductos) {
            totalProd += p.getCantidad();
        }
        return totalProd;
    }
}