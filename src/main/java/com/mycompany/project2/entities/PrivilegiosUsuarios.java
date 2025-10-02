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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author user
 */
@Entity
@Table(name = "privilegios_usuarios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PrivilegiosUsuarios.findAll", query = "SELECT p FROM PrivilegiosUsuarios p"),
    @NamedQuery(name = "PrivilegiosUsuarios.findByIdPrivilegioUsuario", query = "SELECT p FROM PrivilegiosUsuarios p WHERE p.idPrivilegioUsuario = :idPrivilegioUsuario"),
    @NamedQuery(name = "PrivilegiosUsuarios.findByFechaAsignacion", query = "SELECT p FROM PrivilegiosUsuarios p WHERE p.fechaAsignacion = :fechaAsignacion")})
public class PrivilegiosUsuarios implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_PRIVILEGIO_USUARIO")
    private Integer idPrivilegioUsuario;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_ASIGNACION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAsignacion;
    @JoinColumn(name = "acciones_ID_ACCION", referencedColumnName = "ID_ACCION")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Acciones accionesIDACCION;
    @JoinColumn(name = "usuario_ID_USUARIO", referencedColumnName = "ID_USUARIO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuarioIDUSUARIO;

    public PrivilegiosUsuarios() {
    }

    public PrivilegiosUsuarios(Integer idPrivilegioUsuario) {
        this.idPrivilegioUsuario = idPrivilegioUsuario;
    }

    public PrivilegiosUsuarios(Integer idPrivilegioUsuario, Date fechaAsignacion) {
        this.idPrivilegioUsuario = idPrivilegioUsuario;
        this.fechaAsignacion = fechaAsignacion;
    }

    public Integer getIdPrivilegioUsuario() {
        return idPrivilegioUsuario;
    }

    public void setIdPrivilegioUsuario(Integer idPrivilegioUsuario) {
        this.idPrivilegioUsuario = idPrivilegioUsuario;
    }

    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public Acciones getAccionesIDACCION() {
        return accionesIDACCION;
    }

    public void setAccionesIDACCION(Acciones accionesIDACCION) {
        this.accionesIDACCION = accionesIDACCION;
    }

    public Usuario getUsuarioIDUSUARIO() {
        return usuarioIDUSUARIO;
    }

    public void setUsuarioIDUSUARIO(Usuario usuarioIDUSUARIO) {
        this.usuarioIDUSUARIO = usuarioIDUSUARIO;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPrivilegioUsuario != null ? idPrivilegioUsuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PrivilegiosUsuarios)) {
            return false;
        }
        PrivilegiosUsuarios other = (PrivilegiosUsuarios) object;
        if ((this.idPrivilegioUsuario == null && other.idPrivilegioUsuario != null) || (this.idPrivilegioUsuario != null && !this.idPrivilegioUsuario.equals(other.idPrivilegioUsuario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.project2.entities.PrivilegiosUsuarios[ idPrivilegioUsuario=" + idPrivilegioUsuario + " ]";
    }
    
}
