package com.mycompany.project2.services;

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
public class GeoService {

    private static final Logger LOG = Logger.getLogger(GeoService.class.getName());
    private final ObjectMapper mapper = new ObjectMapper();

    public double[] geocodificar(String direccion) {
        try {
            if (direccion == null || direccion.trim().isEmpty()) return null;

            String direccionFinal = direccion;
            String dirLower = direccion.toLowerCase();

            // Si no hay ciudad específica, usamos Bogotá
            if (!dirLower.contains("colombia")) {
                direccionFinal += ", Bogotá, Colombia";
            }

            String query = URLEncoder.encode(direccionFinal, StandardCharsets.UTF_8.toString());
            String urlStr = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=" + query;

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent",
                    "DesayunosYDetalles/1.0 (https://github.com/andres)");
            conn.setConnectTimeout(9000);
            conn.setReadTimeout(9000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = reader.lines().reduce("", (a, b) -> a + b);

            JsonNode root = mapper.readTree(json);
            if (root.isArray() && root.size() > 0) {
                double lat = root.get(0).get("lat").asDouble();
                double lon = root.get(0).get("lon").asDouble();
                return new double[]{lat, lon};
            }

        } catch (Exception e) {
            LOG.severe("Error geocodificando: " + e.getMessage());
        }
        return null;
    }
}
