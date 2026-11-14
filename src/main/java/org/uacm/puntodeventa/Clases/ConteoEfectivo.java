/*
 * Archivo: ConteoEfectivo.java
 * Módulo: Corte de Caja
 * Propósito: Guarda cuántos billetes y monedas de cada denominación se contaron en el corte.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-11-13
 */

package org.uacm.puntodeventa.Clases;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Conteo físico del efectivo de la caja, separado en billetes y monedas.
 * Las cantidades son piezas enteras no negativas; el dinero se suma en centavos.
 */
public final class ConteoEfectivo {

    private static final double CENTAVOS_POR_PESO = 100.0;
    private static final Pattern FORMATO_CANTIDAD = Pattern.compile("\\d{1,5}");

    private final Map<Denominacion, Integer> cantidades = new EnumMap<>(Denominacion.class);

    /**
     * Crea un conteo a partir de los textos capturados en pantalla. Un texto vacío cuenta como cero piezas.
     *
     * @param textos cantidad capturada para cada denominación
     * @return el conteo con las cantidades válidas
     * @throws IllegalArgumentException si alguna cantidad no es un número entero de cero en adelante
     */
    public static ConteoEfectivo desdeTextos(Map<Denominacion, String> textos) {
        ConteoEfectivo conteo = new ConteoEfectivo();
        for (Map.Entry<Denominacion, String> entrada : textos.entrySet()) {
            String texto = entrada.getValue() == null ? "" : entrada.getValue().trim();
            if (!texto.isEmpty() && !FORMATO_CANTIDAD.matcher(texto).matches()) {
                throw new IllegalArgumentException("La cantidad de " + describir(entrada.getKey())
                        + " debe ser un número entero de 0 en adelante (sin signo ni decimales).");
            }
            conteo.establecerCantidad(entrada.getKey(), texto.isEmpty() ? 0 : Integer.parseInt(texto));
        }
        return conteo;
    }

    /**
     * Registra cuántas piezas de una denominación se contaron.
     *
     * @param denominacion billete o moneda contado
     * @param cantidad     número de piezas
     * @throws IllegalArgumentException si la cantidad es negativa
     */
    public void establecerCantidad(Denominacion denominacion, int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad de " + describir(denominacion)
                    + " no puede ser negativa.");
        }
        cantidades.put(denominacion, cantidad);
    }

    /**
     * Devuelve las piezas contadas de una denominación.
     *
     * @param denominacion billete o moneda
     * @return número de piezas, cero si no se capturó
     */
    public int obtenerCantidad(Denominacion denominacion) {
        return cantidades.getOrDefault(denominacion, 0);
    }

    /**
     * Calcula el dinero contado de una denominación.
     *
     * @param denominacion billete o moneda
     * @return importe en pesos
     */
    public double calcularSubtotal(Denominacion denominacion) {
        return convertirAPesos(sumarCentavos(clave -> clave == denominacion));
    }

    /**
     * Calcula el dinero contado de una sección (billetes o monedas).
     *
     * @param tipo sección del conteo
     * @return importe en pesos
     */
    public double calcularSubtotal(Denominacion.Tipo tipo) {
        return convertirAPesos(sumarCentavos(clave -> clave.getTipo() == tipo));
    }

    /**
     * Calcula el total del efectivo contado (billetes más monedas).
     *
     * @return importe en pesos
     */
    public double calcularTotal() {
        return convertirAPesos(sumarCentavos(clave -> true));
    }

    /**
     * Indica si no se contó ninguna pieza.
     *
     * @return verdadero si todas las cantidades son cero
     */
    public boolean estaVacio() {
        return cantidades.values().stream().allMatch(cantidad -> cantidad == 0);
    }

    private long sumarCentavos(Predicate<Denominacion> filtro) {
        long total = 0;
        for (Map.Entry<Denominacion, Integer> entrada : cantidades.entrySet()) {
            if (filtro.test(entrada.getKey())) {
                total += (long) entrada.getKey().getValorCentavos() * entrada.getValue();
            }
        }
        return total;
    }

    private static double convertirAPesos(long centavos) {
        return centavos / CENTAVOS_POR_PESO;
    }

    private static String describir(Denominacion denominacion) {
        String nombre = denominacion.getTipo() == Denominacion.Tipo.BILLETE ? "billetes" : "monedas";
        return nombre + " de " + denominacion.getEtiqueta();
    }
}
