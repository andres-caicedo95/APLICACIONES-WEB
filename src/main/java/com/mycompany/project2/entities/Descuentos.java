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
@Table(name = "descuentos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Descuentos.findAll", query = "SELECT d FROM Descuentos d"),
    @NamedQuery(name = "Descuentos.findByIdDescuentos", query = "SELECT d FROM Descuentos d WHERE d.idDescuentos = :idDescuentos")})
public class Descuentos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_descuentos")
    private Integer idDescuentos;
    @JoinColumn(name = "cliente_ID_CLIENTE", referencedColumnName = "ID_CLIENTE")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cliente clienteIDCLIENTE;
    @JoinColumn(name = "promociones_id_PROMOCION", referencedColumnName = "id_PROMOCION")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Promociones promocionesidPROMOCION;

    public Descuentos() {
    }

    public Descuentos(Integer idDescuentos) {
        this.idDescuentos = idDescuentos;
    }

    public Integer getIdDescuentos() {
        return idDescuentos;
    }

    public void setIdDescuentos(Integer idDescuentos) {
        this.idDescuentos = idDescuentos;
    }

    public Cliente getClienteIDCLIENTE() {
        return clienteIDCLIENTE;
    }

    public void setClienteIDCLIENTE(Cliente clienteIDCLIENTE) {
        this.clienteIDCLIENTE = clienteIDCLIENTE;
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
        hash += (idDescuentos != null ? idDescuentos.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Descuentos)) {
            return false;
        }
        Descuentos other = (Descuentos) object;
        if ((this.idDescuentos == null && other.idDescuentos != null) || (this.idDescuentos != null && !this.idDescuentos.equals(other.idDescuentos))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.project2.entities.Descuentos[ idDescuentos=" + idDescuentos + " ]";
    }
    
}
