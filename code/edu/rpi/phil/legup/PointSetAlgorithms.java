package edu.rpi.phil.legup;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Set;

public class PointSetAlgorithms
{
    public static Rectangle getBoundingRectangle(Set<Point> pointSet)
    {
        int minX, minY, maxX, maxY;
        // initialize to opposite extrema, so that encountered values are set by elementwise min/max
        minX = minY = Integer.MAX_VALUE;
        maxX = maxY = Integer.MIN_VALUE;
        for(Point p : pointSet)
        {
            if(p.x < minX) { minX = p.x; }
            if(p.x > maxX) { maxX = p.x; }
            if(p.y < minY) { minY = p.y; }
            if(p.y > maxY) { maxY = p.y; }
        }
        // AWT Rectangles are point-size (not point-point)
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }
}

/*
; Reflection based helper method (Kawa)
(define (info cls)
    (display "FIELDS: ")
    (display (java.lang.Class:getDeclaredFields cls)) (newline)
    (display "METHODS: ")
    (display (java.lang.Class:getDeclaredMethods cls)) (newline))
*/
