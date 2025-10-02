package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Producto;
import com.mycompany.project2.services.ProductoFacadeLocal;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(value = "catalogoController")
@SessionScoped
public class CatalogoController implements Serializable {
    
    @EJB
    private ProductoFacadeLocal productoFacade;
    
    // ✅ Getter para usar en la vista
    public List<Producto> getProductosActivos() {
        return productoFacade.findByEstado("Activo");
    }
    
    // ✅ Método para agregar al carrito (opcional, si quieres evitar usar CarritoBean)
    public void agregarAlCarrito(Producto producto) {
        // Aquí podrías agregar lógica directa o delegar a CarritoBean
        // Pero mejor usar CarritoBean si ya lo tienes
    }
}
