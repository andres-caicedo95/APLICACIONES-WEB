/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project2.services;

import com.mycompany.project2.entities.Cliente;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Fachada para operaciones con la entidad Cliente.
 * Incluye métodos personalizados para métricas del dashboard.
 * 
 * @author user
 */
@Stateless
public class ClienteFacade extends AbstractFacade<Cliente> implements ClienteFacadeLocal {

    @PersistenceContext(unitName = "com.mycompany_project2_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClienteFacade() {
        super(Cliente.class);
    }

    /**
     * Cuenta la cantidad de clientes según su estado (ACTIVO/INACTIVO)
     * @param estado valor del campo estadoCliente
     * @return número de clientes encontrados
     */
    public long countByEstado(String estado) {
        Long result = (Long) em.createQuery(
                "SELECT COUNT(c) FROM Cliente c WHERE c.estadoCliente = :estado")
                .setParameter("estado", estado)
                .getSingleResult();
        return result != null ? result : 0L;
    }
}
