package com.mycompany.project2.services;

import java.math.BigDecimal;
import java.util.UUID;
import javax.ejb.Stateless;

@Stateless
public class PaymentServiceBean implements PaymentServiceLocal {

    @Override
    public String generarReferencia() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public boolean simularPago(String metodo, String referencia, BigDecimal monto) {
        return true; // Simulación siempre aprobada
    }
}
