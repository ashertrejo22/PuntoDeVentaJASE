package org.uacm.puntodeventa;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.uacm.puntodeventa.Clases.Producto;

public class DetalleProductoController {

    @FXML private Label lblNombre;
    @FXML private Label lblTipo;
    @FXML private Label lblCategoria;
    @FXML private Label lblPrecio;
    @FXML private Label lblStock;
    @FXML private Label lblID;
    @FXML private ImageView imgProducto;

    public void setProducto(Producto p) {
        lblNombre.setText(p.getNombre());
        lblTipo.setText(p.getTipo());
        lblCategoria.setText(p.getCategoria());
        lblPrecio.setText("$" + p.getPrecio_venta());
        lblStock.setText(String.valueOf(p.getStock()));
        lblID.setText(String.valueOf(p.getID_producto()));

        if (p.getRutaImag() != null) {
            imgProducto.setImage(new Image(p.getRutaImag()));
        }
    }

    @FXML
    private void cerrarVentana() {
        imgProducto.getScene().getWindow().hide();
    }
}