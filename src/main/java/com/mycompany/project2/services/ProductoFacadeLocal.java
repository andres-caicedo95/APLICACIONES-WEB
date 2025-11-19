/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.project2.services;

import com.mycompany.project2.entities.Producto;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author user
 */
@Local
public interface ProductoFacadeLocal {

    void create(Producto producto);

    void edit(Producto producto);

    void remove(Producto producto);

    Producto find(Object id);

    List<Producto> findAll();

    List<Producto> findRange(int[] range);

    int count();
    List<String> findCategoriasUnicas();
    List<String> findEstadosUnicos();

    public List<Producto> findByEstado(String activo);

    public Map<String, Long> countByCategoria();

    public List<Producto> findTopByStock(int i);
    
}
