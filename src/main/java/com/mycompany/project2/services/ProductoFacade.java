/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt 
 * to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java 
 * to edit this template
 */
package com.mycompany.project2.services;

import com.mycompany.project2.entities.Producto;
import java.util.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Fachada para la entidad Producto.
 * Incluye métodos personalizados usados en el Dashboard (categorías, stock, etc.)
 * 
 * Esta versión corrige ClassCastException al trabajar con enums en categoríaProducto.
 * 
 * @author user
 */
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

    /**
     * Devuelve todas las categorías únicas existentes en los productos.
     * @return lista de nombres de categorías.
     */
    @Override
    public List<String> findCategoriasUnicas() {
        List<Object> resultados = em.createQuery(
            "SELECT DISTINCT p.categoriaProducto FROM Producto p")
            .getResultList();

        List<String> categorias = new ArrayList<>();
        for (Object obj : resultados) {
            categorias.add(String.valueOf(obj)); // ✅ Convierte enum o string a texto
        }
        return categorias;
    }

    /**
     * Devuelve todos los estados únicos de los productos.
     * @return lista de estados (ejemplo: ACTIVO, INACTIVO)
     */
    @Override
    public List<String> findEstadosUnicos() {
        return em.createQuery(
            "SELECT DISTINCT p.estadoProducto FROM Producto p WHERE p.estadoProducto IS NOT NULL",
            String.class)
            .getResultList();
    }

    /**
     * Busca productos por su estado (ACTIVO / INACTIVO)
     * @param estado valor del campo estadoProducto
     * @return lista de productos que coinciden
     */
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

    /**
     * Cuenta productos agrupados por su categoría.
     * Convierte correctamente enums a String.
     * 
     * @return Mapa (nombreCategoria -> cantidad)
     */
    @Override
    public Map<String, Long> countByCategoria() {
        List<Object[]> resultados = em.createQuery(
            "SELECT p.categoriaProducto, COUNT(p) FROM Producto p GROUP BY p.categoriaProducto",
            Object[].class)
            .getResultList();

        Map<String, Long> mapa = new LinkedHashMap<>();
        for (Object[] fila : resultados) {
            mapa.put(String.valueOf(fila[0]), (Long) fila[1]); // ✅ conversión segura
        }
        return mapa;
    }

    /**
     * Devuelve los productos con mayor stock.
     * @param cantidad número máximo de resultados
     * @return lista de productos ordenados por stock descendente
     */
    @Override
    public List<Producto> findTopByStock(int cantidad) {
        return em.createQuery(
            "SELECT p FROM Producto p ORDER BY p.stockProduccto DESC", Producto.class)
            .setMaxResults(cantidad)
            .getResultList();
    }
}
