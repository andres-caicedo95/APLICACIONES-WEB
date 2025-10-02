/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project2.entities;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author user
 */
@Entity
@Table(name = "cliente")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cliente.findAll", query = "SELECT c FROM Cliente c"),
    @NamedQuery(name = "Cliente.findByIdCliente", query = "SELECT c FROM Cliente c WHERE c.idCliente = :idCliente"),
    @NamedQuery(name = "Cliente.findByTipoDocumentoCliente", query = "SELECT c FROM Cliente c WHERE c.tipoDocumentoCliente = :tipoDocumentoCliente"),
    @NamedQuery(name = "Cliente.findByNumeroDocumentoCliente", query = "SELECT c FROM Cliente c WHERE c.numeroDocumentoCliente = :numeroDocumentoCliente"),
    @NamedQuery(name = "Cliente.findByNombreCliente", query = "SELECT c FROM Cliente c WHERE c.nombreCliente = :nombreCliente"),
    @NamedQuery(name = "Cliente.findByApellidoCliente", query = "SELECT c FROM Cliente c WHERE c.apellidoCliente = :apellidoCliente"),
    @NamedQuery(name = "Cliente.findByTelCliente", query = "SELECT c FROM Cliente c WHERE c.telCliente = :telCliente"),
    @NamedQuery(name = "Cliente.findByDirecccionCliente", query = "SELECT c FROM Cliente c WHERE c.direcccionCliente = :direcccionCliente"),
    @NamedQuery(name = "Cliente.findByCorreoCliente", query = "SELECT c FROM Cliente c WHERE c.correoCliente = :correoCliente"),
    @NamedQuery(name = "Cliente.findByPaswordCliente", query = "SELECT c FROM Cliente c WHERE c.paswordCliente = :paswordCliente"),
    @NamedQuery(name = "Cliente.findByEstadoCliente", query = "SELECT c FROM Cliente c WHERE c.estadoCliente = :estadoCliente")})
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_CLIENTE")
    private Integer idCliente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "TIPO_DOCUMENTO_CLIENTE")
    private String tipoDocumentoCliente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "NUMERO_DOCUMENTO_CLIENTE")
    private String numeroDocumentoCliente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "NOMBRE_CLIENTE")
    private String nombreCliente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "APELLIDO_CLIENTE")
    private String apellidoCliente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 12)
    @Column(name = "TEL_CLIENTE")
    private String telCliente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "DIRECCCION_CLIENTE")
    private String direcccionCliente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "CORREO_CLIENTE")
    private String correoCliente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "PASWORD_CLIENTE")
    private String paswordCliente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "ESTADO_CLIENTE")
    private String estadoCliente;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clienteIDCLIENTE", fetch = FetchType.LAZY)
    private List<Descuentos> descuentosList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clienteIDCLIENTE", fetch = FetchType.LAZY)
    private List<Prototipo> prototipoList;

    public Cliente() {
    }

    public Cliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Cliente(Integer idCliente, String tipoDocumentoCliente, String numeroDocumentoCliente, String nombreCliente, String apellidoCliente, String telCliente, String direcccionCliente, String correoCliente, String paswordCliente, String estadoCliente) {
        this.idCliente = idCliente;
        this.tipoDocumentoCliente = tipoDocumentoCliente;
        this.numeroDocumentoCliente = numeroDocumentoCliente;
        this.nombreCliente = nombreCliente;
        this.apellidoCliente = apellidoCliente;
        this.telCliente = telCliente;
        this.direcccionCliente = direcccionCliente;
        this.correoCliente = correoCliente;
        this.paswordCliente = paswordCliente;
        this.estadoCliente = estadoCliente;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoDocumentoCliente() {
        return tipoDocumentoCliente;
    }

    public void setTipoDocumentoCliente(String tipoDocumentoCliente) {
        this.tipoDocumentoCliente = tipoDocumentoCliente;
    }

    public String getNumeroDocumentoCliente() {
        return numeroDocumentoCliente;
    }

    public void setNumeroDocumentoCliente(String numeroDocumentoCliente) {
        this.numeroDocumentoCliente = numeroDocumentoCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getApellidoCliente() {
        return apellidoCliente;
    }

    public void setApellidoCliente(String apellidoCliente) {
        this.apellidoCliente = apellidoCliente;
    }

    public String getTelCliente() {
        return telCliente;
    }

    public void setTelCliente(String telCliente) {
        this.telCliente = telCliente;
    }

    public String getDirecccionCliente() {
        return direcccionCliente;
    }

    public void setDirecccionCliente(String direcccionCliente) {
        this.direcccionCliente = direcccionCliente;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public String getPaswordCliente() {
        return paswordCliente;
    }

    public void setPaswordCliente(String paswordCliente) {
        this.paswordCliente = paswordCliente;
    }

    public String getEstadoCliente() {
        return estadoCliente;
    }

    public void setEstadoCliente(String estadoCliente) {
        this.estadoCliente = estadoCliente;
    }

    @XmlTransient
    public List<Descuentos> getDescuentosList() {
        return descuentosList;
    }

    public void setDescuentosList(List<Descuentos> descuentosList) {
        this.descuentosList = descuentosList;
    }

    @XmlTransient
    public List<Prototipo> getPrototipoList() {
        return prototipoList;
    }

    public void setPrototipoList(List<Prototipo> prototipoList) {
        this.prototipoList = prototipoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCliente != null ? idCliente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cliente)) {
            return false;
        }
        Cliente other = (Cliente) object;
        if ((this.idCliente == null && other.idCliente != null) || (this.idCliente != null && !this.idCliente.equals(other.idCliente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.project2.entities.Cliente[ idCliente=" + idCliente + " ]";
    }
    
}
