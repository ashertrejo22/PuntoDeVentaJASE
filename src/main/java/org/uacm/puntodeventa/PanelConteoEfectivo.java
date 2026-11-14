/*
 * Archivo: PanelConteoEfectivo.java
 * Módulo: Corte de Caja
 * Propósito: Muestra el apartado para contar billetes y monedas al cerrar el turno.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-11-13
 */

package org.uacm.puntodeventa;

import java.util.EnumMap;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.uacm.puntodeventa.Clases.ConteoEfectivo;
import org.uacm.puntodeventa.Clases.Denominacion;
import org.uacm.puntodeventa.reportes.FormatoReporte;

/**
 * Componente con dos secciones, Billetes y Monedas, donde el cajero captura cuántas piezas
 * de cada denominación hay en la caja. Muestra los subtotales y el total contado al instante.
 * No decide reglas: la validación y la suma las hace {@link ConteoEfectivo}.
 */
public class PanelConteoEfectivo extends VBox {

    private static final double ESPACIO = 8.0;
    private static final double ANCHO_ETIQUETA = 52.0;
    private static final double ANCHO_CAMPO = 56.0;
    private static final double ANCHO_SUBTOTAL = 84.0;
    private static final String TEXTO_INVALIDO = "cantidad inválida";

    private final Map<Denominacion, TextField> campos = new EnumMap<>(Denominacion.class);
    private final Map<Denominacion, Label> subtotales = new EnumMap<>(Denominacion.class);
    private final Label etiquetaSubtotalBilletes = new Label();
    private final Label etiquetaSubtotalMonedas = new Label();
    private final Label etiquetaTotal = new Label();

    /**
     * Crea el panel con una fila por cada billete y moneda.
     */
    public PanelConteoEfectivo() {
        setSpacing(ESPACIO);
        etiquetaTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #00502b; -fx-font-size: 14px;");
        HBox secciones = new HBox(ESPACIO * 2, crearSeccion("Billetes", Denominacion.Tipo.BILLETE),
                crearSeccion("Monedas", Denominacion.Tipo.MONEDA));
        secciones.setAlignment(Pos.TOP_CENTER);
        getChildren().addAll(secciones, etiquetaTotal);
        actualizarTotales();
    }

    /**
     * Lee las cantidades capturadas.
     *
     * @return el conteo con las cantidades de la pantalla
     * @throws IllegalArgumentException si alguna cantidad no es un número entero de cero en adelante
     */
    public ConteoEfectivo leerConteo() {
        Map<Denominacion, String> textos = new EnumMap<>(Denominacion.class);
        campos.forEach((denominacion, campo) -> textos.put(denominacion, campo.getText()));
        return ConteoEfectivo.desdeTextos(textos);
    }

    /**
     * Deja todas las cantidades en blanco.
     */
    public void limpiar() {
        campos.values().forEach(TextField::clear);
    }

    private VBox crearSeccion(String titulo, Denominacion.Tipo tipo) {
        Label encabezado = new Label(titulo);
        encabezado.setStyle("-fx-font-weight: bold;");
        GridPane cuadricula = new GridPane();
        cuadricula.setHgap(ESPACIO);
        cuadricula.setVgap(ESPACIO / 2);
        int fila = 0;
        for (Denominacion denominacion : Denominacion.values()) {
            if (denominacion.getTipo() == tipo) {
                agregarFila(cuadricula, denominacion, fila++);
            }
        }
        Label subtotal = tipo == Denominacion.Tipo.BILLETE ? etiquetaSubtotalBilletes : etiquetaSubtotalMonedas;
        subtotal.setStyle("-fx-font-weight: bold;");
        VBox seccion = new VBox(ESPACIO / 2, encabezado, cuadricula, subtotal);
        seccion.setPadding(new Insets(ESPACIO));
        seccion.setStyle("-fx-background-color: white; -fx-border-color: #c8c8c8;");
        return seccion;
    }

    private void agregarFila(GridPane cuadricula, Denominacion denominacion, int fila) {
        Label etiqueta = new Label(denominacion.getEtiqueta());
        etiqueta.setPrefWidth(ANCHO_ETIQUETA);
        TextField campo = new TextField();
        campo.setId("campoCantidad" + denominacion.name());
        campo.setPrefWidth(ANCHO_CAMPO);
        campo.setPromptText("0");
        campo.textProperty().addListener((valor, anterior, nuevo) -> actualizarTotales());
        Label subtotal = new Label();
        subtotal.setPrefWidth(ANCHO_SUBTOTAL);
        subtotal.setAlignment(Pos.CENTER_RIGHT);
        campos.put(denominacion, campo);
        subtotales.put(denominacion, subtotal);
        cuadricula.addRow(fila, etiqueta, campo, subtotal);
    }

    private void actualizarTotales() {
        try {
            ConteoEfectivo conteo = leerConteo();
            subtotales.forEach((denominacion, etiqueta) ->
                    etiqueta.setText(FormatoReporte.pesos(conteo.calcularSubtotal(denominacion))));
            etiquetaSubtotalBilletes.setText("Subtotal: "
                    + FormatoReporte.pesos(conteo.calcularSubtotal(Denominacion.Tipo.BILLETE)));
            etiquetaSubtotalMonedas.setText("Subtotal: "
                    + FormatoReporte.pesos(conteo.calcularSubtotal(Denominacion.Tipo.MONEDA)));
            etiquetaTotal.setText("Total contado: " + FormatoReporte.pesos(conteo.calcularTotal()));
        } catch (IllegalArgumentException e) {
            etiquetaTotal.setText("Total contado: " + TEXTO_INVALIDO);
        }
    }
}
