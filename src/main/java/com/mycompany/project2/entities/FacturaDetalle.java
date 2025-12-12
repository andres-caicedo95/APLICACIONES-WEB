package com.mycompany.project2.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "factura_detalle")
public class FacturaDetalle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", referencedColumnName = "ID_FACTURA", nullable = false)
    private Factura facturaIDFACTURA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", referencedColumnName = "ID_PRODUCTO", nullable = false)
    private Producto productoIDPRODUCTO;

    @NotNull
    @Column(name = "cantidad")
    private Integer cantidad;

    @NotNull
    @Column(name = "precio_unitario", precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull
    @Column(name = "subtotal", precision = 12, scale = 2)
    private BigDecimal subtotal;

    public FacturaDetalle() {}

    // Getters / Setters
    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }

    public Factura getFacturaIDFACTURA() { return facturaIDFACTURA; }
    public void setFacturaIDFACTURA(Factura facturaIDFACTURA) { this.facturaIDFACTURA = facturaIDFACTURA; }

    public Producto getProductoIDPRODUCTO() { return productoIDPRODUCTO; }
    public void setProductoIDPRODUCTO(Producto productoIDPRODUCTO) { this.productoIDPRODUCTO = productoIDPRODUCTO; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
