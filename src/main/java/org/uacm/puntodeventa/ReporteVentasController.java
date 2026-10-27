package org.uacm.puntodeventa;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        // Cargar datos desde la base de datos
        cargarDatosDesdeBD();

        // Configurar filtro de búsqueda
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

        // Calcular total general
        double totalGeneral = listaCombinada.stream().mapToDouble(ItemReporte::getTotal).sum();
        lblTotalDia.setText(String.format("$%.2f", totalGeneral));

        // Doble clic para ver ticket/comprobante
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

        // 1. Cargar ventas físicas (CORREGIDO: Restaurada la consulta con COUNT para la tabla)
        String sqlVentas = "SELECT v.id_venta, v.folio, v.fecha, v.total_a_pagar, COUNT(d.id_detalle) AS cantidad_productos " +
                           "FROM Venta v LEFT JOIN DetalleVenta d ON v.id_venta = d.id_venta " +
                           "GROUP BY v.id_venta ORDER BY v.fecha DESC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlVentas);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String folio = rs.getString("folio");
                String fecha = rs.getString("fecha"); // Ahora vendrá con fecha y hora de la BD
                int cantidad = rs.getInt("cantidad_productos");
                double total = rs.getDouble("total_a_pagar");
                String detalles = "Venta de " + cantidad + " producto(s)";
                listaCombinada.add(new ItemReporte(folio, fecha, detalles, total));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Cargar recargas
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
        String sqlVenta = "SELECT v.id_venta, v.folio, v.fecha, v.total_a_pagar, v.metodo_pago, " +
                          "d.cantidad, d.precio_unitario, " +
                          "p.id_producto, p.nombre, p.tipo, p.categoria, " +
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
                String fecha = rs.getString("fecha"); 
                double total = rs.getDouble("total_a_pagar");
                
                String nombreEmpleado = rs.getString("nombre_empleado") != null ? rs.getString("nombre_empleado") : "Desconocido";
                int idEmpleado = rs.getInt("id_usuario");

                StringBuilder sb = new StringBuilder();
                
                sb.append("========================================\n");
                sb.append("        ABARROTES JASE S.A. DE C.V.\n");
                sb.append("             RFC: JASE230038LH0\n");
                sb.append("    Prol. San Isidro 151, San Lorenzo\n");
                sb.append("          Tezonco, Iztapalapa, CDMX\n");
                sb.append("========================================\n");

                sb.append("Fecha Venta: ").append(fecha).append("\n");
                sb.append("Folio: ").append(folio).append("\n");

                sb.append("Cajero: ").append(nombreEmpleado).append("\n");
                sb.append("No. Empleado: ").append(idEmpleado).append("\n");
                sb.append("========================================\n");

                sb.append(String.format("%-4s %-12s %-7s %-4s %-7s\n", "ID", "ARTICULO", "CATEG", "CANT", "TOTAL"));
                sb.append("----------------------------------------\n");
                
                do {
                    int idProd = rs.getInt("id_producto");
                    String nombreProd = rs.getString("nombre");
                    
                    String tipoProd = rs.getString("categoria");
                    if (tipoProd == null) tipoProd = "General";
                    // -----------------------------------------------------
                    
                    double cantidad = rs.getDouble("cantidad");
                    double precioUnitario = rs.getDouble("precio_unitario");
                    double subtotal = cantidad * precioUnitario;
                    
                    String nCorto = nombreProd.length() > 11 ? nombreProd.substring(0, 9) + ".." : nombreProd;
                    String cCorta = tipoProd.length() > 7 ? tipoProd.substring(0, 7) : tipoProd;
                    
                    String cantStr = String.valueOf(cantidad);
                    if (cantStr.endsWith(".0")) {
                        cantStr = cantStr.replace(".0", "");
                    }

                    sb.append(String.format("%-4d %-12s %-7s %-4s $%7.2f\n", 
                            idProd, nCorto, cCorta, cantStr, subtotal));
                            
                } while (rs.next());

                sb.append("----------------------------------------\n");
                sb.append(String.format("%-28s $%7.2f\n", "TOTAL PAGADO:", total));
                
                String metodoPago = rs.getString("metodo_pago");
                if (metodoPago == null) metodoPago = "EFECTIVO";
                sb.append(String.format("Método de Pago:           %s\n", metodoPago));
                
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

        // Buscar en Recargas
        String sqlRecarga = "SELECT folio, fecha, compania, monto, numero_telefonico FROM Recargas WHERE folio = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlRecarga)) {
             
            stmt.setString(1, folio);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("========================================\n");
                sb.append("        ABARROTES JASE S.A. DE C.V.\n"); 
                sb.append("========================================\n");
                sb.append("    *** COMPROBANTE DE RECARGA ***\n");
                sb.append("----------------------------------------\n");
                sb.append("Folio: ").append(rs.getString("folio")).append("\n");
                sb.append("Fecha Venta: ").append(rs.getString("fecha")).append("\n");
                sb.append("Compañía: ").append(rs.getString("compania")).append("\n");
                sb.append("Teléfono: ").append(rs.getString("numero_telefonico")).append("\n");
                sb.append("----------------------------------------\n");
                sb.append(String.format("%-28s $%7.2f\n", "MONTO RECARGADO:", rs.getDouble("monto")));
                sb.append("========================================\n");
                
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