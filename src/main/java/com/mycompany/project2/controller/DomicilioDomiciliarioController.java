package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Domicilios;
import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.services.DomiciliosFacadeLocal;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

@Named(value = "domicilioDomiciliarioController")
@SessionScoped
public class DomicilioDomiciliarioController implements Serializable {

    @EJB
    private DomiciliosFacadeLocal domiciliosFacade;

    private Usuario domiciliario;
    private List<Domicilios> domiciliosPendientes;
    private List<Domicilios> misDomicilios;

    @PostConstruct
    public void init() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        this.domiciliario = (Usuario) session.getAttribute("usuarioLogueado");
        cargarDomicilios();
    }

    private void cargarDomicilios() {
        this.domiciliosPendientes = domiciliosFacade.findPendientes();
        this.misDomicilios = domiciliosFacade.findByDomiciliario(domiciliario);
    }

    public String asignarmeDomicilio(Domicilios domicilio) {
        try {
            domicilio.setUsuarioIDUSUARIODOMICILIO(domiciliario);
            domicilio.setEstado("ASIGNADO");
            domiciliosFacade.edit(domicilio);
            cargarDomicilios();
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Domicilio asignado correctamente", ""));
            
            return "/views/domicilioDomiciliario/misDomicilios.xhtml?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error al asignar domicilio", ""));
            return null;
        }
    }

    public String cambiarEstado(Domicilios domicilio, String estado) {
        domicilio.setEstado(estado);
        domiciliosFacade.edit(domicilio);
        cargarDomicilios();
        
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
            "Estado actualizado a: " + estado, ""));
        
        return null;
    }

    // Getters
    public List<Domicilios> getDomiciliosPendientes() {
        return domiciliosPendientes;
    }

    public List<Domicilios> getMisDomicilios() {
        return misDomicilios;
    }
}