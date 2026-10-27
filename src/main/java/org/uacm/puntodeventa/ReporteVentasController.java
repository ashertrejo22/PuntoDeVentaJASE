package org.uacm.puntodeventa;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        // 1. Cargar ventas físicas (con detalle de cantidad de productos)
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
    // Buscar en Venta
    String sqlVenta = "SELECT v.id_venta, v.folio, v.fecha, v.total_a_pagar, d.cantidad, d.precio_unitario, p.nombre " +
                      "FROM Venta v " +
                      "JOIN DetalleVenta d ON v.id_venta = d.id_venta " +
                      "JOIN Producto p ON d.id_producto = p.id_producto " +
                      "WHERE v.folio = ?";
    try (Connection conn = ConexionBD.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sqlVenta)) {
        stmt.setString(1, folio);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String fecha = rs.getString("fecha");
            double total = rs.getDouble("total_a_pagar");  
            StringBuilder sb = new StringBuilder();
            sb.append("\n*************** TICKET VENTA ***************");
            sb.append("\nFolio: ").append(folio);
            sb.append("\nFecha: ").append(fecha);
            sb.append("\n--------------- PRODUCTOS -------------------");
            do {
                String nombre = rs.getString("nombre");
                double cantidad = rs.getDouble("cantidad");
                double precioUnitario = rs.getDouble("precio_unitario");
                double subtotal = cantidad * precioUnitario;
                sb.append("\n").append(nombre)
                  .append(" x").append(cantidad)
                  .append(" --- $").append(subtotal);
            } while (rs.next());
            sb.append("\nTOTAL A PAGAR: $").append(total);
            sb.append("\n--------------------------------------------");
            return sb.toString();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

        // Buscar en Recargas
        String sqlRecarga = "SELECT folio, fecha, compania, monto,numero_telefonico FROM Recargas WHERE folio = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlRecarga)) {
            stmt.setString(1, folio);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n************** COMPROBANTE RECARGA **************");
                sb.append("\nFolio: ").append(rs.getString("folio"));
                sb.append("\nFecha: ").append(rs.getString("fecha"));
                sb.append("\nCompañía: ").append(rs.getString("compania"));
                sb.append("\nMonto: $").append(rs.getDouble("monto"));
                sb.append("\nNumero telefonico: $").append(rs.getString("numero_telefonico"));
                sb.append("\n--------------------------------------------");
                return sb.toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}