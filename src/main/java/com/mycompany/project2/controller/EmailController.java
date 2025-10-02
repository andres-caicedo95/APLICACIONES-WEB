package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.services.UsuarioFacadeLocal;
import com.mycompany.project2.services.EmailService; // ✅ IMPORTACIÓN CLAVE

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named("emailController")
@SessionScoped
public class EmailController implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(EmailController.class.getName());
    
    private String asunto;
    private String cuerpo;
    private String tipoDestinatario;
    private boolean enviarHtml = false;
    private String destinatariosPersonalizadosString;
    
    private List<String> tiposDestinatario;
    private List<String> destinatariosPreview;
    
    @EJB
    private UsuarioFacadeLocal usuarioFacade;
    
    @Inject
    private ProductController productController;
    
    @Inject
    private DomicilioController domicilioController;
    
    @Inject
    private EmailService emailService; // ✅ Servicio de correo centralizado

    @PostConstruct
    public void init() {
        tiposDestinatario = Arrays.asList(
            "todos_usuarios",
            "usuarios_activos", 
            "clientes_domicilios",
            "vendedores",
            "domiciliarios",
            "personalizado"
        );
        
        asunto = "";
        cuerpo = "";
        tipoDestinatario = "";
        destinatariosPersonalizadosString = "";
        destinatariosPreview = new ArrayList<>();
    }

    public List<String> getDestinatariosPreview() {
        if (tipoDestinatario == null || tipoDestinatario.isEmpty() || "personalizado".equals(tipoDestinatario)) {
            return new ArrayList<>();
        }
        
        List<String> preview = new ArrayList<>();
        List<Usuario> usuarios = obtenerUsuariosPorTipo();
        
        for (Usuario usuario : usuarios) {
            if (usuario.getCorreoUsuario() != null && 
                !usuario.getCorreoUsuario().trim().isEmpty() &&
                usuario.getCorreoUsuario().contains("@")) {
                preview.add(usuario.getCorreoUsuario().trim());
            }
        }
        
        return preview;
    }

    private List<Usuario> obtenerUsuariosPorTipo() {
        switch (tipoDestinatario) {
            case "todos_usuarios":
                return usuarioFacade.findAll();
            case "usuarios_activos":
                return usuarioFacade.findByEstado("Activo");
            case "clientes_domicilios":
                return domicilioController.obtenerClientesConDomicilios();
            case "vendedores":
                return usuarioFacade.findByRol(2);
            case "domiciliarios":
                return usuarioFacade.findByRol(3);
            default:
                return new ArrayList<>();
        }
    }

    public String enviarCorreoMasivo() {
        try {
            List<String> destinatarios = obtenerListaDestinatarios();
            
            if (destinatarios.isEmpty()) {
                mostrarMensaje("❌ No hay destinatarios válidos para enviar el correo", FacesMessage.SEVERITY_ERROR);
                return null;
            }

            // ✅ Usa el servicio centralizado (configurado para Outlook)
            String cuerpoFinal = enviarHtml ? cuerpo : "<pre>" + cuerpo + "</pre>";
            boolean exito = emailService.enviarCorreo(
                destinatarios.toArray(new String[0]), 
                asunto, 
                cuerpoFinal
            );

            if (exito) {
                mostrarMensaje("✅ Correo masivo enviado exitosamente a " + destinatarios.size() + " destinatarios", FacesMessage.SEVERITY_INFO);
                limpiarFormulario();
                return "/views/dashboard/dashboard?faces-redirect=true";
            } else {
                mostrarMensaje("❌ Error al enviar correo masivo", FacesMessage.SEVERITY_ERROR);
                return null;
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al enviar correo masivo", e);
            mostrarMensaje("❌ Error al enviar correo masivo: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            return null;
        }
    }

    private List<String> obtenerListaDestinatarios() {
        List<String> destinatarios = new ArrayList<>();
        
        if ("personalizado".equals(tipoDestinatario)) {
            if (destinatariosPersonalizadosString != null && !destinatariosPersonalizadosString.trim().isEmpty()) {
                String[] correos = destinatariosPersonalizadosString.split(",");
                for (String correo : correos) {
                    String correoLimpio = correo.trim();
                    if (!correoLimpio.isEmpty() && correoLimpio.contains("@")) {
                        destinatarios.add(correoLimpio);
                    }
                }
            }
        } else {
            List<Usuario> usuarios = obtenerUsuariosPorTipo();
            for (Usuario usuario : usuarios) {
                if (usuario.getCorreoUsuario() != null && 
                    !usuario.getCorreoUsuario().trim().isEmpty() &&
                    usuario.getCorreoUsuario().contains("@")) {
                    destinatarios.add(usuario.getCorreoUsuario().trim());
                }
            }
        }
        
        return destinatarios;
    }

    private void limpiarFormulario() {
        asunto = "";
        cuerpo = "";
        tipoDestinatario = "";
        enviarHtml = false;
        destinatariosPersonalizadosString = "";
        destinatariosPreview = new ArrayList<>();
    }

    // Getters y Setters
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    public String getCuerpo() { return cuerpo; }
    public void setCuerpo(String cuerpo) { this.cuerpo = cuerpo; }
    public String getTipoDestinatario() { return tipoDestinatario; }
    public void setTipoDestinatario(String tipoDestinatario) { 
        this.tipoDestinatario = tipoDestinatario;
        this.destinatariosPreview = getDestinatariosPreview();
    }
    public boolean isEnviarHtml() { return enviarHtml; }
    public void setEnviarHtml(boolean enviarHtml) { this.enviarHtml = enviarHtml; }
    public String getDestinatariosPersonalizadosString() { return destinatariosPersonalizadosString; }
    public void setDestinatariosPersonalizadosString(String destinatariosPersonalizadosString) { 
        this.destinatariosPersonalizadosString = destinatariosPersonalizadosString; 
    }
    public List<String> getTiposDestinatario() { return tiposDestinatario; }

    private void mostrarMensaje(String mensaje, FacesMessage.Severity severidad) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, mensaje, null));
    }
}

