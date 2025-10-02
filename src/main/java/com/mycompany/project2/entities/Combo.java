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
@Table(name = "combo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Combo.findAll", query = "SELECT c FROM Combo c"),
    @NamedQuery(name = "Combo.findByIdCombo", query = "SELECT c FROM Combo c WHERE c.idCombo = :idCombo")})
public class Combo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_combo")
    private Integer idCombo;
    @JoinColumn(name = "producto_ID_PRODUCTO", referencedColumnName = "ID_PRODUCTO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producto productoIDPRODUCTO;
    @JoinColumn(name = "promociones_id_PROMOCION", referencedColumnName = "id_PROMOCION")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Promociones promocionesidPROMOCION;

    public Combo() {
    }

    public Combo(Integer idCombo) {
        this.idCombo = idCombo;
    }

    public Integer getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(Integer idCombo) {
        this.idCombo = idCombo;
    }

    public Producto getProductoIDPRODUCTO() {
        return productoIDPRODUCTO;
    }

    public void setProductoIDPRODUCTO(Producto productoIDPRODUCTO) {
        this.productoIDPRODUCTO = productoIDPRODUCTO;
    }

    public Promociones getPromocionesidPROMOCION() {
        return promocionesidPROMOCION;
    }

    public void setPromocionesidPROMOCION(Promociones promocionesidPROMOCION) {
        this.promocionesidPROMOCION = promocionesidPROMOCION;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCombo != null ? idCombo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Combo)) {
            return false;
        }
        Combo other = (Combo) object;
        if ((this.idCombo == null && other.idCombo != null) || (this.idCombo != null && !this.idCombo.equals(other.idCombo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.project2.entities.Combo[ idCombo=" + idCombo + " ]";
    }
    
}
