/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.project2.services;

import com.mycompany.project2.entities.Prototipo;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author user
 */
@Local
public interface PrototipoFacadeLocal {

    void create(Prototipo prototipo);

    void edit(Prototipo prototipo);

    void remove(Prototipo prototipo);

    Prototipo find(Object id);

    List<Prototipo> findAll();

    List<Prototipo> findRange(int[] range);

    int count();
    
}
