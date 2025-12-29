/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import de.serosystems.lib1090.Position;

import java.awt.geom.Rectangle2D;

/**
 *
 * @author jvila
 */
public class PositionGraphic {
    private RadarPanelInt rp;
    private int x;
    private int y;

    
    public PositionGraphic(Position p, Rectangle2D rp_area, int rp_width, int rp_height) {
        this.x=((int) (rp_width * (p.getLongitude() - rp_area.getX()) / rp_area.getWidth()));
        this.y=((int) (rp_height * (rp_area.getY() - p.getLatitude()) / rp_area.getHeight()));    
    }
    
    public PositionGraphic(Position p, RadarPanelInt rp) {
        this.rp=rp;
        this.x=((int) (rp.getWidth() * (p.getLongitude() - rp.getArea().getX()) / rp.getArea().getWidth()));
        this.y=((int) (rp.getHeight() * (rp.getArea().getY() - p.getLatitude()) / rp.getArea().getHeight()));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public boolean outOfBounds(){
        if ( (x-5)<0 || x>rp.getWidth() || (y-30)<0 || y> rp.getHeight() )
            return true;
        else
            return false;
    }
}
