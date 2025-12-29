/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import de.serosystems.lib1090.Position;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import javax.swing.JPanel;

/**
 *
 * @author jvila
 */
public class PolygonArrayPlot extends JPanel {
    private RadarPanelInt rp;
    private Dimension preferredSize;
    private Polygon[] poly =null;
    Color color=Color.white;
    
    public PolygonArrayPlot(Polygon[] poly, Color color, RadarPanelInt rp) {
     this.rp = rp;
     this.poly=poly;
     this.color=color;
     
     this.setBackground(Color.black);
     this.setOpaque(false);
     this.setPreferredSize(rp.getSize());
     this.setBounds(rp.getBounds());
     this.setVisible(true);
    }
    
    @Override
    public void paint(Graphics g) {
        paintComponent(g);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        this.setPreferredSize(rp.getSize());
        this.setBounds(rp.getBounds());

        Position p;
        PositionGraphic pnorm;
        Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO);
        for (int i = 0; i < poly.length; i++) { 

            p= new Position(poly[i].point[0].longitude, poly[i].point[0].latitude,0.0);
            //pnorm = new PositionGraphic(p, rp.getArea(), rp.getWidth(), rp.getHeight());
            pnorm = new PositionGraphic(p, rp);
                     
            path.moveTo(pnorm.getX(), pnorm.getY());
            for (int j = 1; j < poly[i].point.length; j++) {
                p= new Position(poly[i].point[j].longitude, poly[i].point[j].latitude,0.0);
                //pnorm = new PositionGraphic(p, rp.getArea(), rp.getWidth(), rp.getHeight());
                pnorm = new PositionGraphic(p, rp);
                path.lineTo(pnorm.getX(), pnorm.getY());
            }
        }
        path.closePath();
        g2.setColor(color);
        g2.draw(path);
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize); //To change body of generated methods, choose Tools | Templates.
        this.preferredSize=preferredSize;
        //super.setPreferredSize(preferredSize); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dimension getPreferredSize() {
        //return super.getPreferredSize(); //To change body of generated methods, choose Tools | Templates.
        return this.preferredSize;
    }   
}
