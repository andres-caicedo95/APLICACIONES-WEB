/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project2.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author user
 */
@Entity
@Table(name = "factura")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Factura.findAll", query = "SELECT f FROM Factura f"),
    @NamedQuery(name = "Factura.findByIdFactura", query = "SELECT f FROM Factura f WHERE f.idFactura = :idFactura"),
    @NamedQuery(name = "Factura.findByFechaFactura", query = "SELECT f FROM Factura f WHERE f.fechaFactura = :fechaFactura")})
public class Factura implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_FACTURA")
    private Integer idFactura;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_FACTURA")
    @Temporal(TemporalType.DATE)
    private Date fechaFactura;
    
    // ✅ CAMPOS AGREGADOS PARA EL CARRITO DE COMPRAS
    @Basic(optional = false)
    @NotNull
    @Column(name = "TOTAL_FACTURA")
    private BigDecimal totalFactura;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "ESTADO_FACTURA")
    private String estadoFactura;
    
    @JoinColumn(name = "USUARIO_ID_USUARIO_CLIENTE", referencedColumnName = "ID_USUARIO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuarioIDUSUARIOCLIENTE;
    
    @JoinColumn(name = "prototipo_ID_PROTOTIPO", referencedColumnName = "ID_PROTOTIPO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Prototipo prototipoIDPROTOTIPO;
    
    @JoinColumn(name = "usuario_ID_USUARIO_VENDEDOR", referencedColumnName = "ID_USUARIO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuarioIDUSUARIOVENDEDOR;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "facturaIDFACTURA", fetch = FetchType.LAZY)
    private List<Domicilios> domiciliosList;

    public Factura() {
    }

    public Factura(Integer idFactura) {
        this.idFactura = idFactura;
    }

    public Factura(Integer idFactura, Date fechaFactura, BigDecimal totalFactura, String estadoFactura) {
        this.idFactura = idFactura;
        this.fechaFactura = fechaFactura;
        this.totalFactura = totalFactura;
        this.estadoFactura = estadoFactura;
    }

    public Integer getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }

    public Date getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(Date fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    // ✅ GETTERS Y SETTERS AGREGADOS
    public BigDecimal getTotalFactura() {
        return totalFactura;
    }

    public void setTotalFactura(BigDecimal totalFactura) {
        this.totalFactura = totalFactura;
    }

    public String getEstadoFactura() {
        return estadoFactura;
    }

    public void setEstadoFactura(String estadoFactura) {
        this.estadoFactura = estadoFactura;
    }

    public Usuario getUsuarioIDUSUARIOCLIENTE() {
        return usuarioIDUSUARIOCLIENTE;
    }

    public void setUsuarioIDUSUARIOCLIENTE(Usuario usuarioIDUSUARIOCLIENTE) {
        this.usuarioIDUSUARIOCLIENTE = usuarioIDUSUARIOCLIENTE;
    }

    public Prototipo getPrototipoIDPROTOTIPO() {
        return prototipoIDPROTOTIPO;
    }

    public void setPrototipoIDPROTOTIPO(Prototipo prototipoIDPROTOTIPO) {
        this.prototipoIDPROTOTIPO = prototipoIDPROTOTIPO;
    }

    public Usuario getUsuarioIDUSUARIOVENDEDOR() {
        return usuarioIDUSUARIOVENDEDOR;
    }

    public void setUsuarioIDUSUARIOVENDEDOR(Usuario usuarioIDUSUARIOVENDEDOR) {
        this.usuarioIDUSUARIOVENDEDOR = usuarioIDUSUARIOVENDEDOR;
    }

    @XmlTransient
    public List<Domicilios> getDomiciliosList() {
        return domiciliosList;
    }

    public void setDomiciliosList(List<Domicilios> domiciliosList) {
        this.domiciliosList = domiciliosList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idFactura != null ? idFactura.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Factura)) {
            return false;
        }
        Factura other = (Factura) object;
        if ((this.idFactura == null && other.idFactura != null) || (this.idFactura != null && !this.idFactura.equals(other.idFactura))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.project2.entities.Factura[ idFactura=" + idFactura + " ]";
    }
}
