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
@Table(name = "acciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Acciones.findAll", query = "SELECT a FROM Acciones a"),
    @NamedQuery(name = "Acciones.findByIdAccion", query = "SELECT a FROM Acciones a WHERE a.idAccion = :idAccion"),
    @NamedQuery(name = "Acciones.findByNombreAccion", query = "SELECT a FROM Acciones a WHERE a.nombreAccion = :nombreAccion")})
public class Acciones implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_ACCION")
    private Integer idAccion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "NOMBRE_ACCION")
    private String nombreAccion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accionesIDACCION", fetch = FetchType.LAZY)
    private List<PrivilegiosUsuarios> privilegiosUsuariosList;

    public Acciones() {
    }

    public Acciones(Integer idAccion) {
        this.idAccion = idAccion;
    }

    public Acciones(Integer idAccion, String nombreAccion) {
        this.idAccion = idAccion;
        this.nombreAccion = nombreAccion;
    }

    public Integer getIdAccion() {
        return idAccion;
    }

    public void setIdAccion(Integer idAccion) {
        this.idAccion = idAccion;
    }

    public String getNombreAccion() {
        return nombreAccion;
    }

    public void setNombreAccion(String nombreAccion) {
        this.nombreAccion = nombreAccion;
    }

    @XmlTransient
    public List<PrivilegiosUsuarios> getPrivilegiosUsuariosList() {
        return privilegiosUsuariosList;
    }

    public void setPrivilegiosUsuariosList(List<PrivilegiosUsuarios> privilegiosUsuariosList) {
        this.privilegiosUsuariosList = privilegiosUsuariosList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAccion != null ? idAccion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Acciones)) {
            return false;
        }
        Acciones other = (Acciones) object;
        if ((this.idAccion == null && other.idAccion != null) || (this.idAccion != null && !this.idAccion.equals(other.idAccion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.project2.entities.Acciones[ idAccion=" + idAccion + " ]";
    }
    
}
