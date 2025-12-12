/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */

package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.entities.Rol;
import com.mycompany.project2.services.UsuarioFacadeLocal;
import com.mycompany.project2.services.RolFacadeLocal;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@Named(value = "login")
@SessionScoped
public class login implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(login.class.getName());
    
    private String usuario;
    private String contrasenna;
    private Integer tipoUsuario;
    private Usuario usuarioLogueado;
    
    @EJB
    private UsuarioFacadeLocal usuarioFacade;
    
    @EJB
    private RolFacadeLocal rolFacade;
    
    private List<SelectItem> listaRoles;

    // Getters y Setters
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenna() {
        return contrasenna;
    }

    public void setContrasenna(String contrasenna) {
        this.contrasenna = contrasenna;
    }

    public Integer getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(Integer tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }
    
    public List<SelectItem> getListaRoles() {
        if (listaRoles == null) {
            listaRoles = new ArrayList<>();
            try {
                for (Rol rol : rolFacade.findAll()) {
                    listaRoles.add(new SelectItem(rol.getIdRol(), rol.getNombreRol()));
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al cargar roles", e);
            }
        }
        return listaRoles;
    }

    public String iniciarSesion() {
        try {
            if (validarCamposLoginVacios()) {
                return null;
            }

            usuarioLogueado = this.usuarioFacade.iniciarSesion(usuario, contrasenna);
            
            if (usuarioLogueado != null) {
                return procesarUsuarioAutenticado();
            } else {
                mostrarMensaje("Credenciales inválidas", FacesMessage.SEVERITY_ERROR);
                return null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en inicio de sesión", e);
            mostrarMensaje("Error en el sistema. Intente más tarde", FacesMessage.SEVERITY_ERROR);
            return null;
        }
    }

    private boolean validarCamposLoginVacios() {
        boolean camposVacios = 
            (usuario == null || usuario.trim().isEmpty()) ||
            (contrasenna == null || contrasenna.trim().isEmpty()) ||
            (tipoUsuario == null);
        
        if (camposVacios) {
            mostrarMensaje("Todos los campos son requeridos", FacesMessage.SEVERITY_ERROR);
        }
        
        return camposVacios;
    }

    private String procesarUsuarioAutenticado() {
        if (!"Activo".equals(usuarioLogueado.getEstadoUsuario())) {
            mostrarMensaje("Usuario inactivo. Contacte al administrador", FacesMessage.SEVERITY_ERROR);
            return null;
        }

        if (!usuarioLogueado.getRolIDROL().getIdRol().equals(tipoUsuario)) {
            mostrarMensaje("El rol seleccionado no coincide con sus credenciales", FacesMessage.SEVERITY_ERROR);
            return null;
        }

        guardarUsuarioEnSesion();
        return redirigirSegunRol();
    }

    private void guardarUsuarioEnSesion() {
        FacesContext.getCurrentInstance().getExternalContext()
            .getSessionMap().put("usuario", usuarioLogueado);
        this.contrasenna = null; // Limpiar contraseña en memoria
    }

    private String redirigirSegunRol() {
        String rol = usuarioLogueado.getRolIDROL().getNombreRol();
        switch(rol) {
            case "Administrador":
                return "views/index?faces-redirect=true";
            case "Vendedor":
                return "views/ventas/index?faces-redirect=true";
            case "Domiciliario":
                return "views/domicilioDomiciliario/pendientesDomiciliario?faces-redirect=true";
            case "Cliente":
                return "views/cliente/perfil?faces-redirect=true";
            default:
                LOGGER.log(Level.WARNING, "Rol no reconocido: {0}", rol);
                return "views/index?faces-redirect=true";
        }
    }

    public String cerrarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/PaginaInicio.xhtml?faces-redirect=true";
        //return "/?faces-redirect=true";
        //return "login?faces-redirect=true";
    }

    private void mostrarMensaje(String mensaje, FacesMessage.Severity severidad) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, mensaje, null));
    }
}
