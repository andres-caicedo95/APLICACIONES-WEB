package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Producto;
import com.mycompany.project2.services.*;
import java.text.SimpleDateFormat;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

// Nuevas importaciones de JFreeChart y AWT para gráficos en servidor
import java.awt.Color;
import java.awt.Font; // Importación de AWT Font
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import com.itextpdf.text.Image; // Importación de iText Image

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.StreamedContent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.chart.*;

// Importaciones para iText (PDF)
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.jfree.chart.ChartUtils;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    // ✅ Inyectar SIEMPRE las interfaces Local
    @EJB
    private ClienteFacadeLocal clienteFacade;
    @EJB
    private ProductoFacadeLocal productoFacade;
    @EJB
    private PedidoFacadeLocal pedidoFacade;
    @EJB
    private PedidoItemFacadeLocal pedidoItemFacade;
    @EJB
    private DomiciliosFacadeLocal domiciliosFacade;
    @EJB
    private FacturaFacadeLocal facturaFacade;
    @EJB
    private DescuentosFacadeLocal descuentosFacade;
    @EJB
    private PrivilegiosUsuariosFacadeLocal privilegiosFacade;

    // Modelos de gráficos (Tus atributos existentes)
    private PieChartModel clientesPorEstado;
    private BarChartModel productosPorCategoria;
    private BarChartModel topStockProductos;
    private LineChartModel ventasDiarias;
    private PieChartModel pedidosPorEstado;
    private PieChartModel domiciliosPorEstado;
    private BarChartModel facturasPorVendedor;
    private BarChartModel topProductosVendidos;

    // ✅ Nuevo atributo para manejar las descargas
    private StreamedContent archivoDescarga;

    @PostConstruct
    public void init() {
        buildClientesPorEstado();
        buildProductosPorCategoria();
        buildStockPorProducto();
        buildVentasDiarias();
        buildPedidosPorEstado();
        buildDomiciliosPorEstado();
        buildFacturasPorVendedor();
        buildTopProductosVendidos();
    }

    // ----------------------------------------------------------------------
    // MÉTODOS BUILD (Existentes - Se mantienen iguales)
    // ----------------------------------------------------------------------
    private void buildClientesPorEstado() {
        clientesPorEstado = new PieChartModel();
        long activos = clienteFacade.countByEstado("ACTIVO");
        long inactivos = clienteFacade.countByEstado("INACTIVO");
        clientesPorEstado.set("Activos", activos);
        clientesPorEstado.set("Inactivos", inactivos);
        clientesPorEstado.setTitle("Clientes por Estado");
        clientesPorEstado.setLegendPosition("w");
        clientesPorEstado.setShowDataLabels(true);
    }

    private void buildProductosPorCategoria() {
        productosPorCategoria = new BarChartModel();
        ChartSeries series = new ChartSeries();
        series.setLabel("Categorías");
        Map<String, Long> data = productoFacade.countByCategoria();
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            series.set(entry.getKey(), entry.getValue());
        }
        productosPorCategoria.addSeries(series);
        productosPorCategoria.setTitle("Productos por Categoría");
        productosPorCategoria.setLegendPosition("ne");
        productosPorCategoria.setAnimate(true);
    }

    private void buildStockPorProducto() {
        topStockProductos = new BarChartModel();
        ChartSeries series = new ChartSeries();
        series.setLabel("Stock");
        List<Producto> productos = productoFacade.findTopByStock(5);
        for (Producto p : productos) {
            series.set(p.getNombreProducto(), p.getStockProduccto());
        }
        topStockProductos.addSeries(series);
        topStockProductos.setTitle("Top 5 Productos con más Stock");
        topStockProductos.setLegendPosition("ne");
        topStockProductos.setAnimate(true);
    }

    private void buildVentasDiarias() {
        ventasDiarias = new LineChartModel();
        LineChartSeries serie = new LineChartSeries();
        serie.setLabel("Ventas");
        Calendar cal = Calendar.getInstance();
        Date hasta = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date desde = cal.getTime();
        Map<Date, BigDecimal> ventas = pedidoItemFacade.totalVentasPorDia(desde, hasta);
        for (Map.Entry<Date, BigDecimal> entry : ventas.entrySet()) {
            serie.set(entry.getKey().toString(), entry.getValue());
        }
        ventasDiarias.addSeries(serie);
        ventasDiarias.setTitle("Ventas Diarias (Últimos 30 días)");
        ventasDiarias.setLegendPosition("e");
        ventasDiarias.setAnimate(true);
    }

    private void buildPedidosPorEstado() {
        pedidosPorEstado = new PieChartModel();
        Map<String, Long> data = pedidoFacade.countByEstado();
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            pedidosPorEstado.set(entry.getKey(), entry.getValue());
        }
        pedidosPorEstado.setTitle("Pedidos por Estado");
        pedidosPorEstado.setLegendPosition("w");
        pedidosPorEstado.setShowDataLabels(true);
    }

    private void buildDomiciliosPorEstado() {
        domiciliosPorEstado = new PieChartModel();
        Map<String, Long> data = domiciliosFacade.countByEstado();
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            domiciliosPorEstado.set(entry.getKey(), entry.getValue());
        }
        domiciliosPorEstado.setTitle("Domicilios por Estado");
        domiciliosPorEstado.setLegendPosition("w");
        domiciliosPorEstado.setShowDataLabels(true);
    }

    private void buildFacturasPorVendedor() {
        facturasPorVendedor = new BarChartModel();
        ChartSeries series = new ChartSeries();
        series.setLabel("Facturas");
        Calendar cal = Calendar.getInstance();
        Date hasta = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date desde = cal.getTime();
        Map<String, Long> data = facturaFacade.countByVendedor(desde, hasta);
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            series.set(entry.getKey(), entry.getValue());
        }
        facturasPorVendedor.addSeries(series);
        facturasPorVendedor.setTitle("Facturas por Vendedor (30 días)");
        facturasPorVendedor.setLegendPosition("ne");
    }

    private void buildTopProductosVendidos() {
        topProductosVendidos = new BarChartModel();
        ChartSeries series = new ChartSeries();
        series.setLabel("Más Vendidos");
        List<Object[]> top = pedidoItemFacade.topProducts(5);
        for (Object[] r : top) {
            series.set(r[0].toString(), ((Number) r[1]).longValue());
        }
        topProductosVendidos.addSeries(series);
        topProductosVendidos.setTitle("Top 5 Productos Más Vendidos");
        topProductosVendidos.setLegendPosition("ne");
    }

    // ----------------------------------------------------------------------
    // GETTERS (Existentes)
    // ----------------------------------------------------------------------
    public PieChartModel getClientesPorEstado() {
        return clientesPorEstado;
    }

    public BarChartModel getProductosPorCategoria() {
        return productosPorCategoria;
    }

    public BarChartModel getTopStockProductos() {
        return topStockProductos;
    }

    public LineChartModel getVentasDiarias() {
        return ventasDiarias;
    }

    public PieChartModel getPedidosPorEstado() {
        return pedidosPorEstado;
    }

    public PieChartModel getDomiciliosPorEstado() {
        return domiciliosPorEstado;
    }

    public BarChartModel getFacturasPorVendedor() {
        return facturasPorVendedor;
    }

    public BarChartModel getTopProductosVendidos() {
        return topProductosVendidos;
    }

    // ✅ Getter para el atributo de descarga
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // ----------------------------------------------------------------------
    // MÉTODOS DE GENERACIÓN DE IMÁGENES DE GRÁFICOS (JFreeChart)
    // ----------------------------------------------------------------------
    /**
     * Genera un gráfico de torta (Pie Chart) en formato PNG.
     */
    private byte[] generarGraficoTorta(String titulo, Map<String, Long> data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            DefaultPieDataset dataset = new DefaultPieDataset();
            data.forEach(dataset::setValue);

            JFreeChart chart = ChartFactory.createPieChart(
                    titulo,
                    dataset,
                    true, // leyenda
                    true, // tooltips
                    false // urls
            );
            chart.setBackgroundPaint(Color.white);

            ChartUtils.writeChartAsPNG(baos, chart, 600, 400);
            return baos.toByteArray();
        } catch (IOException e) {
            System.err.println("Error al generar gráfico Torta: " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera un gráfico de barras (Bar Chart) en formato PNG.
     */
    private byte[] generarGraficoBarras(String titulo, Map<String, Long> data, String ejeX, String ejeY) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            data.forEach((key, value) -> dataset.addValue(value, ejeY, key));

            JFreeChart chart = ChartFactory.createBarChart(
                    titulo,
                    ejeX, // Eje X Label
                    ejeY, // Eje Y Label
                    dataset
            );
            chart.setBackgroundPaint(Color.white);

            ChartUtils.writeChartAsPNG(baos, chart, 600, 400);
            return baos.toByteArray();
        } catch (IOException e) {
            System.err.println("Error al generar gráfico Barras: " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera un gráfico de línea (Line Chart) para series de tiempo en formato
     * PNG.
     */
    private byte[] generarGraficoLinea(String titulo, Map<Date, BigDecimal> data, String ejeY) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            TimeSeries series = new TimeSeries("Ventas");

            // Ordenar por fecha y añadir al TimeSeries
            data.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> series.add(new Day(entry.getKey()), entry.getValue()));

            TimeSeriesCollection dataset = new TimeSeriesCollection(series);

            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    titulo,
                    "Fecha", // Eje X Label
                    ejeY, // Eje Y Label
                    dataset,
                    true, true, false
            );
            chart.setBackgroundPaint(Color.white);

            ChartUtils.writeChartAsPNG(baos, chart, 700, 400);
            return baos.toByteArray();
        } catch (IOException e) {
            System.err.println("Error al generar gráfico Línea: " + e.getMessage());
            return null;
        }
    }

    // ----------------------------------------------------------------------
    // ✅ MÉTODOS DE DESCARGA PARA CADA GRÁFICO (PDF y Texto/CSV) (Mismos nombres)
    // ----------------------------------------------------------------------
    // --- 1. Clientes por Estado ---
    public void descargarClientesPorEstadoPDF() {
        Map<String, Long> data = new HashMap<>();
        data.put("Activos", clienteFacade.countByEstado("ACTIVO"));
        data.put("Inactivos", clienteFacade.countByEstado("INACTIVO"));
        generarDescarga("ClientesPorEstado", "application/pdf", generarContenidoClientesPDF(data));
    }

    public void descargarClientesPorEstadoTexto() {
        Map<String, Long> data = new HashMap<>();
        data.put("Activos", clienteFacade.countByEstado("ACTIVO"));
        data.put("Inactivos", clienteFacade.countByEstado("INACTIVO"));
        generarDescarga("ClientesPorEstado", "text/csv", generarContenidoClientesTexto(data));
    }

    // --- 2. Productos por Categoría ---
    public void descargarProductosPorCategoriaPDF() {
        Map<String, Long> data = productoFacade.countByCategoria();
        generarDescarga("ProductosPorCategoria", "application/pdf", generarContenidoProductosCategoriaPDF(data));
    }

    public void descargarProductosPorCategoriaTexto() {
        Map<String, Long> data = productoFacade.countByCategoria();
        generarDescarga("ProductosPorCategoria", "text/csv", generarContenidoProductosCategoriaTexto(data));
    }

    // --- 3. Top 5 Productos con más Stock ---
    public void descargarTopStockProductosPDF() {
        List<Producto> dataList = productoFacade.findTopByStock(5);
        Map<String, Long> dataMap = new LinkedHashMap<>();
        dataList.forEach(p -> dataMap.put(p.getNombreProducto(), p.getStockProduccto()));
        generarDescarga("TopStockProductos", "application/pdf", generarContenidoTopStockProductosPDF(dataMap, dataList));
    }

    public void descargarTopStockProductosTexto() {
        List<Producto> data = productoFacade.findTopByStock(5);
        generarDescarga("TopStockProductos", "text/csv", generarContenidoTopStockProductosTexto(data));
    }

    // --- 4. Ventas Diarias (Últimos 30 días) ---
    public void descargarVentasDiariasPDF() {
        Calendar cal = Calendar.getInstance();
        Date hasta = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date desde = cal.getTime();
        Map<Date, BigDecimal> data = pedidoItemFacade.totalVentasPorDia(desde, hasta);
        generarDescarga("VentasDiarias", "application/pdf", generarContenidoVentasDiariasPDF(data));
    }

    public void descargarVentasDiariasTexto() {
        Calendar cal = Calendar.getInstance();
        Date hasta = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date desde = cal.getTime();
        Map<Date, BigDecimal> data = pedidoItemFacade.totalVentasPorDia(desde, hasta);
        generarDescarga("VentasDiarias", "text/csv", generarContenidoVentasDiariasTexto(data));
    }

    // --- 5. Pedidos por Estado ---
    public void descargarPedidosPorEstadoPDF() {
        Map<String, Long> data = pedidoFacade.countByEstado();
        generarDescarga("PedidosPorEstado", "application/pdf", generarContenidoPedidosEstadoPDF(data));
    }

    public void descargarPedidosPorEstadoTexto() {
        Map<String, Long> data = pedidoFacade.countByEstado();
        generarDescarga("PedidosPorEstado", "text/csv", generarContenidoPedidosEstadoTexto(data));
    }

    // --- 6. Domicilios por Estado ---
    public void descargarDomiciliosPorEstadoPDF() {
        Map<String, Long> data = domiciliosFacade.countByEstado();
        generarDescarga("DomiciliosPorEstado", "application/pdf", generarContenidoDomiciliosEstadoPDF(data));
    }

    public void descargarDomiciliosPorEstadoTexto() {
        Map<String, Long> data = domiciliosFacade.countByEstado();
        generarDescarga("DomiciliosPorEstado", "text/csv", generarContenidoDomiciliosEstadoTexto(data));
    }

    // --- 7. Facturas por Vendedor (30 días) ---
    public void descargarFacturasPorVendedorPDF() {
        Calendar cal = Calendar.getInstance();
        Date hasta = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date desde = cal.getTime();
        Map<String, Long> data = facturaFacade.countByVendedor(desde, hasta);
        generarDescarga("FacturasPorVendedor", "application/pdf", generarContenidoFacturasVendedorPDF(data));
    }

    public void descargarFacturasPorVendedorTexto() {
        Calendar cal = Calendar.getInstance();
        Date hasta = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date desde = cal.getTime();
        Map<String, Long> data = facturaFacade.countByVendedor(desde, hasta);
        generarDescarga("FacturasPorVendedor", "text/csv", generarContenidoFacturasVendedorTexto(data));
    }

    // --- 8. Top 5 Productos Más Vendidos ---
    public void descargarTopProductosVendidosPDF() {
        List<Object[]> dataList = pedidoItemFacade.topProducts(5);
        Map<String, Long> dataMap = new LinkedHashMap<>();
        dataList.forEach(r -> dataMap.put(r[0].toString(), ((Number) r[1]).longValue()));
        generarDescarga("TopProductosVendidos", "application/pdf", generarContenidoTopProductosVendidosPDF(dataMap, dataList));
    }

    public void descargarTopProductosVendidosTexto() {
        List<Object[]> data = pedidoItemFacade.topProducts(5);
        generarDescarga("TopProductosVendidos", "text/csv", generarContenidoTopProductosVendidosTexto(data));
    }

    // ----------------------------------------------------------------------
