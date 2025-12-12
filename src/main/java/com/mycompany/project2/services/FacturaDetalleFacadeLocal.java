package com.mycompany.project2.services;

import com.mycompany.project2.entities.FacturaDetalle;
import java.util.List;
import javax.ejb.Local;

@Local
public interface FacturaDetalleFacadeLocal {

    void create(FacturaDetalle detalle);

    void edit(FacturaDetalle detalle);

    void remove(FacturaDetalle detalle);

    FacturaDetalle find(Object id);

    List<FacturaDetalle> findAll();

    List<FacturaDetalle> findRange(int[] range);

    int count();

    List<FacturaDetalle> findByFactura(Integer idFactura);
}
