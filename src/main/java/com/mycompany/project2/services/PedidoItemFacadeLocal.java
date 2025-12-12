package com.mycompany.project2.services;

import com.mycompany.project2.entities.PedidoItem;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

@Local
public interface PedidoItemFacadeLocal {
    void create(PedidoItem item);
    PedidoItem edit(PedidoItem item);
    void remove(PedidoItem item);
    PedidoItem find(Integer id);
    List<PedidoItem> findAll();
    
    List<PedidoItem> findByPedido(Integer idPedido);

    public Map<Date, BigDecimal> totalVentasPorDia(Date desde, Date hasta);

    public List<Object[]> topProducts(int i);
}
