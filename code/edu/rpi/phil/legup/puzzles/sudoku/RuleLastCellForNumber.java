package edu.rpi.phil.legup.puzzles.sudoku;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.puzzles.sudoku.Sudoku;

public class RuleLastCellForNumber extends PuzzleRule
{

	// cell index = 9*row+col
	private final int[][] cellToGroupRef;
	private final int[][] groupToCellRef;
	private static final long serialVersionUID = 206709517L;

	RuleLastCellForNumber()
    {
		setName("Last Cell for Number");
		description = "This is the only cell open in its group for some number.";
		image = new ImageIcon("images/sudoku/forcedByElimination.png");

		groupToCellRef = Sudoku.getGroups();
		cellToGroupRef = Sudoku.getCrossReference();
    }

	public String getImageName()
	{
		return "images/sudoku/forcedByElimination.png";
	}

	protected String checkRuleViaCase(BoardState destBoardState) {
		String error = null;
		BoardState origBoardState = destBoardState.getSingleParentState();

		boolean anychange = false;
//    	 Check for only one branch
		if (destBoardState.getParents().size() != 1)
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

		Sudoku.setAnnotations(destBoardState);

		return error;
	}

	protected String checkRuleRaw(BoardState destBoardState)
    {
    	String error = null;
    	BoardState origBoardState = destBoardState.getSingleParentState();

		boolean anychange = false;
//    	 Check for only one branch
		if (destBoardState.getParents().size() != 1)
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

		Sudoku.setAnnotations(destBoardState);

		return error;
	}

	 /**
	  * Check if the number is forced to that location.
	  * @param nx Cell x
	  * @param ny Cell y
	  * @param val The number to add
	  * @param origBoardState The original board state
	  * @return
	  */
    boolean checkForced(int nx, int ny, int val, BoardState origBoardState)
    {
    	// this is exactly one case of possible cells for number
		// therefore use CasePossibleCellsForNumber and make sure it returns a number of 1


		//Determine which cells were empty
		boolean[] possible = new boolean[81];
		for (int i = 0; i < 81; possible[i] = (origBoardState.getCellContents(i%9, i++/9) == Sudoku.CELL_UNKNOWN));

		//Prevent all cells in the same row, same column, or same box as the current cell from having the same value
		for (int y = 0; y < 9; y++) for (int x = 0; x < 9; x++)
			if (origBoardState.getCellContents(x, y) == val)
				filter(y*9+x, possible);

		//Make sure the value is valid in the specified location
		for (int group : cellToGroupRef[ny*9+nx])
			if (isSolitary(ny*9+nx, group, possible))
				return true;

		return false;
    }

    /**
     * Eliminate all cells in the same row, same column, or same box as the current cell that have the same value
     * @param cellIndex Current cell
     * @param elim Array that indicates which cells can have the same value
     */
    private void filter(int cellIndex, boolean[] elim)
    {
    	//Iterate through all three groups: row, column, box.
    	//Iterate through all the cells within those groups.
 		for (int group : cellToGroupRef[cellIndex])
    		for (int cell : groupToCellRef[group])
    		{
    			elim[cell] = false;
    		}
    }

    /**
     * Make sure that this value is the only one that can be placed in this location
     * @param cellIndex Cell to place value in
     * @param group Group index for row, column, box
     * @param elim Array that indicates which cells can have the same value
     * @return
     */
    private boolean isSolitary(int cellIndex, int group, boolean[] elim)
    {
    	//Return false if there is another location where the value can be placed
    	//OR
    	//The value cannot be placed in the current cell because another
    	//cell in the same row, column, or box already has that value
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
