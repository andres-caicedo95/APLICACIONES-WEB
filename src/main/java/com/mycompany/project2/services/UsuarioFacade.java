/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project2.services;

import com.mycompany.project2.entities.Usuario;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> implements UsuarioFacadeLocal {

    @PersistenceContext(unitName = "com.mycompany_project2_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    @Override
    public Usuario iniciarSesion(String usuario, String password) {
        Query query = em.createQuery("SELECT u FROM Usuario u WHERE u.correoUsuario = :usuario "
                + "AND u.paswordUsuario = :password AND u.estadoUsuario = 'Activo'");
        query.setParameter("usuario", usuario);
        query.setParameter("password", password);
        try {
            return (Usuario) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Usuario> findByRol(Integer idRol) {
        Query query = em.createQuery("SELECT u FROM Usuario u WHERE u.rolIDROL.idRol = :idRol");
        query.setParameter("idRol", idRol);
        return query.getResultList();
    }
    
    // ✅ AÑADIDO: Método faltante para correo masivo
    @Override
    public List<Usuario> findByEstado(String estado) {
        Query query = em.createQuery("SELECT u FROM Usuario u WHERE u.estadoUsuario = :estado");
        query.setParameter("estado", estado);
        return query.getResultList();
    }
    
    @Override
    public boolean existeCorreo(String correo) {
        try {
            Long count = (Long) em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.correoUsuario = :correo")
                                .setParameter("correo", correo)
                                .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Usuario findByDocumento(String numeroDocumento) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.numeroDocumento = :doc", Usuario.class)
                    .setParameter("doc", numeroDocumento)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
