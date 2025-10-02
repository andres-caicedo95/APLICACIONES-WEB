/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project2.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "domicilios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Domicilios.findAll", query = "SELECT d FROM Domicilios d"),
    @NamedQuery(name = "Domicilios.findByIdDomicilio", query = "SELECT d FROM Domicilios d WHERE d.idDomicilio = :idDomicilio"),
    @NamedQuery(name = "Domicilios.findByFechaDomicilio", query = "SELECT d FROM Domicilios d WHERE d.fechaDomicilio = :fechaDomicilio"),
    @NamedQuery(name = "Domicilios.findByDirecccionDomicilio", query = "SELECT d FROM Domicilios d WHERE d.direcccionDomicilio = :direcccionDomicilio"),
    @NamedQuery(name = "Domicilios.findByEstado", query = "SELECT d FROM Domicilios d WHERE d.estado = :estado")})
public class Domicilios implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_DOMICILIO")
    private Integer idDomicilio;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_DOMICILIO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDomicilio;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "DIRECCCION_DOMICILIO")
    private String direcccionDomicilio;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "ESTADO")
    private String estado = "PENDIENTE"; // Valores: PENDIENTE, ASIGNADO, EN_CAMINO, ENTREGADO, CANCELADO

    // ✅ NUEVOS CAMPOS PARA GEOLOCALIZACIÓN
    @Column(name = "LATITUD")
    private Double latitud;

    @Column(name = "LONGITUD")
    private Double longitud;
    
    @JoinColumn(name = "factura_ID_FACTURA", referencedColumnName = "ID_FACTURA")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Factura facturaIDFACTURA;
    
    @JoinColumn(name = "usuario_ID_USUARIO_DOMICILIO", referencedColumnName = "ID_USUARIO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuarioIDUSUARIODOMICILIO;

    public Domicilios() {
    }

    public Domicilios(Integer idDomicilio) {
        this.idDomicilio = idDomicilio;
    }

    public Domicilios(Integer idDomicilio, Date fechaDomicilio, String direcccionDomicilio, String estado) {
        this.idDomicilio = idDomicilio;
        this.fechaDomicilio = fechaDomicilio;
        this.direcccionDomicilio = direcccionDomicilio;
        this.estado = estado;
    }

    // Getters y Setters para todos los campos
    public Integer getIdDomicilio() {
        return idDomicilio;
    }

    public void setIdDomicilio(Integer idDomicilio) {
        this.idDomicilio = idDomicilio;
    }

    public Date getFechaDomicilio() {
        return fechaDomicilio;
    }

    public void setFechaDomicilio(Date fechaDomicilio) {
        this.fechaDomicilio = fechaDomicilio;
    }

    public String getDirecccionDomicilio() {
        return direcccionDomicilio;
    }

    public void setDirecccionDomicilio(String direcccionDomicilio) {
        this.direcccionDomicilio = direcccionDomicilio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // ✅ GETTERS Y SETTERS NUEVOS
    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Factura getFacturaIDFACTURA() {
        return facturaIDFACTURA;
    }

    public void setFacturaIDFACTURA(Factura facturaIDFACTURA) {
        this.facturaIDFACTURA = facturaIDFACTURA;
    }

    public Usuario getUsuarioIDUSUARIODOMICILIO() {
        return usuarioIDUSUARIODOMICILIO;
    }

    public void setUsuarioIDUSUARIODOMICILIO(Usuario usuarioIDUSUARIODOMICILIO) {
        this.usuarioIDUSUARIODOMICILIO = usuarioIDUSUARIODOMICILIO;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDomicilio != null ? idDomicilio.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Domicilios)) {
            return false;
        }
        Domicilios other = (Domicilios) object;
        if ((this.idDomicilio == null && other.idDomicilio != null) || (this.idDomicilio != null && !this.idDomicilio.equals(other.idDomicilio))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.project2.entities.Domicilios[ idDomicilio=" + idDomicilio + " ]";
    }
}
