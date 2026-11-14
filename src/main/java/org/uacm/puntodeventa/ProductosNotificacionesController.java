
package org.uacm.puntodeventa;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.uacm.puntodeventa.Clases.AlertasInventario;
import org.uacm.puntodeventa.Clases.InformacionProductos;
import org.uacm.puntodeventa.Clases.PedidoReabastecimiento;
import org.uacm.puntodeventa.Clases.Producto;

/**
 * Controlador de alertas y reabastecimiento.
 * Genera el pedido, actualiza el stock y abre el ticket.
 */
public class ProductosNotificacionesController
        implements Initializable {

    @FXML
    private TableView<Producto> tableViewProductos;

    @FXML
    private TableColumn<Producto, Integer> columnaId;

    @FXML
    private TableColumn<Producto, String> columnaNombre;

    @FXML
    private TableColumn<Producto, Double> columnaStock;

    @FXML
    private TableColumn<Producto, Double> columnaMinimo;

    @FXML
    private TableColumn<Producto, String> columnaProveedor;

    @FXML
    private TableColumn<Producto, Double> columnaCantidad;

    @FXML
    private TableColumn<Producto, String> columnaEstado;

    @FXML
    private Button btnActuStock;

    @FXML
    private Button btnGenerarPedido;

    @FXML
    private Label lblInfo;

    private final ObservableList<Producto> productos =
            InformacionProductos.getInstance().getProductos();

    private final ObservableList<PedidoReabastecimiento>
            pedidosGenerados = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        columnaId.setCellValueFactory(
                new PropertyValueFactory<>("ID_producto"));

        columnaNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        columnaStock.setCellValueFactory(
                new PropertyValueFactory<>("stock"));

        columnaMinimo.setCellValueFactory(
                new PropertyValueFactory<>("stockMinimo"));

        columnaProveedor.setCellValueFactory(
                new PropertyValueFactory<>("proveedor"));

        columnaCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidadReabastecimiento"));

        columnaEstado.setCellValueFactory(
                new PropertyValueFactory<>("estadoInventario"));

        actualizarTabla();
    }

    /**
     * Actualiza la lista de productos con stock bajo.
     */
    private void actualizarTabla() {

        ObservableList<Producto> alertas =
                FXCollections.observableArrayList(
                        AlertasInventario.obtenerProductosConAlerta(
                                productos));

        tableViewProductos.setItems(alertas);
        tableViewProductos.refresh();

        int cantidad = alertas.size();

        if (cantidad == 0) {
            lblInfo.setText(
                    "No hay productos que necesiten reabastecimiento.");
        } else {
            lblInfo.setText(
                    "Hay " + cantidad
                    + " producto(s) que requieren atención.");
        }
    }

    /**
     * Botón para actualizar las alertas.
     */
    @FXML
    private void actualizarStock(ActionEvent event) {
        actualizarTabla();
    }

    /**
     * Genera automáticamente el pedido y actualiza el inventario.
     */
    @FXML
    private void generarPedido(ActionEvent event) {

        Producto seleccionado =
                tableViewProductos.getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Selecciona un producto de la tabla."
            );
            return;
        }

        try {

            // Generar el pedido de reabastecimiento.
            PedidoReabastecimiento pedido =
                    AlertasInventario.generarPedido(seleccionado);

            // Registrar el pedido en la sesión.
            pedidosGenerados.add(pedido);

            // Cantidad solicitada al proveedor.
            double cantidad = pedido.getCantidad();

            // Simular la recepción automática de mercancía.
            seleccionado.agregarStock(cantidad);

            // Actualizar las alertas del inventario.
            actualizarTabla();

            // Mostrar el ticket del pedido.
            abrirTicketPedido(pedido, seleccionado);

        } catch (IllegalArgumentException ex) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "No se pudo generar el pedido",
                    ex.getMessage()
            );
        }
    }

   
/**
 * Abre la ventana del ticket correspondiente a un pedido
 * de reabastecimiento.
 *
 * Reutiliza la plantilla ticket.fxml sin modificarla.
 * Cambia únicamente el encabezado visible a "TICKET"
 * para esta instancia de la ventana.
 *
 * Los tickets de venta conservan su encabezado original
 * "TICKET DE VENTA", ya que cada apertura del FXML
 * crea una nueva interfaz independiente.
 *
 * @param pedido pedido de reabastecimiento generado
 * @param producto producto asociado al pedido
 */
private void abrirTicketPedido(
        PedidoReabastecimiento pedido,
        Producto producto) {

    try {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/pantallas/ticket.fxml"));

        Parent root = loader.load();

        // Obtener el controlador existente.
        TicketController controller = loader.getController();

        // Colocar la información del pedido.
        controller.setTicketTexto(
                crearTextoTicket(pedido, producto));

        // Cambiar el encabezado únicamente en esta ventana.
        cambiarEncabezadoTicket(root);

        Stage stage = new Stage();
        stage.setTitle("Ticket");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

    } catch (IOException ex) {

        ex.printStackTrace();

        mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error al abrir el ticket",
                "No se pudo cargar la ventana del ticket."
        );
    }
}

