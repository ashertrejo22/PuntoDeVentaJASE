/*
 * Clase: InventarioController
 * Versión: 1.1
 * Fecha: 25/10/2026
 * Copyright: Proyecto Sistema de ventas, UACM.
 */
package org.uacm.puntodeventa;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.uacm.puntodeventa.Clases.InformacionProductos;
import org.uacm.puntodeventa.Clases.Producto;

/**
 * Controlador de la pantalla de inventario.
 *
 * Permite consultar, buscar, registrar, actualizar
 * y eliminar productos del sistema.
 *
 * También permite consultar las alertas de inventario
 * y cambia el color del botón Notificaciones según
 * las existencias de los productos.
 *
 * @author josem
 * @version 1.1
 */
public class InventarioController implements Initializable {

    /** Campo para buscar productos por nombre o ID. */
    @FXML
    private TextField txtFieldBuscar;

    /** Botón para registrar productos. */
    @FXML
    private Button btnNuevo;

    /** Botón para actualizar productos. */
    @FXML
    private Button btnActualizar;

    /** Botón para eliminar productos. */
    @FXML
    private Button btnEliminar;

    /** Tabla de productos del inventario. */
    @FXML
    private TableView<Producto> tableViewProductos;

    /** Columna del nombre del producto. */
    @FXML
    private TableColumn<Producto, String> columnaNombre;

    /** Columna del precio de venta. */
    @FXML
    private TableColumn<Producto, Double> columnaPrecio;

    /** Columna de existencias. */
    @FXML
    private TableColumn<Producto, Double> columnaStock;

    /** Columna del tipo de producto. */
    @FXML
    private TableColumn<Producto, String> columnaTipo;

    /** Columna del identificador. */
    @FXML
    private TableColumn<Producto, Integer> columnaide;

    /** Lista observable de productos. */
    private final ObservableList<Producto> list =
            InformacionProductos.getInstance().getProductos();

    /** Lista filtrada para la búsqueda. */
    private FilteredList<Producto> filteredProductos;

    /** Nivel de permisos del usuario. */
    private int nivelUsuarioActual;

    /** Etiqueta informativa. */
    @FXML
    private Label lblInfo;

    /** Botón que muestra las alertas de inventario. */
    @FXML
    private Button notificacion;

    /**
     * Inicializa la pantalla de inventario.
     *
     * Configura las columnas, el filtro de búsqueda,
     * la apertura de detalles y las notificaciones.
     *
     * También actualiza el color del botón cuando
     * cambia la lista de productos o cuando la ventana
     * recupera el foco.
     *
     * @param url ubicación de los recursos
     * @param rb recursos de internacionalización
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Configurar las columnas.
        columnaNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        columnaStock.setCellValueFactory(
                new PropertyValueFactory<>("stock"));

        columnaide.setCellValueFactory(
                new PropertyValueFactory<>("ID_producto"));

        columnaTipo.setCellValueFactory(
                new PropertyValueFactory<>("tipo"));

        columnaPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio_venta"));

        // Configurar la lista filtrada.
        filteredProductos = new FilteredList<>(
                list, p -> true);

        tableViewProductos.setItems(filteredProductos);

        // Buscar productos conforme se escribe.
        txtFieldBuscar.textProperty().addListener(
                (obs, oldVal, newVal) -> {
                    aplicarFiltro(newVal);
                }
        );

        // Abrir detalles con doble clic.
        tableViewProductos.setOnMouseClicked(event -> {

            if (event.getClickCount() == 2) {

                Producto productoSeleccionado =
                        tableViewProductos
                                .getSelectionModel()
                                .getSelectedItem();

                if (productoSeleccionado != null) {

                    try {

                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource(
                                        "/pantallas/DetalleProducto.fxml"
                                )
                        );

                        Parent root = loader.load();

                        DetalleProductoController controller =
                                loader.getController();

                        controller.setProducto(
                                productoSeleccionado);

                        Stage stage = new Stage();

                        stage.setTitle(
                                "Detalles del Producto");

                        stage.setScene(new Scene(root));
                        stage.setResizable(false);
                        stage.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Establecer el color inicial.
        actualizarColorNotificaciones();

        // Actualizar cuando cambie la lista.
        list.addListener(
                (javafx.collections.ListChangeListener<Producto>)
                cambio -> actualizarColorNotificaciones()
        );

        // Actualizar cuando Inventario recupere el foco.
        // Se registra también si la escena o la ventana
        // ya estaban asociadas al momento de inicializar.
        tableViewProductos.sceneProperty().addListener(
                (obs, escenaAnterior, escenaNueva) -> {

                    if (escenaNueva != null) {

                        configurarActualizacionPorFoco(
                                escenaNueva);
                    }
                }
        );

        if (tableViewProductos.getScene() != null) {

            configurarActualizacionPorFoco(
                    tableViewProductos.getScene());
        }
    }

    /**
     * Configura la actualización de notificaciones
     * cuando la ventana de inventario recupera el foco.
     *
     * @param escena escena de la pantalla de inventario
     */
    private void configurarActualizacionPorFoco(
            Scene escena) {

        escena.windowProperty().addListener(
                (obs, ventanaAnterior, ventanaNueva) -> {

                    if (ventanaNueva != null) {
                        registrarListenerDeFoco(ventanaNueva);
                    }
                }
        );

        if (escena.getWindow() != null) {
            registrarListenerDeFoco(escena.getWindow());
        }
    }

