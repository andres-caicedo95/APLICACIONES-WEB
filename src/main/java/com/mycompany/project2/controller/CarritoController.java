package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Domicilios;
import com.mycompany.project2.entities.Factura;
import com.mycompany.project2.entities.Producto;
import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.model.ItemCarrito;
import com.mycompany.project2.services.DomiciliosFacadeLocal;
import com.mycompany.project2.services.FacturaFacadeLocal;
import com.mycompany.project2.services.ProductoFacadeLocal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@Named
@SessionScoped
public class CarritoController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProductoFacadeLocal productoFacade;
    @EJB
    private FacturaFacadeLocal facturaFacade;

    @EJB
    private DomiciliosFacadeLocal domiciliosFacade;

    private Map<Integer, ItemCarrito> items = new LinkedHashMap<>();
    private List<Producto> catalogo;
    private int cantidad = 1;

    @PostConstruct
    public void init() {
        catalogo = productoFacade.findAll();
    }

    public void agregarAlCarrito(Producto producto) {
        Integer id = producto.getIdProducto();
        if (items.containsKey(id)) {
            items.get(id).incrementarCantidad();
        } else {
            ItemCarrito item = new ItemCarrito(
                    id,
                    producto.getNombreProducto(),
                    producto.getValorProducto() // String
            );
            items.put(id, item);
        }
    }

    public void eliminarDelCarrito(Integer idProducto) {
        items.remove(idProducto);
    }

    public void vaciarCarrito() {
        items.clear();
    }

    private String direccionEntrega;

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String confirmarCompra() {

        try {
            // Obtener usuario logueado
            Usuario cliente = (Usuario) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("usuarioLogueado");

            if (cliente == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Debe iniciar sesión para continuar.", ""));
                return null;
            }

            // 1. Crear factura
            Factura factura = new Factura();
            factura.setFechaFactura(new Date());
            factura.setUsuarioIDUSUARIOVENDEDOR(cliente); // según tu BD
            facturaFacade.create(factura);

            // 2. Crear domicilio
            Domicilios domicilio = new Domicilios();
            domicilio.setFechaDomicilio(new Date());
            domicilio.setDirecccionDomicilio(this.direccionEntrega);
            //domicilio.setUsuarioIDUSUARIODOMICILIO(cliente); // cliente solicitante
            domicilio.setEstado("PENDIENTE");
            domicilio.setFacturaIDFACTURA(factura);

            // De momento lat/lon se calculan en punto F
            domicilio.setLatitud(null);
            domicilio.setLongitud(null);

            domiciliosFacade.create(domicilio);

            // 3. Vaciar carrito
            vaciarCarrito();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Pedido confirmado exitosamente", ""));

            // 4. Redirigir
            return "/views/cliente/misPedidos.xhtml?faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al confirmar compra", ""));
            return null;
        }
    }

    // Getters
    public List<ItemCarrito> getItems() {
        return new ArrayList<>(items.values());
    }

    public double getTotal() {
        double total = 0.0;
        for (ItemCarrito item : items.values()) {
            total += item.getSubtotal();
        }
        return total;
    }

    public int getTotalItems() {
        int total = 0;
        for (ItemCarrito item : items.values()) {
            total += item.getCantidad();
        }
        return total;
    }

    public List<Producto> getCatalogo() {
        return catalogo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
