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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "prototipo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Prototipo.findAll", query = "SELECT p FROM Prototipo p"),
    @NamedQuery(name = "Prototipo.findByIdPrototipo", query = "SELECT p FROM Prototipo p WHERE p.idPrototipo = :idPrototipo"),
    @NamedQuery(name = "Prototipo.findByFechaPrototipo", query = "SELECT p FROM Prototipo p WHERE p.fechaPrototipo = :fechaPrototipo"),
    @NamedQuery(name = "Prototipo.findByDescripcionPrototipo", query = "SELECT p FROM Prototipo p WHERE p.descripcionPrototipo = :descripcionPrototipo")})
public class Prototipo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_PROTOTIPO")
    private Integer idPrototipo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_PROTOTIPO")
    @Temporal(TemporalType.DATE)
    private Date fechaPrototipo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "DESCRIPCION_PROTOTIPO")
    private String descripcionPrototipo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prototipoIDPROTOTIPO", fetch = FetchType.LAZY)
    private List<ProductoPorPrototipo> productoPorPrototipoList;
    @JoinColumn(name = "cliente_ID_CLIENTE", referencedColumnName = "ID_CLIENTE")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cliente clienteIDCLIENTE;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prototipoIDPROTOTIPO", fetch = FetchType.LAZY)
    private List<Factura> facturaList;

    public Prototipo() {
    }

    public Prototipo(Integer idPrototipo) {
        this.idPrototipo = idPrototipo;
    }

    public Prototipo(Integer idPrototipo, Date fechaPrototipo, String descripcionPrototipo) {
        this.idPrototipo = idPrototipo;
        this.fechaPrototipo = fechaPrototipo;
        this.descripcionPrototipo = descripcionPrototipo;
    }

    public Integer getIdPrototipo() {
        return idPrototipo;
    }

    public void setIdPrototipo(Integer idPrototipo) {
        this.idPrototipo = idPrototipo;
    }

    public Date getFechaPrototipo() {
        return fechaPrototipo;
    }

    public void setFechaPrototipo(Date fechaPrototipo) {
        this.fechaPrototipo = fechaPrototipo;
    }

    public String getDescripcionPrototipo() {
        return descripcionPrototipo;
    }

    public void setDescripcionPrototipo(String descripcionPrototipo) {
        this.descripcionPrototipo = descripcionPrototipo;
    }

    @XmlTransient
    public List<ProductoPorPrototipo> getProductoPorPrototipoList() {
        return productoPorPrototipoList;
    }

    public void setProductoPorPrototipoList(List<ProductoPorPrototipo> productoPorPrototipoList) {
        this.productoPorPrototipoList = productoPorPrototipoList;
    }

    public Cliente getClienteIDCLIENTE() {
        return clienteIDCLIENTE;
    }

    public void setClienteIDCLIENTE(Cliente clienteIDCLIENTE) {
        this.clienteIDCLIENTE = clienteIDCLIENTE;
    }

    @XmlTransient
    public List<Factura> getFacturaList() {
        return facturaList;
    }

    public void setFacturaList(List<Factura> facturaList) {
        this.facturaList = facturaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPrototipo != null ? idPrototipo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Prototipo)) {
            return false;
        }
        Prototipo other = (Prototipo) object;
        if ((this.idPrototipo == null && other.idPrototipo != null) || (this.idPrototipo != null && !this.idPrototipo.equals(other.idPrototipo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.project2.entities.Prototipo[ idPrototipo=" + idPrototipo + " ]";
    }
    
}
