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
    /*
    PointSetAlgorithms.getPositionsForPointSet is intended to be used for BattleShip (at least), but is written here in a generic way.
    To find the positions a 4x1 battleship can possibly be in, this would be called with
    {(0,0), (1,0), (2,0), (3,0)} as the "inner" argument (the coordinates of the shape of the ship, at the origin), and
    {(-1,-1),(0,-1),(1,-1),(2,-1),(3,-1),(4,-1),
     (-1, 0),                            (4, 0),
     (-1, 1),(0, 1),(1, 1),(2, 1),(3, 1),(4, 1)} as the "outer" argument (the coordinates of what needs to be "water", since ships can't be adjacent).
    innerMask and outerMask specify what is allowed to be already present in the grid.
    Generic unknown (PuzzleModule.CELL_UNKNOWN) is not assumed to be valid, and so should be included explicitly into the masks.
    In the case of BattleShip, innerMask should be all the different types of ship segments (and generic unknown). outerMask should be just water and generic unknown.
    outer will be allowed to be outside the grid (i.e. with the above shapes, (0, 0) may be part of the return set)
    
    The return value is a set of translations to be applied to "inner" and "outer"
    */
    public static Set<Point> getPositionsForPointSet(int[][] grid,
                                                    Set<Point> inner, Set<Point> outer,
                                                    Set<Integer> innerMask, Set<Integer> outerMask)
    {
        // TODO: implement once description is approved
        return null;
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
