/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.project2.services;

import com.mycompany.project2.entities.Usuario;
import java.util.List;
import javax.ejb.Local;

@Local
public interface UsuarioFacadeLocal {
    void create(Usuario usuario);
    void edit(Usuario usuario);
    void remove(Usuario usuario);
    boolean existeCorreo(String correo);
    Usuario find(Object id);
    List<Usuario> findAll();
    List<Usuario> findRange(int[] range);
    int count();
    Usuario iniciarSesion(String usuario, String password);
    List<Usuario> findByRol(Integer idRol);
    Usuario findByDocumento(String numeroDocumento);
    
    // ✅ AÑADIDO: Método faltante para correo masivo
    List<Usuario> findByEstado(String estado);
}
