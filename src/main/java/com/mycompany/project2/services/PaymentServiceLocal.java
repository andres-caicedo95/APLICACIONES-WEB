package com.mycompany.project2.services;

import java.math.BigDecimal;

public interface PaymentServiceLocal {
    String generarReferencia();
    boolean simularPago(String metodo, String referencia, BigDecimal monto);
}
