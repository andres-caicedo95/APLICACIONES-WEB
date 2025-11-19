/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.project2.services;

import com.mycompany.project2.entities.Domicilios;
import com.mycompany.project2.entities.Usuario;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

@Local
public interface DomiciliosFacadeLocal {

    void create(Domicilios domicilios);
    void edit(Domicilios domicilios);
    void remove(Domicilios domicilios);
    Domicilios find(Object id);
    List<Domicilios> findAll();
    List<Domicilios> findRange(int[] range);
    int count();
    
    // Métodos específicos para domiciliarios
    List<Domicilios> findByDomiciliario(Usuario domiciliario);
    List<Domicilios> findPendientes();
    
    // Métodos para administradores (mantenemos los existentes)
    List<Domicilios> findByEstado(String estado);

    public Map<String, Long> countByEstado();
}

