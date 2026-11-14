import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.uacm.puntodeventa.Clases.*;
import org.uacm.puntodeventa.DB.*;
import org.uacm.puntodeventa.cortecaja.*;
import org.uacm.puntodeventa.reportes.*;

/** Pruebas de aceptación PSP1 (ejecutor simple, sin dependencias externas). */
public class PruebasPSP1 {

    private interface Prueba { void correr() throws Exception; }

    private static final List<String> RESULTADOS = new ArrayList<>();
    private static int fallos;

    private static void probar(String id, String descripcion, Prueba p) {
        String estado = "APROBADA";
        String detalle = "";
        try {
            p.correr();
        } catch (Throwable t) {
            estado = "FALLIDA";
            detalle = String.valueOf(t);
            fallos++;
        }
        RESULTADOS.add(id + "|" + descripcion + "|" + estado + "|" + detalle);
        System.out.println(id + " " + estado + " - " + descripcion + (detalle.isEmpty() ? "" : "  >> " + detalle));
    }

    private static void exigir(boolean c, String msg) { if (!c) throw new AssertionError(msg); }
    private static void igual(double a, double b, String msg) {
        if (Math.abs(a - b) > 0.005) throw new AssertionError(msg + " esperado=" + b + " real=" + a);
    }
    private static <T extends Throwable> void lanza(Class<T> tipo, Prueba p) throws Exception {
        try { p.correr(); } catch (Throwable t) {
            if (tipo.isInstance(t)) return;
            throw new AssertionError("se esperaba " + tipo.getSimpleName() + " y fue " + t);
        }
        throw new AssertionError("se esperaba " + tipo.getSimpleName());
    }
    private static void sql(String s) throws Exception {
        try (Statement st = ConexionBD.getConnection().createStatement()) { st.execute(s); }
    }

    private static int usuario(String nombre) throws Exception {
        sql("INSERT INTO Usuario(nombre, contrasena) VALUES('" + nombre + "','x')");
        try (Statement st = ConexionBD.getConnection().createStatement();
             java.sql.ResultSet rs = st.executeQuery("SELECT id_usuario FROM Usuario WHERE nombre = '" + nombre + "'")) {
            return rs.getInt(1);
        }
    }

    private static Venta venta(Producto p, double cant) {
        List<ProductoCarrito> l = new ArrayList<>();
        l.add(new ProductoCarrito(p, cant));
        return new Venta(l);
    }

    private static int cobrar(Venta v, CorteCaja turno) throws Exception {
        int id = VentaABD.insertar(v, turno.getIdCorte());
        for (ProductoCarrito i : v.getProductos()) {
            DetalleABD.insertar(id, i.getId(), i.getCantidad(), i.getPrecio());
        }
        return id;
    }

