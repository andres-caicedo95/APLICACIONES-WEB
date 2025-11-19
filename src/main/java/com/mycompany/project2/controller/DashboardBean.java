package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Producto;
import com.mycompany.project2.services.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.chart.*;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    // ✅ Inyectar SIEMPRE las interfaces Local
    @EJB private ClienteFacadeLocal clienteFacade;
    @EJB private ProductoFacadeLocal productoFacade;
    @EJB private PedidoFacadeLocal pedidoFacade;
    @EJB private PedidoItemFacadeLocal pedidoItemFacade;
    @EJB private DomiciliosFacadeLocal domiciliosFacade;
    @EJB private FacturaFacadeLocal facturaFacade;
    @EJB private DescuentosFacadeLocal descuentosFacade;
    @EJB private PrivilegiosUsuariosFacadeLocal privilegiosFacade;

    // Modelos de gráficos
    private PieChartModel clientesPorEstado;
    private BarChartModel productosPorCategoria;
    private BarChartModel topStockProductos;
    private LineChartModel ventasDiarias;
    private PieChartModel pedidosPorEstado;
    private PieChartModel domiciliosPorEstado;
    private BarChartModel facturasPorVendedor;
    private BarChartModel topProductosVendidos;

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

    // Getters para usar en dashboard.xhtml
    public PieChartModel getClientesPorEstado() { return clientesPorEstado; }
    public BarChartModel getProductosPorCategoria() { return productosPorCategoria; }
    public BarChartModel getTopStockProductos() { return topStockProductos; }
    public LineChartModel getVentasDiarias() { return ventasDiarias; }
    public PieChartModel getPedidosPorEstado() { return pedidosPorEstado; }
    public PieChartModel getDomiciliosPorEstado() { return domiciliosPorEstado; }
    public BarChartModel getFacturasPorVendedor() { return facturasPorVendedor; }
    public BarChartModel getTopProductosVendidos() { return topProductosVendidos; }
}
