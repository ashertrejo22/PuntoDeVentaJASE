/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.uacm.puntodeventa;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.uacm.puntodeventa.Clases.Producto;
import org.uacm.puntodeventa.Clases.ProductoPerecedero;

/**
 * FXML Controller class
 *
 * @author jose-eduardo
 */
public class DetalleProductoDescuentosController implements Initializable {

    @FXML private Label lblNombre;
    @FXML private Label lblTipo;
    @FXML private Label lblPrecio;
    @FXML private Label lblStock;
    @FXML private Label lblID;
    @FXML private Label lblFechaCaducidad;
    @FXML private Label lblDiasCaducar;
    @FXML private ImageView imgProducto;
    
    public void setProducto(Producto p) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblNombre.setText(p.getNombre());
        lblTipo.setText(p.getTipo());
        lblPrecio.setText("$" + p.getPrecio_venta());
        lblStock.setText(String.valueOf(p.getStock()));
        lblID.setText(String.valueOf(p.getID_producto()));
        //Condicion para verificar si es un producto perecedero y mostrar sus detalles
        if(p instanceof ProductoPerecedero){
            ProductoPerecedero productoFecha = (ProductoPerecedero) p;
            lblFechaCaducidad.setText(String.valueOf(((ProductoPerecedero) p).getFechaCaducidad().format(formato)));
            long diasEnCaducar = ChronoUnit.DAYS.between(LocalDate.now(), ((ProductoPerecedero) p).getFechaCaducidad());
            lblDiasCaducar.setText(Math.abs(diasEnCaducar)+ " dias");
            
        }
        

        if (p.getRutaImag() != null) {
            imgProducto.setImage(new Image(p.getRutaImag()));
        }
    }

    @FXML
    private void cerrarVentana() {
        imgProducto.getScene().getWindow().hide();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
    }    
    
}
