/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Producto;
import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.services.ProductoFacadeLocal;
import com.mycompany.project2.controller.UserController;
import com.mycompany.project2.controller.DomicilioController;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Named(value = "productController")
@SessionScoped
public class ProductController implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ProductController.class.getName());
    
    private Producto pro = new Producto();
    private String categoriaTemporal;
    private List<SelectItem> listaCategorias;
    private List<SelectItem> listaEstados;
    
    // ✅ Inyección de otros controladores
    @Inject
    private UserController userController;
    
    @Inject
    private DomicilioController domicilioController;

    @EJB
    private ProductoFacadeLocal productoFacade;

    // Getters y setters para categoriaTemporal
    public String getCategoriaTemporal() {
        if (pro.getCategoriaProducto() != null) {
            return pro.getCategoriaProducto().name();
        }
        return categoriaTemporal;
    }

    public void setCategoriaTemporal(String categoriaTemporal) {
        this.categoriaTemporal = categoriaTemporal;
    }

    public Producto getPro() {
        return pro;
    }

    public void setPro(Producto pro) {
        this.pro = pro;
        if (pro != null && pro.getCategoriaProducto() != null) {
            this.categoriaTemporal = pro.getCategoriaProducto().name();
        }
    }

    public List<Producto> obtenerProductos() {
        try {
            return this.productoFacade.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener productos", e);
            mostrarMensaje("Error al cargar productos", FacesMessage.SEVERITY_ERROR);
            return new ArrayList<>();
        }
    }

    public List<Producto> obtenerProductosActivos() {
        return obtenerProductos().stream()
                .filter(p -> p.getEstadoProducto() != null && "Activo".equals(p.getEstadoProducto()))
                .collect(Collectors.toList());
    }

    // ✅ NUEVO: Método para gráfico - Productos por categoría
    public Map<String, Long> getProductosPorCategoria() {
        return obtenerProductosActivos().stream()
                .collect(Collectors.groupingBy(
                    p -> p.getCategoriaProducto() != null ? p.getCategoriaProducto().name() : "SIN_CATEGORIA",
                    Collectors.counting()
                ));
    }

    // ✅ NUEVO: Métodos auxiliares para el dashboard
    public String[] getProductosCategoriasLabels() {
        return getProductosPorCategoria().keySet().toArray(new String[0]);
    }

    public Long[] getProductosCategoriasData() {
        return getProductosPorCategoria().values().toArray(new Long[0]);
    }

    public List<SelectItem> getListaCategorias() {
        if (listaCategorias == null) {
            listaCategorias = new ArrayList<>();
            for (Producto.Categoria cat : Producto.Categoria.values()) {
                listaCategorias.add(new SelectItem(cat.name(), cat.name()));
            }
        }
        return listaCategorias;
    }

    public List<SelectItem> getListaEstados() {
        if (listaEstados == null) {
            listaEstados = new ArrayList<>();
            listaEstados.add(new SelectItem("Activo", "Activo"));
            listaEstados.add(new SelectItem("Inactivo", "Inactivo"));
        }
        return listaEstados;
    }

    public String prepararCrearProducto() {
        this.pro = new Producto();
        this.categoriaTemporal = null;
        this.pro.setEstadoProducto("Activo");
        return "/views/producto/crearactProducto.xhtml?faces-redirect=true";
    }

    public String prepararEditarProducto(Producto producto) {
        this.pro = producto;
        this.categoriaTemporal = producto.getCategoriaProducto() != null ? 
                                producto.getCategoriaProducto().name() : null;
        return "/views/producto/crearactProducto.xhtml?faces-redirect=true";
    }

    public String guardarProducto() {
        try {
            if (categoriaTemporal != null && !categoriaTemporal.isEmpty()) {
                pro.setCategoriaProducto(Producto.Categoria.valueOf(categoriaTemporal));
            }

            if (pro.getEstadoProducto() == null || pro.getEstadoProducto().trim().isEmpty()) {
                pro.setEstadoProducto("Activo");
            }

            if (validarProducto()) {
                if (pro.getIdProducto() == null) {
                    productoFacade.create(pro);
                    mostrarMensaje("✅ Producto registrado correctamente", FacesMessage.SEVERITY_INFO);
                } else {
                    productoFacade.edit(pro);
                    mostrarMensaje("✅ Producto actualizado correctamente", FacesMessage.SEVERITY_INFO);
                }
                
                this.pro = new Producto();
                this.categoriaTemporal = null;
                return "/views/producto/index.xhtml?faces-redirect=true";
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar producto", e);
            mostrarMensaje("❌ Error: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
        return null;
    }

    public void eliminarProducto(Producto producto) {
        try {
            this.productoFacade.remove(producto);
            mostrarMensaje("✅ Producto eliminado correctamente", FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar producto", e);
            mostrarMensaje("❌ Error al eliminar: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    // ✅ MÉTODO PARA ENVÍO MASIVO DE CORREOS
    public void enviarCorreoMasivo() {
        try {
            // Obtener todos los usuarios con correo válido
            List<Usuario> usuarios = userController.obtenerUsuarios();
            List<String> correosValidos = new ArrayList<>();
            
            for (Usuario usuario : usuarios) {
                if (usuario.getCorreoUsuario() != null && 
                    !usuario.getCorreoUsuario().trim().isEmpty() &&
                    usuario.getCorreoUsuario().contains("@")) {
                    correosValidos.add(usuario.getCorreoUsuario().trim());
                }
            }
            
            if (correosValidos.isEmpty()) {
                mostrarMensaje("❌ No hay usuarios con correos válidos para enviar", FacesMessage.SEVERITY_ERROR);
                return;
            }

            // Configuración del servidor SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            // 🔑 CONFIGURA TUS CREDENCIALES AQUÍ
            String username = "tucorreo@gmail.com"; // ✅ TU CORREO DE GMAIL
            String password = "tu-contraseña-de-aplicacion"; // ✅ CONTRASEÑA DE APLICACIÓN

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // Crear el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setSubject("📢 Notificación Importante - Desayunos y Detalles");
            
            // Contenido del correo masivo
            StringBuilder cuerpo = new StringBuilder();
            cuerpo.append("¡Hola!\n\n");
            cuerpo.append("Este es un mensaje importante de Desayunos y Detalles:\n\n");
            cuerpo.append("• Tenemos nuevos productos disponibles\n");
            cuerpo.append("• Promociones especiales esta semana\n");
            cuerpo.append("• Nuevos servicios de domicilio mejorados\n\n");
            cuerpo.append("Visita nuestra plataforma para más información.\n\n");
            cuerpo.append("Gracias por ser parte de nuestra comunidad.\n");
            cuerpo.append("Equipo de Desayunos y Detalles");
            
            message.setText(cuerpo.toString());

            // Enviar a todos los correos válidos
            InternetAddress[] direcciones = new InternetAddress[correosValidos.size()];
            for (int i = 0; i < correosValidos.size(); i++) {
                direcciones[i] = new InternetAddress(correosValidos.get(i));
            }
            
            message.setRecipients(Message.RecipientType.BCC, direcciones);
            
            // Enviar el mensaje
            Transport.send(message);
            
            mostrarMensaje("✅ Correo masivo enviado a " + correosValidos.size() + " usuarios", FacesMessage.SEVERITY_INFO);
            
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Error al enviar correo masivo", e);
            mostrarMensaje("❌ Error al enviar correo masivo: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    private boolean validarProducto() {
        if (pro.getCodigoProducto() == null || pro.getCodigoProducto().trim().isEmpty()) {
            mostrarMensaje("El código del producto es obligatorio", FacesMessage.SEVERITY_ERROR);
            return false;
        }
        if (pro.getNombreProducto() == null || pro.getNombreProducto().trim().isEmpty()) {
            mostrarMensaje("El nombre del producto es obligatorio", FacesMessage.SEVERITY_ERROR);
            return false;
        }
        if (pro.getValorProducto() == null || pro.getValorProducto().trim().isEmpty()) {
            mostrarMensaje("El valor del producto es obligatorio", FacesMessage.SEVERITY_ERROR);
            return false;
        }
        if (pro.getCategoriaProducto() == null) {
            mostrarMensaje("La categoría del producto es obligatoria", FacesMessage.SEVERITY_ERROR);
            return false;
        }
        if (pro.getEstadoProducto() == null || pro.getEstadoProducto().trim().isEmpty()) {
            mostrarMensaje("El estado del producto es obligatorio", FacesMessage.SEVERITY_ERROR);
            return false;
        }
        return true;
    }

    private void mostrarMensaje(String mensaje, FacesMessage.Severity severidad) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, mensaje, null));
    }
}
