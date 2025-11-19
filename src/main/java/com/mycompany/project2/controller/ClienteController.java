package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Producto;
import com.mycompany.project2.entities.Pedido;
import com.mycompany.project2.entities.PedidoItem;
import com.mycompany.project2.services.ProductoFacadeLocal;
import com.mycompany.project2.services.PedidoFacadeLocal;
import com.mycompany.project2.services.PedidoItemFacadeLocal;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@Named("clienteController")
@SessionScoped
public class ClienteController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProductoFacadeLocal productoFacade;

    @EJB
    private PedidoFacadeLocal pedidoFacade;

    @EJB
    private PedidoItemFacadeLocal pedidoItemFacade;

    // Lista de productos y carrito
    private List<Producto> productos;
    private List<ItemCarrito> carrito = new ArrayList<>();

    // Campos auxiliares
    private int cantidad = 1;
    private String metodoPago = "ContraEntrega";
    private String direccionEntrega;

    // ---------- CAMBIOS MÍNIMOS AÑADIDOS ----------
    /**
     * Cantidades temporales por producto (clave = idProducto).
     * Se usa en la vista del catálogo para cada tarjeta/producto.
     */
    private Map<Integer, Integer> cantidades = new HashMap<>();

    /**
     * Devuelve la cantidad temporal para un producto (fallback = 1).
     * Útil para EL: #{clienteController.getCantidadTemporal(p.idProducto)}
     */
    public int getCantidadTemporal(Integer idProducto) {
        if (idProducto == null) return 1;
        return cantidades.getOrDefault(idProducto, 1);
    }

    /**
     * Setter auxiliar para EL map access: #{clienteController.setCantidadTemporal(p.idProducto, valor)}
     */
    public void setCantidadTemporal(Integer idProducto, Integer cantidadValor) {
        if (idProducto == null) return;
        if (cantidadValor == null || cantidadValor < 1) {
            cantidades.put(idProducto, 1);
        } else {
            cantidades.put(idProducto, cantidadValor);
        }
    }

    /**
     * Incrementa la cantidad temporal (invocado por el botón + en el catálogo).
     */
    public void incrementarCantidadTemporal(Integer idProducto) {
        int c = getCantidadTemporal(idProducto);
        cantidades.put(idProducto, c + 1);
    }

    /**
     * Decrementa la cantidad temporal (invocado por el botón - en el catálogo).
     */
    public void decrementarCantidadTemporal(Integer idProducto) {
        int c = getCantidadTemporal(idProducto);
        if (c > 1) cantidades.put(idProducto, c - 1);
    }
    // ---------- FIN CAMBIOS MÍNIMOS ----------

    // ---------- Getters y Setters ----------
    public List<Producto> getProductos() {
        if (productos == null) {
            productos = productoFacade.findAll();
        }
        return productos;
    }

    public List<ItemCarrito> getCarrito() {
        return carrito;
    }

    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getMetodoPago() {
        return metodoPago;
    }
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }
    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    // ---------- Total del carrito ----------
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrito item : carrito) {
            BigDecimal precio = new BigDecimal(item.getProducto().getValorProducto().toString());
            BigDecimal cantidadBD = BigDecimal.valueOf(item.getCantidad());
            total = total.add(precio.multiply(cantidadBD));
        }
        return total;
    }

    // ---------- Carrito ----------
    /**
     * Método legacy: si la vista usa la propiedad 'cantidad' se mantiene su comportamiento.
     */
    public void agregarAlCarrito(Producto p) {
        if (p == null) return;

        Optional<ItemCarrito> itemOpt = carrito.stream()
                .filter(i -> i.getProducto().getIdProducto().equals(p.getIdProducto()))
                .findFirst();

        if (itemOpt.isPresent()) {
            itemOpt.get().setCantidad(itemOpt.get().getCantidad() + cantidad);
        } else {
            carrito.add(new ItemCarrito(p, cantidad));
        }

        cantidad = 1; // reset
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Producto agregado al carrito", null));
    }

    /**
     * NUEVO: Agregar al carrito pasando la cantidad (método mínimo nuevo y no invasivo).
     * Uso previsto: desde catalogoCliente.xhtml -> agregarAlCarrito(p, clienteController.getCantidadTemporal(p.idProducto))
     */
    public void agregarAlCarrito(Producto p, Integer qty) {
        if (p == null) return;
        int cantidadParaAgregar = (qty == null || qty < 1) ? 1 : qty;

        Optional<ItemCarrito> itemOpt = carrito.stream()
                .filter(i -> i.getProducto().getIdProducto().equals(p.getIdProducto()))
                .findFirst();

        if (itemOpt.isPresent()) {
            itemOpt.get().setCantidad(itemOpt.get().getCantidad() + cantidadParaAgregar);
        } else {
            carrito.add(new ItemCarrito(p, cantidadParaAgregar));
        }

        // Reset mínimo de la cantidad temporal para ese producto (mantener UX consistente)
        cantidades.put(p.getIdProducto(), 1);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Producto agregado al carrito", null));
    }

    /**
     * Eliminar robusto (por idProducto) para evitar problemas si la referencia del objeto difiere.
     */
    public void eliminarDelCarrito(ItemCarrito item) {
        if (item == null) return;
        Integer id = item.getProducto() != null ? item.getProducto().getIdProducto() : null;
        if (id != null) {
            carrito.removeIf(i -> i.getProducto() != null && id.equals(i.getProducto().getIdProducto()));
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Producto eliminado del carrito", null));
        }
    }

    /**
     * Actualiza la cantidad manualmente (mantengo tu firma original).
     */
    public void actualizarCantidad(ItemCarrito item, int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            carrito.remove(item);
        } else {
            item.setCantidad(nuevaCantidad);
        }
    }

    // ---------- NUEVOS métodos para uso desde carrito.xhtml (botones + / -) ----------
    public void incrementarItem(ItemCarrito item) {
        if (item == null) return;
        item.setCantidad(item.getCantidad() + 1);
    }

    public void disminuirItem(ItemCarrito item) {
        if (item == null) return;
        if (item.getCantidad() <= 1) {
            eliminarDelCarrito(item);
        } else {
            item.setCantidad(item.getCantidad() - 1);
        }
    }
    // ---------- FIN nuevos métodos ----------

    // ---------- Checkout ----------
    public String finalizarCompra() {
        if (carrito.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(
                null,
                    (new FacesMessage( "El carrito está vacío"))
            );
            return null;
        }

        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setIdCliente(1); // ⚠️ Ajustar cuando tengas login real
        pedido.setFechaPedido(new Date());
        pedido.setEstado(metodoPago.equals("ContraEntrega") ? "pendiente" : "procesando");
        pedido.setTotal(getTotal());
        pedido.setDireccionEntrega(direccionEntrega);

        pedidoFacade.create(pedido);

        // Crear items del pedido
        for (ItemCarrito i : carrito) {
            PedidoItem pi = new PedidoItem();
            pi.setIdPedido(pedido.getIdPedido());
            pi.setIdProducto(i.getProducto().getIdProducto());
            pi.setCantidad(i.getCantidad());

            // Conversión segura a BigDecimal
            pi.setPrecioUnitario(new BigDecimal(i.getProducto().getValorProducto().toString()));

            pedidoItemFacade.create(pi);

            // Actualizar stock del producto
            Producto p = i.getProducto();
            p.setStockProduccto(p.getStockProduccto() - i.getCantidad());
            productoFacade.edit(p);
        }

        carrito.clear();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Compra realizada con éxito"));

        return "/views/cliente/misPedidos.xhtml?faces-redirect=true";
    }

    // ---------- Mis Pedidos ----------
    public List<Pedido> getMisPedidos() {
        // ⚠️ Actualmente filtra por idCliente = 1
        Integer idCliente = 1;
        List<Pedido> lista = pedidoFacade.findAll();
        List<Pedido> filtrados = new ArrayList<>();
        for (Pedido p : lista) {
            if (p.getIdCliente().equals(idCliente)) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    // ---------- Clase interna ItemCarrito ----------
    public static class ItemCarrito implements Serializable {
        private static final long serialVersionUID = 1L;
        private Producto producto;
        private int cantidad;

        public ItemCarrito(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }

        public Producto getProducto() {
            return producto;
        }

        public int getCantidad() {
            return cantidad;
        }
        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return new BigDecimal(producto.getValorProducto().toString());
        }
    }
}
