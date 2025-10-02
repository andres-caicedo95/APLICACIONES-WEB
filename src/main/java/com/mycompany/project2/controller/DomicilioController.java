package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Domicilios;
import com.mycompany.project2.entities.Factura;
import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.services.DomiciliosFacadeLocal;
import com.mycompany.project2.services.FacturaFacadeLocal;
import com.mycompany.project2.services.UsuarioFacadeLocal;
import com.mycompany.project2.service.GeolocationService;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import java.util.logging.Logger;
import java.text.SimpleDateFormat; // ← IMPORT AÑADIDO

@Named(value = "domicilioController")
@ViewScoped
public class DomicilioController implements Serializable {

    private static final Logger LOG = Logger.getLogger(DomicilioController.class.getName());

    private Domicilios domicilioSeleccionado = new Domicilios();
    
    @EJB
    private DomiciliosFacadeLocal dfl;
    
    @EJB
    private FacturaFacadeLocal ffl;
    
    @EJB
    private UsuarioFacadeLocal ufl;

    @Inject
    private GeolocationService geolocationService;

    private Integer facturaTemporal;
    private Integer vendedorTemporal;

    // ✅ Parámetro de URL
    private Integer idDomicilioParam;

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance()
            .getExternalContext().getRequest();
        String idParam = request.getParameter("id");
        
