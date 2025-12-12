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

@Entity
@Table(name = "factura")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Factura.findAll", query = "SELECT f FROM Factura f"),
    @NamedQuery(name = "Factura.findByIdFactura", query = "SELECT f FROM Factura f WHERE f.idFactura = :idFactura"),
    @NamedQuery(name = "Factura.findByFechaFactura", query = "SELECT f FROM Factura f WHERE f.fechaFactura = :fechaFactura")
})
public class Factura implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===============================
    //         CAMPOS PRINCIPALES
    // ===============================
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

    // ===============================
    //     CAMPOS AGREGADOS (NUEVOS)
    // ===============================
    @Basic(optional = false)
    @NotNull
    @Column(name = "TOTAL_FACTURA")
    private BigDecimal totalFactura;

    @Basic(optional = false)
    @NotNull
    @Column(name = "ESTADO_FACTURA")
    private String estadoFactura;

    @Column(name = "METODO_PAGO")
    private String metodoPago; // ✔ CORRECTO

    // ===============================
    //         RELACIONES
    // ===============================
    @JoinColumn(name = "USUARIO_ID_USUARIO_CLIENTE", referencedColumnName = "ID_USUARIO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuarioIDUSUARIOCLIENTE;

    @JoinColumn(name = "prototipo_ID_PROTOTIPO", referencedColumnName = "ID_PROTOTIPO")
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Prototipo prototipoIDPROTOTIPO;

    @JoinColumn(name = "usuario_ID_USUARIO_VENDEDOR", referencedColumnName = "ID_USUARIO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuarioIDUSUARIOVENDEDOR;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "facturaIDFACTURA", fetch = FetchType.LAZY)
    private List<Domicilios> domiciliosList;

    // ===============================
    //         CONSTRUCTORES
    // ===============================
    public Factura() {
        // Inicializar campos obligatorios para evitar null
        this.fechaFactura = new Date();
        this.estadoFactura = "PENDIENTE";
        this.totalFactura = java.math.BigDecimal.ZERO;
        this.metodoPago = "ContraEntrega";
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

    // ===============================
    //      GETTERS Y SETTERS
    // ===============================
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

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
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

    // ===============================
    //     MÉTODOS GENERALES
    // ===============================
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
        return !((this.idFactura == null && other.idFactura != null)
                || (this.idFactura != null && !this.idFactura.equals(other.idFactura)));
    }

    @Override
    public String toString() {
        return "com.mycompany.project2.entities.Factura[ idFactura=" + idFactura + " ]";
    }

}
