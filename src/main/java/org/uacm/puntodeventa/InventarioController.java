/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.uacm.puntodeventa;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
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
 * Controlador de la ventana de inventario.
 * Ahora obtiene los productos desde la base de datos mediante InformacionProductos.
 * 
 * @author josem
 */
public class InventarioController implements Initializable {

    @FXML
    private TextField txtFieldBuscar;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnEliminar;
    @FXML 
    private TableView<Producto> tableViewProductos;
    @FXML
    private TableColumn<Producto, String> columnaNombre;
    @FXML
    private TableColumn<Producto, Double> columnaPrecio;
    @FXML
    private TableColumn<Producto, Integer> columnaStock;
    @FXML
    private TableColumn<Producto, String> columnaTipo;
    @FXML
    private TableColumn<Producto, Integer> columnaide;
    
    @FXML 
    private TableColumn<Producto, String> colCategoria;
    
    // La lista observable se obtiene desde el singleton que ahora lee de la BD
    ObservableList<Producto> list = InformacionProductos.getInstance().getProductos();
    private FilteredList<Producto> filteredProductos;
    private int nivelUsuarioActual;
    @FXML
    private Label lblInfo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar columnas de la tabla
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        columnaide.setCellValueFactory(new PropertyValueFactory<>("ID_producto"));
        columnaTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio_venta"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        
        // Aplicar filtro de búsqueda en tiempo real
        filteredProductos = new FilteredList<>(list, p -> true);
        tableViewProductos.setItems(filteredProductos);
        txtFieldBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            aplicarFiltro(newVal);
        });
        
        // Doble clic para ver detalles del producto
        tableViewProductos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Producto productoSeleccionado = tableViewProductos.getSelectionModel().getSelectedItem();
                if (productoSeleccionado != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallas/DetalleProducto.fxml"));
                        Parent root = loader.load();
                        DetalleProductoController controller = loader.getController();
                        controller.setProducto(productoSeleccionado);
                        Stage stage = new Stage();
                        stage.setTitle("Detalles del Producto");
                        stage.setScene(new Scene(root));
                        stage.setResizable(false);
                        stage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    
    private void aplicarFiltro(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            filteredProductos.setPredicate(p -> true);
            return;
        }
        String lower = texto.toLowerCase();
        filteredProductos.setPredicate(p -> 
            p.getNombre().toLowerCase().contains(lower) ||
            String.valueOf(p.getID_producto()).contains(lower)
        );
    }
    
    @FXML
    private void click(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallas/ProductoNuevo.fxml"));
            Parent root = loader.load();
            ProductoNuevoController controller = loader.getController();
            // Le pasamos la misma lista observable (que ahora está respaldada por BD)
            controller.setProductoList(this.list);
            Stage stage = new Stage();
            stage.setTitle("Registrar Nuevo Producto");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar ProductoNuevo.fxml");
        }
    }
    
    @FXML
    private void eliminar(ActionEvent event) {
        Producto seleccionado = tableViewProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            // Mostrar alerta de selección (puedes usar tu ventana personalizada)
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallas/Eliminar.fxml"));
            Parent root = loader.load();
            EliminarController controller = loader.getController();
            // Pasamos el producto y la lista observable (EliminarController usará InformacionProductos para borrar)
            controller.setProducto(seleccionado, this.list);
            Stage stage = new Stage();
            stage.setTitle("Eliminar Producto");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void configurarPermisos(int nivel) {
        this.nivelUsuarioActual = nivel;
        if (nivel == 2) {  // Empleado
            btnNuevo.setDisable(true);
            btnActualizar.setDisable(true);
            btnEliminar.setDisable(true);
        }
    }
    
    @FXML
    private void actualizarProducto(ActionEvent event) {
        Producto seleccionado = tableViewProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallas/Actualizar.fxml"));
            Parent root = loader.load();
            ActualizarController controller = loader.getController();
            controller.setProducto(seleccionado);
            controller.setListaProductos(this.list);  // Se pasa la lista observable
            Stage stage = new Stage();
            stage.setTitle("Actualizar Producto");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}