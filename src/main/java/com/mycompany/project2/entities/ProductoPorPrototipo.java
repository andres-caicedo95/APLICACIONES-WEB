/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project2.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author user
 */
@Entity
@Table(name = "producto_por_prototipo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductoPorPrototipo.findAll", query = "SELECT p FROM ProductoPorPrototipo p"),
    @NamedQuery(name = "ProductoPorPrototipo.findByIdPrototipoProducto", query = "SELECT p FROM ProductoPorPrototipo p WHERE p.idPrototipoProducto = :idPrototipoProducto")})
public class ProductoPorPrototipo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_PROTOTIPO_PRODUCTO")
    private Integer idPrototipoProducto;
    @JoinColumn(name = "producto_ID_PRODUCTO", referencedColumnName = "ID_PRODUCTO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producto productoIDPRODUCTO;
    @JoinColumn(name = "prototipo_ID_PROTOTIPO", referencedColumnName = "ID_PROTOTIPO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Prototipo prototipoIDPROTOTIPO;

    public ProductoPorPrototipo() {
    }

    public ProductoPorPrototipo(Integer idPrototipoProducto) {
        this.idPrototipoProducto = idPrototipoProducto;
    }

    public Integer getIdPrototipoProducto() {
        return idPrototipoProducto;
    }

    public void setIdPrototipoProducto(Integer idPrototipoProducto) {
        this.idPrototipoProducto = idPrototipoProducto;
    }

    public Producto getProductoIDPRODUCTO() {
        return productoIDPRODUCTO;
    }

    public void setProductoIDPRODUCTO(Producto productoIDPRODUCTO) {
        this.productoIDPRODUCTO = productoIDPRODUCTO;
    }

    public Prototipo getPrototipoIDPROTOTIPO() {
        return prototipoIDPROTOTIPO;
    }

    public void setPrototipoIDPROTOTIPO(Prototipo prototipoIDPROTOTIPO) {
        this.prototipoIDPROTOTIPO = prototipoIDPROTOTIPO;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPrototipoProducto != null ? idPrototipoProducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductoPorPrototipo)) {
            return false;
        }
        ProductoPorPrototipo other = (ProductoPorPrototipo) object;
        if ((this.idPrototipoProducto == null && other.idPrototipoProducto != null) || (this.idPrototipoProducto != null && !this.idPrototipoProducto.equals(other.idPrototipoProducto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.project2.entities.ProductoPorPrototipo[ idPrototipoProducto=" + idPrototipoProducto + " ]";
    }
    
}
