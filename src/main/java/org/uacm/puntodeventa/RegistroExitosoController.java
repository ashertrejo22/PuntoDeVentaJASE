package org.uacm.puntodeventa;

import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.uacm.puntodeventa.Clases.Producto;

public class RegistroExitosoController {

    @FXML 
    private Text txtNombre;
    
    @FXML 
    private Text txtTipo;
    
    @FXML 
    private Text txtPrcioBase;
    
    @FXML 
    private Text txtStock;
    
    @FXML 
    private Text txtPrecioVenta;
    
    @FXML 
    private Text txtFecha;
    
    @FXML 
    private ImageView imgProducto;
    
    @FXML
    private Button btnAceptar;

    public void setDatos(Producto p) {
        if (p == null) return;
        
        if (txtNombre != null) {
            txtNombre.setText(p.getNombre());
        }
        
        if (txtTipo != null) {
            txtTipo.setText(p.getTipo());
        }
        
        if (txtPrcioBase != null) {
            txtPrcioBase.setText(String.format("$%.2f", p.getPrecio_prov()));
        }
        
        if (txtStock != null) {
            txtStock.setText(String.valueOf(p.getStock()));
        }

        if (txtPrecioVenta != null) {
            double precioVenta = p.getPrecio_prov() * 1.3;
            txtPrecioVenta.setText(String.format("$%.2f", precioVenta));
        }

        if (txtFecha != null) {
            if (p instanceof org.uacm.puntodeventa.Clases.ProductoPerecedero) {
                org.uacm.puntodeventa.Clases.ProductoPerecedero perecedero = (org.uacm.puntodeventa.Clases.ProductoPerecedero) p;
                
                if (perecedero.getFechaCaducidad() != null) {
                    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    txtFecha.setText(perecedero.getFechaCaducidad().format(formato));
                } else {
                    txtFecha.setText("No asignada");
                }
            } else {
                txtFecha.setText("No aplica");
            }
        }

        if (imgProducto != null) {
            if (p.getRutaImag() != null && !p.getRutaImag().isEmpty()) {
                try {
                    Image imagen = new Image(p.getRutaImag(), 150, 150, true, true);
                    imgProducto.setImage(imagen);
                } catch (Exception e) {
                    imgProducto.setImage(null);
                }
            } else {
                imgProducto.setImage(null);
            }
        }
    }
    @FXML
    private void clickAceptar(ActionEvent event) {
        Stage stage = (Stage) btnAceptar.getScene().getWindow();
        stage.close();
    }
}