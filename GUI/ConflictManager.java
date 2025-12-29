/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author apermac
 */

package GUI;

import java.util.List;
import traffic.Traffic;

/**
 * Gestor de Conflictos (TCAS Simplificado)
 * Comprueba violaciones de separación estándar: 5 NM horizontal / 1000 ft vertical.
 */
public class ConflictManager {
    
    // Constantes de separación estándar
    // Modificar estos valores si se quiere comprobar el funcionamiento, ya que
    // no suele haber aeronaves lo suficientemente cercanas para saltar la
    // alerta
    private static final double SEPARATION_HORIZONTAL_NM = 30.0; // 5 Millas
    private static final double SEPARATION_VERTICAL_FT = 20000.0; // 1000 Pies
    
    // Radio de la Tierra en Millas Náuticas
    private static final double EARTH_RADIUS_NM = 3440.065;

    public static void updateConflicts(List<TrafficGraphic> trafficList) {
        // 1. Resetear el estado de conflicto de todos (volver a blanco/verde)
        for (TrafficGraphic t : trafficList) {
            t.setInConflict(false);
        }

        // 2. Comparar todos contra todos (sin repetir parejas)
        for (int i = 0; i < trafficList.size(); i++) {
            for (int j = i + 1; j < trafficList.size(); j++) {
                TrafficGraphic tg1 = trafficList.get(i);
                TrafficGraphic tg2 = trafficList.get(j);

                // Si detectamos conflicto, marcamos AMBOS aviones
                if (checkConflict(tg1, tg2)) {
                    tg1.setInConflict(true);
                    tg2.setInConflict(true);
                    
                    // Opcional: Imprimir aviso en consola para depurar
                    System.out.println("ALERTA: Conflicto entre " + 
                            tg1.getTraffic().getCallsign() + " y " + 
                            tg2.getTraffic().getCallsign());
                }
            }
        }
    }

    private static boolean checkConflict(TrafficGraphic tg1, TrafficGraphic tg2) {
        Traffic t1 = tg1.getTraffic();
        Traffic t2 = tg2.getTraffic();
        
        // --- 1. Chequeo Vertical ---
        double alt1 = t1.getAltitude(); // Pies
        double alt2 = t2.getAltitude();
        double diffAlt = Math.abs(alt1 - alt2);
        
        // Si están separados verticalmente más de 1000 pies, NO hay conflicto.
        // Ahorramos cálculo trigonométrico si ya están lejos en altura.
        if (diffAlt >= SEPARATION_VERTICAL_FT) {
            return false;
        }

        // --- 2. Chequeo Horizontal (Solo si falló el vertical) ---
        double lat1 = t1.getPosition().getLatitude();
        double lon1 = t1.getPosition().getLongitude();
        double lat2 = t2.getPosition().getLatitude();
        double lon2 = t2.getPosition().getLongitude();

        double distNM = calcularDistanciaNM(lat1, lon1, lat2, lon2);

        // Conflicto si están cerca horizontalmente (< 5 NM) Y verticalmente (< 1000 ft)
        return (distNM < SEPARATION_HORIZONTAL_NM);
    }

    // Fórmula de Haversine para distancia precisa en esfera
    private static double calcularDistanciaNM(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
                   
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_NM * c;
    }
}