package edu.rpi.phil.legup;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashSet;
import java.util.Map;
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
    public static Set<Point> translatePointSet(Set<Point> pointSet, int dx, int dy)
    {
        Set<Point> result = new LinkedHashSet<Point>();
        for(Point p : pointSet) { result.add(new Point(p.x+dx, p.y+dy)); }
        return result;
    }
    /*
    PointSetAlgorithms.getPositionsForPointSet is intended to be used for BattleShip (at least), but is written here in a generic way.
    To find the positions a 4x1 battleship can possibly be in, this would be called with
    (using the notation {KEY -> VALUE, ...} for maps, and (x,y) for points)
    {(-1,-1) -> O,(0,-1) -> O,(1,-1) -> O,(2,-1) -> O,(3,-1) -> O,(4,-1) -> O,
     (-1, 0) -> O,(0, 0) -> L,(1, 0) -> M,(2, 0) -> M,(3, 0) -> R,(4, 0) -> O,
     (-1, 1) -> O,(0, 1) -> O,(1, 1) -> O,(2, 1) -> O,(3, 1) -> O,(4, 1) -> O}
    where O, L, M, and R are the sets
    O = {POINT_OUTSIDE, CELL_UNKNOWN, CELL_WATER}
    L = {CELL_UNKNOWN, CELL_SHIP, CELL_LEFT_CAP}
    M = {CELL_UNKNOWN, CELL_SHIP, CELL_MIDDLE}
    R = {CELL_UNKNOWN, CELL_SHIP, CELL_RIGHT_CAP}
    (all the CELL_ constants are from the BattleShip PuzzleModule, and POINT_OUTSIDE is a constant on PointSetAlgorithms)

    The "masks" argument specifies what is allowed to be already present in the grid.
    The magic value POINT_OUTSIDE in a mask specifies that a point is allowed the be outside the grid.
    Generic unknown (PuzzleModule.CELL_UNKNOWN) is not assumed to be valid, and so should be included explicitly into the masks.
    The return value is a set of translations
    */
    public static final Integer POINT_OUTSIDE = -42;
    public static Set<Point> getPositionsForPointSet(int[][] grid, Map<Point, Set<Integer>> masks)
    {
        Set<Point> results = new LinkedHashSet<Point>();
        int height = grid.length;
        // assumes grid isn't actually a jagged array
        // TODO: consider a different internal representation for 
        //  the backing arrays, that would eliminate these sorts of 
        //   edge cases?
        int width = grid[0].length;
        for(int dy = 0; dy < height; dy++) {
            for(int dx = 0; dx < width; dx++) {
                if(gPFPS_validateTranslation(grid, masks, dx, dy)) {
                    results.add(new Point(dx, dy));
                }
            }
        }
        return results;
    }
    private static boolean gPFPS_validateTranslation(int[][] grid, Map<Point, Set<Integer>> masks, int dx, int dy)
    {
        Set<Point> pointSet = masks.keySet();
        //Set<Point> translated = translatePointSet(masks.keySet(), dx, dy);
        int height = grid.length;
        int width = grid[0].length;
        for(Point p : pointSet) {
            Point t = new Point(p.x+dx, p.y+dy);
            if((t.x < 0) || (t.x >= width) || (t.y < 0) || (t.y >= height)) {
                if(masks.get(p).contains(POINT_OUTSIDE)) { continue; }
                else { return false; }
            }
            if(!masks.get(p).contains(grid[t.y][t.x])) { return false; }
        }
        return true;
    }
    /*
    ; Test for getPositionsForPointSet, in Kawa
    (let ((PSA edu.rpi.phil.legup.PointSetAlgorithms) (B edu.rpi.phil.legup.puzzles.battleship.BattleShip))
        (define grid (int[][]
            (int[] B:CELL_UNKNOWN B:CELL_UNKNOWN B:CELL_UNKNOWN B:CELL_BOTTOM_CAP)
            (int[] B:CELL_UNKNOWN B:CELL_UNKNOWN B:CELL_UNKNOWN B:CELL_UNKNOWN)
            (int[] B:CELL_UNKNOWN B:CELL_SHIP B:CELL_UNKNOWN B:CELL_UNKNOWN)
            (int[] B:CELL_UNKNOWN B:CELL_WATER B:CELL_UNKNOWN B:CELL_UNKNOWN)
            (int[] B:CELL_UNKNOWN B:CELL_UNKNOWN B:CELL_SHIP B:CELL_UNKNOWN)
        ))
        (define mask4x1ship (java.util.LinkedHashMap))
        (define (make-set . items) (let ((set (java.util.LinkedHashSet))) (for-each set:add items) set))
        (define outs (make-set PSA:POINT_OUTSIDE B:CELL_UNKNOWN B:CELL_WATER))
        (define left (make-set B:CELL_UNKNOWN B:CELL_SHIP B:CELL_LEFT_CAP))
        (define midl (make-set B:CELL_UNKNOWN B:CELL_SHIP B:CELL_MIDDLE))
        (define rght (make-set B:CELL_UNKNOWN B:CELL_SHIP B:CELL_RIGHT_CAP))
        (for-each (lambda (x y set) (mask4x1ship:put (java.awt.Point x y) set))
      '(-1    0    1    2    3    4   -1    0    1    2    3    4   -1    0    1    2    3    4)
      '(-1   -1   -1   -1   -1   -1    0    0    0    0    0    0    1    1    1    1    1    1)
(list outs outs outs outs outs outs outs left midl midl rght outs outs outs outs outs outs outs)
        )
        (PSA:getPositionsForPointSet grid mask4x1ship)
    )
    */
}

/*
; Reflection based helper method (Kawa)
(define (info cls)
    (display "FIELDS: ")
    (display (java.lang.Class:getDeclaredFields cls)) (newline)
    (display "METHODS: ")
    (display (java.lang.Class:getDeclaredMethods cls)) (newline))
*/
