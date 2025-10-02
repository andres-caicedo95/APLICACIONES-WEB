/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.project2.services;

import com.mycompany.project2.entities.ProductoPorPrototipo;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author user
 */
@Local
public interface ProductoPorPrototipoFacadeLocal {

    void create(ProductoPorPrototipo productoPorPrototipo);

    void edit(ProductoPorPrototipo productoPorPrototipo);

    void remove(ProductoPorPrototipo productoPorPrototipo);

    ProductoPorPrototipo find(Object id);

    List<ProductoPorPrototipo> findAll();

    List<ProductoPorPrototipo> findRange(int[] range);

    int count();
    
}
