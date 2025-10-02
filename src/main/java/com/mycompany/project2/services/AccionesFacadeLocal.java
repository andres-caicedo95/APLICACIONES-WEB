/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.project2.services;

import com.mycompany.project2.entities.Acciones;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author user
 */
@Local
public interface AccionesFacadeLocal {

    void create(Acciones acciones);

    void edit(Acciones acciones);

    void remove(Acciones acciones);

    Acciones find(Object id);

    List<Acciones> findAll();

    List<Acciones> findRange(int[] range);

    int count();
    
}
