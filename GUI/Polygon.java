/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import java.awt.Color;

/**
 *
 * @author jvila
 */
public class Polygon {

    public Point[] point;
    public Color color=Color.black;

    public Polygon(int size, Color color) {
        this.point = new Point[size];
        this.color = color;
    }

    public static class Point {

        public double longitude;
        public double latitude;

        public Point(double lat, double lon) {
            latitude = lat;
            longitude = lon;
        }

    }
}
