package edu.rpi.phil.legup.puzzles.sudoku;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleForcedLocation extends PuzzleRule
{

	// cell index = 9*row+col
	private final int[][] cellToGroupRef;
	private final int[][] groupToCellRef;

	RuleForcedLocation()
    {
		name = "Forced by Elimination";
		description = "This is the only spot left for a number to go in this row, column, or square";
		image = new ImageIcon("images/sudoku/forcedByElimination.png");

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
							if (!checkForced(x, y, n, origBoardState))
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

    boolean checkForced(int nx, int ny, int val, BoardState origBoardState)
    {
		boolean[] possible = new boolean[81];
		for (int i = 0; i < 81; possible[i] = (origBoardState.getCellContents(i%9, i++/9) == Sudoku.CELL_UNKNOWN));

		for (int y = 0; y < 9; y++) for (int x = 0; x < 9; x++)
			if (origBoardState.getCellContents(x, y) == val)
				filter(y*9+x, possible);

		for (int group : cellToGroupRef[ny*9+nx])
			if (isSolitary(ny*9+nx, group, possible))
				return true;

		return false;
    }

    private void filter(int cellIndex, boolean[] elim)
    {
 		for (int group : cellToGroupRef[cellIndex])
    		for (int cell : groupToCellRef[group])
    			elim[cell] = false;
    }

    private boolean isSolitary(int cellIndex, int group, boolean[] elim)
    {
    	for (int cell : groupToCellRef[group])
    		if ((elim[cell] && cell != cellIndex) || (!elim[cell] && cell == cellIndex))
    			return false;
    	return true;
    }

	protected boolean doDefaultApplicationRaw(BoardState state)
	{
		boolean[][][] possMatrix = Sudoku.getPossMatrix(state);

		boolean updated = false;
		BoardState noUpdate = state.getSingleParentState().copy();
		for (int[] group : groupToCellRef)
			for (int i = 1; i <= 9; i++)
			{
				int numPoss = 0;
				for (int cell : group) if (possMatrix[cell%9][cell/9][i-1]) numPoss++;
				if (numPoss == 1)
				{
					updated = true;
					for (int cell : group) if (possMatrix[cell%9][cell/9][i-1]) { state.setCellContents(cell%9, cell/9, i); break; }
				}
			}

		if (!updated)
			state = noUpdate;

    	Legup.getInstance().getPuzzleModule().updateState(state);

		return updated;
	}

}