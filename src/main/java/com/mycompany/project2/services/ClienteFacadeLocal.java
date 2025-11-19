package com.mycompany.project2.services;

import com.mycompany.project2.entities.Cliente;
import java.util.List;
import javax.ejb.Local;

/**
 * Interfaz local para la fachada de Cliente.
 * Define las operaciones disponibles para EJB y el Dashboard.
 * 
 * @author user
 */
@Local
public interface ClienteFacadeLocal {

    void create(Cliente cliente);

    void edit(Cliente cliente);

    void remove(Cliente cliente);

    Cliente find(Object id);

    List<Cliente> findAll();

    List<Cliente> findRange(int[] range);

    int count();

    /**
     * Cuenta clientes según su estado (ACTIVO / INACTIVO).
     * Usado por el Dashboard para graficar clientes por estado.
     */
    long countByEstado(String estado);
}
