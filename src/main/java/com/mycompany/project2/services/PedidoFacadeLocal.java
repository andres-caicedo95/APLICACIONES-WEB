package com.mycompany.project2.services;

import com.mycompany.project2.entities.Pedido;
import com.mycompany.project2.entities.Usuario;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

@Local
public interface PedidoFacadeLocal {
    void create(Pedido pedido);
    Pedido edit(Pedido pedido);
    void remove(Pedido pedido);
    Pedido find(Integer id);
    List<Pedido> findAll();
    List<Pedido> findPendientes();

    List<Pedido> findByDomiciliario(Usuario usuario);

    public Map<String, Long> countByEstado();
    
    
}
