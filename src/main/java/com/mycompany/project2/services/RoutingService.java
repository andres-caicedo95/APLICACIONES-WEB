package com.mycompany.project2.services;

import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@ApplicationScoped
public class RoutingService {

    /**
     * Llama a OSRM demo server para obtener la ruta entre dos coordenadas.
     * @param startLon longitud inicio (tienda)
     * @param startLat lat inicio
     * @param endLon longitud fin (cliente)
     * @param endLat lat fin
     * @return String JSON completo devuelto por OSRM (contiene geometry GeoJSON)
     */
    public String getRouteGeoJson(double startLon, double startLat, double endLon, double endLat) {
        try {
            String coords = startLon + "," + startLat + ";" + endLon + "," + endLat;
            // OSRM public server (uso para pruebas)
            String urlStr = "https://router.project-osrm.org/route/v1/driving/" + coords
                          + "?overview=full&geometries=geojson&steps=false";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "DesayunosYDetallesApp/1.0");
            int code = conn.getResponseCode();
            if (code != 200) {
                return null;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
            in.close();
            return sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
