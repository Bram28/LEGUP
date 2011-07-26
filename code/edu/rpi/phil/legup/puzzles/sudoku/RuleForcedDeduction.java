package edu.rpi.phil.legup.puzzles.sudoku;

import javax.swing.ImageIcon;
import java.awt.Point;
import java.util.LinkedList;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleRule;

/**
 *	RuleForcedDeduction applies when there is a specific cell for which all but one possible answers can
 *	be eliminated by the rules of Sudoku
 *	@author: Daniel Ploch
 */
public class RuleForcedDeduction extends PuzzleRule
{

	// cell index = 9*row+col
	private final int[][] cellToGroupRef;
	private final int[][] groupToCellRef;

	RuleForcedDeduction()
    {
		setName("Forced by Deduction");
		description = "This is the only the number left that can validly fit in the row, column, and square";
		image = new ImageIcon("images/sudoku/forcedByDeduction.png");

		groupToCellRef = Sudoku.getGroups();
		cellToGroupRef = Sudoku.getCrossReference();
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
	    	for (int y = 0; y < 9 ;++y)
	    	{
	    		for (int x = 0; x < 9 ;++x)
	    		{
	    			int o = origBoardState.getCellContents(x,y);
					int n = destBoardState.getCellContents(x,y);

					if (o != n)
					{
						anychange = true;
						if (o != Sudoku.CELL_UNKNOWN)
						{
							error = "You can not change numbers, only insert them.";
						}
						else
						{
							if (!checkDeduced(x, y, n, origBoardState))
							{
								error = "The number "+n+" at ("+x+", "+y+") is not forced";
							}
						}
					}
	    		}
	    	}

			if (!anychange)
				error = "You must insert a number to apply this rule!";
		}

		return error;
	}

	boolean checkDeduced(int x, int y, int n, BoardState origBoardState)
	{
		int index = 9*y+x;

		LinkedList<Integer> list = new LinkedList<Integer>();
		for (int i = 0; i < 9; i++) if (i+1 != n) list.add(new Integer(i+1));

		for (int group : cellToGroupRef[index])
			for (int cell : groupToCellRef[group])
				if (origBoardState.getCellContents(cell%9, cell/9) != Sudoku.CELL_UNKNOWN)
					list.remove(new Integer(origBoardState.getCellContents(cell%9, cell/9)));

		return (list.size() == 0);
	}

	protected boolean doDefaultApplicationRaw(BoardState state)
	{
		boolean[][][] possMatrix = Sudoku.getPossMatrix(state);

		boolean updated = false;
		BoardState noUpdate = state.getSingleParentState().copy();
		for (int x = 0; x < 9; x++) for (int y = 0; y < 9; y++)
		{
			int numSoln = 9;
			for (boolean poss : possMatrix[x][y]) if (!poss) numSoln--;
			if (numSoln == 1)
			{
				updated = true;
				for (int i = 0; i < 9; i++) if (possMatrix[x][y][i]) state.setCellContents(x, y, i+1);
			}
		}

		if (!updated)
			state = noUpdate;

    	Legup.getInstance().getPuzzleModule().updateState(state);

		return updated;
	}

}