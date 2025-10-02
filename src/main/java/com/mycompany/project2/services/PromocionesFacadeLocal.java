/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.project2.services;

import com.mycompany.project2.entities.Promociones;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author user
 */
@Local
public interface PromocionesFacadeLocal {

    void create(Promociones promociones);

    void edit(Promociones promociones);

    void remove(Promociones promociones);

    Promociones find(Object id);

    List<Promociones> findAll();

    List<Promociones> findRange(int[] range);

    int count();
    
}
