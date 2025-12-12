package com.mycompany.project2.services;

import com.mycompany.project2.entities.FacturaDetalle;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FacturaDetalleFacade extends AbstractFacade<FacturaDetalle> implements FacturaDetalleFacadeLocal {

    @PersistenceContext(unitName = "com.mycompany_project2_war_1.0PU")
    private EntityManager em;

    public FacturaDetalleFacade() {
        super(FacturaDetalle.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<FacturaDetalle> findByFactura(Integer idFactura) {
        return em.createQuery(
                "SELECT d FROM FacturaDetalle d WHERE d.facturaIDFACTURA.idFactura = :id",
                FacturaDetalle.class
        )
        .setParameter("id", idFactura)
        .getResultList();
    }
}
