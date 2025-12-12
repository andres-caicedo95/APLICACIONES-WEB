package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Pedido;
import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.services.PedidoFacadeLocal;
import com.mycompany.project2.services.UsuarioFacadeLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

@Named("domicilioDomiciliarioController")
@ViewScoped
public class domicilioDomiciliarioController implements Serializable {

    @EJB
    private PedidoFacadeLocal pedidoFacade;
    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    private Usuario domiciliario;
    private List<Pedido> pedidosPendientes;
    private List<Pedido> misPedidos;
    private List<Pedido> misPedidosActivos;
    private List<Pedido> misPedidosEntregados;

    @PostConstruct
    public void init() {

        FacesContext faces = FacesContext.getCurrentInstance();

        // Si se crea fuera del ciclo JSF, simplemente no ejecutamos aún
        if (faces == null) {
            return;
        }

        HttpSession session = (HttpSession) faces.getExternalContext().getSession(false);

        if (session != null) {
            this.domiciliario = (Usuario) session.getAttribute("usuario");
        }

        cargarPedidos();
    }

    private void cargarPedidos() {

        // Pedidos PENDIENTES
        this.pedidosPendientes = pedidoFacade.findPendientes();
        if (pedidosPendientes == null) {
            pedidosPendientes = new ArrayList<>();
        }

        // Si no hay domiciliario en sesión, no cargamos nada de mis pedidos
        if (domiciliario != null) {
            this.misPedidos = pedidoFacade.findByDomiciliario(domiciliario);
            if (misPedidos == null) {
                misPedidos = new ArrayList<>();
            }
            // NUEVO: separar los pedidos entregados del resto
            this.misPedidosActivos = new ArrayList<>();
            this.misPedidosEntregados = new ArrayList<>();

            for (Pedido p : misPedidos) {
                if ("entregado".equalsIgnoreCase(p.getEstado())) {
                    misPedidosEntregados.add(p);
                } else {
                    misPedidosActivos.add(p);
                }
            }

        } else {
            misPedidos = new ArrayList<>();
            misPedidosActivos = new ArrayList<>();
            misPedidosEntregados = new ArrayList<>();
        }
    }

    public String asignarmePedido(Pedido pedido) {
        try {
            pedido.setUsuarioDomiciliario(domiciliario);
            pedido.setEstado("asignado");

            pedidoFacade.edit(pedido);
            cargarPedidos();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Pedido asignado correctamente", ""));

            return "/views/domicilioDomiciliario/misDomicilios.xhtml?faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al asignar el pedido", ""));

            return null;
        }
    }

    public String cambiarEstado(Pedido pedido, String estado) {
        pedido.setEstado(estado);
        pedidoFacade.edit(pedido);

        cargarPedidos();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Estado actualizado a: " + estado, ""));

        return null;
    }

    // Getters
    public List<Pedido> getPedidosPendientes() {
        return pedidosPendientes;
    }

    public List<Pedido> getMisPedidos() {
        return misPedidos;
    }
    
    public List<Pedido> getMisPedidosActivos() {
    return misPedidosActivos;
}

public List<Pedido> getMisPedidosEntregados() {
    return misPedidosEntregados;
}


}
