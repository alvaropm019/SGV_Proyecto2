 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Archivos;

/**
 *
 * @author jorge
 */
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que define la estructura del espacio aereo (Aeropuertos y Aerovias).
 * Datos extraidos de Routes.m 
 * // >>> CLASE DE DATOS ESTATICOS 
 * // >>> Esta clase funciona como una base de datos local. 
 * // >>> Su unica funcion es almacenar las coordenadas fijas de aeropuertos y rutas 
 * // >>> para que RadarPanel pueda leerlas y pintarlas como capa de fondo.
 */
public class AirwayMap {

    // >>> Lista de Puntos (Nodos): Aeropuertos, VORs, Waypoints
    // >>> RadarPanel recorrera esta lista para pintar los cuadraditos azules.
    public List<NamedPoint> waypoints;

    // >>> Lista de Lineas (Aristas): Aerovias que conectan los puntos
    // >>> RadarPanel recorrera esta lista para pintar las lineas grises.
    public List<NamedRoute> routes;

    public AirwayMap() {
        waypoints = new ArrayList<>();
        routes = new ArrayList<>();

        // >>> Al crear la clase, cargamos inmediatamente los datos en memoria
        cargarDatos();
    }

    private void cargarDatos() {
        // --- 1. DEFINICION DE PUNTOS (Latitud, Longitud) ---
        // Extraidos de Routes.m y convertidos a decimal

        // >>> AEROPUERTOS PRINCIPALES
        NamedPoint LEVC = new NamedPoint("LEVC", 39.4855, -0.4833); // Valencia
        NamedPoint LEBL = new NamedPoint("LEBL", 41.3072, 2.1080);  // Barcelona
        NamedPoint LEAL = new NamedPoint("LEAL", 38.2683, -0.5700); // Alicante
        NamedPoint LEPA = new NamedPoint("LEPA", 39.4352, 2.7580);  // Palma (MJV en matlab)
        NamedPoint LEIB = new NamedPoint("LEIB", 38.8683, 1.3658);  // Ibiza
        NamedPoint LECH = new NamedPoint("LECH", 40.2308, 0.1430);  // Castellon (CTN)

        // >>> OTROS AEROPUERTOS
        NamedPoint LEBT = new NamedPoint("LEBT", 39.6238, -0.4738); // Betera
        NamedPoint LERE = new NamedPoint("LERE", 39.4747, -1.0344); // Requena

        // >>> VORs y NDBs
        NamedPoint CJN  = new NamedPoint("CJN", 40.3719, -2.3947);  // Castejon
        NamedPoint CDP  = new NamedPoint("CDP", 39.6975, 3.4341);   // Capdepera
        NamedPoint YES  = new NamedPoint("YES", 38.3608, -2.3525);  // Yeste
        NamedPoint MLA  = new NamedPoint("MLA", 41.1297, 0.1655);   // Maella
        NamedPoint CMA  = new NamedPoint("CMA", 40.8672, -1.1980);  // Calamocha
        NamedPoint PDT  = new NamedPoint("PDT", 40.2527, -3.3477);  // PDT
        NamedPoint LRD  = new NamedPoint("LRD", 41.5530, 0.6480);   // Lerida (NDB)
        NamedPoint IZA  = new NamedPoint("IZA", 38.9155, 1.4702);   // Ibiza NDB

        // >>> FIXES (Puntos de notificacion)
        NamedPoint RAFOL = new NamedPoint("RAFOL", 37.9494, -0.0169);
        NamedPoint SOPET = new NamedPoint("SOPET", 39.8338, -0.0047);
        NamedPoint LABRO = new NamedPoint("LABRO", 37.2747, 1.1788);
        NamedPoint ABOSI = new NamedPoint("ABOSI", 39.7791, -1.1951);
        NamedPoint MABUX = new NamedPoint("MABUX", 39.5491, -1.1830);
        NamedPoint LASPO = new NamedPoint("LASPO", 39.2825, -0.5444);
        NamedPoint ASTRO = new NamedPoint("ASTRO", 39.0244, -1.1963);
        NamedPoint RESTU = new NamedPoint("RESTU", 37.9075, -1.2241);
        NamedPoint LOTOS = new NamedPoint("LOTOS", 40.5497, 1.1697);
        NamedPoint GERVU = new NamedPoint("GERVU", 38.8197, 0.4833);

        // >>> FIXES CERCANOS A VALENCIA
        NamedPoint MULAT = new NamedPoint("MULAT", 39.4000, -0.1800);
        NamedPoint OPERA = new NamedPoint("OPERA", 39.6227, -0.7788);
        NamedPoint ARGOR = new NamedPoint("ARGOR", 39.5386, 0.2986);
        NamedPoint NINOT = new NamedPoint("NINOT", 39.2088, 0.4833);
        NamedPoint CLS   = new NamedPoint("CLS",   39.7072, -0.9863);
        NamedPoint SAURA = new NamedPoint("SAURA", 40.2561, -0.1833);
        NamedPoint NARGO = new NamedPoint("NARGO", 38.7383, -0.9986);

        // >>> Guardamos todos los puntos en la lista publica 'waypoints'
        // >>> Asi RadarPanel puede hacer un bucle 'for' y pintarlos todos.
        waypoints.add(LEVC); waypoints.add(LEBL); waypoints.add(LEAL); waypoints.add(LEPA);
        waypoints.add(LEIB); waypoints.add(LECH); waypoints.add(LEBT); waypoints.add(LERE);
        waypoints.add(CJN); waypoints.add(CDP); waypoints.add(YES); waypoints.add(MLA);
        waypoints.add(CMA); waypoints.add(PDT); waypoints.add(LRD); waypoints.add(IZA);
        waypoints.add(RAFOL); waypoints.add(SOPET); waypoints.add(LABRO); waypoints.add(ABOSI);
        waypoints.add(MABUX); waypoints.add(LASPO); waypoints.add(ASTRO); waypoints.add(RESTU);
        waypoints.add(LOTOS); waypoints.add(GERVU);
        waypoints.add(MULAT); waypoints.add(OPERA); waypoints.add(ARGOR); waypoints.add(NINOT);
        waypoints.add(CLS); waypoints.add(SAURA); waypoints.add(NARGO);

        // --- 2. DEFINICION DE RUTAS (Conexiones) ---
        // >>> Aqui definimos las carreteras del cielo.

        // Ruta A33: CJN -> LEVC -> CDP
        routes.add(new NamedRoute("A33", CJN, LEVC));
        routes.add(new NamedRoute("A33", LEVC, CDP));

        // Ruta B28: LEBL -> LOTOS -> SOPET -> LEVC -> YES
        routes.add(new NamedRoute("B28", LEBL, LOTOS));
        routes.add(new NamedRoute("B28", LOTOS, SOPET));
        routes.add(new NamedRoute("B28", SOPET, LEVC));
        routes.add(new NamedRoute("B28", LEVC, YES));

        // Ruta N608: RAFOL -> SOPET -> LRD
        routes.add(new NamedRoute("N608", RAFOL, SOPET));
        routes.add(new NamedRoute("N608", SOPET, LRD));

        // Ruta A34: MLA -> LEVC -> LEAL
        routes.add(new NamedRoute("A34", MLA, LEVC));
        routes.add(new NamedRoute("A34", LEVC, LEAL));

        // Ruta R29: LEAL -> CMA
        routes.add(new NamedRoute("R29", LEAL, CMA));

        // Ruta L150: LABRO -> LEAL -> CJN
        routes.add(new NamedRoute("L150", LABRO, LEAL));
        routes.add(new NamedRoute("L150", LEAL, CJN));

        // Ruta M871: PDT -> ABOSI -> MABUX -> LASPO -> LEPA
        routes.add(new NamedRoute("M871", PDT, ABOSI));
        routes.add(new NamedRoute("M871", ABOSI, MABUX));
        routes.add(new NamedRoute("M871", MABUX, LASPO));
        routes.add(new NamedRoute("M871", LASPO, LEPA));

        // Ruta Z224: LASPO -> ASTRO -> LEIB
        routes.add(new NamedRoute("Z224", LASPO, ASTRO));
        routes.add(new NamedRoute("Z224", ASTRO, LEIB));

        // Ruta G850: LEVC -> RESTU
        routes.add(new NamedRoute("G850", LEVC, RESTU));

        // Ruta R59: LEAL -> GERVU -> CDP
        routes.add(new NamedRoute("R59", LEAL, GERVU));
        routes.add(new NamedRoute("R59", GERVU, CDP));

        // Ruta B46: LEIB -> LEAL -> RESTU
        routes.add(new NamedRoute("B46", LEIB, LEAL));
        routes.add(new NamedRoute("B46", LEAL, RESTU));

        // Ruta T412: LOTOS -> LECH
        routes.add(new NamedRoute("T412", LOTOS, LECH));

        // Ruta G30: IZA -> LEVC
        routes.add(new NamedRoute("G30", IZA, LEVC));
    }

    // >>> Clases internas (Helpers)
    // >>> Estructuras de datos simples para agrupar nombre y coordenadas.
    public class NamedPoint {

        String name;
        double lat, lon;

        public NamedPoint(String n, double la, double lo) {
            name = n;
            lat = la;
            lon = lo;
        }
    }

    public class NamedRoute {

        String name;
        NamedPoint p1, p2;

        // >>> Guardamos el punto de inicio y el de fin para poder tirar la linea entre ellos
        public NamedRoute(String n, NamedPoint a, NamedPoint b) {
            name = n;
            p1 = a;
            p2 = b;
        }
    }
}