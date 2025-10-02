package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Producto;
import com.mycompany.project2.entities.Factura;
import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.model.ItemCarrito;
import com.mycompany.project2.services.ProductoFacadeLocal;
import com.mycompany.project2.services.FacturaFacadeLocal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@Named
@SessionScoped
public class ClienteController implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private ProductoFacadeLocal productoFacade;
    
    @EJB
    private FacturaFacadeLocal facturaFacade;

    @Inject
    private login login; // Para validar sesión

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
            items.put(id, new ItemCarrito(
                id,
                producto.getNombreProducto(),
                producto.getValorProducto()
            ));
        }
    }

    public void eliminarDelCarrito(Integer idProducto) {
        items.remove(idProducto);
    }

    public void vaciarCarrito() {
        items.clear();
    }

    // Métodos para la vista
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

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    // Método para redirigir al carrito (usado desde index.xhtml)
    public String verCarrito() {
        if (login.getUsuarioLogueado() == null) {
            return "/login?faces-redirect=true";
        }
        if ("Cliente".equals(login.getUsuarioLogueado().getRolIDROL().getNombreRol())) {
            return "/views/cliente/carrito?faces-redirect=true";
        }
        return "/login?faces-redirect=true";
    }
    
    // ✅ MÉTODO FINALIZAR COMPRA - AHORA CON NOMBRES CORRECTOS
    public String finalizarCompra() {
        try {
            // 1. Validar que el usuario esté logueado
            if (login.getUsuarioLogueado() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debes iniciar sesión para finalizar la compra"));
                return null;
            }

            // 2. Validar que el carrito no esté vacío
            if (items.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Carrito vacío", "Agrega productos antes de finalizar"));
                return null;
            }

            // 3. Crear la factura
            Factura factura = new Factura();
            factura.setFechaFactura(new Date());
            factura.setTotalFactura(BigDecimal.valueOf(getTotal()));
            factura.setEstadoFactura("Pendiente");
            
            // Asignar el cliente (usuario logueado)
            Usuario cliente = login.getUsuarioLogueado();
            factura.setUsuarioIDUSUARIOCLIENTE(cliente);

            // 4. Guardar la factura en la base de datos
            facturaFacade.create(factura);

            // 5. Limpiar el carrito
            vaciarCarrito();

            // 6. Mostrar mensaje de éxito
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "¡Compra finalizada!", "Tu pedido ha sido registrado"));

            // 7. Redirigir a la página de perfil
            return "perfil?faces-redirect=true";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al finalizar compra", e.getMessage()));
            return null;
        }
    }
}
