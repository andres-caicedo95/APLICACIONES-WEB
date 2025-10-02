package com.mycompany.project2.controller;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import java.util.Date;

@Named
@RequestScoped
public class DashboardBean {
    
    private Date now;
    
    public void init() {
        this.now = new Date();
    }
    
    public Date getNow() {
        return now;
    }
}
