/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project2.security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author user
 */
public class Filtro implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No necesitas implementar nada aquí si no hay configuración
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest solicitud = (HttpServletRequest) request;
        HttpServletResponse respuesta = (HttpServletResponse) response;
        HttpSession sesion = solicitud.getSession();
        String rutaSolicitud = solicitud.getRequestURI();
        String raiz = solicitud.getContextPath();

        // Encabezados para evitar caché
        respuesta.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        respuesta.setHeader("Pragma", "no-cache");
        respuesta.setDateHeader("Expires", 0);

        // Validaciones
        boolean validarSesion = (sesion != null && sesion.getAttribute("usuario") != null);
        boolean validarRutaLogin = (
            rutaSolicitud.equals(raiz + "/") ||
            rutaSolicitud.equals(raiz + "/PaginaInicio.xhtml") ||
            rutaSolicitud.equals(raiz + "/login.xhtml") ||
            rutaSolicitud.equals(raiz + "/registro.xhtml")
        );

        boolean validarContenido = (
            rutaSolicitud.contains("/resources/") ||
            rutaSolicitud.contains("/javax.faces.resource/") ||
            rutaSolicitud.endsWith(".css") ||
            rutaSolicitud.endsWith(".js") ||
            rutaSolicitud.endsWith(".png") ||
            rutaSolicitud.endsWith(".jpg") ||
            rutaSolicitud.endsWith(".jpeg") ||
            rutaSolicitud.endsWith(".gif")
        );

        // ✅ NUEVA VALIDACIÓN: Permitir acceso a vistas de cliente SI el usuario está logueado
        boolean validarRutaCliente = (
            validarSesion && 
            rutaSolicitud.startsWith(raiz + "/views/cliente/")
        );

        if (validarSesion || validarRutaLogin || validarContenido || validarRutaCliente) {
            chain.doFilter(request, response);
        } else {
            // Redirigir a la página principal si no tiene permiso
            respuesta.sendRedirect(raiz);
        }
    }

    @Override
    public void destroy() {
        // No necesitas implementar nada aquí
    }
}
