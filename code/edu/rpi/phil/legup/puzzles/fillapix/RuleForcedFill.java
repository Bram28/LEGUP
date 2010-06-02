package edu.rpi.phil.legup.puzzles.fillapix;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleRule;

import java.awt.Point;
import javax.swing.ImageIcon;

/**
 *	Author: Daniel Ploch
 *	Rule applies to Fill-a-pix Cells that are forced to be black or white, based on numbers.
 */
public class RuleForcedFill extends PuzzleRule
{

	public RuleForcedFill()
	{
		name = "Forced Black or White";
		description = "Numbers in the grid force square(s) to be black or white";
		image = new ImageIcon("images/fillapix/ruleforcedfill.png");
	}

	 protected String checkRuleRaw(BoardState destBoardState)
    {
    	String error = null;
    	BoardState origBoardState = destBoardState.getSingleParentState();

		boolean anychange = false;
//    	 Check for only one branch
		if (destBoardState.getTransitionsTo().size() != 1)
		{
			error = "This rule only involves having a single branch!";
		}
		else
		{
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
	}

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

}