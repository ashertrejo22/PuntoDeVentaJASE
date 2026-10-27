package org.uacm.puntodeventa;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import org.uacm.puntodeventa.Clases.Producto;
import org.uacm.puntodeventa.Clases.InformacionProductos;
import org.uacm.puntodeventa.Clases.ProductoCarrito;
import org.uacm.puntodeventa.Clases.Venta;
import org.uacm.puntodeventa.DB.DetalleABD;
import org.uacm.puntodeventa.DB.VentaABD;

public class PantallaPrincipalController implements Initializable {

    @FXML private TextField busquedaCarrito;
    @FXML private Button btnAgregarCarrito;
    @FXML private TableView<ProductoCarrito> tableViewCarrito;
    @FXML private TableColumn<ProductoCarrito, Integer> colId;
    @FXML private TableColumn<ProductoCarrito, String> colNombre;
    @FXML private TableColumn<ProductoCarrito, Double> colCantidad; 
    @FXML private TableColumn<ProductoCarrito, Double> colPrecio;
    @FXML private TableColumn<ProductoCarrito, Double> colTotal;
    @FXML private Label lblArticulosCount;
    @FXML private Label lblTotal;
    private Button btnMonedero;
    @FXML private Button btnReportes;
    @FXML private Button btnInventario;
    @FXML private Button btnEliminar;
    @FXML private Button btnCobrar;
    @FXML private Button botonMas;
    @FXML private Button botonMenos;
    @FXML private Button btnCerrarSesion;
    private Button btnCambiar;
    @FXML private Label lblSesion;
    @FXML private Label lblFecha;

    private ObservableList<Producto> list = InformacionProductos.getInstance().getProductos();
    private ObservableList<ProductoCarrito> carritoList = FXCollections.observableArrayList();
    private FilteredList<Producto> filteredProducts;
    private Popup suggestionPopup;
    private ListView<Producto> suggestionList;

    private int nivelUsuarioActual;
    private LocalDate fecha_actual = LocalDate.now();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        // Regla para que las columnas se ajusten automáticamente al maximizar
        tableViewCarrito.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        if (lblFecha != null) {
            lblFecha.setText(fecha_actual.toString());
        }

        colCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter() {
            @Override
            public Double fromString(String value) {
                try {
                    return super.fromString(value);
                } catch (NumberFormatException e) {
                    mostrarAlerta("Por favor, ingresa un número válido (ej. 1.5).");
                    return null;
                }
            }
        }));

        colCantidad.setOnEditCommit(event -> {
            try {
                ProductoCarrito item = event.getRowValue();
                Double nuevaCantidad = event.getNewValue();

                if (nuevaCantidad == null) {
                    tableViewCarrito.refresh();
                    return;
                }

                if (nuevaCantidad <= 0) {
                    throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
                }

                Producto original = list.stream()
                        .filter(p -> p.getID_producto() == item.getId())
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Producto original no encontrado."));

                if (nuevaCantidad > original.getStock()) {
                    throw new IllegalStateException("No hay suficiente stock para " + original.getNombre() + ". Disponible: " + original.getStock());
                }

                item.setCantidad(nuevaCantidad);
                actualizarTotales();
                tableViewCarrito.refresh();

            } catch (IllegalArgumentException | IllegalStateException e) {
                mostrarAlerta(e.getMessage());
                tableViewCarrito.refresh(); 
            }
        });

        tableViewCarrito.setItems(carritoList);
        filteredProducts = new FilteredList<>(list, p -> true);
        suggestionList = new ListView<>(filteredProducts);

        suggestionList.setCellFactory(lv -> new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getID_producto() + " - " + item.getNombre() + " (Stock: " + item.getStock() + ")");
                }
            }
        });

        suggestionList.setPrefWidth(busquedaCarrito.getPrefWidth());
        suggestionList.setMaxHeight(150);
        suggestionList.getStyleClass().add("suggestion-list");
        suggestionPopup = new Popup();
        suggestionPopup.getContent().add(suggestionList);
        suggestionPopup.setAutoHide(true);

        busquedaCarrito.textProperty().addListener((obs, oldVal, newVal) -> {
            String lower = (newVal == null) ? "" : newVal.toLowerCase();
            filteredProducts.setPredicate(p -> {
                if (lower.isEmpty()) {
                    return true;
                }
                return p.getNombre().toLowerCase().contains(lower)
                        || String.valueOf(p.getID_producto()).contains(lower);
            });
            if (!filteredProducts.isEmpty() && !newVal.isEmpty()) {
                Bounds bounds = busquedaCarrito.localToScreen(busquedaCarrito.getBoundsInLocal());
                suggestionPopup.show(busquedaCarrito, bounds.getMinX(), bounds.getMaxY());
            } else {
                suggestionPopup.hide();
            }
        });

        suggestionList.setOnMouseClicked(e -> {
            Producto selected = suggestionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                busquedaCarrito.setText(selected.getNombre());
                suggestionPopup.hide();
            }
        });

        busquedaCarrito.setOnAction(e -> agregarProductoAlCarrito(null));
    }

    private void agregarProductoDirecto(Producto seleccionado) throws IllegalStateException {
        double cantidadAAgregar = 1.0; 

        // Verificamos si es a Granel usando la herencia
        if (seleccionado instanceof org.uacm.puntodeventa.Clases.ProductoGranel || "Granel".equalsIgnoreCase(seleccionado.getTipo())) {
            try {
                FXMLLoader loader = new FXMLLoader(PantallaPrincipalController.class.getResource("/pantallas/CantidadGranel.fxml")); 
                Parent root = loader.load();
                
                CantidadGranelController controller = loader.getController();
                
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Producto a Granel");
                stage.initModality(javafx.stage.Modality.APPLICATION_MODAL); 
                stage.setResizable(false);
                
                stage.showAndWait(); 
                
                double peso = controller.getCantidadFinal();
                
                if (peso == -1.0) {
                    return; // El usuario canceló la operación
                } else {
                    cantidadAAgregar = peso; 
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException("Error al abrir ventana de cantidad a granel.");
            }
        }

        if (seleccionado.getStock() < cantidadAAgregar) {
            throw new IllegalStateException("Stock insuficiente para " + seleccionado.getNombre() + ". Disponible: " + seleccionado.getStock());
        }

        for (ProductoCarrito item : carritoList) {
            if (item.getId() == seleccionado.getID_producto()) {
                if ((item.getCantidad() + cantidadAAgregar) > seleccionado.getStock()) {
                    throw new IllegalStateException("No hay suficiente stock para agregar más unidades de " + seleccionado.getNombre());
                }
                item.setCantidad(item.getCantidad() + cantidadAAgregar);
                actualizarTotales();
                tableViewCarrito.refresh();
                return;
            }
        }

        ProductoCarrito nuevo = new ProductoCarrito(seleccionado, cantidadAAgregar);
        carritoList.add(nuevo);
        actualizarTotales();
    }

    public void configurarPermisos(int nivel, String nombreUsuario) {
        this.nivelUsuarioActual = nivel;
        if (lblSesion != null) {
            String rol = (nivel == 1) ? "Administrador" : "Empleado";
            lblSesion.setText("Sesión: " + rol);
        }

        if (nivel == 2) {
            if (btnReportes != null) btnReportes.setDisable(true);
            if (btnMonedero != null) btnMonedero.setDisable(true);
            if (btnCambiar != null) btnCambiar.setDisable(true);
        }
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(PantallaPrincipalController.class.getResource("/pantallas/Login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root, 380, 520));
            stage.show();
            Stage actual = (Stage) btnCerrarSesion.getScene().getWindow();
            actual.close();
        } catch (IOException e) {
            mostrarAlertaError("Error Crítico", "No se pudo cargar la pantalla de inicio de sesión.");
            e.printStackTrace();
        }
    }

    @FXML
    private void agregarProductoAlCarrito(ActionEvent event) {
        try {
            String texto = busquedaCarrito.getText().trim();
            if (texto.isEmpty()) {
                throw new IllegalArgumentException("/pantallas/OpcionValida.fxml");
            }

            Producto seleccionado = list.stream()
                    .filter(p -> p.getNombre().equalsIgnoreCase(texto) || String.valueOf(p.getID_producto()).equals(texto))
                    .findFirst()
                    .orElseGet(() -> !filteredProducts.isEmpty() ? filteredProducts.get(0) : null);

            if (seleccionado == null) {
                throw new IllegalArgumentException("Producto no encontrado");
            }

            agregarProductoDirecto(seleccionado);
            busquedaCarrito.clear();
            suggestionPopup.hide();

        } catch (IllegalArgumentException | IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("/pantallas/")) {
                mostrarMensaje(e.getMessage());
            } else {
                mostrarAlerta(e.getMessage());
            }
        } catch (Exception e) {
            mostrarAlertaError("Error Inesperado", "Ocurrió un error al intentar agregar el producto al carrito.");
        }
    }

    private void actualizarTotales() {
        double totalArticulos = carritoList.stream().mapToDouble(ProductoCarrito::getCantidad).sum();
        double totalPagar = carritoList.stream().mapToDouble(ProductoCarrito::getTotal).sum();
        lblArticulosCount.setText(String.format("%.2f", totalArticulos));
        lblTotal.setText(String.format("$%.2f", totalPagar));
    }

    @FXML
    private void cobrar(ActionEvent event) {
        try {
            if (carritoList.isEmpty()) {
                throw new IllegalStateException("/pantallas/AlertaCobrar.fxml");
            }
            
            // 1. Validar stock suficiente para todos los productos
            for (ProductoCarrito item : carritoList) {
                Producto original = list.stream()
                        .filter(p -> p.getID_producto() == item.getId())
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("El producto " + item.getNombre() + " ya no existe en el inventario."));

                if (original.getStock() < item.getCantidad()) {
                    throw new IllegalStateException("Stock insuficiente para " + original.getNombre());
                }
            }

            // --- ABRIR VENTANA DE SELECCIÓN DE PAGO ---
            FXMLLoader loader = new FXMLLoader(PantallaPrincipalController.class.getResource("/pantallas/tipoPago.fxml"));
            Parent root = loader.load();
            TipoPagoController pagoController = loader.getController();

            Stage stagePago = new Stage();
            stagePago.setTitle("Método de Pago");
            stagePago.setScene(new Scene(root));
            stagePago.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stagePago.setResizable(false);
            stagePago.showAndWait();

            String metodoPago = pagoController.getMetodoSeleccionado();
            
            // Si el método es null, el cajero cerró la ventana sin elegir, abortamos el cobro
            if (metodoPago == null) {
                return; 
            }
            // ------------------------------------------

            // 2. Crear objeto Venta pasando el Carrito y el Método de Pago elegido
            Venta nuevaVenta = new Venta(carritoList, metodoPago); 
            System.out.println("Total calculado en memoria: " + nuevaVenta.getTotal());
            
            // 3. Guardar la venta en la BD y obtener el ID generado
            int idVenta = VentaABD.insertar(nuevaVenta);

            // 4. Guardar cada detalle de venta y actualizar el stock en BD
            for (ProductoCarrito item : carritoList) {
                DetalleABD.insertar(idVenta, item.getId(), item.getCantidad(), item.getPrecio());

                Producto original = list.stream()
                        .filter(p -> p.getID_producto() == item.getId())
                        .findFirst().get();
                double nuevoStock = original.getStock() - item.getCantidad();
                InformacionProductos.getInstance().actualizarStock(item.getId(), nuevoStock);
            }

            // 5. Generar ticket y mostrarlo
            String textoTicket = nuevaVenta.generarTicket();
            abrirVentanaTicket(textoTicket);

            // 6. Limpiar carrito
            carritoList.clear();
            actualizarTotales();

        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("/pantallas/")) {
                mostrarMensaje(e.getMessage());
            } else {
                mostrarAlerta(e.getMessage());
            }
        } catch (Exception e) {
            mostrarAlertaError("Error de Cobro", "Ocurrió un error inesperado durante el cobro.");
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirInventario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(PantallaPrincipalController.class.getResource("/pantallas/Inventario.fxml"));
            Parent root = loader.load();

            InventarioController invController = loader.getController();
            invController.configurarPermisos(this.nivelUsuarioActual);

            Stage stage = new Stage();
            stage.setTitle("INVENTARIO");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            mostrarAlertaError("Archivo no encontrado", "No se pudo cargar la pantalla de Inventario.");
            e.printStackTrace();
        }
    }

    private void abrirVentanaTicket(String textoTicket) {
        try {
            FXMLLoader loader = new FXMLLoader(PantallaPrincipalController.class.getResource("/pantallas/ticket.fxml"));
            Parent root = loader.load();

            TicketController controller = loader.getController();
            controller.setTicketTexto(textoTicket);

            Stage stage = new Stage();
            stage.setTitle("Ticket");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            mostrarAlertaError("Archivo no encontrado", "No se pudo cargar la pantalla del Ticket.");
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminar(ActionEvent event) {
        try {
            ProductoCarrito seleccionado = tableViewCarrito.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                throw new IllegalArgumentException("/pantallas/OpcionValida.fxml");
            }
            carritoList.remove(seleccionado);
            actualizarTotales();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("/pantallas/")) {
                mostrarMensaje(e.getMessage());
            } else {
                mostrarAlerta(e.getMessage());
            }
        }
    }

    @FXML
    public void abrirRecargas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(PantallaPrincipalController.class.getResource("/pantallas/Recargas.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("RECARGAS");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            mostrarAlertaError("Archivo no encontrado", "No se pudo abrir la ventana de Recargas.");
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirReportes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(PantallaPrincipalController.class.getResource("/pantallas/ReporteVentas.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Reporte de ventas");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            mostrarAlertaError("Archivo no encontrado", "No se pudo abrir la ventana de Reportes.");
            e.printStackTrace();
        }
    }
    
    @FXML
    public void agregarMas(ActionEvent event) {
        try {
            ProductoCarrito seleccionado = tableViewCarrito.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                throw new IllegalArgumentException("/pantallas/OpcionValida.fxml");
            }

            Producto original = list.stream()
                    .filter(p -> p.getID_producto() == seleccionado.getId())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("El producto original ya no existe."));

            double incremento = 1.0; 

            if (original instanceof org.uacm.puntodeventa.Clases.ProductoGranel || "Granel".equalsIgnoreCase(original.getTipo())) {
                try {
                    FXMLLoader loader = new FXMLLoader(PantallaPrincipalController.class.getResource("/pantallas/CantidadGranel.fxml")); 
                    Parent root = loader.load();
                    CantidadGranelController controller = loader.getController();
                    
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Agregar más peso"); 
                    stage.initModality(javafx.stage.Modality.APPLICATION_MODAL); 
                    stage.setResizable(false);
                    stage.showAndWait(); 
                    
                    double peso = controller.getCantidadFinal();
                    if (peso == -1.0) {
                        return; 
                    }
                    incremento = peso; 
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalStateException("Error al abrir ventana de cantidad a granel.");
                }
            }

            if ((seleccionado.getCantidad() + incremento) <= original.getStock()) {
                double nuevaCantidad = Math.round((seleccionado.getCantidad() + incremento) * 1000.0) / 1000.0;
                seleccionado.setCantidad(nuevaCantidad);
                actualizarTotales();
                tableViewCarrito.refresh();
            } else {
                if (botonMas != null) {
                    botonMas.setDisable(true);
                }
                throw new IllegalStateException("No hay más stock disponible para: " + original.getNombre());
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("/pantallas/")) {
                mostrarMensaje(e.getMessage());
            } else {
                mostrarAlerta(e.getMessage());
            }
        }
    }

    @FXML
    public void agregarMenos(ActionEvent event) {
        try {
            ProductoCarrito seleccionado = tableViewCarrito.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                throw new IllegalArgumentException("/pantallas/OpcionValida.fxml");
            }

            Producto original = list.stream()
                    .filter(p -> p.getID_producto() == seleccionado.getId())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("El producto original ya no existe."));

            double decremento = 1.0; 

            if (original instanceof org.uacm.puntodeventa.Clases.ProductoGranel || "Granel".equalsIgnoreCase(original.getTipo())) {
                try {
                    FXMLLoader loader = new FXMLLoader(PantallaPrincipalController.class.getResource("/pantallas/CantidadGranel.fxml")); 
                    Parent root = loader.load();
                    CantidadGranelController controller = loader.getController();
                    
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Quitar peso"); 
                    stage.initModality(javafx.stage.Modality.APPLICATION_MODAL); 
                    stage.setResizable(false);
                    stage.showAndWait(); 
                    
                    double peso = controller.getCantidadFinal();
                    if (peso == -1.0) {
                        return; 
                    }
                    decremento = peso; 
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalStateException("Error al abrir ventana.");
                }
            }

            double nuevaCantidad = Math.round((seleccionado.getCantidad() - decremento) * 1000.0) / 1000.0;

            if (nuevaCantidad > 0) {
                seleccionado.setCantidad(nuevaCantidad);
                actualizarTotales();
                tableViewCarrito.refresh();
            } else {
                carritoList.remove(seleccionado);
                actualizarTotales();
            }

            if (botonMas != null && botonMas.isDisabled()) {
                botonMas.setDisable(false);
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("/pantallas/")) {
                mostrarMensaje(e.getMessage());
            } else {
                mostrarAlerta(e.getMessage());
            }
        }
    }
    
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atención");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarMensaje(String ruta) {
        try {
            Parent root = FXMLLoader.load(PantallaPrincipalController.class.getResource(ruta));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}