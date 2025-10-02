/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.project2.services;

import com.mycompany.project2.entities.Combo;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author user
 */
@Local
public interface ComboFacadeLocal {

    void create(Combo combo);

    void edit(Combo combo);

    void remove(Combo combo);

    Combo find(Object id);

    List<Combo> findAll();

    List<Combo> findRange(int[] range);

    int count();
    
}
