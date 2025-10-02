package com.mycompany.project2.services; // ✅ Ahora es "services" (plural)

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@ApplicationScoped
public class GeolocationService {

    private static final Logger LOG = Logger.getLogger(GeolocationService.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Geocodifica una dirección usando OpenStreetMap (Nominatim).
     * Añade automáticamente ", Bogotá, Colombia" si no está presente.
     * 
     * @param direccion Dirección en texto (ej: "Carrera 7 #22-33")
     * @return double[]{latitud, longitud} o null si falla
     */
    public double[] geocodificar(String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) {
            return null;
        }

        try {
            String dir = direccion.trim().toLowerCase();
            String direccionCompleta;

            // Si ya contiene una ciudad conocida, solo añadimos "Colombia"
            if (dir.contains("bogotá") || dir.contains("bogota") ||
                dir.contains("medellín") || dir.contains("medellin") ||
                dir.contains("cali") || dir.contains("barranquilla") ||
                dir.contains("cartagena") || dir.contains("colombia")) {
                direccionCompleta = direccion.trim() + (dir.contains("colombia") ? "" : ", Colombia");
            } else {
                // Si no, asumimos Bogotá
                direccionCompleta = direccion.trim() + ", Bogotá, Colombia";
            }

            String encodedAddress = URLEncoder.encode(direccionCompleta, StandardCharsets.UTF_8.toString());
            String urlStr = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=" + encodedAddress;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // User-Agent obligatorio para Nominatim
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; DesayunosYDetallesApp/1.0; +http://localhost:8080)");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                LOG.severe("Nominatim respondió con código: " + responseCode);
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line);
            }
            in.close();
            conn.disconnect();

            JsonNode jsonArray = objectMapper.readTree(content.toString());
            if (jsonArray.isArray() && jsonArray.size() > 0) {
                JsonNode first = jsonArray.get(0);
                double lat = first.get("lat").asDouble();
                double lon = first.get("lon").asDouble();
                LOG.info("✅ Geocodificado: " + direccion + " → (" + lat + ", " + lon + ")");
                return new double[]{lat, lon};
            } else {
                LOG.warning("❌ No se encontraron resultados para: " + direccionCompleta);
            }

        } catch (Exception e) {
            LOG.severe("❌ Error al geocodificar '" + direccion + "': " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
