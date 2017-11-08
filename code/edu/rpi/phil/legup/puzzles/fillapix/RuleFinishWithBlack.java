package edu.rpi.phil.legup.puzzles.fillapix;

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
					//Make sure cells placed are black cells
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


    	/* OLD SYSTEM?
    	String error = null;
    	BoardState origBoardState = destBoardState.getSingleParentState();

		boolean anychange = false;
		// Check for only one branch
		if (destBoardState.getParents().size() != 1) {
			error = "This rule only involves having a single branch!";
		} else {
			int[][] forceMatrix = generateForceMatrix(origBoardState);
	    	outer: for (int y = 0; y < destBoardState.getHeight(); ++y)
	    	{
	    		for (int x = 0; x < destBoardState.getWidth(); ++x)
	    		{
	    			int o = origBoardState.getCellContents(x,y);
					int n = destBoardState.getCellContents(x,y);

					if (o != n)
					{
						anychange = true;
						if (o != Fillapix.CELL_UNKNOWN)
						{
							error = "You can not change pixels, only define them.";
							break outer;
						}
						else if (n != forceMatrix[y][x])
						{
							error = "The cell at (" + x + ", " + y + ") is not forced.";
							break outer;
						}
					}
	    		}
	    	}

			if (!anychange)
				error = "You must define a pixel to apply this rule!";
		}
		return error;
		*/
	}

	/*
	protected int[][] generateForceMatrix(BoardState state)
	{
		int w = state.getWidth(), h = state.getHeight();

		int[][] mat = new int[h][w];

		for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) mat[y][x] = state.getCellContents(x, y);
		for (ExtraCellNumber ECN : Fillapix.getECNs(state))
		{
			for (Point p : ECN.getForcedFills(state)) mat[p.y][p.x] = Fillapix.FILLED;
			for (Point p : ECN.getForcedWhite(state)) mat[p.y][p.x] = Fillapix.EMPTY;
		}

		return mat;
	}

	protected boolean doDefaultApplicationRaw(BoardState state)
	{
		boolean updated = false;
		BoardState noUpdate = state.getSingleParentState().copy();

		int[][] forceMatrix = generateForceMatrix(state);

		int w = state.getWidth(), h = state.getHeight();
		for (int y = 0; y < h; y++) for (int x = 0; x < w; x++)
			if (state.getCellContents(x, y) != forceMatrix[y][x])
			{
				updated = true;
				state.setCellContents(x, y, forceMatrix[y][x]);
			}

		if (!updated)
			state = noUpdate;

    	Legup.getInstance().getPuzzleModule().updateState(state);

		return updated;
	}
	*/
}