// ✅ NUEVOS MÉTODOS DE ACCIÓN DE DESCARGA (Listado de Productos Completo)
// ----------------------------------------------------------------------
    /**
     * Acción para descargar el listado completo de productos en formato PDF,
     * ordenado por categoría.
     */
    public void descargarListadoProductosPDF() {
        // Asumiendo que productoFacade.findAll() existe y trae todos los productos
        List<Producto> data = productoFacade.findAll();

        // Llama al método genérico de descarga, pasando el contenido PDF generado
        // No hay gráfico, por lo que la lógica de generación sabrá manejarlo.
        generarDescarga("ListadoProductos", "application/pdf", generarContenidoListadoProductosPDF(data));
    }

    /**
     * Acción para descargar el listado completo de productos en formato CSV,
     * ordenado por categoría.
     */
    public void descargarListadoProductosTexto() {
        List<Producto> data = productoFacade.findAll();
        generarDescarga("ListadoProductos", "text/csv", generarContenidoListadoProductosTexto(data));
    }

    /**
     * NUEVO: Genera contenido PDF para el listado de productos, agrupado por
     * categoría.
     */
    private byte[] generarContenidoListadoProductosPDF(List<Producto> data) {
        // Agrupar los productos por el nombre de su categoría y ordenar por nombre
        Map<String, List<Producto>> grouped = data.stream()
                .sorted(Comparator.comparing(Producto::getNombreProducto))
                .collect(Collectors.groupingBy(p -> p.getCategoriaProducto().name()));

        List<String> lines = new ArrayList<>();

        // Encabezado
        lines.add("ID | CÓDIGO | NOMBRE | STOCK | PRECIO | ESTADO");
        lines.add("=================================================================");

        // Iterar sobre las categorías ordenadas alfabéticamente
        grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    lines.add("\n--- CATEGORÍA: " + entry.getKey() + " ---");
                    for (Producto p : entry.getValue()) {
                        lines.add(String.format("%d | %s | %s | %d | $%s | %s",
                                p.getIdProducto(),
                                p.getCodigoProducto(),
                                p.getNombreProducto(),
                                p.getStockProduccto(),
                                Double.parseDouble(p.getValorProducto()),
                                p.getEstadoProducto()));
                    }
                });

        // Llamamos al helper general que maneja la creación final del PDF (se asume que acepta null para la imagen)
        return generarPDFGenerico(
                "Listado Completo de Productos (Ordenado por Categoría)",
                lines.toArray(new String[0]),
                null // Se pasa null porque no hay imagen de gráfico para este listado
        );
    }

    // ----------------------------------------------------------------------
    // MÉTODOS DE SOPORTE GENERALES Y LÓGICA DE GENERACIÓN
    // ----------------------------------------------------------------------
    /**
     * Método genérico para generar el objeto StreamedContent y manejar
     * mensajes. (No se modifica)
     */
    private void generarDescarga(String baseName, String contentType, byte[] fileBytes) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (fileBytes == null || fileBytes.length == 0) {
                throw new Exception("El contenido del archivo es nulo o vacío.");
            }
            InputStream stream = new ByteArrayInputStream(fileBytes);

            // Determinar extensión del archivo
            String extension = contentType.endsWith("pdf") ? ".pdf" : (contentType.endsWith("csv") ? ".csv" : ".txt");

            archivoDescarga = DefaultStreamedContent.builder()
                    .contentType(contentType)
                    .name("Informe_" + baseName + extension)
                    .stream(() -> stream)
                    .build();

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Descarga lista", "El informe de " + baseName + " está listo para descargar."));

        } catch (Exception e) {
            archivoDescarga = null;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al generar informe", "Error: " + e.getMessage()));
            // e.printStackTrace(); // Descomentar para debug
        }
    }

    // ======================================================================
    // ✅ MÉTODOS DE GENERACIÓN DE CONTENIDO PDF (CORREGIDOS PARA INCLUIR GRÁFICO)
    // ======================================================================
    private byte[] generarContenidoClientesPDF(Map<String, Long> data) {
        // Generar Imagen: Gráfico de Torta
        byte[] imagenGrafico = generarGraficoTorta("Clientes por Estado", data);

        return generarPDFGenerico(
                "Informe Clientes por Estado",
                data.entrySet().stream()
                        .map(e -> e.getKey() + ": " + e.getValue() + " clientes")
                        .toArray(String[]::new),
                imagenGrafico // Pasar imagen al Helper
        );
    }

    private byte[] generarContenidoProductosCategoriaPDF(Map<String, Long> data) {
        // Generar Imagen: Gráfico de Barras
        byte[] imagenGrafico = generarGraficoBarras("Productos por Categoría", data, "Categoría", "Cantidad");

        return generarPDFGenerico(
                "Informe Productos por Categoría",
                data.entrySet().stream()
                        .map(e -> e.getKey() + ": " + e.getValue() + " productos")
                        .toArray(String[]::new),
                imagenGrafico
        );
    }

    private byte[] generarContenidoTopStockProductosPDF(Map<String, Long> dataMap, List<Producto> dataList) {
        // Generar Imagen: Gráfico de Barras
        byte[] imagenGrafico = generarGraficoBarras("Top 5 Productos con más Stock", dataMap, "Producto", "Stock");

        return generarPDFGenerico(
                "Informe Top 5 Productos con más Stock",
                dataList.stream()
                        .map(p -> p.getNombreProducto() + ": " + p.getStockProduccto() + " unidades")
                        .toArray(String[]::new),
                imagenGrafico
        );
    }

    private byte[] generarContenidoVentasDiariasPDF(Map<Date, BigDecimal> data) {
        // Generar Imagen: Gráfico de Línea
        byte[] imagenGrafico = generarGraficoLinea("Ventas Diarias (30 días)", data, "Venta Total ($)");

        return generarPDFGenerico(
                "Informe Ventas Diarias (30 días)",
                data.entrySet().stream()
                        .map(e -> new SimpleDateFormat("dd/MM/yyyy").format(e.getKey()) + ": $" + e.getValue())
                        .toArray(String[]::new),
                imagenGrafico
        );
    }

    private byte[] generarContenidoPedidosEstadoPDF(Map<String, Long> data) {
        // Generar Imagen: Gráfico de Torta
        byte[] imagenGrafico = generarGraficoTorta("Pedidos por Estado", data);

        return generarPDFGenerico(
                "Informe Pedidos por Estado",
                data.entrySet().stream()
                        .map(e -> e.getKey() + ": " + e.getValue() + " pedidos")
                        .toArray(String[]::new),
                imagenGrafico
        );
    }

    private byte[] generarContenidoDomiciliosEstadoPDF(Map<String, Long> data) {
        // Generar Imagen: Gráfico de Torta
        byte[] imagenGrafico = generarGraficoTorta("Domicilios por Estado", data);

        return generarPDFGenerico(
                "Informe Domicilios por Estado",
                data.entrySet().stream()
                        .map(e -> e.getKey() + ": " + e.getValue() + " domicilios")
                        .toArray(String[]::new),
                imagenGrafico
        );
    }

    private byte[] generarContenidoFacturasVendedorPDF(Map<String, Long> data) {
        // Generar Imagen: Gráfico de Barras
        byte[] imagenGrafico = generarGraficoBarras("Facturas por Vendedor (30 días)", data, "Vendedor", "Facturas");

        return generarPDFGenerico(
                "Informe Facturas por Vendedor (30 días)",
                data.entrySet().stream()
                        .map(e -> e.getKey() + ": " + e.getValue() + " facturas")
                        .toArray(String[]::new),
                imagenGrafico
        );
    }

    private byte[] generarContenidoTopProductosVendidosPDF(Map<String, Long> dataMap, List<Object[]> dataList) {
        // Generar Imagen: Gráfico de Barras
        byte[] imagenGrafico = generarGraficoBarras("Top 5 Productos Más Vendidos", dataMap, "Producto", "Unidades Vendidas");

        return generarPDFGenerico(
                "Informe Top 5 Productos Más Vendidos",
                dataList.stream()
                        .map(r -> r[0].toString() + ": " + ((Number) r[1]).longValue() + " unidades vendidas")
                        .toArray(String[]::new),
                imagenGrafico
        );
    }

    /**
     * Helper para crear un PDF simple con iText. ✅ ACEPTA EL NUEVO ARGUMENTO
     * byte[] imagenBytes
     */
    private byte[] generarPDFGenerico(String titulo, String[] lineas, byte[] imagenBytes) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            com.itextpdf.text.Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            com.itextpdf.text.Font fontContent = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            document.add(new Paragraph(titulo, fontTitle));
            document.add(new Paragraph("Generado el: " + new Date().toString(), fontContent));
            document.add(new Paragraph("\n"));

            // 🚨 INCORPORACIÓN DEL GRÁFICO
            if (imagenBytes != null) {
                Image image = Image.getInstance(imagenBytes);
                // Ajustar el tamaño de la imagen para que quepa en el PDF
                image.scaleToFit(500, 500);
                document.add(image);
                document.add(new Paragraph("\n"));
            }

            for (String linea : lineas) {
                document.add(new Paragraph("• " + linea, fontContent));
            }

            document.close();
            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ======================================================================
    // MÉTODOS DE GENERACIÓN DE CONTENIDO DE TEXTO/CSV (Se mantienen iguales)
    // ======================================================================
    private byte[] generarContenidoClientesTexto(Map<String, Long> data) {
        StringBuilder sb = new StringBuilder("Estado,Cantidad\n");
        data.forEach((estado, cantidad) -> sb.append(estado).append(",").append(cantidad).append("\n"));
        return sb.toString().getBytes();
    }

    private byte[] generarContenidoProductosCategoriaTexto(Map<String, Long> data) {
        StringBuilder sb = new StringBuilder("Categoria,Cantidad\n");
        data.forEach((cat, count) -> sb.append(cat).append(",").append(count).append("\n"));
        return sb.toString().getBytes();
    }

    private byte[] generarContenidoTopStockProductosTexto(List<Producto> data) {
        StringBuilder sb = new StringBuilder("Producto,Stock\n");
        data.forEach(p -> sb.append(p.getNombreProducto()).append(",").append(p.getStockProduccto()).append("\n"));
        return sb.toString().getBytes();
    }

    private byte[] generarContenidoVentasDiariasTexto(Map<Date, BigDecimal> data) {
        StringBuilder sb = new StringBuilder("Fecha,Venta Total\n");
        data.forEach((date, total) -> sb.append(new SimpleDateFormat("yyyy-MM-dd").format(date)).append(",").append(total).append("\n"));
        return sb.toString().getBytes();
    }

    private byte[] generarContenidoPedidosEstadoTexto(Map<String, Long> data) {
        StringBuilder sb = new StringBuilder("Estado,Cantidad\n");
        data.forEach((estado, cantidad) -> sb.append(estado).append(",").append(cantidad).append("\n"));
        return sb.toString().getBytes();
    }

    private byte[] generarContenidoDomiciliosEstadoTexto(Map<String, Long> data) {
        StringBuilder sb = new StringBuilder("Estado,Cantidad\n");
        data.forEach((estado, cantidad) -> sb.append(estado).append(",").append(cantidad).append("\n"));
        return sb.toString().getBytes();
    }

    private byte[] generarContenidoFacturasVendedorTexto(Map<String, Long> data) {
        StringBuilder sb = new StringBuilder("Vendedor,Facturas Emitidas\n");
        data.forEach((vendedor, count) -> sb.append(vendedor).append(",").append(count).append("\n"));
        return sb.toString().getBytes();
    }

    private byte[] generarContenidoTopProductosVendidosTexto(List<Object[]> data) {
        StringBuilder sb = new StringBuilder("Producto,Unidades Vendidas\n");
        data.forEach(r -> sb.append(r[0].toString()).append(",").append(((Number) r[1]).longValue()).append("\n"));
        return sb.toString().getBytes();
    }

    /**
     * NUEVO: Genera contenido CSV para el listado de productos, ordenado por
     * categoría.
     */
    private byte[] generarContenidoListadoProductosTexto(List<Producto> data) {
        StringBuilder sb = new StringBuilder("ID,CÓDIGO,NOMBRE,STOCK,PRECIO,CATEGORÍA,ESTADO\n");

        // Ordenar primero por categoría y luego por nombre del producto (opcional, mejora la lectura)
        data.sort(Comparator
                .comparing((Producto p) -> p.getCategoriaProducto().name()) // Ordena por categoría
                .thenComparing(Producto::getNombreProducto)); // Luego por nombre

        for (Producto p : data) {
            sb.append(p.getIdProducto()).append(",");
            sb.append(p.getCodigoProducto()).append(",");
            sb.append(p.getNombreProducto()).append(",");
            sb.append(p.getStockProduccto()).append(",");
            sb.append(p.getValorProducto()).append(",");
            sb.append(p.getCategoriaProducto()).append(",");
            sb.append(p.getEstadoProducto()).append("\n");
        }
        return sb.toString().getBytes();
    }
}
