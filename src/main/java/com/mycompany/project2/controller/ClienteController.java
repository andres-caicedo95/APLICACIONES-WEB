package com.mycompany.project2.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.project2.entities.Cliente;
import com.mycompany.project2.entities.Domicilios;
import com.mycompany.project2.entities.Factura;
import com.mycompany.project2.entities.Producto;
import com.mycompany.project2.entities.Pedido;
import com.mycompany.project2.entities.PedidoItem;
import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.entities.Rol;
import com.mycompany.project2.services.ClienteFacadeLocal;
import com.mycompany.project2.services.DomiciliosFacadeLocal;
import com.mycompany.project2.services.FacturaFacadeLocal;
import com.mycompany.project2.services.GeoService;
import com.mycompany.project2.services.ProductoFacadeLocal;
import com.mycompany.project2.services.PedidoFacadeLocal;
import com.mycompany.project2.services.PedidoItemFacadeLocal;
import com.mycompany.project2.services.UsuarioFacadeLocal;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import javax.validation.ConstraintViolationException;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

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

    @EJB
    private FacturaFacadeLocal facturaFacade;

    @EJB
    private DomiciliosFacadeLocal domiciliosFacade;

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    @EJB
    private ClienteFacadeLocal clienteFacade;

    @Inject
    private GeoService geoService;

    // Lista de productos y carrito
    private List<Producto> productos;
    private List<ItemCarrito> carrito = new ArrayList<>();

    // Campos auxiliares
    private int cantidad = 1;
    private String metodoPago = "ContraEntrega";
    private String direccionEntrega;

    // ===== CAMPOS PARA ASIGNAR DOMICILIARIO A PEDIDO =====
    private Integer idDomiciliarioTemporal;
    private Integer idFacturaTemporal;

    // ---------- ----------
    /**
     * Cantidades temporales por producto (clave = idProducto). Se usa en la
     * vista del catálogo para cada tarjeta/producto.
     */
    private Map<Integer, Integer> cantidades = new HashMap<>();

    /**
     * Devuelve la cantidad temporal para un producto (fallback = 1). Útil para
     * EL: #{clienteController.getCantidadTemporal(p.idProducto)}
     */
    public int getCantidadTemporal(Integer idProducto) {
        if (idProducto == null) {
            return 1;
        }
        return cantidades.getOrDefault(idProducto, 1);
    }

    /**
     * Setter auxiliar para EL map access:
     * #{clienteController.setCantidadTemporal(p.idProducto, valor)}
     */
    public void setCantidadTemporal(Integer idProducto, Integer cantidadValor) {
        if (idProducto == null) {
            return;
        }
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
        if (c > 1) {
            cantidades.put(idProducto, c - 1);
        }
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
     * Método legacy: si la vista usa la propiedad 'cantidad' se mantiene su
     * comportamiento.
     */
    public void agregarAlCarrito(Producto p) {
        if (p == null) {
            return;
        }

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
     * NUEVO: Agregar al carrito pasando la cantidad (método mínimo nuevo y no
     * invasivo). Uso previsto: desde catalogoCliente.xhtml ->
     * agregarAlCarrito(p, clienteController.getCantidadTemporal(p.idProducto))
     */
    public void agregarAlCarrito(Producto p, Integer qty) {
        if (p == null) {
            return;
        }
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
     * Eliminar robusto (por idProducto) para evitar problemas si la referencia
     * del objeto difiere.
     */
    public void eliminarDelCarrito(ItemCarrito item) {
        if (item == null) {
            return;
        }
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

    // === Método requerido para que el modal funcione con Ajax ===
    public void prepararPago() {
        // Solo asegura la actualización de valores antes de abrir el modal
        System.out.println("Preparando modal de pago: métodoPago=" + metodoPago + ", direccion=" + direccionEntrega);
    }

    // ---------- NUEVOS métodos para uso desde carrito.xhtml (botones + / -) ----------
    public void incrementarItem(ItemCarrito item) {
        if (item == null) {
            return;
        }
        item.setCantidad(item.getCantidad() + 1);
    }

    public void disminuirItem(ItemCarrito item) {
        if (item == null) {
            return;
        }
        if (item.getCantidad() <= 1) {
            eliminarDelCarrito(item);
        } else {
            item.setCantidad(item.getCantidad() - 1);
        }
    }
    // ---------- FIN nuevos métodos ----------

    /**
     * Obtiene el cliente asociado a un usuario (por correo). Si no existe, crea
     * uno nuevo automáticamente.
     */
    private Cliente obtenerClientePorUsuario(Usuario usuario) {
        if (usuario == null || usuario.getCorreoUsuario() == null) {
            return null;
        }

        // Buscar cliente por correo (usando el método del facade)
        Cliente cliente = clienteFacade.findByCorreo(usuario.getCorreoUsuario());

        if (cliente == null) {
            // Crear nuevo cliente automáticamente
            cliente = new Cliente();
            cliente.setTipoDocumentoCliente(usuario.getTipoDocumentoUsuario());
            cliente.setNumeroDocumentoCliente(usuario.getNumeroDocumento());
            cliente.setNombreCliente(usuario.getNombreUsuario());
            cliente.setApellidoCliente(usuario.getApellidoUsuario());
            cliente.setTelCliente(usuario.getTelUsuario());
            cliente.setDirecccionCliente(usuario.getDirecccionUsuario());
            cliente.setCorreoCliente(usuario.getCorreoUsuario());
            cliente.setPaswordCliente(usuario.getPaswordUsuario());
            cliente.setEstadoCliente("Activo");

            clienteFacade.create(cliente);
        }

        return cliente;
    }
    // ---------- validar domicilio ----------

    private void validarDomicilio(Domicilios domicilio) {
        System.out.println("=== VALIDANDO DOMICILIO ===");

        if (domicilio.getFechaDomicilio() == null) {
            System.out.println("ERROR: fechaDomicilio es null");
            domicilio.setFechaDomicilio(new Date());
        }

        if (domicilio.getEstado() == null || domicilio.getEstado().trim().isEmpty()) {
            System.out.println("ERROR: estado es null o vacío");
            domicilio.setEstado("PENDIENTE");
        }

        if (domicilio.getDirecccionDomicilio() == null || domicilio.getDirecccionDomicilio().trim().isEmpty()) {
            System.out.println("ERROR: direcccionDomicilio es null o vacío");
            // No podemos continuar sin dirección
            throw new IllegalArgumentException("La dirección del domicilio es obligatoria");
        }

        if (domicilio.getUsuarioIDUSUARIODOMICILIO() == null) {
            System.out.println("ERROR: usuarioIDUSUARIODOMICILIO es null");
            throw new IllegalArgumentException("El usuario del domicilio es obligatorio");
        }

        if (domicilio.getFacturaIDFACTURA() == null) {
            System.out.println("ERROR: facturaIDFACTURA es null");
            throw new IllegalArgumentException("La factura del domicilio es obligatoria");
        }

        System.out.println("Domicilio validado correctamente");
    }

    // ---------- Checkout ----------
    public String finalizarCompra() {
        System.out.println("=== INICIANDO FINALIZAR COMPRA ===");
        System.out.println("Carrito: " + carrito.size() + " items");
        System.out.println("Total: " + getTotal());
        System.out.println("Dirección entrega: " + direccionEntrega);
        System.out.println("Método pago: " + metodoPago);
        try {
            if (carrito.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("El carrito está vacío"));
                return null;
            }

            // Obtener usuario logueado
            Usuario usuario = (Usuario) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("usuario");

            if (usuario == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Debe iniciar sesión para continuar.", ""));
                return null;
            }

// ✅ OBTENER CLIENTE ASOCIADO (no fijo)
            Cliente cliente = obtenerClientePorUsuario(usuario);

            // ===========================
            // 1. CREAR FACTURA - CORREGIDO
            // ===========================
            Factura factura = new Factura();
            factura.setFechaFactura(new Date());
            factura.setUsuarioIDUSUARIOVENDEDOR(usuario);  // Vendedor
            factura.setUsuarioIDUSUARIOCLIENTE(usuario);   // Cliente - ¡OBLIGATORIO ANTES DE create()!
            factura.setTotalFactura(getTotal());
            factura.setEstadoFactura("PENDIENTE");         // Estado - ¡OBLIGATORIO ANTES DE create()!
            factura.setMetodoPago(metodoPago != null ? metodoPago : "ContraEntrega");

            // Solo después de asignar TODOS los campos obligatorios:
            facturaFacade.create(factura);  // LÍNEA 291 - AHORA SÍ

            // ===========================
            // 2. CREAR PEDIDO
            // ===========================
            Pedido pedido = new Pedido();
            pedido.setIdCliente(cliente.getIdCliente());      // AHORA cliente real
            pedido.setFechaPedido(new Date());
            pedido.setEstado("pendiente");
            pedido.setTotal(getTotal());
            pedido.setDireccionEntrega(direccionEntrega);
            pedido.setIdFactura(factura.getIdFactura());       // ENLAZADO A FACTURA

            pedidoFacade.create(pedido);

            // ===========================
            // 3. CREAR ITEMS DEL PEDIDO
            // ===========================
            for (ItemCarrito i : carrito) {
                PedidoItem pi = new PedidoItem();
                pi.setIdPedido(pedido.getIdPedido());
                pi.setIdProducto(i.getProducto().getIdProducto());
                pi.setCantidad(i.getCantidad());
                pi.setPrecioUnitario(new BigDecimal(i.getProducto().getValorProducto().toString()));

                pedidoItemFacade.create(pi);

                // Reducir stock
                Producto p = i.getProducto();
                p.setStockProduccto(p.getStockProduccto() - i.getCantidad());
                productoFacade.edit(p);
            }

            // ===========================
            // 4. CREAR DOMICILIO
            // ===========================
// ===========================
// Validar que la dirección no sea nula
            if (direccionEntrega == null || direccionEntrega.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "La dirección de entrega es obligatoria.", ""));
                return null;
            }

// Validar que el usuario existe
            if (usuario == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "El usuario no está disponible.", ""));
                return null;
            }

// Validar que la factura existe
            if (factura == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "La factura no está disponible.", ""));
                return null;
            }

            try {
                Domicilios domicilio = new Domicilios();
                domicilio.setFechaDomicilio(new Date());
                domicilio.setDirecccionDomicilio(direccionEntrega.trim());
                domicilio.setEstado("PENDIENTE");
                domicilio.setUsuarioIDUSUARIODOMICILIO(usuario);
                domicilio.setFacturaIDFACTURA(factura);

                // Coordenadas (opcional, no son obligatorias)
                double[] coords = geoService.geocodificar(direccionEntrega);
                if (coords != null && coords.length >= 2) {
                    domicilio.setLatitud(coords[0]);
                    domicilio.setLongitud(coords[1]);
                }
                validarDomicilio(domicilio);
                domiciliosFacade.create(domicilio);
                System.out.println("✅ Domicilio creado exitosamente. ID: " + domicilio.getIdDomicilio());

            } catch (Exception e) {
                System.out.println("❌ Error al crear domicilio: " + e.getMessage());
                e.printStackTrace();
                throw e; // Re-lanzar para que sea capturado por el catch general
            }

            // ===========================
            // 5. LIMPIAR CARRITO
            // ===========================
            carrito.clear();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Compra realizada con éxito"));

            return "/views/cliente/misPedidos.xhtml?faces-redirect=true";

        } catch (ConstraintViolationException e) {
            // Capturar errores de validación específicos
            StringBuilder sb = new StringBuilder();
            for (javax.validation.ConstraintViolation<?> violation : e.getConstraintViolations()) {
                sb.append("• ").append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("\n");
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error de validación:", sb.toString()));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al procesar la compra: " + e.getMessage(), ""));
            return null;
        }
    }

    // ---------- Mis Pedidos ----------
    public List<Pedido> getMisPedidos() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("usuario");

        if (usuario == null) {
            return Collections.emptyList();
        }

        // Obtener cliente real relacionado
        Cliente cliente = obtenerClientePorUsuario(usuario);
        if (cliente == null) {
            return Collections.emptyList();
        }

        Integer idCliente = cliente.getIdCliente();

        List<Pedido> lista = pedidoFacade.findAll();
        List<Pedido> filtrados = new ArrayList<>();
        for (Pedido p : lista) {
            if (p.getIdCliente().equals(idCliente)) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    public List<PedidoItem> getItemsPedido(Pedido pedido) {
        if (pedido == null || pedido.getIdPedido() == null) {
            return Collections.emptyList();
        }
// Llama al método optimizado de la fachada
        return pedidoItemFacade.findByPedido(pedido.getIdPedido());
    }

    public Producto getProducto(Integer idProducto) {
        if (idProducto == null) {
            return null;
        }
        return productoFacade.find(idProducto);
    }

    public Domicilios getDomicilio(Integer idDomicilio) {
        if (idDomicilio == null) {
            return null;
        }
        return domiciliosFacade.find(idDomicilio);
    }

    // ==============================
// MÉTODOS ADICIONALES PARA MIS PEDIDOS / DETALLE
// ==============================
    private Pedido pedidoSeleccionado;

    public Pedido getPedidoSeleccionado() {
        return pedidoSeleccionado;
    }

    /**
     * Devuelve lista de items por idPedido (comodidad para EL).
     */
    public List<PedidoItem> getItemsByPedido(Integer idPedido) {
        if (idPedido == null) {
            return Collections.emptyList();
        }
        return pedidoItemFacade.findByPedido(idPedido);
    }

    /**
     * Llama cuando el usuario hace "Ver detalles". Guarda el pedido
     * seleccionado y redirige a la página de detalle.
     */
    public String verDetallePedido(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        this.pedidoSeleccionado = pedido;
        return "/views/cliente/detallePedido.xhtml?faces-redirect=true";
    }

    /**
     * DESCARGAR FACTURA
     */
    public void descargarFactura(Pedido pedido) {
        if (pedido == null || pedido.getIdFactura() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Este pedido no tiene factura disponible.", ""));
            return;
        }

        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ec.getResponse();

        try {
            Factura factura = facturaFacade.find(pedido.getIdFactura());
            if (factura == null) {
                throw new IllegalArgumentException("Factura no encontrada");
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            // ================= LOGO Y DATOS EMPRESA =================
            try {
                String logoPath = ec.getRealPath("/resources/images/logo.png"); // Ajusta la ruta
                Image logo = Image.getInstance(logoPath);
                logo.scaleToFit(100, 100);
                logo.setAlignment(Element.ALIGN_LEFT);

                // Datos empresa
                PdfPTable empresaTable = new PdfPTable(2);
                empresaTable.setWidthPercentage(100);
                empresaTable.setWidths(new float[]{1f, 3f});
                empresaTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(PdfPCell.NO_BORDER);
                empresaTable.addCell(logoCell);

                PdfPCell datosEmpresa = new PdfPCell();
                datosEmpresa.setBorder(PdfPCell.NO_BORDER);
                Paragraph p = new Paragraph();
                p.add(new Phrase("Desayunos&Detalles S.A.\n", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
                p.add(new Phrase("Dirección:Carrera 13 No. 65 - 10, Bogota\n", new Font(Font.FontFamily.HELVETICA, 11)));
                p.add(new Phrase("Teléfono: 3112175356\nEmail: contacto@desayunosdetalles.com", new Font(Font.FontFamily.HELVETICA, 11)));
                datosEmpresa.addElement(p);
                empresaTable.addCell(datosEmpresa);

                document.add(empresaTable);
                document.add(Chunk.NEWLINE);

            } catch (Exception e) {
                System.out.println("Logo no cargado: " + e.getMessage());
            }

            // ================= ENCABEZADO FACTURA =================
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
            Paragraph titulo = new Paragraph("FACTURA #" + factura.getIdFactura(), tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10);
            document.add(titulo);

            Font infoFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
            Paragraph infoCliente = new Paragraph(
                    "Fecha: " + new SimpleDateFormat("dd/MM/yyyy").format(factura.getFechaFactura()) + "\n"
                    + "Cliente: " + factura.getUsuarioIDUSUARIOCLIENTE().getNombreUsuario() + " "
                    + factura.getUsuarioIDUSUARIOCLIENTE().getApellidoUsuario() + "\n"
                    + "Método de pago: " + factura.getMetodoPago(),
                    infoFont
            );
            infoCliente.setSpacingAfter(10);
            document.add(infoCliente);

            // ================= DIRECCIÓN DE ENTREGA =================
            Pedido pedidoCompleto = pedidoFacade.find(pedido.getIdPedido());

            if (pedidoCompleto != null && pedidoCompleto.getDireccionEntrega() != null) {
                Paragraph direccionEntregaPDF = new Paragraph(
                        "Dirección de entrega: " + pedidoCompleto.getDireccionEntrega(),
                        new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK)
                );
                direccionEntregaPDF.setSpacingAfter(10);
                document.add(direccionEntregaPDF);
            }

            // ================= TABLA DE PRODUCTOS =================
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 1f, 2f, 2f});

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            BaseColor headerColor = new BaseColor(0, 102, 204); // azul
            String[] headers = {"Producto", "Cantidad", "Precio Unit.", "Subtotal"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            Font cellFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
            BigDecimal total = BigDecimal.ZERO;
            List<PedidoItem> items = getItemsByPedido(pedido.getIdPedido());
            boolean gris = false;
            for (PedidoItem item : items) {
                String nombre = getProducto(item.getIdProducto()).getNombreProducto();
                BigDecimal precio = item.getPrecioUnitario();
                BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(item.getCantidad()));
                total = total.add(subtotal);

                BaseColor bgColor = gris ? new BaseColor(240, 240, 240) : BaseColor.WHITE;
                gris = !gris;

                PdfPCell c1 = new PdfPCell(new Phrase(nombre, cellFont));
                c1.setBackgroundColor(bgColor);
                c1.setPadding(5);
                table.addCell(c1);

                PdfPCell c2 = new PdfPCell(new Phrase(String.valueOf(item.getCantidad()), cellFont));
                c2.setHorizontalAlignment(Element.ALIGN_CENTER);
                c2.setBackgroundColor(bgColor);
                c2.setPadding(5);
                table.addCell(c2);

                PdfPCell c3 = new PdfPCell(new Phrase("$" + precio, cellFont));
                c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c3.setBackgroundColor(bgColor);
                c3.setPadding(5);
                table.addCell(c3);

                PdfPCell c4 = new PdfPCell(new Phrase("$" + subtotal, cellFont));
                c4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c4.setBackgroundColor(bgColor);
                c4.setPadding(5);
                table.addCell(c4);
            }

            document.add(table);

            // ================= TOTAL =================
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(40);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setSpacingBefore(10f);

            Font totalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);

            PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL", totalFont));
            totalLabel.setBackgroundColor(new BaseColor(200, 200, 200));
            totalLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalLabel.setPadding(5);
            totalTable.addCell(totalLabel);

            PdfPCell totalValue = new PdfPCell(new Phrase("$" + total, totalFont));
            totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValue.setPadding(5);
            totalTable.addCell(totalValue);

            document.add(totalTable);

            document.close();

            // ================= ENVIAR PDF AL NAVEGADOR =================
            byte[] pdfBytes = baos.toByteArray();
            response.reset();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=factura_" + factura.getIdFactura() + ".pdf");
            response.setContentLength(pdfBytes.length);

            OutputStream out = response.getOutputStream();
            out.write(pdfBytes);
            out.flush();
            out.close();

            fc.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al generar la factura: " + e.getMessage(), ""));
        }
    }

    // ---------- Total de pedidos ---------- 
    public int getTotalPedidos() {
        return getMisPedidos().size();
    }

    // ----------  MÉTODOS PARA DOMICILIOS -admin----------
    // Devuelve todos los pedidos registrados
    public List<Pedido> getTodosPedidos() {
        return pedidoFacade.findAll();
    }

    // Cancelar pedido (actualiza estado)
    public void cancelarPedido(Pedido pedido) {
        if (pedido != null) {
            pedido.setEstado("Cancelado");
            pedidoFacade.edit(pedido);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Pedido cancelado", null));
        }
    }

    // Listar domiciliarios (rol DOMICILIARIO)
    public List<Usuario> getListaDomiciliarios() {
        return usuarioFacade.findByRol(3);
    }

