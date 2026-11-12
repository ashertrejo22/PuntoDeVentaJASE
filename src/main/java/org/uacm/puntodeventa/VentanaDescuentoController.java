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
import java.net.URL;
import static java.sql.JDBCType.NULL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.uacm.puntodeventa.Clases.ProductoPerecedero;
import org.uacm.puntodeventa.DetalleProductoController;

/**
 * FXML Controller class
 *
 * @author jose-eduardo
 */
public class VentanaDescuentoController implements Initializable {
    @FXML
    private TextField txtFieldBuscar;
    @FXML 
    private TableView<Producto> tableViewProductos;
    @FXML
    private TableColumn<Producto, String> columnaNombre;
    @FXML
    private TableColumn<Producto, Double> columnaPrecio;
    @FXML
    private TableColumn<Producto, Integer> columnaStock;
    @FXML
    private TableColumn<Producto, LocalDate> columnaFecha;
    @FXML
    private TableColumn<Producto, Integer> columnaide;
    ObservableList<Producto> list = InformacionProductos.getInstance().getProductos();
    private FilteredList<Producto> filteredProductos;
    private int nivelUsuarioActual;
    @FXML
    private Label lblInfo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        columnaide.setCellValueFactory(new PropertyValueFactory<>("ID_producto"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio_venta"));
        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCaducidad"));
        
        // Aplicar filtro de búsqueda en tiempo real
        filteredProductos = new FilteredList<>(list, p -> {
            if(p instanceof ProductoPerecedero){
            ProductoPerecedero productoFecha = (ProductoPerecedero) p;
            LocalDate fecha = productoFecha.getFechaCaducidad();
            if(fecha != null && fecha.isBefore(LocalDate.now().plusDays(8)) && !fecha.isBefore(LocalDate.now())){
                return true;
            }
            }
            return false;
        });
        
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
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallas/DetalleProductoDescuentos.fxml"));
                        Parent root = loader.load();
                        DetalleProductoDescuentosController controller = loader.getController();
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
    
}

