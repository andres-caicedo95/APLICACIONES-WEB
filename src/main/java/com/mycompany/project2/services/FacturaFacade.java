package com.mycompany.project2.services;

import com.mycompany.project2.entities.Factura;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Fachada para la entidad Factura.
 * Permite consultar y agrupar datos de facturación
 * para reportes y dashboard.
 */
@Stateless
public class FacturaFacade extends AbstractFacade<Factura> implements FacturaFacadeLocal {

    @PersistenceContext(unitName = "com.mycompany_project2_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FacturaFacade() {
        super(Factura.class);
    }

    // =======================================================
    // NUEVO MÉTODO PARA DASHBOARD (Gráfico Ventas por Vendedor)
    // =======================================================
    /**
     * Retorna la cantidad de facturas emitidas por cada vendedor
     * en el rango de fechas especificado.
     *
     * @param fechaInicio Fecha inicial del rango
     * @param fechaFin Fecha final del rango
     * @return Mapa con nombre del vendedor y número de facturas
     */
    public Map<String, Long> countByVendedor(Date fechaInicio, Date fechaFin) {
    List<Object[]> resultados = em.createNativeQuery(
        "SELECT CONCAT(u.NOMBRE_USUARIO, ' ', u.APELLIDO_USUARIO) AS vendedor, COUNT(f.ID_FACTURA) AS total " +
        "FROM factura f " +
        "JOIN usuario u ON f.usuario_ID_USUARIO_VENDEDOR = u.ID_USUARIO " +
        "WHERE f.FECHA_FACTURA BETWEEN ? AND ? " +
        "GROUP BY u.ID_USUARIO, u.NOMBRE_USUARIO, u.APELLIDO_USUARIO " +
        "ORDER BY total DESC"
    )
    .setParameter(1, new java.sql.Date(fechaInicio.getTime()))
    .setParameter(2, new java.sql.Date(fechaFin.getTime()))
    .getResultList();

    Map<String, Long> data = new LinkedHashMap<>();
    for (Object[] fila : resultados) {
        String vendedor = (String) fila[0];
        Long cantidad = ((Number) fila[1]).longValue();
        data.put(vendedor != null ? vendedor : "Desconocido", cantidad);
    }

    return data;
}

}
