/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Archivos;

import GUI.Map;
import GUI.GenericRadarPanel;
import GUI.Polygon;
import GUI.Polygon.Point;
import GUI.PolygonArrayPlot;
import GUI.PositionGraphic;
import GUI.TrafficGraphic;
import GUI.TrafficPlot;
import antenna.AntennaReceiver;
import java.awt.BasicStroke;
import java.awt.Color;
import traffic.Traffic;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;
import traffic.TrafficListener;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author jvila * // >>> CLASE PRINCIPAL DE DIBUJO // >>> Este panel se encarga
 * de pintar todo: mapa, rutas, aviones y herramientas. // >>> Tambien gestiona
 * la interaccion del raton (Zoom, Pan y Clics).
 */
public class RadarPanel extends GenericRadarPanel implements TrafficListener {

    // --- NUEVOS LÍMITES: ESPAÑA COMPLETA (Datos de 'Límites bitmap.txt') ---
    private final Double latNorth = 44.4;
    private final Double latSouth = 34.7;
    private final Double lonEast  = 4.8;
    private final Double lonWest  = -9.9;
    
    // size
    double sizeEW = lonEast - lonWest;
    double sizeNS = latNorth - latSouth;
    
    private Dimension preferredSize;
    
    // Map definition
    private String mapfile = "src/resources/mapa_espana.txt";
    private Map map;
    private Polygon[] map_p;
    private Component map_c;

    // >>> VARIABLE CLAVE PARA EL ZOOM
    // >>> mapArea define que trozo de mundo estamos viendo. Al hacer zoom,
    // >>> hacemos este rectangulo mas peque;o o mas grande.
    private Rectangle2D mapArea;

    // Traffic source
    AntennaReceiver ar;

    private Timeout tout;

    // >>> VENTANAS FLOTANTES Y HERRAMIENTAS
    private TrafficDetailDialog infoWindow;      // Ventana de info simple
    private TrafficComparisonDialog compareWindow; // Ventana comparativa
    private AirwayMap airways;                   // Capa de aerovias y aeropuertos
    private java.awt.Point lastDragPoint = null; // Para calcular el arrastre del mapa

