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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Lob; // <-- Añadido
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
@Table(name = "usuario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findByIdUsuario", query = "SELECT u FROM Usuario u WHERE u.idUsuario = :idUsuario"),
    @NamedQuery(name = "Usuario.findByTipoDocumentoUsuario", query = "SELECT u FROM Usuario u WHERE u.tipoDocumentoUsuario = :tipoDocumentoUsuario"),
    @NamedQuery(name = "Usuario.findByNumeroDocumento", query = "SELECT u FROM Usuario u WHERE u.numeroDocumento = :numeroDocumento"),
    @NamedQuery(name = "Usuario.findByNombreUsuario", query = "SELECT u FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario"),
    @NamedQuery(name = "Usuario.findByApellidoUsuario", query = "SELECT u FROM Usuario u WHERE u.apellidoUsuario = :apellidoUsuario"),
    @NamedQuery(name = "Usuario.findByTelUsuario", query = "SELECT u FROM Usuario u WHERE u.telUsuario = :telUsuario"),
    @NamedQuery(name = "Usuario.findByDirecccionUsuario", query = "SELECT u FROM Usuario u WHERE u.direcccionUsuario = :direcccionUsuario"),
    @NamedQuery(name = "Usuario.findByCorreoUsuario", query = "SELECT u FROM Usuario u WHERE u.correoUsuario = :correoUsuario"),
    @NamedQuery(name = "Usuario.findByPaswordUsuario", query = "SELECT u FROM Usuario u WHERE u.paswordUsuario = :paswordUsuario"),
    @NamedQuery(name = "Usuario.findByEstadoUsuario", query = "SELECT u FROM Usuario u WHERE u.estadoUsuario = :estadoUsuario")})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_USUARIO")
    private Integer idUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "TIPO_DOCUMENTO_USUARIO")
    private String tipoDocumentoUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "NUMERO_DOCUMENTO")
    private String numeroDocumento;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "NOMBRE_USUARIO")
    private String nombreUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "APELLIDO_USUARIO")
    private String apellidoUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 12)
    @Column(name = "TEL_USUARIO")
    private String telUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "DIRECCCION_USUARIO")
    private String direcccionUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "CORREO_USUARIO")
    private String correoUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "PASWORD_USUARIO")
    private String paswordUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "ESTADO_USUARIO")
    private String estadoUsuario;

    // ✅ CAMPO AGREGADO: imagenUsuario (para resolver el error de compilación)
    @Lob
    @Column(name = "IMAGEN_USUARIO")
    private byte[] imagenUsuario;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuarioIDUSUARIOVENDEDOR", fetch = FetchType.LAZY)
    private List<Factura> facturaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuarioIDUSUARIO", fetch = FetchType.LAZY)
    private List<PrivilegiosUsuarios> privilegiosUsuariosList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuarioIDUSUARIODOMICILIO", fetch = FetchType.LAZY)
    private List<Domicilios> domiciliosList;
    @JoinColumn(name = "rol_ID_ROL", referencedColumnName = "ID_ROL")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Rol rolIDROL;

    public Usuario() {
    }

    public Usuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuario(Integer idUsuario, String tipoDocumentoUsuario, String numeroDocumento, String nombreUsuario, String apellidoUsuario, String telUsuario, String direcccionUsuario, String correoUsuario, String paswordUsuario, String estadoUsuario) {
        this.idUsuario = idUsuario;
        this.tipoDocumentoUsuario = tipoDocumentoUsuario;
        this.numeroDocumento = numeroDocumento;
        this.nombreUsuario = nombreUsuario;
        this.apellidoUsuario = apellidoUsuario;
        this.telUsuario = telUsuario;
        this.direcccionUsuario = direcccionUsuario;
        this.correoUsuario = correoUsuario;
        this.paswordUsuario = paswordUsuario;
        this.estadoUsuario = estadoUsuario;
    }

    // ✅ GETTER Y SETTER AGREGADOS (solo esto se añade nuevo)
    public byte[] getImagenUsuario() {
        return imagenUsuario;
    }

    public void setImagenUsuario(byte[] imagenUsuario) {
        this.imagenUsuario = imagenUsuario;
    }

    // --- Métodos existentes (sin cambios) ---
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoDocumentoUsuario() {
        return tipoDocumentoUsuario;
    }

    public void setTipoDocumentoUsuario(String tipoDocumentoUsuario) {
        this.tipoDocumentoUsuario = tipoDocumentoUsuario;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    public void setApellidoUsuario(String apellidoUsuario) {
        this.apellidoUsuario = apellidoUsuario;
    }

    public String getTelUsuario() {
        return telUsuario;
    }

    public void setTelUsuario(String telUsuario) {
        this.telUsuario = telUsuario;
    }

    public String getDirecccionUsuario() {
        return direcccionUsuario;
    }

    public void setDirecccionUsuario(String direcccionUsuario) {
        this.direcccionUsuario = direcccionUsuario;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public String getPaswordUsuario() {
        return paswordUsuario;
    }

    public void setPaswordUsuario(String paswordUsuario) {
        this.paswordUsuario = paswordUsuario;
    }

    public String getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(String estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }

    @XmlTransient
    public List<Factura> getFacturaList() {
        return facturaList;
    }

    public void setFacturaList(List<Factura> facturaList) {
        this.facturaList = facturaList;
    }

    @XmlTransient
    public List<PrivilegiosUsuarios> getPrivilegiosUsuariosList() {
        return privilegiosUsuariosList;
    }

    public void setPrivilegiosUsuariosList(List<PrivilegiosUsuarios> privilegiosUsuariosList) {
        this.privilegiosUsuariosList = privilegiosUsuariosList;
    }

    @XmlTransient
    public List<Domicilios> getDomiciliosList() {
        return domiciliosList;
    }

    public void setDomiciliosList(List<Domicilios> domiciliosList) {
        this.domiciliosList = domiciliosList;
    }

    public Rol getRolIDROL() {
        return rolIDROL;
    }

    public void setRolIDROL(Rol rolIDROL) {
        this.rolIDROL = rolIDROL;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUsuario != null ? idUsuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.idUsuario == null && other.idUsuario != null) || (this.idUsuario != null && !this.idUsuario.equals(other.idUsuario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.project2.entities.Usuario[ idUsuario=" + idUsuario + " ]";
    }
}
