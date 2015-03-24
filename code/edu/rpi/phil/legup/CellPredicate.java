package edu.rpi.phil.legup;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class CellPredicate
{
    public abstract boolean check(BoardState s, int x, int y);

    public static boolean inBounds(BoardState s, int x, int y, boolean includeEdges) {
        int w = s.getWidth(); int h = s.getHeight();
        if(includeEdges) { return !((x < -1)||(x > w)||(y < -1)||(y > h)); }
        else { return !((x <= -1)||(x >= w)||(y <= -1)||(y >= h)); }
    }
    public static boolean isCorner(BoardState s, int x, int y) {
        int w = s.getWidth(); int h = s.getHeight();
        return ((x == -1) && (y == -1)) ||
               ((x ==  w) && (y == -1)) ||
               ((x == -1) && (y ==  h)) ||
               ((x ==  w) && (y ==  h));
    }
    public static CellPredicate modifiableCell() {
        return new CellPredicate() { @Override public boolean check(BoardState s, int x, int y) {
            return inBounds(s, x, y, false) && s.isModifiableCell(x, y);
        }};
    }
    public static CellPredicate edge() {
        return new CellPredicate() { @Override public boolean check(BoardState s, int x, int y) {
            return inBounds(s, x, y, true) && !inBounds(s, x, y, false) && !isCorner(s, x, y);
        }};
    }
    public static CellPredicate typeWhitelist(final Integer... whitelist) {
        return typeWhitelist(new LinkedHashSet<Integer>(Arrays.asList(whitelist)));
    }
    public static CellPredicate typeWhitelist(final Set<Integer> whitelist) {
        return new CellPredicate() { @Override public boolean check(BoardState s, int x, int y) {
            return inBounds(s, x, y, false) && whitelist.contains(s.getCellContents(x, y));
        }};
    }
    public static CellPredicate constFalse() {
        return new CellPredicate() { @Override public boolean check(BoardState s, int x, int y) {
            return false;
        }};
    }

    public static CellPredicate sameRowOrColumn(final Point p) {
        return new CellPredicate() {
            @Override public boolean check(BoardState s, int x, int y) {
                if((p.x == -1) && (p.y == -1)) { return false; }
                if(p.x == -1) { return p.y == y; }
                if(p.y == -1) { return p.x == x; }
                return false;
            }
        };
    }

    public static CellPredicate subGrid(final Point p) {
        return new CellPredicate() {
            @Override public boolean check(BoardState s, int x, int y) {
                if(p.x > -1 && p.y > -1) {
                    // x = x/3 * 3;
                    // y = y/3 * 3;
                    p.x = p.x/3 * 3;
                    p.y = p.y/3 * 3;
                    return (x >= p.x && x < p.x+3) && (y >= p.y && y < p.y+3);
                }
                return false;
            }
        };
    }

    public static CellPredicate union(final CellPredicate... preds) {
        return new CellPredicate() { @Override public boolean check(BoardState s, int x, int y) {
            boolean result = false;
            for(CellPredicate p : preds) { result |= p.check(s, x, y); }
            return result;
        }};
    }
    public static CellPredicate intersection(final CellPredicate... preds) {
        return new CellPredicate() { @Override public boolean check(BoardState s, int x, int y) {
            boolean result = true;
            for(CellPredicate p : preds) { result &= p.check(s, x, y); }
            return result;
        }};
    }
    public static CellPredicate negate(final CellPredicate p) {
        return new CellPredicate() { @Override public boolean check(BoardState s, int x, int y) {
            return !p.check(s, x, y);
        }};
    }
}
