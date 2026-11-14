package org.uacm.puntodeventa.Clases;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author juanarturogonzalezgomez
 */
public class RegistroVentas {

    private static RegistroVentas instancia;
        private List<Venta> historialVentas;
    private List<Recarga> historialRecargas; 

    private RegistroVentas() {
        historialVentas = new ArrayList<>();
        historialRecargas = new ArrayList<>();
    }

    public static RegistroVentas getInstance() {
        if (instancia == null) {
            instancia = new RegistroVentas();
        }
        return instancia;
    }

    public void agregarVenta(Venta venta) {
        historialVentas.add(venta);
    }

    public List<Venta> getHistorialVentas() {
        return historialVentas;
    }
    
    public void agregarRecarga(Recarga recarga) {
        historialRecargas.add(recarga);
    }

    public List<Recarga> getHistorialRecargas() {
        return historialRecargas;
    }
}