package org.uacm.puntodeventa.Clases;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Recarga {
    private String numero;
    private String compania;
    private double monto;
    private LocalDateTime fecha;
    private String folio;
    
    public Recarga(String num, String com, double mon){
        this.numero= num;
        this.compania= com;
        this.monto=mon;
        this.fecha = LocalDateTime.now();
        this.folio = generarFolio();
    }

    public String getNumero() {
        return numero;
    }
    
    public String getCompania() {
        return compania;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getFolio() {
        return folio;
    }
    
    private String generarFolio() {
        String fechaParte = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String caracteres = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder randomParte = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 4; i++) {
            randomParte.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
        }
        return "REC-" + fechaParte + "-" + randomParte.toString();
    }

    // Método para generar el formato del comprobante con encabezado de la tienda
    public String generarComprobante() {
        StringBuilder ticket = new StringBuilder();
        
        ticket.append("--------------------------------------\n");
        ticket.append("     ABARROTES JASE S.A. DE C.V.\n");
        ticket.append("         RFC: JASE230038LH0\n");
        ticket.append("  Prol. San Isidro 151, San Lorenzo\n");
        ticket.append("   Tezonco, Iztapalapa, CDMX, 09790 \n");
        ticket.append("--------------------------------------\n");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        ticket.append("Fecha: ").append(this.fecha.format(dtf)).append("\n");
        ticket.append("Folio: ").append(this.folio).append("\n");
        
        ticket.append("--------------------------------------\n");
        ticket.append(String.format("Compañía: %s\n", this.compania));
        ticket.append(String.format("Teléfono: %s\n", this.numero));
        ticket.append("--------------------------------------\n");
        
        ticket.append(String.format("%-25s $%8.2f\n", "MONTO RECARGADO:", this.monto));
        ticket.append("--------------------------------------\n");
        ticket.append("       ¡Gracias por su compra!\n");
        
        return ticket.toString();
    }
}