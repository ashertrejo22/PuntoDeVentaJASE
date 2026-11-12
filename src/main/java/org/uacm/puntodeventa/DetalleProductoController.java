package org.uacm.puntodeventa;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.uacm.puntodeventa.Clases.Producto;

public class DetalleProductoController {

    @FXML private Label lblNombre;
    @FXML private Label lblTipo;
    @FXML private Label lblPrecio;
    @FXML private Label lblStock;
    @FXML private Label lblID;
    @FXML private ImageView imgProducto;

    public void setProducto(Producto p) {
        lblNombre.setText(p.getNombre());
        lblTipo.setText(p.getTipo());
        lblPrecio.setText("$" + p.getPrecio_venta());
        lblStock.setText(String.valueOf(p.getStock()));
        lblID.setText(String.valueOf(p.getID_producto()));

        if (p.getRutaImag() != null && !p.getRutaImag().isEmpty()) {
        try {
            imgProducto.setImage(new Image(p.getRutaImag()));
        } catch (Exception e) {
            
            System.out.println("Error al cargar la imagen. La ruta no es válida: " + p.getRutaImag());
            imgProducto.setImage(null); // Dejamos el espacio en blanco
        }
    } else {
        imgProducto.setImage(null);
    }
    }

    @FXML
    private void cerrarVentana() {
        imgProducto.getScene().getWindow().hide();
    }
}