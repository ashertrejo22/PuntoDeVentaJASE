package org.uacm.puntodeventa.Clases;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Recarga {
    private String folio;
    private String numero;
    private String compania;
    private double monto;
    private LocalDateTime fecha;
    private String tipoPago;

    // Constructor principal que recibe el método de pago
    public Recarga(String numero, String compania, double monto, String metodoPago) {
        this.folio = generarFolio();
        this.numero = numero;
        this.compania = compania;
        this.monto = monto;
        this.fecha = LocalDateTime.now();
        this.tipoPago = (metodoPago != null && !metodoPago.isEmpty()) ? metodoPago : "EFECTIVO";
    }

    public String generarFolio() {
        String fechaParte = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomParte = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 4; i++) {
            randomParte.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
        }
        return "REC-" + fechaParte + "-" + randomParte.toString();
    }

    public String generarComprobante() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("        ABARROTES JASE S.A. DE C.V.\n"); 
        sb.append("             RFC: JASE230038LH0\n");
        sb.append("    Prol. San Isidro 151, San Lorenzo\n");
        sb.append("         Tezonco, Iztapalapa, CDMX\n");
        sb.append("========================================\n");
        sb.append("     *** COMPROBANTE DE RECARGA ***\n");
        sb.append("----------------------------------------\n");
        sb.append("Folio: ").append(this.folio).append("\n");
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        sb.append("Fecha Venta: ").append(this.fecha.format(dtf)).append("\n");
        sb.append("Compañía: ").append(this.compania).append("\n");
        sb.append("Teléfono: ").append(this.numero).append("\n");
        sb.append("----------------------------------------\n");
        
        String montoFormateado = String.format("$%.2f MXN", this.monto);
        sb.append(String.format("%-25s %s\n", "MONTO RECARGADO:", montoFormateado));
        sb.append("Método de Pago:\n").append(this.tipoPago).append("\n");
        sb.append("========================================\n");
        sb.append("       ¡Gracias por su recarga!\n");
        sb.append("========================================\n");
        
        return sb.toString();
    }

    public String getFolio() { return folio; }
    public String getNumero() { return numero; }
    public String getCompania() { return compania; }
    public double getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public String getMetodoPago() { return tipoPago; }
}