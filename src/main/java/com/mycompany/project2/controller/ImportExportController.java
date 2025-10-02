package com.mycompany.project2.controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Named
@SessionScoped
public class ImportExportController implements Serializable {

private Part file;
private String selectedTable;
private List<String> availableTables;

// Configuración de la base de datos - AJUSTA ESTOS VALORES
private static final String DB_URL = "jdbc:mysql://127.0.0.1:3307/desyunosydetalles?useSSL=false&serverTimezone=UTC";
private static final String DB_USER = "root"; // Cambia por tu usuario
private static final String DB_PASSWORD = ""; // Cambia por tu contraseña

public ImportExportController() {
    availableTables = Arrays.asList(
        "usuario", "cliente", "producto", "domicilios", 
        "factura", "promociones", "rol", "acciones"
    );
}

// --- CORRECCIÓN: método de navegación al dashboard ---
public String goDashboard() {
    // Retorna la ruta real de la vista y fuerza redirección
    return "/views/dashboard/dashboard.xhtml?faces-redirect=true";
}


public void prepareImport() {
    try {
        // <-- CORRECCIÓN: usar getRequestContextPath() para formar la URL correcta
        FacesContext fc = FacesContext.getCurrentInstance();
        String contextPath = fc.getExternalContext().getRequestContextPath();
        // redirigir a la página relativa al contexto actual
        fc.getExternalContext().redirect(contextPath + "/views/import-export/import.xhtml");
    } catch (Exception e) {
        e.printStackTrace();
        addMessage("Error", "No se pudo redirigir a la página de importación: " + e.getMessage());
    }
}

public void importFile() {
    if (file == null) {
        addMessage("Error", "Por favor seleccione un archivo");
        return;
    }
    
    if (selectedTable == null || selectedTable.isEmpty()) {
        addMessage("Error", "Por favor seleccione una tabla");
        return;
    }
    
    try {
        String fileName = file.getSubmittedFileName();
        if (fileName == null || fileName.isEmpty()) {
            addMessage("Error", "Nombre de archivo inválido");
            return;
        }
        
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        
        if ("csv".equals(fileType)) {
            boolean success = importCSV(file, selectedTable);
            if (success) {
                addMessage("Éxito", "Archivo importado correctamente a la tabla: " + selectedTable);
                // Limpiar el formulario
                file = null;
                selectedTable = null;
            }
        } else {
            addMessage("Error", "Solo se permiten archivos CSV");
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        addMessage("Error", "Error durante la importación: " + e.getMessage());
    }
}

private boolean importCSV(Part file, String tableName) {
    Connection conn = null;
    BufferedReader reader = null;
    int lineNumber = 0;
    int insertedRows = 0;
    
    try {
        // Establecer conexión a la base de datos
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        conn.setAutoCommit(false);
        
        InputStream is = file.getInputStream();
        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        
        String line;
        String[] columns = null;
        
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            
            // Saltar líneas vacías
            if (line.trim().isEmpty()) continue;
            
            // Procesar CSV (manejar comas dentro de comillas)
            String[] values = parseCSVLine(line);
            
            if (lineNumber == 1) {
                // Primera línea - nombres de columnas
                columns = values;
                System.out.println("Columnas detectadas: " + Arrays.toString(columns));
                continue;
            }
            
            // Validar que tenemos columnas
            if (columns == null) {
                addMessage("Error", "El archivo no tiene encabezados de columnas");
                return false;
            }
            
            // Insertar datos en la base de datos
            if (insertData(conn, tableName, columns, values)) {
                insertedRows++;
            } else {
                addMessage("Advertencia", "Error al insertar línea " + lineNumber);
            }
        }
        
        conn.commit();
        addMessage("Éxito", "Se importaron " + insertedRows + " registros correctamente");
        return true;
        
    } catch (Exception e) {
        try {
            if (conn != null) conn.rollback();
        } catch (Exception rollbackEx) {
            rollbackEx.printStackTrace();
        }
        e.printStackTrace();
        addMessage("Error", "Error en línea " + lineNumber + ": " + e.getMessage());
        return false;
    } finally {
        try {
            if (reader != null) reader.close();
            if (conn != null) conn.close();
        } catch (Exception closeEx) {
            closeEx.printStackTrace();
        }
    }
}

private String[] parseCSVLine(String line) {
    // Manejo básico de CSV - separar por comas, ignorando comas dentro de comillas
    List<String> result = new ArrayList<>();
    boolean inQuotes = false;
    StringBuilder field = new StringBuilder();
    
    for (int i = 0; i < line.length(); i++) {
        char c = line.charAt(i);
        if (c == '"') {
            inQuotes = !inQuotes;
        } else if (c == ',' && !inQuotes) {
            result.add(field.toString().trim());
            field.setLength(0);
        } else {
            field.append(c);
        }
    }
    result.add(field.toString().trim());
    
    return result.toArray(new String[0]);
}

private boolean insertData(Connection conn, String tableName, String[] columns, String[] values) {
    PreparedStatement pstmt = null;
    
    try {
        // Construir la consulta SQL
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName).append(" (");
        
        // Agregar columnas
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) sql.append(", ");
            sql.append(columns[i]);
        }
        
        sql.append(") VALUES (");
        
        // Agregar placeholders
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sql.append(", ");
            sql.append("?");
        }
        
        sql.append(")");
        
        pstmt = conn.prepareStatement(sql.toString());
        
        // Establecer valores
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            
            // Manejar valores vacíos como NULL
            if (value == null || value.trim().isEmpty()) {
                pstmt.setNull(i + 1, java.sql.Types.VARCHAR);
            } else {
                // Remover comillas si existen
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                pstmt.setString(i + 1, value);
            }
        }
        
        int result = pstmt.executeUpdate();
        return result > 0;
        
    } catch (Exception e) {
        System.err.println("Error insertando datos: " + e.getMessage());
        return false;
    } finally {
        try {
            if (pstmt != null) pstmt.close();
        } catch (Exception closeEx) {
            closeEx.printStackTrace();
        }
    }
}

private void addMessage(String summary, String detail) {
    FacesContext.getCurrentInstance().addMessage(null, 
        new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
}

// Getters y Setters
public Part getFile() {
    return file;
}

public void setFile(Part file) {
    this.file = file;
}

public String getSelectedTable() {
    return selectedTable;
}

public void setSelectedTable(String selectedTable) {
    this.selectedTable = selectedTable;
}

public List<String> getAvailableTables() {
    return availableTables;
}

}