// Listar facturas
    public List<Factura> getListaFacturas() {
        return facturaFacade.findAll();
    }

// Getters/Setters
    public Integer getIdDomiciliarioTemporal() {
        return idDomiciliarioTemporal;
    }

    public void setIdDomiciliarioTemporal(Integer idDomiciliarioTemporal) {
        this.idDomiciliarioTemporal = idDomiciliarioTemporal;
    }

    public Integer getIdFacturaTemporal() {
        return idFacturaTemporal;
    }

    public void setIdFacturaTemporal(Integer idFacturaTemporal) {
        this.idFacturaTemporal = idFacturaTemporal;
    }

    public String guardarPedido() {
        try {
            if (pedidoSeleccionado == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "No hay pedido seleccionado", null));
                return null;
            }

            // Asignar factura
            if (idFacturaTemporal != null) {
                pedidoSeleccionado.setIdFactura(idFacturaTemporal);
            }

            // Asignar domiciliario
            if (idDomiciliarioTemporal != null) {
                Usuario dom = usuarioFacade.find(idDomiciliarioTemporal);
                pedidoSeleccionado.setUsuarioDomiciliario(dom);
            } else {
                pedidoSeleccionado.setUsuarioDomiciliario(null);
            }

            // Actualizar pedido
            pedidoFacade.edit(pedidoSeleccionado);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Pedido actualizado correctamente", null));

            return "indexDomi?faces-redirect=true";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al actualizar el pedido: " + e.getMessage(), null));
            return null;
        }
    }

    public String editarPedido(Pedido pedido) {
        this.pedidoSeleccionado = pedido;

        // Cargar ID del domiciliario temporal
        if (pedido.getUsuarioDomiciliario() != null) {
            this.idDomiciliarioTemporal = pedido.getUsuarioDomiciliario().getIdUsuario();
        } else {
            this.idDomiciliarioTemporal = null;
        }

        // Cargar ID de la factura temporal
        this.idFacturaTemporal = pedido.getIdFactura();

        return "/views/admin/crearactDomicilio.xhtml?faces-redirect=true";
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
