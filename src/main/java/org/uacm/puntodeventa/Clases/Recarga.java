package org.uacm.puntodeventa.Clases;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Recarga {

    private final String folio;
    private final String numero;
    private final String compania;
    private final double monto;
    private final LocalDateTime fecha;
    private final String tipoPago;
    private final String numeroRastreo;
    private final int idEmpleado;
    private final String nombreEmpleado;

    // Constructor 1: Compatible con tu código actual (3 parámetros)
    public Recarga(String numero, String compania, double monto) {
        this(numero, compania, monto, "EFECTIVO", "N/A", 1, "CAJERO GENERAL");
    }

    // Constructor 2: Compatible con tu código actual (4 parámetros)
    public Recarga(String numero, String compania, double monto, String metodoPago) {
        this(numero, compania, monto, metodoPago, "N/A", 1, "CAJERO GENERAL");
    }

    // Constructor 3: Completo (con rastreo y datos del empleado)
    public Recarga(String numero, String compania, double monto, String metodoPago,
                   String numeroRastreo, int idEmpleado, String nombreEmpleado) {
        this.folio = generarFolioRecarga();
        this.numero = numero;
        this.compania = compania;
        this.monto = monto;
        this.fecha = LocalDateTime.now();
        this.tipoPago = (metodoPago != null && !metodoPago.isEmpty()) ? metodoPago : "EFECTIVO";
        this.numeroRastreo = (numeroRastreo != null && !numeroRastreo.isEmpty()) ? numeroRastreo : "N/A";
        this.idEmpleado = idEmpleado > 0 ? idEmpleado : 1;
        this.nombreEmpleado = (nombreEmpleado != null && !nombreEmpleado.isEmpty()) ? nombreEmpleado : "CAJERO GENERAL";
    }

    private String generarFolioRecarga() {
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
        sb.append("Fecha: ").append(this.fecha.format(dtf)).append("\n");
        sb.append("Cajero: ").append(this.nombreEmpleado)
          .append(" (No. Emp: ").append(this.idEmpleado).append(")\n");
        sb.append("Compañía: ").append(this.compania).append("\n");
        sb.append("Teléfono: ").append(this.numero).append("\n");
        sb.append("----------------------------------------\n");

        sb.append(String.format("%-20s $%.2f MXN\n", "MONTO RECARGADO:", this.monto));
        sb.append("SON: ").append(ConvierteNumeroLetra.convertir(this.monto)).append("\n");
        sb.append("MÉTODO DE PAGO: ").append(this.tipoPago).append("\n");

        if (!"EFECTIVO".equalsIgnoreCase(this.tipoPago)) {
            sb.append("NO. RASTREO: ").append(this.numeroRastreo).append("\n");
        }

        sb.append("========================================\n");
        sb.append("       ¡GRACIAS POR SU RECARGA!\n");
        sb.append("========================================\n");

        return sb.toString();
    }

    // Getters exactos que pide RecargaABD y tus controladores
    public String getFolio() { return folio; }
    public String getNumero() { return numero; }
    public String getCompania() { return compania; }
    public double getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public String getTipoPago() { return tipoPago; }
    public String getMetodoPago() { return tipoPago; }
    public String getNumeroRastreo() { return numeroRastreo; }
    public int getIdEmpleado() { return idEmpleado; }
    public String getNombreEmpleado() { return nombreEmpleado; }
}