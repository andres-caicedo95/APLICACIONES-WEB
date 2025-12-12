package com.mycompany.project2.services;

public interface OrderServiceLocal {
    java.math.BigDecimal calcularTotalCarrito(Integer clienteId);
    Long finalizarPedidoDesdeCarrito(Integer clienteId, String metodoPago, String referenciaPago, String direccion) throws Exception;
}