    public static void main(String[] args) throws Exception {
        IniciarBD.inicializar();
        CorteCajaServicio caja = new CorteCajaServicio();
        ReporteVentasServicio rep = new ReporteVentasServicio();
        LocalDate hoy = LocalDate.now();

        sql("INSERT INTO Producto(id_producto,nombre,tipo,stock,precio_proveedor,precio_venta) VALUES(1,'Cemento','Normal',100,100,120)");
        sql("INSERT INTO Producto(id_producto,nombre,tipo,stock,precio_proveedor,precio_venta) VALUES(2,'=HYPERLINK(\"x\")','Normal',100,10,12)");
        sql("INSERT INTO Producto(id_producto,nombre,tipo,stock,precio_proveedor,precio_venta) VALUES(3,'<script>alert(1)</script>','Normal',100,10,12)");
        Producto cemento = new Producto(1, "Cemento", "Normal", 100, 100, "", "");
        Producto formula = new Producto(2, "=HYPERLINK(\"x\")", "Normal", 100, 10, "", "");
        Producto xss = new Producto(3, "<script>alert(1)</script>", "Normal", 100, 10, "", "");
        int ana = usuario("Ana");
        int luis = usuario("Luis");

        probar("PR-01", "BR-2: fecha inicio posterior a fecha fin se rechaza", () ->
            lanza(IllegalArgumentException.class, () ->
                new PeriodoReporte(hoy, hoy.minusDays(1), FiltroTransaccion.AMBAS)));
        probar("PR-02", "BR-2: inicio igual a fin es válido", () ->
            new PeriodoReporte(hoy, hoy, FiltroTransaccion.AMBAS));
        probar("PR-03", "Período con fechas vacías se rechaza", () ->
            lanza(IllegalArgumentException.class, () -> new PeriodoReporte(null, hoy, FiltroTransaccion.AMBAS)));
        probar("PR-04", "BR-3: sin turno abierto no se permite vender", () ->
            lanza(IllegalStateException.class, () -> caja.exigirTurnoAbierto(ana)));
        probar("PR-05", "Fondo inicial negativo se rechaza", () ->
            lanza(IllegalArgumentException.class, () -> caja.abrirTurno(ana, -1)));
        final CorteCaja[] turnoAna = new CorteCaja[1];
        probar("PR-06", "R1: abrir turno asigna folio y estado abierto", () -> {
            turnoAna[0] = caja.abrirTurno(ana, 500);
            exigir(turnoAna[0].getFolio() != null && !turnoAna[0].getFolio().isEmpty(), "sin folio");
            exigir(turnoAna[0].getEstado() == EstadoCorte.ABIERTO, "estado");
            exigir(caja.turnoAbierto(ana).isPresent(), "no persistió");
        });
        probar("PR-07", "BR-4: un cajero no puede tener dos turnos abiertos", () ->
            lanza(IllegalStateException.class, () -> caja.abrirTurno(ana, 100)));
        final CorteCaja[] turnoLuis = new CorteCaja[1];
        probar("PR-08", "BR-4: otro cajero abre su propio turno independiente", () -> {
            turnoLuis[0] = caja.abrirTurno(luis, 200);
            exigir(turnoLuis[0].getIdCorte() != turnoAna[0].getIdCorte(), "mismo turno");
        });
        probar("PR-09", "BR-3: con turno abierto se permite vender y la venta queda ligada al turno", () -> {
            caja.exigirTurnoAbierto(ana);
            cobrar(venta(cemento, 2), turnoAna[0]);
            cobrar(venta(formula, 1), turnoAna[0]);
            cobrar(venta(xss, 1), turnoAna[0]);
            try (var st = ConexionBD.getConnection().createStatement();
                 var rs = st.executeQuery("SELECT COUNT(*) FROM Venta WHERE id_corte=" + turnoAna[0].getIdCorte()
                         + " AND id_usuario=" + ana)) {
                rs.next();
                exigir(rs.getInt(1) == 3, "ventas ligadas=" + rs.getInt(1));
            }
        });
        probar("PR-10", "Retiro mayor al efectivo disponible se rechaza", () ->
            lanza(IllegalArgumentException.class, () -> caja.registrarRetiro(turnoAna[0], 99999, "x")));
        probar("PR-11", "Retiro con monto cero o negativo se rechaza", () ->
            lanza(IllegalArgumentException.class, () -> caja.registrarRetiro(turnoAna[0], 0, "x")));
        probar("PR-12", "R2: se registra un retiro válido", () -> caja.registrarRetiro(turnoAna[0], 50, "Pago de flete"));
        probar("PR-13", "BR-5/BR-6: esperado = fondo + ventas - retiros; diferencia cero = cuadrado", () -> {
            double esperado = 500 + (240 + 12 + 12) - 50;
            caja.cerrarTurno(turnoAna[0], esperado);
            igual(turnoAna[0].getDineroEsperado(), esperado, "esperado");
            igual(turnoAna[0].getDiferencia(), 0, "diferencia");
            exigir(turnoAna[0].clasificarDiferencia() == ResultadoCorte.CUADRADO, "clasificación");
            exigir(turnoAna[0].getEstado() == EstadoCorte.CERRADO, "estado");
        });
        probar("PR-14", "R3: tras cerrar ya no hay turno abierto y no se puede vender", () -> {
            exigir(!caja.turnoAbierto(ana).isPresent(), "sigue abierto");
            lanza(IllegalStateException.class, () -> caja.exigirTurnoAbierto(ana));
        });
        probar("PR-15", "No se puede cerrar dos veces el mismo turno", () ->
            lanza(IllegalStateException.class, () -> caja.cerrarTurno(turnoAna[0], 1)));
        probar("PR-16", "BR-6: dinero contado menor al esperado se registra como faltante", () -> {
            cobrar(venta(cemento, 1), turnoLuis[0]);
            caja.cerrarTurno(turnoLuis[0], 300);
            igual(turnoLuis[0].getDineroEsperado(), 320, "esperado");
            igual(turnoLuis[0].getDiferencia(), -20, "diferencia");
            exigir(turnoLuis[0].clasificarDiferencia() == ResultadoCorte.FALTANTE, "clasificación");
        });
        probar("PR-17", "BR-6: dinero contado mayor al esperado se registra como sobrante", () -> {
            CorteCaja t = caja.abrirTurno(luis, 100);
            caja.cerrarTurno(t, 130);
            igual(t.getDiferencia(), 30, "diferencia");
            exigir(t.clasificarDiferencia() == ResultadoCorte.SOBRANTE, "clasificación");
        });
        probar("PR-18", "Dinero contado negativo se rechaza", () -> {
            CorteCaja t = caja.abrirTurno(luis, 100);
            lanza(IllegalArgumentException.class, () -> caja.cerrarTurno(t, -5));
            caja.cerrarTurno(t, 100);
        });
        probar("PR-19", "R9: el comprobante no se genera con el turno abierto", () -> {
            CorteCaja t = caja.abrirTurno(luis, 10);
            lanza(IllegalStateException.class, () -> caja.generarComprobante(t));
            caja.cerrarTurno(t, 10);
        });
        probar("PR-20", "R9: el comprobante incluye folio, cajero, montos y resultado", () -> {
            String c = caja.generarComprobante(turnoAna[0]);
            exigir(c.contains(turnoAna[0].getFolio()), "folio");
            exigir(c.contains("Ana"), "cajero");
            exigir(c.contains("714.00"), "esperado");
            exigir(c.toLowerCase().contains("cuadrad"), "resultado");
        });

        PeriodoReporte periodo = new PeriodoReporte(hoy.minusDays(1), hoy, FiltroTransaccion.AMBAS);
        final ReporteVentas[] r = new ReporteVentas[1];
        probar("PR-21", "R1/R2: el reporte lista transacciones y calcula total, cantidad y promedio", () -> {
            sql("INSERT INTO Recargas(folio,numero_telefonico,compania,monto,fecha,id_usuario) VALUES('REC-1','5512345678','Telcel',50,'" + hoy + "'," + ana + ")");
            r[0] = rep.consultar(periodo, 5);
            ResumenReporte s = r[0].getResumen();
            exigir(s.getTransacciones() == 5, "transacciones=" + s.getTransacciones());
            igual(s.getTotal(), 240 + 12 + 12 + 120 + 50, "total");
            igual(s.getTicketPromedio(), s.getTotal() / 5, "promedio");
            igual(s.getEfectivo() + s.getTarjeta(), s.getTotal(), "desglose = total");
        });
        probar("PR-22", "R1: el filtro Solo recargas excluye las ventas", () -> {
            ReporteVentas x = rep.consultar(new PeriodoReporte(hoy, hoy, FiltroTransaccion.SOLO_RECARGAS), 5);
            exigir(x.getTransacciones().size() == 1, "n=" + x.getTransacciones().size());
        });
        probar("PR-23", "R1: el filtro Solo ventas excluye las recargas", () -> {
            ReporteVentas x = rep.consultar(new PeriodoReporte(hoy, hoy, FiltroTransaccion.SOLO_VENTAS), 5);
            exigir(x.getTransacciones().size() == 4, "n=" + x.getTransacciones().size());
        });
        probar("PR-24", "R1: un período sin movimientos devuelve reporte vacío", () -> {
            ReporteVentas x = rep.consultar(new PeriodoReporte(hoy.minusDays(30), hoy.minusDays(20), FiltroTransaccion.AMBAS), 5);
            exigir(x.isVacio(), "debía ser vacío");
            igual(x.getResumen().getTotal(), 0, "total");
        });
        probar("PR-25", "R3: más vendidos ordenados de mayor a menor y respeta el límite", () -> {
            List<ProductoVendido> m = r[0].getMasVendidos();
            exigir(m.get(0).getNombre().equals("Cemento"), "primero=" + m.get(0).getNombre());
            for (int i = 1; i < m.size(); i++) {
                exigir(m.get(i - 1).getUnidades() >= m.get(i).getUnidades(), "orden");
            }
            exigir(rep.consultar(periodo, 1).getMasVendidos().size() == 1, "límite");
        });
        probar("PR-29", "R5: se recupera el comprobante de una venta por folio", () -> {
            String folio = r[0].getTransacciones().stream().filter(i -> i.getTotal() == 240).findFirst().get().getFolio();
            var c = rep.buscarComprobante(folio);
            exigir(c.isPresent(), "no encontrado");
            exigir(FormatoReporte.comprobante(c.get()).contains("Cemento"), "sin producto");
        });
        probar("PR-30", "R5: un folio inexistente devuelve vacío", () ->
            exigir(!rep.buscarComprobante("NO-EXISTE").isPresent(), "debía ser vacío"));
        probar("PR-31", "Usuario inexistente señala NoSuchElementException (sin códigos -1)", () ->
            lanza(NoSuchElementException.class, () -> UsuarioABD.obtenerNombre(987654)));
        probar("PR-32", "Regresión: el FXML de Reportes no tiene valores que empiecen con $ sin escapar", () -> {
            String fxml = new String(Files.readAllBytes(Paths.get(args[0], "pantallas", "ReporteVentas.fxml")), "UTF-8");
            exigir(!java.util.regex.Pattern.compile("=\"\\$").matcher(fxml).find(), "valor con $ sin escapar");
        });
        probar("PR-42", "La ventana de Reportes ya no tiene botones de exportar", () -> {
            String fxml = new String(Files.readAllBytes(Paths.get(args[0], "pantallas", "ReporteVentas.fxml")), "UTF-8");
            exigir(!fxml.contains("Exportar") && !fxml.contains("alPresionarExportar"), "quedan botones de exportar");
        });
        probar("PR-33", "Formato de pesos con separador de miles", () ->
            exigir(FormatoReporte.pesos(1234.5).contains("1,234.50"), FormatoReporte.pesos(1234.5)));

        probar("PR-34", "Regresión: fechas guardadas como milisegundos se normalizan y aparecen en el reporte", () -> {
            sql("INSERT INTO Venta(folio,fecha,total_a_pagar,id_usuario) VALUES('LEGADO-1','1780812000000',30," + ana + ")");
            IniciarBD.inicializar();
            ReporteVentas x = rep.consultar(new PeriodoReporte(LocalDate.of(2026, 6, 7),
                    LocalDate.of(2026, 6, 7), FiltroTransaccion.SOLO_VENTAS), 5);
            exigir(x.getTransacciones().stream().anyMatch(i -> i.getFolio().equals("LEGADO-1")), "no apareció");
        });

        // Conteo de efectivo por billetes y monedas (PSP2, historia de usuario 5)
        final CorteCaja[] turnoConteo = new CorteCaja[1];
        probar("PR-35", "Conteo: subtotales por denominación, por sección y total de billetes y monedas", () -> {
            ConteoEfectivo c = new ConteoEfectivo();
            c.establecerCantidad(Denominacion.BILLETE_500, 3);
            c.establecerCantidad(Denominacion.BILLETE_100, 2);
            c.establecerCantidad(Denominacion.BILLETE_50, 1);
            c.establecerCantidad(Denominacion.MONEDA_10, 4);
            c.establecerCantidad(Denominacion.MONEDA_5, 5);
            c.establecerCantidad(Denominacion.MONEDA_1, 2);
            c.establecerCantidad(Denominacion.MONEDA_50_CENTAVOS, 1);
            igual(c.calcularSubtotal(Denominacion.BILLETE_500), 1500, "subtotal $500");
            igual(c.calcularSubtotal(Denominacion.Tipo.BILLETE), 1750, "billetes");
            igual(c.calcularSubtotal(Denominacion.Tipo.MONEDA), 67.5, "monedas");
            igual(c.calcularTotal(), 1817.5, "total");
            exigir(!c.estaVacio() && new ConteoEfectivo().estaVacio(), "vacío");
        });
        probar("PR-36", "Conteo: una cantidad negativa se rechaza", () ->
            lanza(IllegalArgumentException.class, () ->
                new ConteoEfectivo().establecerCantidad(Denominacion.BILLETE_20, -1)));
        probar("PR-37", "Conteo: texto vacío cuenta como cero; negativos, decimales y letras se rechazan", () -> {
            java.util.Map<Denominacion, String> textos = new java.util.EnumMap<>(Denominacion.class);
            textos.put(Denominacion.BILLETE_20, "");
            textos.put(Denominacion.MONEDA_1, "7");
            igual(ConteoEfectivo.desdeTextos(textos).calcularTotal(), 7, "total con vacío");
            for (String malo : new String[] {"-3", "2.5", "abc", "100000"}) {
                textos.put(Denominacion.BILLETE_50, malo);
                lanza(IllegalArgumentException.class, () -> ConteoEfectivo.desdeTextos(textos));
            }
        });
        probar("PR-38", "Cierre con conteo: el total del conteo es el dinero contado y el desglose se guarda", () -> {
            CorteCaja t = caja.abrirTurno(luis, 200);
            ConteoEfectivo c = new ConteoEfectivo();
            c.establecerCantidad(Denominacion.BILLETE_100, 1);
            c.establecerCantidad(Denominacion.BILLETE_50, 2);
            caja.cerrarTurno(t, c);
            igual(t.getDineroContado(), 200, "contado");
            exigir(t.clasificarDiferencia() == ResultadoCorte.CUADRADO, "clasificación");
            ConteoEfectivo guardado = ConteoEfectivoABD.obtener(t.getIdCorte());
            exigir(guardado.obtenerCantidad(Denominacion.BILLETE_50) == 2, "cantidad guardada");
            igual(guardado.calcularTotal(), 200, "total guardado");
            lanza(IllegalStateException.class, () -> caja.cerrarTurno(t, c));
            turnoConteo[0] = t;
        });
        probar("PR-39", "R9: el comprobante muestra el desglose de billetes y monedas con sus subtotales", () -> {
            String c = caja.generarComprobante(turnoConteo[0]);
            exigir(c.contains("DESGLOSE DEL EFECTIVO CONTADO"), "encabezado del desglose");
            exigir(c.contains("$50") && c.contains("Subtotal billetes") && c.contains("200.00"), c);
        });
        probar("PR-40", "Un corte cerrado sin desglose (versión anterior) sigue mostrando comprobante sin desglose", () -> {
            exigir(ConteoEfectivoABD.obtener(turnoAna[0].getIdCorte()).estaVacio(), "debe estar vacío");
            exigir(!caja.generarComprobante(turnoAna[0]).contains("DESGLOSE"), "no debe haber desglose");
        });
        probar("PR-41", "BR-6 con conteo: un conteo menor al esperado se registra como faltante", () -> {
            CorteCaja t = caja.abrirTurno(luis, 100);
            ConteoEfectivo c = new ConteoEfectivo();
            c.establecerCantidad(Denominacion.BILLETE_50, 1);
            caja.cerrarTurno(t, c);
            igual(t.getDiferencia(), -50, "diferencia");
            exigir(t.clasificarDiferencia() == ResultadoCorte.FALTANTE, "clasificación");
        });

        Path salida = Paths.get(args[1]);
        Files.write(salida, RESULTADOS, java.nio.charset.StandardCharsets.UTF_8);
        System.out.println("TOTAL=" + RESULTADOS.size() + " FALLIDAS=" + fallos);
        System.exit(fallos == 0 ? 0 : 1);
    }
}
