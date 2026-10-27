package org.uacm.puntodeventa;

import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import org.uacm.puntodeventa.Clases.InformacionProductos;
import org.uacm.puntodeventa.Clases.Producto;
import org.uacm.puntodeventa.Clases.ProductoGranel;
import org.uacm.puntodeventa.Clases.ProductoPerecedero;

public class ProductoNuevoController implements Initializable {

    @FXML private TextField txtFieldNomProducto;
    @FXML private TextField txtFielPrecioProv;
    @FXML private ComboBox<String> comboBoxTipPro;
    @FXML private ComboBox<String> comboBoxCategoria; 
    @FXML private DatePicker datePickCaducidad;
    @FXML private TextField txtFieldCantidad;
    @FXML private Button btnSeleccionarImagen;
    @FXML private ImageView previewImagen;
    @FXML private Text txtCantidad;

    private ObservableList<Producto> productoList;
    private String rutaImagenSeleccionada = null;

    public void setProductoList(ObservableList<Producto> lista) {
        this.productoList = lista;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboBoxTipPro.getItems().addAll("Normal", "Perecedero", "Granel");

        comboBoxCategoria.getItems().addAll("Limpieza", "Grasas", "Lácteos", "Bebidas", "Abarrotes", "General");
        comboBoxCategoria.setValue("General"); // Valor por defecto

        datePickCaducidad.setDisable(true);

        comboBoxTipPro.setOnAction(e -> {
            if ("Perecedero".equals(comboBoxTipPro.getValue())) {
                datePickCaducidad.setDisable(false);
            } else {
                datePickCaducidad.setDisable(true);
                datePickCaducidad.setValue(null);
            }
        });
    }

    @FXML
    private void seleccionarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) btnSeleccionarImagen.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            rutaImagenSeleccionada = file.toURI().toString();
            previewImagen.setImage(new Image(rutaImagenSeleccionada));
        }
    }

    @FXML
    private void clickAceptar(ActionEvent event) {
        try {
            if (txtFieldNomProducto.getText().trim().isEmpty()) {
                mostrarMensaje("/pantallas/Nombre.fxml");
                return;
            }

            String precioText = txtFielPrecioProv.getText().trim();
            String cantidadText = txtFieldCantidad.getText().trim();
            
            if (precioText.isEmpty() || cantidadText.isEmpty()) {
                mostrarMensaje("/pantallas/NumerosValidos.fxml");
                return;
            }

            double precio;
            double cantidad; 
            
            try {
                precio = Double.parseDouble(precioText);
                cantidad = Double.parseDouble(cantidadText);
            } catch (NumberFormatException e) {
                mostrarMensaje("/pantallas/NumerosValidos.fxml");
                return;
            }

            if (precio < 0 || cantidad < 0) {
                mostrarMensaje("/pantallas/NumerosValidos.fxml");
                return;
            }

            if (comboBoxTipPro.getValue() == null || comboBoxCategoria.getValue() == null) {
                mostrarMensaje("/pantallas/OpcionValida.fxml");
                return;
            }

            String nombre = txtFieldNomProducto.getText().trim();
            String tipo = comboBoxTipPro.getValue();
            String categoria = comboBoxCategoria.getValue(); // --- CAPTURAMOS LA CATEGORÍA ---
            LocalDate fecha = null;

            if ("Perecedero".equals(tipo)) {
                fecha = datePickCaducidad.getValue();
                if (fecha == null || !fecha.isAfter(LocalDate.now())) {
                    mostrarMensaje("/pantallas/FechaValida.fxml");
                    return;
                }
            }
            int idTemporal = 0;

            Producto nuevoProducto;

            // --- SE PASA LA CATEGORÍA AL CONSTRUCTOR DE CADA TIPO DE PRODUCTO ---
            switch (tipo) {
                case "Perecedero":
                    nuevoProducto = new ProductoPerecedero(idTemporal, nombre, tipo, cantidad, precio, fecha, "Lote-001", rutaImagenSeleccionada, categoria);
                    break;
                case "Granel":
                    nuevoProducto = new ProductoGranel(idTemporal, nombre, tipo, cantidad, precio, "Kg/Lts", rutaImagenSeleccionada, categoria);
                    break;
                default:
                    nuevoProducto = new Producto(idTemporal, nombre, tipo, cantidad, precio, rutaImagenSeleccionada, categoria);
                    break;
            }
            
            // Esto guarda en memoria y a su vez invocará a ProductosADB.insertar(...) si está conectado al gestor
            InformacionProductos.getInstance().agregarProducto(nuevoProducto);
            
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallas/RegistroExitoso.fxml"));
                Parent root = loader.load();
                RegistroExitosoController controller = loader.getController();
                controller.setDatos(nuevoProducto);
                Stage stageExito = new Stage();
                stageExito.setScene(new Scene(root));
                stageExito.setTitle("Registro Exitoso");
                stageExito.setResizable(false);
                stageExito.show();
                
                Stage currentStage = (Stage) txtFieldNomProducto.getScene().getWindow();
                currentStage.close();
                
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("/pantallas/ErrorGeneral.fxml");
        }
    }

    private void mostrarMensaje(String ruta) {
        try {
            Parent root = FXMLLoader.load(ProductoNuevoController.class.getResource(ruta));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        Stage stage = (Stage) txtFieldNomProducto.getScene().getWindow();
        stage.close();
    }
}