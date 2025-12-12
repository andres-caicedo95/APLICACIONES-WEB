package com.mycompany.project2.services;

import com.mycompany.project2.entities.Cliente;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.NoResultException;

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
     */
    @Override
    public long countByEstado(String estado) {
        Long result = (Long) em.createQuery(
                "SELECT COUNT(c) FROM Cliente c WHERE c.estadoCliente = :estado")
                .setParameter("estado", estado)
                .getSingleResult();
        return result != null ? result : 0L;
    }

    // ✅ AGREGAR ESTE MÉTODO
    /**
     * Busca un cliente por su correo electrónico.
     */
    @Override
    public Cliente findByCorreo(String correo) {
        try {
            // Usar LOWER para búsqueda case-insensitive
            return (Cliente) em.createQuery(
                "SELECT c FROM Cliente c WHERE LOWER(c.correoCliente) = LOWER(:correo)")
                .setParameter("correo", correo.trim())
                .getSingleResult();
        } catch (NoResultException e) {
            return null; // No se encontró cliente
        }
    }
}