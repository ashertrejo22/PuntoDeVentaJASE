package org.uacm.puntodeventa.Clases;

/**
 * @author  ashjer
 */
public class ItemReporte {
    private String folio;
    private String fecha;
    private String detalles; 
    private double total;

    public ItemReporte(String folio, String fecha, String detalles, double total) {
        this.folio = folio;
        this.fecha = fecha;
        this.detalles = detalles;
        this.total = total;
    }

    public String getFolio() { 
        return folio; 
    }
    
    public String getFecha() { 
        return fecha; 
    }
    
    public String getDetalles() { 
        return detalles; 
    }
    
    public double getTotal() { 
        return total; 
    }
}