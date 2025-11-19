package com.mycompany.project2.services;

import com.mycompany.project2.entities.PedidoItem;
import java.math.BigDecimal;
import java.util.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PedidoItemFacade implements PedidoItemFacadeLocal {

@PersistenceContext(unitName = "com.mycompany_project2_war_1.0PU")
private EntityManager em;

@Override
public void create(PedidoItem item) {
    em.persist(item);
    em.flush();
}

@Override
public PedidoItem edit(PedidoItem item) {
    return em.merge(item);
}

@Override
public void remove(PedidoItem item) {
    PedidoItem attached = em.merge(item);
    em.remove(attached);
}

@Override
public PedidoItem find(Integer id) {
    return em.find(PedidoItem.class, id);
}

@Override
public List<PedidoItem> findAll() {
    return em.createQuery("SELECT i FROM PedidoItem i", PedidoItem.class).getResultList();
}

// ======================================
// NUEVOS MÉTODOS PARA DASHBOARD
// ======================================

/**
 * Calcula el total de ventas por día en el rango indicado.
 * Se utiliza en la gráfica "Ventas Diarias (últimos 30 días)".
 */
@SuppressWarnings("unchecked")
public Map<Date, BigDecimal> totalVentasPorDia(Date desde, Date hasta) {
    List<Object[]> rows = em.createNativeQuery(
        "SELECT DATE(p.fecha_pedido) AS dia, SUM(pi.cantidad * pi.precio_unitario) AS total " +
        "FROM pedidos p " +
        "JOIN pedido_items pi ON p.id_pedido = pi.id_pedido " +
        "WHERE p.fecha_pedido BETWEEN ? AND ? " +
        "GROUP BY DATE(p.fecha_pedido) ORDER BY DATE(p.fecha_pedido)"
    )
    .setParameter(1, new java.sql.Timestamp(desde.getTime()))
    .setParameter(2, new java.sql.Timestamp(hasta.getTime()))
    .getResultList();

    Map<Date, BigDecimal> data = new LinkedHashMap<>();
    for (Object[] r : rows) {
        java.sql.Date fecha = (java.sql.Date) r[0];
        BigDecimal total = (r[1] != null)
                ? new BigDecimal(r[1].toString())
                : BigDecimal.ZERO;
        data.put(new Date(fecha.getTime()), total);
    }
    return data;
}

/**
 * Devuelve los productos más vendidos según la cantidad total vendida.
 * Usado en el gráfico "Top 5 Productos Más Vendidos".
 */
@SuppressWarnings("unchecked")
public List<Object[]> topProducts(int limit) {
    return em.createNativeQuery(
        "SELECT pr.NOMBRE_PRODUCTO, SUM(pi.cantidad) AS cantidad, " +
        "SUM(pi.cantidad * pi.precio_unitario) AS total " +
        "FROM pedido_items pi " +
        "JOIN producto pr ON pr.ID_PRODUCTO = pi.id_producto " +
        "GROUP BY pr.NOMBRE_PRODUCTO " +
        "ORDER BY cantidad DESC LIMIT ?"
    )
    .setParameter(1, limit)
    .getResultList();
}

}