        if (idParam != null && !idParam.isEmpty()) {
            try {
                idDomicilioParam = Integer.parseInt(idParam);
                Domicilios dom = dfl.find(idDomicilioParam);
                if (dom != null) {
                    this.domicilioSeleccionado = dom;
                    this.facturaTemporal = dom.getFacturaIDFACTURA() != null ? dom.getFacturaIDFACTURA().getIdFactura() : null;
                    this.vendedorTemporal = dom.getUsuarioIDUSUARIODOMICILIO() != null ? dom.getUsuarioIDUSUARIODOMICILIO().getIdUsuario() : null;
                    LOG.info("✅ Editando domicilio ID=" + idDomicilioParam);
                } else {
                    LOG.warning("⚠️ Domicilio no encontrado ID=" + idDomicilioParam);
                    // Si no existe, crea uno nuevo
                    this.domicilioSeleccionado = new Domicilios();
                    this.domicilioSeleccionado.setFechaDomicilio(new Date());
                }
            } catch (NumberFormatException e) {
                LOG.severe("❌ ID inválido: " + idParam);
                this.domicilioSeleccionado = new Domicilios();
                this.domicilioSeleccionado.setFechaDomicilio(new Date());
            }
        } else {
            // Crear nuevo
            this.domicilioSeleccionado = new Domicilios();
            this.domicilioSeleccionado.setFechaDomicilio(new Date());
            LOG.info("🆕 Creando nuevo domicilio");
        }
    }

    // ✅ Getter seguro
    public Domicilios getDomicilioSeleccionado() {
        if (domicilioSeleccionado == null) {
            domicilioSeleccionado = new Domicilios();
        }
        return domicilioSeleccionado;
    }

    public void setDomicilioSeleccionado(Domicilios domicilioSeleccionado) {
        this.domicilioSeleccionado = domicilioSeleccionado;
    }

    public Integer getFacturaTemporal() { return facturaTemporal; }
    public void setFacturaTemporal(Integer facturaTemporal) { this.facturaTemporal = facturaTemporal; }

    public Integer getVendedorTemporal() { return vendedorTemporal; }
    public void setVendedorTemporal(Integer vendedorTemporal) { this.vendedorTemporal = vendedorTemporal; }

    public List<Factura> getListaFacturas() {
        return this.ffl.findAll();
    }

    public List<Usuario> getListaVendedores() {
        return this.ufl.findByRol(3);
    }

    // ✅ MÉTODO AJUSTADO CON DIAGNÓSTICO DETALLADO
    public void geolocalizarDireccion() {
        // 🔍 Verificación 1: ¿El servicio está inyectado?
        if (geolocationService == null) {
            LOG.severe("❌ ERROR: geolocationService es NULL. Falta beans.xml o CDI no está activo.");
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Servicio de geolocalización no disponible"));
            return;
        }

        // 🔍 Verificación 2: ¿Hay dirección?
        String direccion = domicilioSeleccionado != null ? domicilioSeleccionado.getDirecccionDomicilio() : null;
        if (direccion == null || direccion.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "❌ Dirección vacía", 
                    "Ingresa una dirección antes de geolocalizar."));
            return;
        }

        LOG.info("🔍 Geolocalizando dirección: '" + direccion + "'");

        // 🔍 Llamada al servicio
        double[] coords = geolocationService.geocodificar(direccion);
        if (coords != null) {
            domicilioSeleccionado.setLatitud(coords[0]);
            domicilioSeleccionado.setLongitud(coords[1]);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "✅ Éxito", 
                    "Coordenadas obtenidas: Lat " + coords[0] + ", Lng " + coords[1]));
        } else {
            LOG.warning("⚠️ Geolocalización falló para: '" + direccion + "'");
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "⚠️ No se pudo geolocalizar", 
                    "Dirección no encontrada o error de red. Ejemplo válido: 'Carrera 7 #22-33, Bogotá'"));
        }
    }

    public String guardarDomicilio() {
        try {
            if (facturaTemporal != null) {
                Factura factura = ffl.find(facturaTemporal);
                domicilioSeleccionado.setFacturaIDFACTURA(factura);
            }
            if (vendedorTemporal != null) {
                Usuario domiciliario = ufl.find(vendedorTemporal);
                domicilioSeleccionado.setUsuarioIDUSUARIODOMICILIO(domiciliario);
            }

            if (domicilioSeleccionado.getLatitud() == null || domicilioSeleccionado.getLongitud() == null) {
                double[] coords = geolocationService.geocodificar(domicilioSeleccionado.getDirecccionDomicilio());
                if (coords != null) {
                    domicilioSeleccionado.setLatitud(coords[0]);
                    domicilioSeleccionado.setLongitud(coords[1]);
                }
            }

            if (domicilioSeleccionado.getIdDomicilio() == null) {
                dfl.create(domicilioSeleccionado);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "✅ Domicilio asignado", ""));
            } else {
                dfl.edit(domicilioSeleccionado);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "✅ Domicilio actualizado", ""));
            }

            // Redirigir a indexDomi
            return "indexDomi?faces-redirect=true";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "❌ Error", e.getMessage()));
            return null;
        }
    }

    // === Métodos de consulta (sin cambios) ===
    public List<Domicilios> obtenerDomicilios() {
        return this.dfl.findAll();
    }
    
    public List<Domicilios> obtenerDomiciliosHoy() {
        LocalDate hoy = LocalDate.now();
        return obtenerDomicilios().stream()
                .filter(d -> {
                    if (d.getFechaDomicilio() == null) return false;
                    LocalDate fechaDomicilio = d.getFechaDomicilio().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return fechaDomicilio.equals(hoy);
                })
                .collect(Collectors.toList());
    }
    
    public BigDecimal obtenerTotalVentas() {
        return BigDecimal.ZERO;
    }
    
    public List<Domicilios> obtenerUltimosDomicilios(int n) {
        return obtenerDomicilios().stream()
                .sorted((d1, d2) -> Long.compare(d2.getIdDomicilio(), d1.getIdDomicilio()))
                .limit(n)
                .collect(Collectors.toList());
    }
    
    public Map<String, Long> getDomiciliosPorDia() {
        LocalDate hoy = LocalDate.now();
        Map<String, Long> resultado = new LinkedHashMap<>();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate fecha = hoy.minusDays(i);
            resultado.put(fecha.format(DateTimeFormatter.ofPattern("dd/MM")), 0L);
        }
        
        obtenerDomicilios().stream()
            .filter(d -> d.getFechaDomicilio() != null)
            .forEach(d -> {
                LocalDate fechaDom = d.getFechaDomicilio().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                String fechaStr = fechaDom.format(DateTimeFormatter.ofPattern("dd/MM"));
                if (resultado.containsKey(fechaStr)) {
                    resultado.put(fechaStr, resultado.get(fechaStr) + 1);
                }
            });
        
        return resultado;
    }

    public String[] getDomiciliosFechasLabels() {
        return getDomiciliosPorDia().keySet().toArray(new String[0]);
    }

    public Long[] getDomiciliosFechasData() {
        return getDomiciliosPorDia().values().toArray(new Long[0]);
    }
    
    public List<Factura> obtenerFacturasDisponibles() {
        return this.ffl.findAll();
    }
    
    public List<Usuario> obtenerDomiciliarios() {
        return this.ufl.findByRol(3);
    }
    
    public List<Usuario> obtenerClientesConDomicilios() {
        List<Domicilios> domicilios = obtenerDomicilios();
        List<Usuario> clientes = new ArrayList<>();
        
        for (Domicilios dom : domicilios) {
            if (dom.getFacturaIDFACTURA() != null && 
                dom.getFacturaIDFACTURA().getUsuarioIDUSUARIOVENDEDOR() != null) {
                Usuario cliente = dom.getFacturaIDFACTURA().getUsuarioIDUSUARIOVENDEDOR();
                if (!clientes.contains(cliente)) {
                    clientes.add(cliente);
                }
            }
        }
        
        return clientes;
    }
    
    public void cancelarDomicilio(Domicilios dom2) {
        try {
            this.dfl.remove(dom2);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "✅ Domicilio cancelado correctamente", ""));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "❌ Error al cancelar domicilio", e.getMessage()));
        }
    }
    
    /**
     * Genera CSV para exportación desde el dashboard
     */
    public String exportDomiciliosCSV() {
        try {
            StringBuilder csv = new StringBuilder();
            csv.append("ID,Vendedor,Dirección,Fecha,Estado\n");
            
            for (Domicilios dom : obtenerUltimosDomicilios(5)) {
                String vendedor = "N/A";
                if (dom.getFacturaIDFACTURA() != null && 
                    dom.getFacturaIDFACTURA().getUsuarioIDUSUARIOVENDEDOR() != null) {
                    vendedor = dom.getFacturaIDFACTURA().getUsuarioIDUSUARIOVENDEDOR().getNombreUsuario() + " " +
                              dom.getFacturaIDFACTURA().getUsuarioIDUSUARIOVENDEDOR().getApellidoUsuario();
                }
                
                String direccion = dom.getDirecccionDomicilio() != null ? 
                    dom.getDirecccionDomicilio().replace(",", ";") : "";
                
                String fecha = dom.getFechaDomicilio() != null ? 
                    new SimpleDateFormat("dd/MM/yyyy").format(dom.getFechaDomicilio()) : "";
                
                String estado = dom.getEstado() != null ? dom.getEstado() : "Pendiente";
                
                csv.append(dom.getIdDomicilio()).append(",")
                   .append("\"").append(vendedor).append("\"").append(",")
                   .append("\"").append(direccion).append("\"").append(",")
                   .append(fecha).append(",")
                   .append(estado).append("\n");
            }
            return csv.toString();
        } catch (Exception e) {
            LOG.severe("Error al generar CSV: " + e.getMessage());
            return "ID,Vendedor,Dirección,Fecha,Estado\nError al generar datos";
        }
    }
}
