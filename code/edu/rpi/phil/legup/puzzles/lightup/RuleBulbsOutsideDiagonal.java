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
        return "images/lightup/rules/EmptyCornersLegacy.png";
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
                    //Make sure cells placed are empty cells
                    if (destBoardState.getCellContents(x, y) == LightUp.CELL_EMPTY) {
                        return "Only bulbs are allowed for this rule!";
                    }

                    // Create alternative board state to apply other case/contradiction
                    modified = origBoardState.copy();
                    modified.getBoardCells()[y][x] = LightUp.CELL_LIGHT;
                    for (Contradiction c : contras) {
                        if (c.checkContradictionRaw(modified) == null)
                            return "Too many bulbs placed.";
                    }
                    int cellvalue = origBoardState.getCellContents(x, y);
                    String err = "Only valid when cell requires 3 bulbs and is on outer edges of cell.";
                    if (origBoardState.getCellContents(x-1, y) >= 10 && origBoardState.getCellContents(x-1, y) < 15 && origBoardState.getCellContents(x-1, y) != 13) {
                        return err;
                    }
                    if (origBoardState.getCellContents(x+1, y) >= 10 && origBoardState.getCellContents(x+1, y) < 15 && origBoardState.getCellContents(x+1, y) != 13) {
                        return err;
                    }
                    if (origBoardState.getCellContents(x, y-1) >= 10 && origBoardState.getCellContents(x, y-1) < 15 && origBoardState.getCellContents(x, y-1) != 13) {
                        return err;
                    }
                    if (origBoardState.getCellContents(x, y+1) >= 10 && origBoardState.getCellContents(x, y+1) < 15 && origBoardState.getCellContents(x, y+1) != 13) {
                        return err;
                    }
                }

            }
        }
        return null;
    }
}
