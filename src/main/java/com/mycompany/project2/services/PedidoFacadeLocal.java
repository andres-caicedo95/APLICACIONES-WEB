package com.mycompany.project2.services;

import com.mycompany.project2.entities.Pedido;
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

    public Map<String, Long> countByEstado();
}
