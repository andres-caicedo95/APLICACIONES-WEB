package com.mycompany.project2.services;

import com.mycompany.project2.entities.Pedido;
import com.mycompany.project2.entities.Usuario;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PedidoFacade implements PedidoFacadeLocal {

    @PersistenceContext(unitName = "com.mycompany_project2_war_1.0PU")
    private EntityManager em;

    @Override
    public void create(Pedido pedido) {
        em.persist(pedido);
        em.flush();
    }

    @Override
    public Pedido edit(Pedido pedido) {
        return em.merge(pedido);
    }

    @Override
    public void remove(Pedido pedido) {
        Pedido attached = em.merge(pedido);
        em.remove(attached);
    }

    @Override
    public Pedido find(Integer id) {
        return em.find(Pedido.class, id);
    }

    @Override
    public List<Pedido> findAll() {
        return em.createQuery("SELECT p FROM Pedido p", Pedido.class).getResultList();
    }

    // ======================================
    // DOMICILIARIO - PENDIENTES
    // ======================================
    public List<Pedido> findPendientes() {
        return em.createQuery("SELECT p FROM Pedido p "
                + "WHERE p.estado = :estado "
                + "AND p.usuarioDomiciliario IS NULL",
                Pedido.class
        )
                .setParameter("estado", "pendiente")
                .getResultList();
    }

    // ======================================
    // DOMICILIARIO - MIS PEDIDOS
    // ======================================
    @Override
    public List<Pedido> findByDomiciliario(Usuario usuario) {
        if (usuario == null) {
            return new ArrayList<>();
        }

        return em.createQuery("SELECT p FROM Pedido p WHERE p.usuarioDomiciliario.idUsuario = :id", Pedido.class)
                .setParameter("id", usuario.getIdUsuario())
                .getResultList();
    }

    // ======================================
    // MÉTODO PARA DASHBOARD
    // ======================================
    @Override
    public Map<String, Long> countByEstado() {
        List<Object[]> rows = em.createQuery(
                "SELECT p.estado, COUNT(p) FROM Pedido p GROUP BY p.estado",
                Object[].class
        ).getResultList();

        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] r : rows) {
            String estado = (String) r[0];
            Long cantidad = (Long) r[1];
            data.put(estado != null ? estado : "Sin estado", cantidad);
        }
        return data;
    }
}
