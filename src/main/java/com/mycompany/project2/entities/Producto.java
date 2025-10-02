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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
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
@Table(name = "producto")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Producto.findAll", query = "SELECT p FROM Producto p"),
    @NamedQuery(name = "Producto.findByIdProducto", query = "SELECT p FROM Producto p WHERE p.idProducto = :idProducto"),
    @NamedQuery(name = "Producto.findByCodigoProducto", query = "SELECT p FROM Producto p WHERE p.codigoProducto = :codigoProducto"),
    @NamedQuery(name = "Producto.findByNombreProducto", query = "SELECT p FROM Producto p WHERE p.nombreProducto = :nombreProducto"),
    @NamedQuery(name = "Producto.findByStockProduccto", query = "SELECT p FROM Producto p WHERE p.stockProduccto = :stockProduccto"),
    @NamedQuery(name = "Producto.findByValorProducto", query = "SELECT p FROM Producto p WHERE p.valorProducto = :valorProducto"),
    @NamedQuery(name = "Producto.findByEstadoProducto", query = "SELECT p FROM Producto p WHERE p.estadoProducto = :estadoProducto"),
    @NamedQuery(name = "Producto.findByCategoriaProducto", query = "SELECT p FROM Producto p WHERE p.categoriaProducto = :categoriaProducto"),
 @NamedQuery(name = "Producto.findCategoriasUnicas", 
        query = "SELECT DISTINCT p.categoriaProducto FROM Producto p"),
    @NamedQuery(name = "Producto.findEstadosUnicos", 
        query = "SELECT DISTINCT p.estadoProducto FROM Producto p WHERE p.estadoProducto IS NOT NULL")
})
public class Producto implements Serializable {

    public enum Categoria {
        PANES, BEBIDAS, TORTAS, CHOCOLATES, SANDWICHES, CAFE, HELADOS, POSTRES, OTROS
    }

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_PRODUCTO")
    private Integer idProducto;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "CODIGO_PRODUCTO")
    private String codigoProducto;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "NOMBRE_PRODUCTO")
    private String nombreProducto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STOCK_PRODUCCTO")
    private long stockProduccto;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "VALOR_PRODUCTO")
    private String valorProducto;
    @Basic(optional = false)
    /*@NotNull*/
    @Lob
    @Column(name = "IMAGEN_PRODUCTO")
    private byte[] imagenProducto;
    @Size(max = 8)
    @Column(name = "ESTADO_PRODUCTO")
    private String estadoProducto;
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_producto")
    private Categoria categoriaProducto = Categoria.OTROS;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productoIDPRODUCTO", fetch = FetchType.LAZY)
    private List<Combo> comboList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productoIDPRODUCTO", fetch = FetchType.LAZY)
    private List<ProductoPorPrototipo> productoPorPrototipoList;

    public Producto() {
    }

    public Producto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Producto(Integer idProducto, String codigoProducto, String nombreProducto, long stockProduccto, String valorProducto, byte[] imagenProducto, Categoria categoriaProducto) {
        this.idProducto = idProducto;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.stockProduccto = stockProduccto;
        this.valorProducto = valorProducto;
        this.imagenProducto = imagenProducto;
        this.categoriaProducto = categoriaProducto;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public long getStockProduccto() {
        return stockProduccto;
    }

    public void setStockProduccto(long stockProduccto) {
        this.stockProduccto = stockProduccto;
    }

    public String getValorProducto() {
        return valorProducto;
    }

    public void setValorProducto(String valorProducto) {
        this.valorProducto = valorProducto;
    }

    public byte[] getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(byte[] imagenProducto) {
        this.imagenProducto = imagenProducto;
    }

    public String getEstadoProducto() {
        return estadoProducto;
    }

    public void setEstadoProducto(String estadoProducto) {
        this.estadoProducto = estadoProducto;
    }

    public Categoria getCategoriaProducto() {
        return categoriaProducto;
    }

    public void setCategoriaProducto(Categoria categoriaProducto) {
        this.categoriaProducto = categoriaProducto;
    }

    @XmlTransient
    public List<Combo> getComboList() {
        return comboList;
    }

    public void setComboList(List<Combo> comboList) {
        this.comboList = comboList;
    }

    @XmlTransient
    public List<ProductoPorPrototipo> getProductoPorPrototipoList() {
        return productoPorPrototipoList;
    }

    public void setProductoPorPrototipoList(List<ProductoPorPrototipo> productoPorPrototipoList) {
        this.productoPorPrototipoList = productoPorPrototipoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProducto != null ? idProducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        if ((this.idProducto == null && other.idProducto != null) || (this.idProducto != null && !this.idProducto.equals(other.idProducto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.project2.entities.Producto[ idProducto=" + idProducto + " ]";
    }
}