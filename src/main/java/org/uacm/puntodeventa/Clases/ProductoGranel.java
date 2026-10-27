package org.uacm.puntodeventa.Clases;
/**
 *
 * @author ashje
 */
public class ProductoGranel extends Producto{
    private String unidadMedida;
    public ProductoGranel(int id, String nom, String tip, double stock, double precio_pro, String uM, String ruta) {
        super(id, nom, tip, stock, precio_pro, ruta);
        this.unidadMedida = uM;
    }
    
    public double calcularPrecioExacto(double pesoVendido) {
        return super.getPrecio_venta() * pesoVendido; 
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
    
    @Override
    public String mostrarDetalles() {
        return super.mostrarDetalles() + "\nUnidad de medida: " + this.unidadMedida;
    }
    
    
}
