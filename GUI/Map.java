/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import GUI.Polygon;
import de.serosystems.lib1090.Position;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 *
 * @author fms
 */
public class Map {

    public Polygon[] data;
    private Color mapColor;//=Color.green;

    public Map(int size, Color c) {
        data = new Polygon[size];
        mapColor=c;
    }
    
    public Map(String file, Color c) throws FileNotFoundException, IOException {
        //data = new Polygon[size];
        SetMap(file);
        mapColor=c;
    }
    
    private void SetMap(String file) throws FileNotFoundException, IOException {
        String numPol, numPoint, coords, value[];
        int np;
        FileReader in = new FileReader(file);
        BufferedReader b = new BufferedReader(in);
        //BufferedReader b = new BufferedReader(new InputStreamReader(file.openStream()));
 
        numPol = b.readLine();
        data = new Polygon[Integer.parseInt(numPol)];
        for (int i = 0; i < Integer.parseInt(numPol); i++) {
            numPoint = b.readLine();
            np=Integer.parseInt(numPoint);
            ArrayList<Position> shape = new ArrayList(np);
            for (int j = 0; j < Integer.parseInt(numPoint); j++) {
                coords = b.readLine();
                coords = coords.trim();
                value = coords.split(",");
                Position pos = new Position(Double.valueOf(value[0]), Double.valueOf(value[1]),0.0);
                shape.add(pos);               
            }
                       
            Polygon polygon = new Polygon(np, mapColor);
            
            int rj=0;
            for (Position rpos : shape){
                Polygon.Point point = new Polygon.Point(rpos.getLongitude(), rpos.getLatitude());
                polygon.point[rj] = point;        
                rj++;
            }
            this.data[i] = polygon;
        }
    }
    
    public Polygon[] getMap() {
        return data;
    }

    public Color getColor() {
        return mapColor;
    }
}
