package org.uacm.puntodeventa.Clases;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class Venta {

    private final String folio;
    private final LocalDateTime fecha_venta;
    private final List<ProductoCarrito> productos;
    private final double total;
    private final double pagoCliente;
    private final double cambio;
    private final String metodoPago;
    private final String numeroRastreo;
    private final int idEmpleado;
    private final String nombreEmpleado;

    // Constructor exacto que pide PantallaPrincipalController: new Venta(carritoList, metodoFinal)
    public Venta(List<ProductoCarrito> productos, String metodoPago) {
        this(
            productos,
            calcularTotalLista(productos),
            calcularTotalLista(productos),
            0.0,
            metodoPago,
            "N/A",
            1,
            "CAJERO GENERAL"
        );
    }

    // Constructor con 1 parámetro (solo la lista) por si otra clase lo usa
    public Venta(List<ProductoCarrito> productos) {
        this(productos, "EFECTIVO");
    }

    // Constructor con 4 parámetros
    public Venta(List<ProductoCarrito> productos, double total, double pagoCliente, double cambio) {
        this(productos, total, pagoCliente, cambio, "EFECTIVO", "N/A", 1, "CAJERO GENERAL");
    }

    // Constructor con 5 parámetros
    public Venta(List<ProductoCarrito> productos, double total, double pagoCliente, double cambio, String metodoPago) {
        this(productos, total, pagoCliente, cambio, metodoPago, "N/A", 1, "CAJERO GENERAL");
    }

    // Constructor para consultas de base de datos / reportes
    public Venta(String folio, LocalDateTime fecha_venta, double total, String metodoPago) {
        this.folio = (folio != null && !folio.isEmpty()) ? folio : generarFolio();
        this.fecha_venta = (fecha_venta != null) ? fecha_venta : LocalDateTime.now();
        this.productos = null;
        this.total = total;
        this.pagoCliente = total;
        this.cambio = 0.0;
        this.metodoPago = (metodoPago != null && !metodoPago.isEmpty()) ? metodoPago : "EFECTIVO";
        this.numeroRastreo = "N/A";
        this.idEmpleado = 1;
        this.nombreEmpleado = "CAJERO GENERAL";
    }

    // Constructor completo (con rastreo y datos del empleado)
    public Venta(List<ProductoCarrito> productos, double total, double pagoCliente, double cambio,
                 String metodoPago, String numeroRastreo, int idEmpleado, String nombreEmpleado) {
        this.folio = generarFolio();
        this.fecha_venta = LocalDateTime.now();
        this.productos = productos;
        this.total = total;
        this.pagoCliente = pagoCliente;
        this.cambio = cambio;
        this.metodoPago = (metodoPago != null && !metodoPago.isEmpty()) ? metodoPago : "EFECTIVO";
        this.numeroRastreo = (numeroRastreo != null && !numeroRastreo.isEmpty()) ? numeroRastreo : "N/A";
        this.idEmpleado = idEmpleado > 0 ? idEmpleado : 1;
        this.nombreEmpleado = (nombreEmpleado != null && !nombreEmpleado.isEmpty()) ? nombreEmpleado : "CAJERO GENERAL";
    }

    private static double calcularTotalLista(List<ProductoCarrito> lista) {
        if (lista == null) return 0.0;
        return lista.stream().mapToDouble(ProductoCarrito::getTotal).sum();
    }

    private String generarFolio() {
        String fechaParte = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 4; i++) {
            sb.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
        }
        return "VT-" + fechaParte + "-" + sb.toString();
    }

    public String generarTicket() {
        return generarComprobante();
    }

    public String generarComprobanteVenta() {
        return generarComprobante();
    }

    public String generarComprobante() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("        ABARROTES JASE S.A. DE C.V.\n");
        sb.append("             RFC: JASE230038LH0\n");
        sb.append("    Prol. San Isidro 151, San Lorenzo\n");
        sb.append("         Tezonco, Iztapalapa, CDMX\n");
        sb.append("========================================\n");
        sb.append("Folio: ").append(this.folio).append("\n");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        sb.append("Fecha: ").append(this.fecha_venta.format(dtf)).append("\n");
        sb.append("Cajero: ").append(this.nombreEmpleado)
          .append(" (No. Emp: ").append(this.idEmpleado).append(")\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-20s %5s %10s\n", "PRODUCTO", "CANT", "IMPORTE"));
        sb.append("----------------------------------------\n");

        if (this.productos != null) {
            for (ProductoCarrito item : this.productos) {
                String nombre = item.getNombre();
                if (nombre.length() > 18) {
                    nombre = nombre.substring(0, 18);
                }
                sb.append(String.format("%-20s %5.1f $%9.2f\n",
                        nombre, item.getCantidad(), item.getTotal()));
            }
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("TOTAL A PAGAR:      $%.2f MXN\n", this.total));
        sb.append("SON: ").append(ConvierteNumeroLetra.convertir(this.total)).append("\n");
        sb.append(String.format("RECIBIDO:           $%.2f MXN\n", this.pagoCliente));
        sb.append(String.format("CAMBIO:             $%.2f MXN\n", this.cambio));
        sb.append("----------------------------------------\n");
        sb.append("MÉTODO DE PAGO: ").append(this.metodoPago).append("\n");

        if (!"EFECTIVO".equalsIgnoreCase(this.metodoPago)) {
            sb.append("NO. RASTREO: ").append(this.numeroRastreo).append("\n");
        }

        sb.append("========================================\n");
        sb.append("       ¡GRACIAS POR SU COMPRA!\n");
        sb.append("========================================\n");

        return sb.toString();
    }

    public String getFolio() { return folio; }
    public LocalDateTime getFecha_venta() { return fecha_venta; }
    public LocalDateTime getFecha() { return fecha_venta; }
    public double getTotal() { return total; }
    public double getTotalAPagar() { return total; }
    public double getPagoCliente() { return pagoCliente; }
    public double getCambio() { return cambio; }
    public String getMetodoPago() { return metodoPago; }
    public List<ProductoCarrito> getProductos() { return productos; }
    public String getNumeroRastreo() { return numeroRastreo; }
    public int getIdEmpleado() { return idEmpleado; }
    public String getNombreEmpleado() { return nombreEmpleado; }
}