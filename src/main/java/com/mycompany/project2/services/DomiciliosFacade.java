package com.mycompany.project2.services;

import com.mycompany.project2.entities.Domicilios;
import com.mycompany.project2.entities.Usuario;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class DomiciliosFacade extends AbstractFacade<Domicilios> implements DomiciliosFacadeLocal {

    private static final Logger LOGGER = Logger.getLogger(DomiciliosFacade.class.getName());
    
    @PersistenceContext(unitName = "com.mycompany_project2_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DomiciliosFacade() {
        super(Domicilios.class);
    }
    
    @Override
    public void create(Domicilios domicilio) {
        try {
            LOGGER.info("📍 Creando nuevo domicilio: " + domicilio.getDirecccionDomicilio());
            
            // Coordenadas por defecto de Bogotá
            domicilio.setLatitud(4.710989);
            domicilio.setLongitud(-74.072092);
            
            super.create(domicilio);
            LOGGER.info("✅ Domicilio creado exitosamente con ID: " + domicilio.getIdDomicilio());
            
        } catch (Exception e) {
            LOGGER.severe("❌ Error al crear domicilio: " + e.getMessage());
            throw new RuntimeException("Error al crear domicilio: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void edit(Domicilios domicilio) {
        try {
            LOGGER.info("📍 Actualizando domicilio ID: " + domicilio.getIdDomicilio());
            super.edit(domicilio);
            LOGGER.info("✅ Domicilio actualizado exitosamente");
        } catch (Exception e) {
            LOGGER.severe("❌ Error al actualizar domicilio: " + e.getMessage());
            throw new RuntimeException("Error al actualizar domicilio: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Domicilios> findByDomiciliario(Usuario domiciliario) {
        return em.createQuery(
            "SELECT d FROM Domicilios d WHERE d.usuarioIDUSUARIODOMICILIO = :domiciliario "
            + "AND d.estado IN ('ASIGNADO', 'EN_CAMINO') "
            + "ORDER BY d.fechaDomicilio DESC", Domicilios.class)
            .setParameter("domiciliario", domiciliario)
            .getResultList();
    }

    @Override
    public List<Domicilios> findPendientes() {
        return em.createQuery(
            "SELECT d FROM Domicilios d WHERE d.estado = 'PENDIENTE' "
            + "ORDER BY d.fechaDomicilio ASC", Domicilios.class)
            .getResultList();
    }

    @Override
    public List<Domicilios> findByEstado(String estado) {
        return em.createQuery(
            "SELECT d FROM Domicilios d WHERE d.estado = :estado "
            + "ORDER BY d.fechaDomicilio DESC", Domicilios.class)
            .setParameter("estado", estado)
            .getResultList();
    }

    // =====================================================
    // NUEVO MÉTODO PARA DASHBOARD (Gráfico Domicilios/Estado)
    // =====================================================
    /**
     * Retorna la cantidad de domicilios agrupados por estado.
     * Usado para la gráfica "Domicilios por Estado".
     */
    public Map<String, Long> countByEstado() {
        List<Object[]> resultados = em.createQuery(
            "SELECT d.estado, COUNT(d) FROM Domicilios d GROUP BY d.estado",
            Object[].class
        ).getResultList();

        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] fila : resultados) {
            String estado = (String) fila[0];
            Long cantidad = (Long) fila[1];
            data.put(estado != null ? estado : "Sin estado", cantidad);
        }

        return data;
    }
}
