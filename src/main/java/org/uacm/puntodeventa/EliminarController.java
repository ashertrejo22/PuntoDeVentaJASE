package org.uacm.puntodeventa;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.uacm.puntodeventa.Clases.InformacionProductos;
import org.uacm.puntodeventa.Clases.Producto;

/**
 * Controlador de la ventana de confirmación de eliminación.
 * Ahora elimina el producto de la base de datos a través de InformacionProductos.
 * 
 * @author jaasielVC
 */
public class EliminarController implements Initializable {

    @FXML
    private Button buttonAceptar;
    @FXML
    private Button buttonCancelar;

    private Producto productoAEliminar;
    private ObservableList<Producto> listaProductos;

    public void setProducto(Producto producto, ObservableList<Producto> lista) {
        this.productoAEliminar = producto;
        this.listaProductos = lista;  
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void aceptarEliminar(ActionEvent event) {
        if (productoAEliminar != null) {
            InformacionProductos.getInstance().eliminarProducto(productoAEliminar.getID_producto());
        }
        cerrarVentana();
    }

    @FXML
    private void cancelarEliminar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) buttonCancelar.getScene().getWindow();
        stage.close();
    }
}