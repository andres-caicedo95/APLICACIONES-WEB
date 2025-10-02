/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project2.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author user
 */
@Entity
@Table(name = "promociones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Promociones.findAll", query = "SELECT p FROM Promociones p"),
    @NamedQuery(name = "Promociones.findByIdPROMOCION", query = "SELECT p FROM Promociones p WHERE p.idPROMOCION = :idPROMOCION"),
    @NamedQuery(name = "Promociones.findByNombrePromocion", query = "SELECT p FROM Promociones p WHERE p.nombrePromocion = :nombrePromocion"),
    @NamedQuery(name = "Promociones.findByValorPromocion", query = "SELECT p FROM Promociones p WHERE p.valorPromocion = :valorPromocion"),
    @NamedQuery(name = "Promociones.findByFecha", query = "SELECT p FROM Promociones p WHERE p.fecha = :fecha")})
public class Promociones implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_PROMOCION")
    private Integer idPROMOCION;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "NOMBRE_PROMOCION")
    private String nombrePromocion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "VALOR_PROMOCION")
    private long valorPromocion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "promocionesidPROMOCION", fetch = FetchType.LAZY)
    private List<Descuentos> descuentosList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "promocionesidPROMOCION", fetch = FetchType.LAZY)
    private List<Combo> comboList;

    public Promociones() {
    }

    public Promociones(Integer idPROMOCION) {
        this.idPROMOCION = idPROMOCION;
    }

    public Promociones(Integer idPROMOCION, String nombrePromocion, long valorPromocion, Date fecha) {
        this.idPROMOCION = idPROMOCION;
        this.nombrePromocion = nombrePromocion;
        this.valorPromocion = valorPromocion;
        this.fecha = fecha;
    }

    public Integer getIdPROMOCION() {
        return idPROMOCION;
    }

    public void setIdPROMOCION(Integer idPROMOCION) {
        this.idPROMOCION = idPROMOCION;
    }

    public String getNombrePromocion() {
        return nombrePromocion;
    }

    public void setNombrePromocion(String nombrePromocion) {
        this.nombrePromocion = nombrePromocion;
    }

    public long getValorPromocion() {
        return valorPromocion;
    }

    public void setValorPromocion(long valorPromocion) {
        this.valorPromocion = valorPromocion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @XmlTransient
    public List<Descuentos> getDescuentosList() {
        return descuentosList;
    }

    public void setDescuentosList(List<Descuentos> descuentosList) {
        this.descuentosList = descuentosList;
    }

    @XmlTransient
    public List<Combo> getComboList() {
        return comboList;
    }

    public void setComboList(List<Combo> comboList) {
        this.comboList = comboList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPROMOCION != null ? idPROMOCION.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Promociones)) {
            return false;
        }
        Promociones other = (Promociones) object;
        if ((this.idPROMOCION == null && other.idPROMOCION != null) || (this.idPROMOCION != null && !this.idPROMOCION.equals(other.idPROMOCION))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.project2.entities.Promociones[ idPROMOCION=" + idPROMOCION + " ]";
    }
    
}
