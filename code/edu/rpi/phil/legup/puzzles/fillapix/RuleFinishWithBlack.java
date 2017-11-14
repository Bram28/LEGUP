package edu.rpi.phil.legup.puzzles.fillapix;

import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleRule;

import java.awt.Point;

import javax.swing.ImageIcon;

/**
 *	Author: Daniel Ploch
 *	Rule applies to Fill-a-pix Cells that are forced to be black or white, based on numbers.
 */
public class RuleFinishWithBlack extends PuzzleRule
{
	private static final long serialVersionUID = 730983709L;
    public RuleFinishWithBlack() {
		setName("Finish with Black");
		description = "The remaining unknowns around a block must be black to satisfy the number.";
		image = new ImageIcon("images/fillapix/rules/FinishWithBlack.png");
	}

	public String getImageName() {
		return "images/fillapix/rules/FinishWithBlack.png";
	}

	protected String checkRuleRaw(BoardState destBoardState)
    {
		// Check for only one branch
		if (destBoardState.getParents().size() != 1) {
			return "This rule only involves having a single branch!";
		}

		// The only contradiction being used
		ContradictionTooFewBlackCells c = new ContradictionTooFewBlackCells();

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
				if (destBoardState.getCellContents(x, y) != origBoardState.getCellContents(x, y)) {
					// Make sure cells placed are black cells
					if (!Fillapix.isBlack(destBoardState.getCellContents(x, y))) {
						return "Only black cells are allowed for this rule!";
					}

					// Create alternative boardstate to apply other case/contradiction
					BoardState modified = origBoardState.copy();
					if (Fillapix.isUnknown(modified.getBoardCells()[y][x])) {
						modified.getBoardCells()[y][x]+=(Fillapix.CELL_BLACK+Fillapix.CELL_WHITE);
					} else if (Fillapix.isBlack(modified.getBoardCells()[y][x])) {
						modified.getBoardCells()[y][x]+=Fillapix.CELL_WHITE;
					} // else the cell is already white!

					if (c.checkContradictionRaw(modified) != null) {
						return "It is not required for the modified cell(s) to be Black!";
					}
				}
			}
		}
		return null;
	}
}