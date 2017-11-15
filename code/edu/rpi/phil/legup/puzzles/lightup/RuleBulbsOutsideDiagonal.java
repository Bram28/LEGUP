package edu.rpi.phil.legup.puzzles.lightup;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.newgui.LEGUP_Gui;
import edu.rpi.phil.legup.Contradiction;

public class RuleBulbsOutsideDiagonal extends PuzzleRule {
    static final long serialVersionUID = 9501L;
    public String getImageName() {
        return "images/lightup/rules/BulbsOutsideDiagonal.png";
    }
    RuleBulbsOutsideDiagonal() {
        setName("Bulbs Outside Diagonal");
        description = "Cells on the external edges of a 3 diagonal to a numerical block must be bulbs.";
    }

    protected String checkRuleRaw(BoardState destBoardState) {
        // Check for only one branch
        if (destBoardState.getParents().size() != 1) {
            return "This rule only involves having a single branch!";
        }

        // Add contradictions to check to set contras
        Set<Contradiction> contras = new LinkedHashSet<Contradiction>();
        contras.add(new ContradictionTooManyBulbs());

        // Copy the parent state to compare with current state to find changes
        BoardState origBoardState = destBoardState.getSingleParentState();
        int width = origBoardState.getWidth();
        int height = origBoardState.getHeight();

        // Check each tile for any changes,
        // make sure changes are allowed
        // check that doing the opposite case would lead to a contradiction (thus proving the rule)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Check for changes
                BoardState modified;
                if (destBoardState.getCellContents(x, y) != origBoardState.getCellContents(x, y)) {

                    // Create alternative board state to apply other case/contradiction
                    modified = origBoardState.copy();
                    modified.getBoardCells()[y][x] = LightUp.CELL_LIGHT;
                    for (Contradiction c : contras) {
                        if (c.checkContradictionRaw(modified) == null)
                            return "Too many bulbs placed.";
                    }
                    int cellvalue = destBoardState.getCellContents(x, y);
                    String err = "Only valid when cell requires 3 bulbs diagonal to a cell that requires 1 bulb and is on outer edges of cells.";
                    if (!(isStateValid(x-1,y,destBoardState,cellvalue) || isStateValid(x+1,y,destBoardState,cellvalue) ||
                            isStateValid(x,y-1,destBoardState,cellvalue) || isStateValid(x,y+1,destBoardState,cellvalue) )) {
                        return err;
                    }
                }
            }
        }
        return null;
    }
    private boolean isStateValid(int x, int y, BoardState origBoardState, int cellvalue) {
        if (x<0 || x>=origBoardState.getWidth() || y<0 || y>=origBoardState.getHeight()) return false;
        if (origBoardState.getCellContents(x,y) >= 10 &&
                ((origBoardState.getCellContents(x, y) == 13 && cellvalue == LightUp.CELL_LIGHT)
                || (origBoardState.getCellContents(x, y) == 11 && cellvalue == LightUp.CELL_EMPTY))) {
            return true && findDiagonal(x, y, origBoardState.getCellContents(x,y), origBoardState);
        }
        return false;
    }
    private boolean findDiagonal(int x, int y, int cellvalue, BoardState state) {
        int toFind = 0;
        if (cellvalue == 13) { toFind = 11; }
        if (cellvalue == 11) { toFind = 13; }
        if (state.getCellContents(x+1,y+1) == toFind || state.getCellContents(x-1,y-1) == toFind ||
                state.getCellContents(x-1,y+1) == toFind || state.getCellContents(x+1,y-1) == toFind) {
            return true;
        }
        return false;
    }
}