    public RadarPanel(String mapfile, AntennaReceiver ar) {
        initComponents();
        this.ar = ar;

        // >>> Inicializamos el area visible con los limites por defecto
        mapArea = new Rectangle2D.Double(lonWest, latNorth, lonEast - lonWest, latNorth - latSouth);

        // >>> Inicializamos las ventanas ocultas y cargamos las rutas
        infoWindow = new TrafficDetailDialog(null);
        compareWindow = new TrafficComparisonDialog(null);
        this.airways = new AirwayMap();

        preferredSize = new Dimension(750, 900);
        this.setPreferredSize(preferredSize);
        this.setBounds(0, 0, 750, 900);

        ar.setListener(this);

        try {
            map = new Map(mapfile, Color.GREEN);
        } catch (IOException ex) {
            System.out.println("RadarPanel: file " + mapfile + " not found.");
        }

        this.map_p = this.map.getMap();

        if (this.map != null) {
            map_c = new PolygonArrayPlot(this.map_p, map.getColor(), this);
            this.add(map_c);
        }

        this.setVisible(true);

        tout = new Timeout(2000);

        // --- INTERACTIVIDAD: ZOOM (AL RATÓN) Y PAN ---
        // >>> 1. OYENTE PARA EL ZOOM (Rueda del Raton)
        this.addMouseWheelListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
                double zoomFactor = 1.1; // Factor de velocidad del zoom

                // Si rueda abajo -> Zoom Out (Alejar)
                // Si rueda arriba -> Zoom In (Acercar)
                if (e.getWheelRotation() < 0) {
                    zoomFactor = 1 / zoomFactor;
                }

                double oldW = mapArea.getWidth();
                double oldH = mapArea.getHeight();
                double newW = oldW * zoomFactor;
                double newH = oldH * zoomFactor;

                // >>> LIMITE DE ZOOM (SEGURIDAD)
                // Evitamos que el usuario haga zoom infinito y rompa las matematicas (overflow)
                // Si el ancho es menor de 0.01 grados (aprox 1km) o mayor de 40 grados, paramos.
                if (newW < 0.01 || newW > 40.0) {
                    return;
                }

                // >>> ZOOM INTELIGENTE (HACIA EL RATON)
                // Calculamos donde esta el raton en porcentaje de la pantalla (0.0 a 1.0)
                double mouseRatioX = e.getX() / (double) getWidth();
                double mouseRatioY = e.getY() / (double) getHeight();

                // La diferencia de tama;o (lo que crece o encoge el mapa)
                double dW = newW - oldW;
                double dH = newH - oldH;

                // Movemos el origen del mapa compensando exactamente donde esta el raton
                // Matematicas: El punto bajo el raton debe mantener su lat/lon
                double newX = mapArea.getX() - (dW * mouseRatioX);
                double newY = mapArea.getY() + (dH * mouseRatioY); // Signo + porque Y latitud va al reves que Y pantalla

                // Aplicamos el nuevo area
                mapArea.setRect(newX, newY, newW, newH);

                repaint(); // Redibujamos todo con el nuevo zoom
            }
        });

        // >>> 2. OYENTE PARA EL CLIC (Guardar punto de inicio para arrastrar)
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                lastDragPoint = e.getPoint();
                // (Opcional) Aqui puedes llamar a tu logica de trafficClicked si quieres
                // que funcione tambien al arrastrar, pero mejor dejarlo solo en TrafficPlot
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                lastDragPoint = null; // Soltamos el mapa
            }
        });

        // >>> 3. OYENTE PARA EL MOVIMIENTO (Arrastrar / Pan)
        this.addMouseMotionListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (lastDragPoint != null) {
                    // Distancia movida en pixeles
                    double dxPx = e.getX() - lastDragPoint.getX();
                    double dyPx = e.getY() - lastDragPoint.getY();

                    // Convertir pixeles a grados (Lat/Lon)
                    double dxDeg = dxPx * (mapArea.getWidth() / getWidth());
                    double dyDeg = dyPx * (mapArea.getHeight() / getHeight());

                    // Desplazar el mapa (Invertimos signos para efecto "arrastrar papel")
                    mapArea.setRect(
                            mapArea.getX() - dxDeg,
                            mapArea.getY() + dyDeg,
                            mapArea.getWidth(),
                            mapArea.getHeight()
                    );

                    lastDragPoint = e.getPoint();
                    repaint();
                }
            }
        });
        tout.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public Dimension getPreferredSize() {
        //return super.getPreferredSize(); //To change body of generated methods, choose Tools | Templates.
        return this.preferredSize;
    }

    // >>> BUCLE DE PINTADO (IMPORTANTE)
    // >>> Este metodo se ejecuta constantemente. Aqui definimos el orden de las capas:
    // >>> 1. Mapa Base -> 2. Rutas/Aerovias -> 3. Estelas de aviones -> 4. Iconos -> 5. Interfaz
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // >>> CAPA 1: RUTAS Y AEROPUERTOS
        if (airways != null) {
            // Pintar Rutas (Lineas grises finas)
            g2.setColor(new Color(100, 100, 100)); // Gris oscuro
            g2.setStroke(new BasicStroke(1.0f));

            for (AirwayMap.NamedRoute r : airways.routes) {
                // Usamos java.awt.Point explicitamente para evitar error de tipos
                java.awt.Point p1 = toScreen(r.p1.lat, r.p1.lon);
                java.awt.Point p2 = toScreen(r.p2.lat, r.p2.lon);

                if (p1 != null && p2 != null) {
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }

            // Pintar Aeropuertos/Puntos (Cuadrados azules)
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            for (AirwayMap.NamedPoint wp : airways.waypoints) {
                java.awt.Point p = toScreen(wp.lat, wp.lon);
                if (p != null) {
                    g2.setColor(Color.CYAN);
                    g2.drawRect(p.x - 3, p.y - 3, 6, 6);
                    g2.setColor(Color.GRAY);
                    g2.drawString(wp.name, p.x + 5, p.y + 5);
                }
            }
        }

        List<TrafficGraphic> traffics = getAllTrafficGraphics();

        // >>> CAPA 2: ESTELAS Y POSICIONAMIENTO DE AVIONES
        for (TrafficGraphic tg : traffics) {
            // Recuperamos lista de POSITIONS GPS (No pixeles fijos)
            java.util.List<de.serosystems.lib1090.Position> trail = tg.getTrail();

            if (trail != null && trail.size() > 0) {
                g2.setColor(tg.getColor());
                float[] dashPattern = {5.0f, 5.0f};
                g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));

                // Recorremos el historial GPS y convertimos a pantalla en tiempo real
                for (int i = 0; i < trail.size() - 1; i++) {
                    de.serosystems.lib1090.Position pos1 = trail.get(i);
                    de.serosystems.lib1090.Position pos2 = trail.get(i + 1);

                    // CONVERSION DINAMICA: GPS -> Pixeles actuales
                    // Usamos tu metodo 'toScreen' que usa el mapArea actual
                    java.awt.Point p1 = toScreen(pos1.getLatitude(), pos1.getLongitude());
                    java.awt.Point p2 = toScreen(pos2.getLatitude(), pos2.getLongitude());

                    if (p1 != null && p2 != null) {
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }

                // Unir el ultimo punto historico con la posicion ACTUAL del avion
                de.serosystems.lib1090.Position lastPos = trail.get(trail.size() - 1);
                de.serosystems.lib1090.Position currPosObj = tg.getTraffic().getPosition_ext();

                if (lastPos != null && currPosObj != null) {
                    java.awt.Point pLast = toScreen(lastPos.getLatitude(), lastPos.getLongitude());
                    java.awt.Point pCurr = toScreen(currPosObj.getLatitude(), currPosObj.getLongitude());

                    if (pLast != null && pCurr != null) {
                        g2.drawLine(pLast.x, pLast.y, pCurr.x, pCurr.y);
                    }
                }
                g2.setStroke(new BasicStroke());
            }

            // >>> FIX DE ZOOM: FORZAR POSICION DEL ICONO
            // >>> Calculamos donde debe estar el avion AHORA MISMO segun el zoom actual
            // >>> y movemos el componente visual manualmente.
            de.serosystems.lib1090.Position posActual = tg.getTraffic().getPosition_ext();
            if (posActual != null) {
                java.awt.Point pScreen = toScreen(posActual.getLatitude(), posActual.getLongitude());

                if (pScreen != null) {
                    // Recuperamos la "pegatina" del avion (TrafficPlot)
                    TrafficPlot plot = tg.getTrafficPlot();

                    // La movemos al pixel exacto. 
                    // Restamos la mitad de su tama;o (40, 15) para centrarlo.
                    plot.setBounds(pScreen.x - 40, pScreen.y - 15, 80, 30);

                    // Importante: Forzamos que se repinte el icono tambien
                    plot.revalidate();
                    plot.repaint();
                }
            }
        }

        // >>> CAPA 3: INTERACCION Y MEDICION (LINEA AMARILLA)
        java.util.List<TrafficGraphic> selected = new java.util.ArrayList<>();
        for (TrafficGraphic tg : traffics) {
            if (tg.isSelected()) {
                selected.add(tg);
            }
        }

        // GESTION DE DATOS EN VENTANAS (YA NO ABRIMOS VENTANAS AQUI)
        if (selected.size() == 2) {
            TrafficGraphic t1 = selected.get(0);
            TrafficGraphic t2 = selected.get(1);

            // Solo actualizamos datos SI la ventana esta visible
            if (compareWindow.isVisible()) {
                compareWindow.updateComparison(t1.getTraffic(), t2.getTraffic());
            }

            // DIBUJAR LINEA AMARILLA (Esto si se pinta siempre que haya 2 seleccionados)
            // Calculamos posiciones actuales para que la linea siga a los aviones al hacer zoom
            de.serosystems.lib1090.Position pos1 = t1.getTraffic().getPosition_ext();
            de.serosystems.lib1090.Position pos2 = t2.getTraffic().getPosition_ext();

            if (pos1 != null && pos2 != null) {
                java.awt.Point p1 = toScreen(pos1.getLatitude(), pos1.getLongitude());
                java.awt.Point p2 = toScreen(pos2.getLatitude(), pos2.getLongitude());

                if (p1 != null && p2 != null) {
                    g2.setColor(Color.YELLOW);
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);

                    double dist = distanceNM(
                            pos1.getLatitude(), pos1.getLongitude(),
                            pos2.getLatitude(), pos2.getLongitude()
                    );

                    int midX = (p1.x + p2.x) / 2;
                    int midY = (p1.y + p2.y) / 2;

                    String text = String.format("%.1f NM", dist);
                    g2.setColor(Color.BLACK);
                    g2.fillRect(midX - 25, midY - 10, 50, 20);
                    g2.setColor(Color.YELLOW);
                    g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
                    g2.drawString(text, midX - 20, midY + 5);
                }
            }
        } else if (selected.size() == 1) {
            TrafficGraphic tg = selected.get(0);

            // CORRECCION: Solo actualizamos si el usuario tiene la ventana abierta
            if (infoWindow.isVisible()) {
                infoWindow.updateInfo(tg.getTraffic());
            }
        }
    }

    @Override
    public Rectangle2D getArea() {
        return mapArea;
    }

    private synchronized void removeTraffics() {
        Component[] componentList = this.getComponents();
        for (Component c : componentList) {
            if (c instanceof TrafficPlot) {
                remove(c);
            }
        }
        //IMPORTANT
        this.revalidate();
    }

    @Override
    public void putTraffic(Traffic t) {
        TrafficGraphic tg = (TrafficGraphic) t.getTrafficExt();

        if (tg == null) {
            tg = new TrafficGraphic(t, this);
            //t.setTrafficExt(tg);
            this.add(tg.getTrafficPlot());
            System.out.println("added: " + t.getCallsign());
        }

        tout.activity();
        revalidate();
        repaint();
    }

    @Override
    public void removeTraffic(Traffic t) {
        TrafficGraphic tg = (TrafficGraphic) t.getTrafficExt();
        this.remove(tg.getTrafficPlot());
        tout.activity();
        revalidate();
        repaint();
    }

    @Override
    public void updateTraffic(Traffic t) {
        TrafficGraphic tg = (TrafficGraphic) t.getTrafficExt();

        if (tg == null) {
            tg = new TrafficGraphic(t, this);
            //t.setTrafficExt(tg);
            this.add(tg.getTrafficPlot());
        }
        System.out.println("updated: " + t.getCallsign());

        java.util.List<TrafficGraphic> allTraffic = getAllTrafficGraphics();
        GUI.ConflictManager.updateConflicts(allTraffic);

        tout.activity();
        repaint();
    }

    @Override
    public void stop() {
        this.removeTraffics();
        revalidate();
        repaint();
    }

    private java.util.List<TrafficGraphic> getAllTrafficGraphics() {
        java.util.List<TrafficGraphic> list = new java.util.ArrayList<>();
        for (java.awt.Component c : this.getComponents()) {
            if (c instanceof TrafficPlot) {
                list.add(((TrafficPlot) c).getTrafficGraphic());
            }
        }
        return list;
    }

    //--------------------------------------------------------------------------
    class Timeout extends Thread {

        private boolean activity;
        private int period;

        public Timeout(int period) {
            this.period = period;
            activity = false;
        }

        public void activity() {
            activity = true;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(period);
                } catch (InterruptedException ex) {
                }
                if (!activity) {
                    removeTraffics();
                    //refreshTraffics();
                    repaint();
                }
                activity = false;
            }
        }
    }

    // Formula de Haversine para calcular distancia entre dos coordenadas (Lat/Lon)
    private double distanceNM(double lat1, double lon1, double lat2, double lon2) {
        double EARTH_RADIUS_NM = 3440.065;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_NM * c;
    }

    // Metodo auxiliar para convertir Lat/Lon a Pixeles de pantalla usando el mapArea actual
    private java.awt.Point toScreen(double lat, double lon) {
        if (mapArea == null) {
            return null;
        }

        double x = this.getWidth() * (lon - mapArea.getX()) / mapArea.getWidth();
        double y = this.getHeight() * (mapArea.getY() - lat) / mapArea.getHeight();

        return new java.awt.Point((int) x, (int) y);
    }

    // >>> LOGICA INTELIGENTE DE SELECCION (CLICS)
    // >>> Gestiona si seleccionamos uno, dos para comparar, o si cerramos ventanas.
    public void trafficClicked(TrafficGraphic clickedTg) {
        // 1. Miramos quien esta seleccionado ahora mismo
        java.util.List<TrafficGraphic> currentSelected = new java.util.ArrayList<>();
        for (TrafficGraphic tg : getAllTrafficGraphics()) {
            if (tg.isSelected()) {
                currentSelected.add(tg);
            }
        }

        // CASOS 1, 2, 3, 4 (Seleccion simple, deseleccion, comparacion)
        if (currentSelected.isEmpty()) {
            clickedTg.setSelected(true);
        } else if (currentSelected.contains(clickedTg)) {
            clickedTg.setSelected(false);
        } else if (currentSelected.size() == 1) {
            TrafficGraphic otherTg = currentSelected.get(0);
            java.awt.Toolkit.getDefaultToolkit().beep();

            int response = javax.swing.JOptionPane.showConfirmDialog(this,
                    "Ya tienes seleccionado el vuelo " + otherTg.getTraffic().getCallsign() + ".\n"
                    + "¿Quieres COMPARARLO con el " + clickedTg.getTraffic().getCallsign() + "?",
                    "Gestion de Seleccion",
                    javax.swing.JOptionPane.YES_NO_OPTION);

            if (response == javax.swing.JOptionPane.YES_OPTION) {
                clickedTg.setSelected(true);
            } else {
                otherTg.setSelected(false);
                clickedTg.setSelected(true);
            }
        } else {
            for (TrafficGraphic t : currentSelected) {
                t.setSelected(false);
            }
            clickedTg.setSelected(true);
        }

        // >>> GESTION DE APERTURA DE VENTANAS
        // >>> Recalculamos cuantos hay seleccionados y abrimos/cerramos ventanas
        int count = 0;
        for (TrafficGraphic tg : getAllTrafficGraphics()) {
            if (tg.isSelected()) {
                count++;
            }
        }

        if (count == 1) {
            // Abrir ficha simple si no estaba abierta
            if (!infoWindow.isVisible()) {
                infoWindow.setLocation(100, 100);
                infoWindow.setVisible(true);
            }
            if (compareWindow.isVisible()) {
                compareWindow.setVisible(false);
            }
        } else if (count == 2) {
            // Abrir comparador si no estaba abierto
            if (!compareWindow.isVisible()) {
                compareWindow.setLocation(100, 400);
                compareWindow.setVisible(true);
            }
            if (infoWindow.isVisible()) {
                infoWindow.setVisible(false);
            }
        } else {
            // Cerrar todo si no hay seleccion
            if (infoWindow.isVisible()) {
                infoWindow.setVisible(false);
            }
            if (compareWindow.isVisible()) {
                compareWindow.setVisible(false);
            }
        }

        repaint();
    }

//    public void paintMap(Graphics2D g) {
//        Graphics2D g2 = (Graphics2D) g;
//
//        Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD);
//        for (int i = 0; i < map.data.length; i++) {
//            int lonNor = (int) (this.getWidth() * (map.data[i].point[0].longitude - mapArea.getX()) / mapArea.getWidth()); // normalization
//            int latNor = (int) (this.getHeight() * (mapArea.getY() - map.data[i].point[0].latitude) / mapArea.getHeight());
//            path.moveTo(lonNor, latNor);
//            for (int j = 1; j < map.data[i].point.length; j++) {
//                //    System.out.println(map.data[i].data[j].latitude + " " + map.data[i].data[j].longitude);
//
//                lonNor = (int) (this.getWidth() * (map.data[i].point[j].longitude - mapArea.getX()) / mapArea.getWidth()); // normalization
//                latNor = (int) (this.getHeight() * (mapArea.getY() - map.data[i].point[j].latitude) / mapArea.getHeight());
//                path.lineTo(lonNor, latNor);
//
//            }
//
//            //System.out.println(i + " ********************************************");
//        }
//        path.closePath();
//        g2.setColor(Color.yellow);
//        g2.draw(path);
//
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
