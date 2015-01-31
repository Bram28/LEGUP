package edu.rpi.phil.legup.puzzles.fillapix;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleRule;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

/**
 *	Author: Daniel Ploch
 *	Rule applies to Fill-a-pix Cells that are forced to be black or white, based on numbers.
 */
public class RuleSharedCells extends PuzzleRule
{
	private static final long serialVersionUID = 525623582L;
	
    public RuleSharedCells()
	{
		setName("Deduce shared cells");
		description = "Two neighboring numbers dictate the state of their shared adjacency cells";
		image = new ImageIcon("images/fillapix/rulesharedcells.png");
	}

	 public String getImageName()
	{
		return "images/fillapix/rulesharedcells.png";
	}

	protected String checkRuleRaw(BoardState destBoardState)
    {
    	String error = null;
    	BoardState origBoardState = destBoardState.getSingleParentState();

		boolean anychange = false;
		// Check for only one branch
		if (destBoardState.getParents().size() != 1)
		{
			error = "This rule only involves having a single branch!";
		}
		else
		{
			int[][] deductionMatrix = generateDeductionMatrix(origBoardState);
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
						else if (n != deductionMatrix[y][x])
						{
							error = "The cell at (" + x + ", " + y + ") cannot be deduced.";
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

	protected int[][] generateDeductionMatrix(BoardState state)
	{
		int w = state.getWidth(), h = state.getHeight();

		int[][] mat = new int[h][w];

		for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) mat[y][x] = state.getCellContents(x, y);
		ArrayList<ExtraCellNumber> list = Fillapix.getECNs(state);
		for (ExtraCellNumber ECN1 : list) for (ExtraCellNumber ECN2 : list) if (ECN2 != ECN1)
		{
			ArrayList<Point> forcedBlack = ExtraCellNumber.adjacencyTestBlack(ECN1, ECN2, state);
			ArrayList<Point> forcedWhite = ExtraCellNumber.adjacencyTestWhite(ECN1, ECN2, state);

			for (Point point : forcedBlack) mat[point.y][point.x] = Fillapix.FILLED;
			for (Point point : forcedWhite) mat[point.y][point.x] = Fillapix.EMPTY;
		}

		return mat;
	}

	protected boolean doDefaultApplicationRaw(BoardState state)
	{
		boolean updated = false;
		BoardState noUpdate = state.getSingleParentState().copy();

		int[][] forceMatrix = generateDeductionMatrix(state);

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