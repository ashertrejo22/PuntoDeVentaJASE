/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.uacm.puntodeventa.Clases;

import java.time.LocalDate;
import java.util.Random;

public class Recarga {
    private String numero;
    private String compania;
    private double monto;
    private LocalDate fecha;
    private String folio;
    
    public Recarga(String num, String com, double mon){
        this.numero= num;
        this.compania= com;
        this.monto=mon;
        this.folio = generarFolio();
        this.fecha = LocalDate.now();
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

    public LocalDate getFecha() {
        return fecha;
    }

    public String getFolio() {
        return folio;
    }
    
    private String generarFolio() {
        String caracteres = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder("REC-");
        Random rnd = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
        }
        return sb.toString();
    }

    // Método para generar el formato del comprobante
    public String generarComprobante() {
        StringBuilder ticket = new StringBuilder();
        ticket.append("======== RECARGA TELEFÓNICA ========\n");
        ticket.append("Folio: ").append(this.folio).append("\n");
        ticket.append("Fecha: ").append(this.fecha).append("\n");
        ticket.append("------------------------------------\n");
        ticket.append(String.format("Compañía: %s\n", this.compania));
        ticket.append(String.format("Número:   %s\n", this.numero));
        ticket.append("------------------------------------\n");
        ticket.append(String.format("MONTO RECARGADO:      $%10.2f\n", this.monto));
        ticket.append("====================================\n");
        return ticket.toString();
    }
    
    
}