/**
 * Cambia temporalmente el encabezado del ticket de pedido.
 *
 * Busca el Label que contiene "TICKET DE VENTA" dentro
 * de la interfaz cargada y reemplaza su texto por "TICKET".
 *
 * Este cambio afecta solamente a la instancia actual
 * del ticket y no modifica el archivo ticket.fxml.
 *
 * @param root nodo raíz de la interfaz del ticket
 */
private void cambiarEncabezadoTicket(Parent root) {

    for (javafx.scene.Node nodo : root.lookupAll(".label")) {

        if (nodo instanceof Label) {

            Label etiqueta = (Label) nodo;

            if ("TICKET DE VENTA".equals(
                    etiqueta.getText().trim())) {

                etiqueta.setText("TICKET");
                break;
            }
        }
    }
}


    /**
     * Construye el comprobante del pedido.
     */
    private String crearTextoTicket(
            PedidoReabastecimiento pedido,
            Producto producto) {

        final int ancho = 62;
        final String linea = "-".repeat(ancho);

        double cantidad = pedido.getCantidad();

        double precioUnitario =
                producto.getPrecio_prov();

        double subtotal =
                cantidad * precioUnitario;

        // Cálculo referencial, no fiscal.
        double ivaEstimado = subtotal * 0.16;

        double totalEstimado =
                subtotal + ivaEstimado;

        StringBuilder sb = new StringBuilder();

        sb.append(
                "               PEDIDO DE REABASTECIMIENTO\n");

        sb.append(linea).append("\n");

        sb.append("PEDIDO / FOLIO: ")
                .append(pedido.getFolio())
                .append("\n");

        sb.append("FECHA: ")
                .append(pedido.getFechaFormateada())
                .append("\n");

        sb.append("PROVEEDOR: ")
                .append(pedido.getProveedor())
                .append("\n");

        sb.append(
                "CLIENTE / TIENDA: No registrados en el sistema\n");

        sb.append(linea).append("\n");

        sb.append(String.format(
                Locale.US,
                "%-9s %-35s %s%n",
                "CODIGO",
                "DESCRIPCION",
                "UNIDAD"));

        sb.append(String.format(
                Locale.US,
                "%-12s %17s %18s%n",
                "CANTIDAD",
                "P. UNITARIO",
                "SUBTOTAL"));

        sb.append(linea).append("\n");

        sb.append(String.format(
                Locale.US,
                "%-9d %-35.35s %s%n",
                pedido.getIdProducto(),
                pedido.getNombreProducto(),
                "PZA"));

        sb.append(String.format(
                Locale.US,
                "%-12s %17s %18s%n",
                formatoCantidad(cantidad),
                moneda(precioUnitario),
                moneda(subtotal)));

        sb.append(linea).append("\n");

        sb.append("TOTAL DE UNIDADES: ")
                .append(formatoCantidad(cantidad))
                .append("\n");

        sb.append(String.format(
                Locale.US,
                "%42s %18s%n",
                "SUBTOTAL:",
                moneda(subtotal)));

        sb.append(String.format(
                Locale.US,
                "%42s %18s%n",
                "IVA ESTIMADO (16%):",
                moneda(ivaEstimado)));

        sb.append(String.format(
                Locale.US,
                "%42s %18s%n",
                "TOTAL ESTIMADO:",
                moneda(totalEstimado)));

        sb.append(linea).append("\n");

        sb.append("ESTADO: ")
                .append(pedido.getEstado())
                .append("\n");

        sb.append(
                "RECEPCION SIMULADA: STOCK ACTUALIZADO\n");

        sb.append(
                "IMPORTES REFERENCIALES. VERIFICAR IMPUESTOS.\n");

        return sb.toString();
    }

    /**
     * Formatea importes monetarios.
     */
    private String moneda(double importe) {

        return String.format(
                Locale.US,
                "$%,.2f",
                importe);
    }

    /**
     * Formatea las cantidades.
     */
    private String formatoCantidad(double cantidad) {

        if (cantidad == Math.rint(cantidad)) {

            return String.format(
                    Locale.US,
                    "%.0f",
                    cantidad);
        }

        return String.format(
                Locale.US,
                "%.2f",
                cantidad);
    }

    /**
     * Muestra mensajes al usuario.
     */
    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Devuelve los pedidos generados en la sesión.
     */
    public ObservableList<PedidoReabastecimiento>
            getPedidosGenerados() {

        return pedidosGenerados;
    }
}
