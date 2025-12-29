/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package GUI;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

/**
 *
 * @author jvila
 */
public interface RadarPanelInt {
    public Rectangle2D getArea();
    public int getWidth();
    public int getHeight();

    public Dimension getSize();

    public Rectangle getBounds();
}
