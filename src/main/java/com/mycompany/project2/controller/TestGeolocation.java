package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Domicilios;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;

@Named
@ViewScoped
public class TestGeolocation implements Serializable {

    private Domicilios domicilio = new Domicilios();

    @PostConstruct
    public void init() {
        // Inicializa con datos de prueba
        domicilio.setDirecccionDomicilio("Calle 123 #45-67, Medellín");
        domicilio.setFechaDomicilio(new Date());
        domicilio.setEstado("Pendiente");
    }

    public void guardar() {
        System.out.println("✅ Guardando domicilio de prueba:");
        System.out.println("   Dirección: " + domicilio.getDirecccionDomicilio());
        System.out.println("   Fecha: " + domicilio.getFechaDomicilio());
        System.out.println("   Estado: " + domicilio.getEstado());
    }

    // Getters
    public Domicilios getDomicilio() {
        return domicilio;
    }
}
