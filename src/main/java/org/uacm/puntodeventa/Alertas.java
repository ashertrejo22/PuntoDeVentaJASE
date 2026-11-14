/*
 * Archivo: Alertas.java
 * Módulo: Interfaz
 * Propósito: Centraliza los cuadros de diálogo de aviso, error y texto del sistema.
 * Autor: Juan Arturo González Gómez        Fecha: 2026-10-25
 */

package org.uacm.puntodeventa;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

/**
 * Cuadros de diálogo compartidos por las pantallas de Reporte de Ventas y Corte de Caja.
 */
public final class Alertas {

    private static final int COLUMNAS_TEXTO = 44;
    private static final int FILAS_TEXTO = 24;
    private static final int FILAS_DE_MARGEN = 3;

    private static final String ESTILO_MONOESPACIADO = "-fx-font-family: monospace; -fx-font-size: 13;";

    private Alertas() {
    }

    /**
     * Muestra una advertencia para que el usuario corrija un dato.
     *
     * @param titulo  título de la ventana
     * @param mensaje texto que se muestra al usuario
     */
    public static void aviso(String titulo, String mensaje) {
        mostrar(Alert.AlertType.WARNING, titulo, mensaje);
    }

    /**
     * Muestra un error del sistema.
     *
     * @param titulo  título de la ventana
     * @param mensaje texto que se muestra al usuario
     */
    public static void error(String titulo, String mensaje) {
        mostrar(Alert.AlertType.ERROR, titulo, mensaje);
    }

    /**
     * Muestra un mensaje informativo.
     *
     * @param titulo  título de la ventana
     * @param mensaje texto que se muestra al usuario
     */
    public static void informacion(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    /**
     * Muestra un texto con columnas alineadas, como un comprobante.
     *
     * @param titulo título de la ventana
     * @param texto  texto a mostrar con letra de ancho fijo
     */
    public static void textoMonoespaciado(String titulo, String texto) {
        TextArea area = new TextArea(texto);
        area.setEditable(false);
        area.setStyle(ESTILO_MONOESPACIADO);
        area.setWrapText(true);
        area.setPrefColumnCount(COLUMNAS_TEXTO);
        area.setPrefRowCount(Math.max(FILAS_TEXTO, (int) texto.lines().count() + FILAS_DE_MARGEN));
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.getDialogPane().setContent(area);
        alerta.showAndWait();
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
