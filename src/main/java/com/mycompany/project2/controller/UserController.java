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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@Named(value = "userController")
@SessionScoped
public class UserController implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());
    
    private Usuario user = new Usuario();
    private List<SelectItem> listaRoles;
    private List<SelectItem> listaEstados;
    private Integer rolSeleccionado;

    @EJB
    private UsuarioFacadeLocal usuarioFacade;
    
    @EJB
    private RolFacadeLocal rolFacade;

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
        if (user != null && user.getRolIDROL() != null) {
            this.rolSeleccionado = user.getRolIDROL().getIdRol();
        }
    }

    public Integer getRolSeleccionado() {
        return rolSeleccionado;
    }

    public void setRolSeleccionado(Integer rolSeleccionado) {
        this.rolSeleccionado = rolSeleccionado;
    }

    public List<Usuario> obtenerUsuarios() {
        try {
            return this.usuarioFacade.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuarios", e);
            mostrarMensaje("Error al cargar usuarios", FacesMessage.SEVERITY_ERROR);
            return new ArrayList<>();
        }
    }

    // ✅ NUEVO: Método para gráfico - Usuarios por estado
    public Map<String, Long> getUsuariosPorEstado() {
        return obtenerUsuarios().stream()
                .collect(Collectors.groupingBy(
                    u -> u.getEstadoUsuario() != null ? u.getEstadoUsuario() : "SIN_ESTADO",
                    Collectors.counting()
                ));
    }

    // ✅ NUEVO: Métodos auxiliares para el dashboard
    public String[] getUsuariosEstadosLabels() {
        return getUsuariosPorEstado().keySet().toArray(new String[0]);
    }

    public Long[] getUsuariosEstadosData() {
        return getUsuariosPorEstado().values().toArray(new Long[0]);
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

    public List<SelectItem> getListaEstados() {
        if (listaEstados == null) {
            listaEstados = new ArrayList<>();
            listaEstados.add(new SelectItem("Activo", "Activo"));
            listaEstados.add(new SelectItem("Inactivo", "Inactivo"));
        }
        return listaEstados;
    }

    public String prepararCrearUsuario() {
        this.user = new Usuario();
        this.rolSeleccionado = 4; // Rol Cliente por defecto
        return "/views/usuario/crearactUsuario.xhtml?faces-redirect=true";
    }

    public String prepararEditarUsuario(Usuario user) {
        this.user = user;
        if (user.getRolIDROL() != null) {
            this.rolSeleccionado = user.getRolIDROL().getIdRol();
        }
        return "/views/usuario/crearactUsuario.xhtml?faces-redirect=true";
    }

    public String guardarUsuario() {
        try {
            if (validarUsuario()) {
                // Asignar rol al usuario
                Rol rol = rolFacade.find(rolSeleccionado);
                user.setRolIDROL(rol);
                
                if (user.getIdUsuario() == null) {
                    usuarioFacade.create(user);
                    mostrarMensaje("Usuario registrado correctamente", FacesMessage.SEVERITY_INFO);
                } else {
                    usuarioFacade.edit(user);
                    mostrarMensaje("Usuario actualizado correctamente", FacesMessage.SEVERITY_INFO);
                }
                
                this.user = new Usuario();
                return "/views/usuario/indexUsuario.xhtml?faces-redirect=true";
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar usuario", e);
            mostrarMensaje("Error al guardar usuario: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
        return null;
    }

    public void eliminarUsuario(Usuario user) {
        try {
            this.usuarioFacade.remove(user);
            mostrarMensaje("Usuario eliminado correctamente", FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar usuario", e);
            mostrarMensaje("Error al eliminar usuario: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    private boolean validarUsuario() {
        if (user.getNombreUsuario() == null || user.getNombreUsuario().trim().isEmpty()) {
            mostrarMensaje("El nombre es obligatorio", FacesMessage.SEVERITY_ERROR);
            return false;
        }
        if (user.getCorreoUsuario() == null || user.getCorreoUsuario().trim().isEmpty()) {
            mostrarMensaje("El correo es obligatorio", FacesMessage.SEVERITY_ERROR);
            return false;
        }
        if (user.getPaswordUsuario() == null || user.getPaswordUsuario().trim().isEmpty()) {
            mostrarMensaje("La contraseña es obligatoria", FacesMessage.SEVERITY_ERROR);
            return false;
        }
        return true;
    }
    
     public String registrarCliente() {
        try {
            // Validar campos obligatorios
            if (!validarUsuario()) {
                return null;
            }
            
            // Verificar si el usuario ya existe
            if (usuarioFacade.findByDocumento(user.getNumeroDocumento()) != null) {
                mostrarMensaje("El número de documento ya está registrado", FacesMessage.SEVERITY_ERROR);
                return null;
            }
            
            if (usuarioFacade.existeCorreo(user.getCorreoUsuario())) {
                mostrarMensaje("El correo electrónico ya está registrado", FacesMessage.SEVERITY_ERROR);
                return null;
            }
            
            // Asignar rol Cliente (ID 4) por defecto
            this.rolSeleccionado = 4;
            Rol rolCliente = rolFacade.find(rolSeleccionado);
            user.setRolIDROL(rolCliente);
            
            // Establecer estado Activo por defecto
            user.setEstadoUsuario("Activo");
            
            // Guardar el usuario
            usuarioFacade.create(user);
            
            // Mensaje de éxito mejorado
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO,
            "¡Registro exitoso!", 
            "Gracias por registrarte. Ya puedes iniciar sesión con tus credenciales."));
        
        this.user = new Usuario();
        return "/login.xhtml?faces-redirect=true";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al registrar cliente", e);
            mostrarMensaje("Error al registrar: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            return null;
        }
    }

    private void mostrarMensaje(String mensaje, FacesMessage.Severity severidad) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, mensaje, null));
    }
}
