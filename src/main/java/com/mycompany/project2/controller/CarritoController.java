package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Domicilios;
import com.mycompany.project2.entities.Factura;
import com.mycompany.project2.entities.FacturaDetalle;
import com.mycompany.project2.entities.Producto;
import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.model.ItemCarrito;

import com.mycompany.project2.services.DomiciliosFacadeLocal;
import com.mycompany.project2.services.FacturaFacadeLocal;
import com.mycompany.project2.services.FacturaDetalleFacadeLocal;
import com.mycompany.project2.services.ProductoFacadeLocal;
import com.mycompany.project2.services.PdfFacturaService;
import com.mycompany.project2.services.EmailService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;

@Named
@SessionScoped
public class CarritoController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB private ProductoFacadeLocal productoFacade;
    @EJB private FacturaFacadeLocal facturaFacade;
    @EJB private FacturaDetalleFacadeLocal facturaDetalleFacade;
    @EJB private DomiciliosFacadeLocal domiciliosFacade;
    @EJB private PdfFacturaService pdfFacturaService;
    @EJB private EmailService emailService;

    private Map<Integer, ItemCarrito> items = new LinkedHashMap<>();
    private List<Producto> catalogo;
    private String direccionEntrega;
    private String selectedMetodoPago = "NEQUI";
    private int cantidad = 1;

    @PostConstruct
    public void init() {
        catalogo = productoFacade.findAll();
    }

    // =======================================================
    //               AGREGAR / ELIMINAR CARRITO
    // =======================================================
    public void agregarAlCarrito(Producto producto) {
        Integer id = producto.getIdProducto();
        if (items.containsKey(id)) {
            items.get(id).incrementarCantidad();
        } else {
            ItemCarrito item = new ItemCarrito(
                id,
                producto.getNombreProducto(),
                producto.getValorProducto()
            );
            items.put(id, item);
        }
    }

    public void eliminarDelCarrito(Integer id) {
        items.remove(id);
    }

    public void vaciarCarrito() {
        items.clear();
    }

    // =======================================================
    //               CONFIRMAR COMPRA
    // =======================================================
    public String confirmarCompra() {

        try {

            Usuario cliente = (Usuario) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("usuario");

            if (cliente == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Debe iniciar sesión para continuar.", ""));
                return null;
            }

            if (items.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "El carrito está vacío.", ""));
                return null;
            }

            // ===============================
            // 1. Crear factura
            // ===============================
            Factura factura = new Factura();
            factura.setFechaFactura(new Date());

            BigDecimal total = new BigDecimal(getTotal())
                    .setScale(2, RoundingMode.HALF_UP);

            factura.setTotalFactura(total);
            factura.setMetodoPago(selectedMetodoPago);
            factura.setEstadoFactura("COMPLETADA");
            factura.setUsuarioIDUSUARIOCLIENTE(cliente);

            facturaFacade.create(factura);

            // ===============================
            // 2. Crear detalles
            // ===============================
            for (ItemCarrito item : items.values()) {
                Producto p = productoFacade.find(item.getIdProducto());

                BigDecimal precio = new BigDecimal(
                    p.getValorProducto().replace(",", "").trim()
                );

                BigDecimal subtotal = precio.multiply(
                    new BigDecimal(item.getCantidad())
                );

                FacturaDetalle det = new FacturaDetalle();
                det.setFacturaIDFACTURA(factura);
                det.setProductoIDPRODUCTO(p);
                det.setCantidad(item.getCantidad());
                det.setPrecioUnitario(precio);
                det.setSubtotal(subtotal);

                facturaDetalleFacade.create(det);
            }

            // ===============================
            // 3. Crear domicilio
            // ===============================
            Domicilios d = new Domicilios();
            d.setFechaDomicilio(new Date());
            d.setDirecccionDomicilio(direccionEntrega);
            d.setEstado("PENDIENTE");
            d.setFacturaIDFACTURA(factura);

            domiciliosFacade.create(d);

            // ===============================
            // 4. Generar PDF
            // ===============================
            List<FacturaDetalle> detalles =
                facturaDetalleFacade.findByFactura(factura.getIdFactura());

            byte[] pdf = pdfFacturaService.generarFactura(factura, detalles);

            // ===============================
            // 5. Enviar PDF al correo
            // ===============================
            emailService.enviarCorreoConAdjunto(
                cliente.getCorreoUsuario(),
                "Factura #" + factura.getIdFactura(),
                "Gracias por tu compra. Te enviamos tu factura.",
                pdf,
                "Factura_" + factura.getIdFactura() + ".pdf"
            );

            vaciarCarrito();

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Compra realizada. La factura fue enviada a tu correo.", ""));

            return "/views/cliente/misPedidos.xhtml?faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error al procesar la compra: " + e.getMessage(), ""));
            return null;
        }
    }

    // =======================================================
    //               MIS PEDIDOS
    // =======================================================
    public List<Factura> getMisPedidos() {
        Usuario cliente = (Usuario) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("usuarioLogueado");

        if (cliente == null) return Collections.emptyList();

        List<Factura> all = facturaFacade.findAll();
        List<Factura> mine = new ArrayList<>();

        for (Factura f : all) {
            if (f.getUsuarioIDUSUARIOCLIENTE() != null &&
                f.getUsuarioIDUSUARIOCLIENTE().getIdUsuario()
                    .equals(cliente.getIdUsuario())) {
                mine.add(f);
            }
        }

        mine.sort(Comparator.comparing(Factura::getFechaFactura).reversed());
        return mine;
    }

    // =======================================================
    //               DESCARGAR FACTURA PDF
    // =======================================================
    public void descargarFactura(Integer idFactura) {
        try {
            Factura factura = facturaFacade.find(idFactura);
            List<FacturaDetalle> detalles =
                    facturaDetalleFacade.findByFactura(idFactura);

            byte[] pdf = pdfFacturaService.generarFactura(factura, detalles);

            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();

            ec.responseReset();
            ec.setResponseContentType("application/pdf");
            ec.setResponseHeader(
                "Content-Disposition",
                "attachment; filename=factura_" + idFactura + ".pdf"
            );
            ec.setResponseContentLength(pdf.length);

            OutputStream os = ec.getResponseOutputStream();
            os.write(pdf);
            os.flush();

            fc.responseComplete();

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "No se pudo descargar la factura.", ""));
        }
    }

    // =======================================================
    //               REENVIAR FACTURA POR CORREO
    // =======================================================
    public void reenviarFacturaPorCorreo(Integer idFactura) {
        try {
            Usuario cliente = (Usuario) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("usuarioLogueado");

            Factura factura = facturaFacade.find(idFactura);
            List<FacturaDetalle> detalles =
                    facturaDetalleFacade.findByFactura(idFactura);

            byte[] pdf = pdfFacturaService.generarFactura(factura, detalles);

            emailService.enviarCorreoConAdjunto(
                cliente.getCorreoUsuario(),
                "Factura #" + factura.getIdFactura(),
                "Aquí tienes nuevamente tu factura.",
                pdf,
                "Factura_" + factura.getIdFactura() + ".pdf"
            );

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Factura reenviada correctamente."));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Error reenviando factura."));
        }
    }

    // =======================================================
    // GETTERS / SETTERS
    // =======================================================
    public List<ItemCarrito> getItems() { return new ArrayList<>(items.values()); }
    public double getTotal() {
        return items.values().stream().mapToDouble(ItemCarrito::getSubtotal).sum();
    }

    public int getTotalItems() {
        return items.values().stream().mapToInt(ItemCarrito::getCantidad).sum();
    }

    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }

    public String getSelectedMetodoPago() { return selectedMetodoPago; }
    public void setSelectedMetodoPago(String selectedMetodoPago) { this.selectedMetodoPago = selectedMetodoPago; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public List<Producto> getCatalogo() { return catalogo; }
}
