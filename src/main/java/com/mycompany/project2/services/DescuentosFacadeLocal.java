/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.project2.services;

import com.mycompany.project2.entities.Descuentos;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author user
 */
@Local
public interface DescuentosFacadeLocal {

    void create(Descuentos descuentos);

    void edit(Descuentos descuentos);

    void remove(Descuentos descuentos);

    Descuentos find(Object id);

    List<Descuentos> findAll();

    List<Descuentos> findRange(int[] range);

    int count();
    
}
