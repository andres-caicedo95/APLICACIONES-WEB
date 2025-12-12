/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project2.services;

import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.security.PasswordUtil;
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
    
    // =======================================================================
    // MODIFICACIÓN NECESARIA PARA HASHEAR NUEVOS USUARIOS O EDICIONES
    // =======================================================================

    /**
     * Sobreescribe el método create para hashear la contraseña del usuario antes de persistirlo.
     */
    @Override
    public void create(Usuario usuario) {
        // Solo hashea si la contraseña no está vacía o ya hasheada (aunque BCrypt lo maneja)
        if (usuario.getPaswordUsuario() != null && !usuario.getPaswordUsuario().isEmpty()) {
            String hashedPassword = PasswordUtil.hashPassword(usuario.getPaswordUsuario());
            usuario.setPaswordUsuario(hashedPassword);
        }
        super.create(usuario);
    }
    
    /**
     * Sobreescribe el método edit para hashear la contraseña si ha sido modificada.
     * Si no se modifica, se asume que ya está hasheada.
     */
    @Override
    public void edit(Usuario usuario) {
        // En una edición, solo hasheamos si se ha proporcionado una nueva contraseña en texto plano.
        // La lógica ideal aquí dependería de cómo maneja el formulario de edición (si el campo de contraseña
        // viene en blanco, significa que no se cambia y no se hashea; si viene con un valor, se asume que es nuevo).
        // Por simplicidad en el Facade, si se recibe una contraseña que no es un hash (ej. texto plano), la hasheamos.
        // Implementación más segura requiere revisar si el campo es un hash válido antes de hashear de nuevo, 
        // pero para evitar errores de salt, aplicaremos el hasheo si parece ser nuevo.
        
        // **Nota: Una lógica más limpia es manejar el hasheo en el servicio/controlador**
        // Sin embargo, para mantenerlo aquí:
        
        // Asume que si la contraseña no empieza con '$', es nueva y debe ser hasheada.
        String passwordActual = usuario.getPaswordUsuario();
        if (passwordActual != null && !passwordActual.startsWith("$2a") && !passwordActual.startsWith("$2b") && !passwordActual.startsWith("$2y")) {
            String hashedPassword = PasswordUtil.hashPassword(passwordActual);
            usuario.setPaswordUsuario(hashedPassword);
        }
        
        super.edit(usuario);
    }

    // =======================================================================
    // EL RESTO DEL CÓDIGO PERMANECE IGUAL
    // =======================================================================

    @Override
    public Usuario iniciarSesion(String usuario, String password) {
        Usuario usuarioValidar;
        try {
            // Se realiza la búsqueda del usuario por correo y estado 'Activo'
            Query query = em.createQuery("SELECT u FROM Usuario u WHERE u.correoUsuario = :usuario AND u.estadoUsuario = 'Activo'");
            query.setParameter("usuario", usuario);
            
            // Se espera un único resultado
            usuarioValidar = (Usuario) query.getSingleResult();
            
            // Se valida la contraseña
            if (PasswordUtil.checkPassword(password, usuarioValidar.getPaswordUsuario())) {
                return usuarioValidar;
            } else {
                // Si la contraseña no coincide, se devuelve null o un objeto vacío
                return null; // O new Usuario(), dependiendo de la implementación de newUsuario()
            }
        
        // Manejo de errores si no se encuentra el usuario
        } catch (NoResultException e) {
            return null; // Si no encuentra usuario, retorna null
        } catch (Exception e) {
            // Manejo de otras excepciones (problemas de BD, etc.)
            e.printStackTrace(); 
            return null;
        }
    }
    
    @Override
    public List<Usuario> findByRol(Integer idRol) {
        Query query = em.createQuery("SELECT u FROM Usuario u WHERE u.rolIDROL.idRol = :idRol");
        query.setParameter("idRol", idRol);
        return query.getResultList();
    }
    
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
        } catch (NoResultException e) { 
             return false;
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