    /**
     * Registra un listener para detectar cuando
     * la ventana recupera el foco.
     *
     * @param ventana ventana que contiene el inventario
     */
    private void registrarListenerDeFoco(
            javafx.stage.Window ventana) {

        ventana.focusedProperty().addListener(
                (obs, focoAnterior, tieneFoco) -> {

                    if (tieneFoco) {

                        actualizarColorNotificaciones();

                        // Refrescar la tabla para mostrar
                        // las existencias actualizadas.
                        tableViewProductos.refresh();
                    }
                }
        );
    }

    /**
     * Filtra los productos por nombre o ID.
     *
     * @param texto texto que se desea buscar
     */
    private void aplicarFiltro(String texto) {

        if (texto == null || texto.trim().isEmpty()) {

            filteredProductos.setPredicate(p -> true);
            return;
        }

        String lower = texto.toLowerCase();

        filteredProductos.setPredicate(
                p -> p.getNombre().toLowerCase().contains(lower)
                || String.valueOf(
                        p.getID_producto()).contains(lower)
        );
    }

    /**
     * Abre la ventana para registrar un producto.
     *
     * @param event evento del botón Nuevo
     */
    @FXML
    private void click(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/pantallas/ProductoNuevo.fxml"
                    )
            );

            Parent root = loader.load();

            ProductoNuevoController controller =
                    loader.getController();

            controller.setProductoList(this.list);

            Stage stage = new Stage();

            stage.setTitle("Registrar Nuevo Producto");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {

            e.printStackTrace();

            System.out.println(
                    "Error al cargar ProductoNuevo.fxml");
        }
    }

    /**
     * Abre la ventana para eliminar un producto.
     *
     * @param event evento del botón Eliminar
     */
    @FXML
    private void eliminar(ActionEvent event) {

        Producto seleccionado =
                tableViewProductos.getSelectionModel()
                        .getSelectedItem();

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/pantallas/Eliminar.fxml"
                    )
            );

            Parent root = loader.load();

            EliminarController controller =
                    loader.getController();

            controller.setProducto(
                    seleccionado, this.list);

            Stage stage = new Stage();

            stage.setTitle("Eliminar Producto");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configura los permisos del usuario.
     *
     * El nivel 2 deshabilita las operaciones
     * de registro, actualización y eliminación.
     *
     * @param nivel nivel de permisos
     */
    public void configurarPermisos(int nivel) {

        this.nivelUsuarioActual = nivel;

        if (nivel == 2) {

            btnNuevo.setDisable(true);
            btnActualizar.setDisable(true);
            btnEliminar.setDisable(true);
        }
    }

    /**
     * Abre la ventana para actualizar un producto.
     *
     * @param event evento del botón Actualizar
     */
    @FXML
    private void actualizarProducto(ActionEvent event) {

        Producto seleccionado =
                tableViewProductos.getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {
            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/pantallas/Actualizar.fxml"
                    )
            );

            Parent root = loader.load();

            ActualizarController controller =
                    loader.getController();

            controller.setProducto(seleccionado);

            controller.setListaProductos(this.list);

            Stage stage = new Stage();

            stage.setTitle("Actualizar Producto");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la pantalla de alertas de inventario
     * y pedidos de reabastecimiento.
     *
     * @param event evento del botón Notificaciones
     */
    @FXML
    private void Notificaciones(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/pantallas/ProductosNotificaciones.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setTitle(
                    "Productos por actualizar");

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {

            e.printStackTrace();

            System.out.println(
                    "Error al cargar ProductosNotificaciones.fxml"
            );
        }
    }

    /**
     * Actualiza el color del botón Notificaciones.
     *
     * Utiliza rojo cuando existe al menos un
     * producto con stock bajo y verde cuando
     * no existen alertas.
     */
    private void actualizarColorNotificaciones() {

        boolean hayAlertas = false;

        for (Producto producto : list) {

            if (producto != null
                    && producto.necesitaReabastecimiento()) {

                hayAlertas = true;
                break;
            }
        }

        if (hayAlertas) {

            notificacion.setStyle(
                    "-fx-background-color: #e53935;"
                    + "-fx-text-fill: white;"
            );

        } else {

            notificacion.setStyle(
                    "-fx-background-color: #4caf50;"
                    + "-fx-text-fill: white;"
            );
        }
    }
}
