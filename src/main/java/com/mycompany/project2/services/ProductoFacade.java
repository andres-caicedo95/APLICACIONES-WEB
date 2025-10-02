package com.mycompany.project2.services;

import com.mycompany.project2.entities.Producto;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ProductoFacade extends AbstractFacade<Producto> implements ProductoFacadeLocal {

    @PersistenceContext(unitName = "com.mycompany_project2_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductoFacade() {
        super(Producto.class);
    }

    @Override
    public List<String> findCategoriasUnicas() {
        return em.createQuery(
            "SELECT DISTINCT p.categoriaProducto FROM Producto p", String.class)
            .getResultList();
    }

    @Override
    public List<String> findEstadosUnicos() {
        return em.createQuery(
            "SELECT DISTINCT p.estadoProducto FROM Producto p WHERE p.estadoProducto IS NOT NULL", String.class)
            .getResultList();
    }

    @Override
    public List<Producto> findByEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return findAll();
        }
        return em.createQuery(
            "SELECT p FROM Producto p WHERE p.estadoProducto = :estado", Producto.class)
            .setParameter("estado", estado)
            .getResultList();
    }
}

