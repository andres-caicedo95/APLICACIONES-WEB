package com.mycompany.project2.model;

import java.io.Serializable;

/**
 * DTO para representar un ítem en el carrito de compras.
 * No es una entidad JPA, solo un modelo de vista.
 */
public class ItemCarrito implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer idProducto;
    private String nombre;
    private String valorProducto; // Tu entidad Producto tiene valorProducto como String
    private int cantidad = 1;

    // Constructor vacío (requerido por JavaBeans)
    public ItemCarrito() {}

    // Constructor con datos esenciales
    public ItemCarrito(Integer idProducto, String nombre, String valorProducto) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.valorProducto = valorProducto;
    }

    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValorProducto() {
        return valorProducto;
    }

    public void setValorProducto(String valorProducto) {
        this.valorProducto = valorProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad < 1) {
            this.cantidad = 1;
        } else {
            this.cantidad = cantidad;
        }
    }

    // Métodos de utilidad
    public void incrementarCantidad() {
        this.cantidad++;
    }

    public void decrementarCantidad() {
        if (this.cantidad > 1) {
            this.cantidad--;
        }
    }

    /**
     * Calcula el subtotal del ítem.
     * @return subtotal como double (0.0 si el valorProducto no es numérico)
     */
    public double getSubtotal() {
        try {
            double precio = Double.parseDouble(valorProducto);
            return precio * cantidad;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Formatea el subtotal con 2 decimales (útil para mostrar en la vista).
     * @return subtotal como String formateado
     */
    public String getSubtotalFormateado() {
        return String.format("%.2f", getSubtotal());
    }
}
