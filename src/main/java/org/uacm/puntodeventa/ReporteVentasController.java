package org.uacm.puntodeventa;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.uacm.puntodeventa.Clases.ConvierteNumeroLetra;
import org.uacm.puntodeventa.Clases.ItemReporte;
import org.uacm.puntodeventa.DB.ConexionBD;

public class ReporteVentasController implements Initializable {

    @FXML private TextField txtBusqueda;
    @FXML private TableView<ItemReporte> tablaVentas;
    @FXML private TableColumn<ItemReporte, String> colFolio;
    @FXML private TableColumn<ItemReporte, String> colFecha;
    @FXML private TableColumn<ItemReporte, String> colCantidadProductos;
    @FXML private TableColumn<ItemReporte, Double> colTotal;
    @FXML private Label lblTotalDia;

    private ObservableList<ItemReporte> listaCombinada = FXCollections.observableArrayList();
    private FilteredList<ItemReporte> listaFiltrada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colFolio.setCellValueFactory(new PropertyValueFactory<>("folio"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCantidadProductos.setCellValueFactory(new PropertyValueFactory<>("detalles"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        cargarDatosDesdeBD();

        listaFiltrada = new FilteredList<>(listaCombinada, item -> true);
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> {
            String texto = (newVal == null) ? "" : newVal.toLowerCase();
            listaFiltrada.setPredicate(item -> {
                if (texto.isEmpty()) return true;
                return item.getFolio().toLowerCase().contains(texto)
                        || item.getFecha().toLowerCase().contains(texto)
                        || item.getDetalles().toLowerCase().contains(texto);
            });
        });
        tablaVentas.setItems(listaFiltrada);

        double totalGeneral = listaCombinada.stream().mapToDouble(ItemReporte::getTotal).sum();
        lblTotalDia.setText(String.format("$%.2f", totalGeneral));

        tablaVentas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ItemReporte seleccionado = tablaVentas.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    mostrarTicket(seleccionado.getFolio());
                }
            }
        });
    }

    private void cargarDatosDesdeBD() {
        listaCombinada.clear();

        String sqlVentas = "SELECT v.id_venta, v.folio, v.fecha, v.total_a_pagar, COUNT(d.id_detalle) AS cantidad_productos " +
                           "FROM Venta v LEFT JOIN DetalleVenta d ON v.id_venta = d.id_venta " +
                           "GROUP BY v.id_venta ORDER BY v.fecha DESC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlVentas);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String folio = rs.getString("folio");
                String fecha = rs.getString("fecha"); 
                int cantidad = rs.getInt("cantidad_productos");
                double total = rs.getDouble("total_a_pagar");
                String detalles = "Venta de " + cantidad + " producto(s)";
                listaCombinada.add(new ItemReporte(folio, fecha, detalles, total));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sqlRecargas = "SELECT folio, fecha, compania, monto FROM Recargas ORDER BY fecha DESC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlRecargas);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String folio = rs.getString("folio");
                String fecha = rs.getString("fecha");
                String compania = rs.getString("compania");
                double monto = rs.getDouble("monto");
                String detalles = "Recarga " + compania;
                listaCombinada.add(new ItemReporte(folio, fecha, detalles, monto));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mostrarTicket(String folio) {
        try {
            String texto = buscarTicketPorFolio(folio);
            if (texto == null) return;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallas/ticket.fxml"));
            Parent root = loader.load();
            TicketController controller = loader.getController();
            controller.setTicketTexto(texto);
            Stage stage = new Stage();
            stage.setTitle("Ticket");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   private String buscarTicketPorFolio(String folio) {
        // 1. Buscar primero en Ventas Físicas usando el INNER JOIN para obtener al cajero original
        String sqlVenta = "SELECT v.id_venta, v.folio, v.fecha, v.total_a_pagar, v.metodo_pago, v.num_rastreo, " +
                          "d.cantidad, d.precio_unitario, " +
                          "p.id_producto, p.nombre, p.tipo, p.categoria, p.stock, p.precio_proveedor, " +
                          "u.id_usuario, u.nombre AS nombre_empleado " +
                          "FROM Venta v " +
                          "JOIN DetalleVenta d ON v.id_venta = d.id_venta " +
                          "JOIN Producto p ON d.id_producto = p.id_producto " +
                          "LEFT JOIN Usuario u ON v.id_usuario = u.id_usuario " +
                          "WHERE v.folio = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlVenta)) {
             
            stmt.setString(1, folio);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Recuperar datos generales de la venta
                String fechaStr = rs.getString("fecha"); 
                // Evitamos el parseo a LocalDateTime aquí porque la clase Venta ya maneja su fecha internamente,
                // o puedes crear un constructor en Venta que reciba la fecha si necesitas que el ticket histórico
                // muestre la fecha original exacta en lugar de la fecha de reimpresión.
                
                double total = rs.getDouble("total_a_pagar");
                String metodoPagoBD = rs.getString("metodo_pago");
                String numRastreoBD = rs.getString("num_rastreo");
                
                String nombreEmpleado = rs.getString("nombre_empleado");
                if (nombreEmpleado == null) nombreEmpleado = "Cajero General";
                
                int idEmpleado = rs.getInt("id_usuario");
                if (idEmpleado <= 0) idEmpleado = 1;

                // Crear la lista de productos
                java.util.List<org.uacm.puntodeventa.Clases.ProductoCarrito> productos = new java.util.ArrayList<>();
                
                do {
                    int idProd = rs.getInt("id_producto");
                    String nombreProd = rs.getString("nombre");
                    String tipoProd = rs.getString("categoria");
                    double cantidad = rs.getDouble("cantidad");
                    double precioUnitario = rs.getDouble("precio_unitario");
                    
                    // CORRECCIÓN AQUÍ: Creamos el producto temporal con los 7 parámetros exactos
                    org.uacm.puntodeventa.Clases.Producto p = new org.uacm.puntodeventa.Clases.Producto(
                        idProd, 
                        nombreProd, 
                        tipoProd != null ? tipoProd : "General", 
                        0.0,             // stock temporal (no se usa en el ticket)
                        precioUnitario,  // precio de venta para el subtotal
                        "",              // campo extra 1
                        ""               // campo extra 2
                    );
                    
                    productos.add(new org.uacm.puntodeventa.Clases.ProductoCarrito(p, cantidad));
                } while (rs.next());

                // Utilizamos tu constructor de 8 parámetros que diseñaste en Venta.java
                org.uacm.puntodeventa.Clases.Venta ventaHistorica = new org.uacm.puntodeventa.Clases.Venta(
                    productos, total, total, 0.0, metodoPagoBD, numRastreoBD, idEmpleado, nombreEmpleado
                );
                
                // Generamos el ticket y le agregamos la leyenda de histórico
                StringBuilder sb = new StringBuilder(ventaHistorica.generarTicket());
                sb.append("========================================\n");
                sb.append("  *** COPIA DE TICKET (HISTÓRICO) ***\n");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                sb.append("  Reimpreso el: ").append(LocalDateTime.now().format(dtf)).append("\n");
                sb.append("========================================\n");
                
                return sb.toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Buscar en Recargas
        String sqlRecarga = "SELECT r.folio, r.fecha, r.compania, r.monto, r.numero_telefonico, " +
                            "r.metodo_pago, r.num_rastreo, r.id_usuario, u.nombre AS nombre_empleado " +
                            "FROM Recargas r " +
                            "LEFT JOIN Usuario u ON r.id_usuario = u.id_usuario " +
                            "WHERE r.folio = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlRecarga)) {
             
            stmt.setString(1, folio);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double monto = rs.getDouble("monto");
                String metodoPagoRecarga = rs.getString("metodo_pago");
                String numRastreoRec = rs.getString("num_rastreo");
                String nombreEmpleado = rs.getString("nombre_empleado") != null ? rs.getString("nombre_empleado") : "Cajero General";
                int idEmpleado = rs.getInt("id_usuario");
                if (idEmpleado <= 0) idEmpleado = 1;

                StringBuilder sb = new StringBuilder();
                sb.append("========================================\n");
                sb.append("        ABARROTES JASE S.A. DE C.V.\n");
                sb.append("             RFC: JASE230038LH0\n");
                sb.append("    Prol. San Isidro 151, San Lorenzo\n");
                sb.append("         Tezonco, Iztapalapa, CDMX\n");
                sb.append("========================================\n");
                sb.append("    *** COMPROBANTE DE RECARGA ***\n");
                sb.append("----------------------------------------\n");
                sb.append("Folio: ").append(rs.getString("folio")).append("\n");
                sb.append("Fecha Venta: ").append(rs.getString("fecha")).append("\n");
                sb.append("Cajero: ").append(nombreEmpleado).append("\n");
                sb.append("No. Empleado: ").append(idEmpleado).append("\n");
                sb.append("Compañía: ").append(rs.getString("compania")).append("\n");
                sb.append("Teléfono: ").append(rs.getString("numero_telefonico")).append("\n");
                sb.append("----------------------------------------\n");
                
                String montoFormateado = String.format("$%.2f MXN", monto);
                sb.append(String.format("%-25s %s\n", "MONTO RECARGADO:", montoFormateado));
                sb.append("SON: ").append(ConvierteNumeroLetra.convertir(monto)).append("\n");
                sb.append("----------------------------------------\n");
                
                if (metodoPagoRecarga == null || metodoPagoRecarga.isEmpty()) {
                    metodoPagoRecarga = "EFECTIVO";
                }
                sb.append("Método de Pago:\n").append(metodoPagoRecarga).append("\n");

                if (!"EFECTIVO".equalsIgnoreCase(metodoPagoRecarga)) {
                    String rastreoMostrar = (numRastreoRec != null && !numRastreoRec.isEmpty() && !"N/A".equalsIgnoreCase(numRastreoRec))
                            ? numRastreoRec
                            : "TR-" + folio.replace("REC-", "");
                    sb.append("No. Rastreo: ").append(rastreoMostrar).append("\n");
                }
                
                sb.append("========================================\n");
                sb.append("  *** COPIA DE TICKET (HISTÓRICO) ***\n");
                
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String fechaHoraImpresion = LocalDateTime.now().format(dtf);
                sb.append("  Reimpreso el: ").append(fechaHoraImpresion).append("\n");
                sb.append("========================================\n");
                
                return sb.toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}