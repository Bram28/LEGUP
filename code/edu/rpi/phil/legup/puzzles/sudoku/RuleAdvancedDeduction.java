package edu.rpi.phil.legup.puzzles.sudoku;

import javax.swing.ImageIcon;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.puzzles.sudoku.Sudoku;

/**
 *	RuleAdvancedDeduction uses group logic to make eliminations, i.e., if there are only
 *	two squares in a box where a 1 or a 2 could go, then those two squares cannot contain
 *	any other values.  The deductions coming from this logic may lead to more vindictive
 *	cases.
 *	@author: Daniel Ploch
 */
public class RuleAdvancedDeduction extends PuzzleRule
{
	private static final long serialVersionUID = 423983627L;

	// cell index = 9*row+col
	private final int[][] cellToGroupRef;
	private final int[][] groupToCellRef;

	RuleAdvancedDeduction()
    {
		setName("Proved by Advanced Deduction");
		description = "Use of group logic deduces more answers by means of Forced by Location and Forced by Deduction";
		image = new ImageIcon("images/sudoku/AdvancedDeduction.png");

		groupToCellRef = Sudoku.getGroups();
		cellToGroupRef = Sudoku.getCrossReference();
    }
	
	public String getImageName()
	{
		return "images/sudoku/AdvancedDeduction.png";
	}

	protected String checkRuleRaw(BoardState destBoardState)
    {
    	BoardState orig = destBoardState.getSingleParentState();
    	if (orig == null)
    		return "This rule only applies to single parenting!";

		BoardState copy = orig.copy();
		getDefApplication(copy);

		boolean foundChange = false;
		for (int x = 0; x < 9; x++) for (int y = 0; y < 9; y++)
		{
			int o = orig.getCellContents(x, y), d = destBoardState.getCellContents(x, y),
				 a = copy.getCellContents(x, y);

			if (o != d)
			{
				if (o != Sudoku.CELL_UNKNOWN)
					return "This rule only applies to inserting new numbers, not changing them";
				else if (d == Sudoku.CELL_UNKNOWN)
					return "This rule only applies to inserting new numbers, not deleting old ones";
				else if (d == a)
					foundChange = true;
				else
					return "The number " + d + " at (" + x + ", " + y + ") is not deducible";
			}
		}

		if (!foundChange) return "You must do something to apply this rule!";
		else return null;
	}

	private void getDefApplication(BoardState state)
	{
		boolean[][][] possMatrix = Sudoku.getPossMatrix(state);

		boolean advanced;
		do
		{
			advanced = false;
			for (int g = 0; g < 27; g++)
			{
				int[] group = groupToCellRef[g];
				int[] possCount = {0,0,0,0,0,0,0,0,0};  // possCount[i] = # of cells that could hold (i+1)
				int[] groupThink = {0,0,0,0,0,0,0,0,0}; // groupThink[i] = # of numbers that can be held in exactly (i+1) places
																    // keycase: groupThink[i] = i+1, 2 <= i+1 <= 8

				// Step 1: See how many cells each value can occur in, while the groupThink
				// array keeps track of how many numbers have a certain number of possible locations
				int max = 9;
				for (int cell : group)
				{
					if (state.getCellContents(cell%9, cell/9) != Sudoku.CELL_UNKNOWN) max--;
					else for (int i = 0; i < 9; i++) if (possMatrix[cell%9][cell/9][i])
					{
						groupThink[possCount[i]++]++;
						if (possCount[i]-2 >= 0)
							groupThink[possCount[i]-2]--;
					}
				}

				// Check for possible groups
				HashSet<Integer> cellSet = new HashSet<Integer>();
				HashSet<Integer> valSet = new HashSet<Integer>();
				outer: for (int i = 2; i < max; i++)
					if (groupThink[i-1] == i)
					{
					 	for (int j = 1; j <= 9; j++) if (possCount[j-1] == i)
						{
							valSet.add(new Integer(j));
							for (int cell : group) if (possMatrix[cell%9][cell/9][j-1]) cellSet.add(new Integer(cell));
						}
						if (cellSet.size() > i)
						{
							cellSet.clear();
							valSet.clear();
							continue outer;
						}
						// Group found - eliminate possible values not in group
						for (Integer cellVal : cellSet)
						{
							int cell = cellVal.intValue();
							if (state.getCellContents(cell%9, cell/9) == Sudoku.CELL_UNKNOWN)
								for (int k = 0; k < 9; k++) if (!valSet.contains(new Integer(k+1)) && possMatrix[cell%9][cell/9][k])
								{
									advanced = true;
									possMatrix[cell%9][cell/9][k] = false;
								}
						}
					}
			}
		}
		while (advanced);

		for (int x = 0; x < 9; x++) for (int y = 0; y < 9; y++)
		{
			int numSoln = 9;
			for (boolean poss : possMatrix[x][y]) if (!poss) numSoln--;
			if (numSoln == 1)	for (int i = 0; i < 9; i++) if (possMatrix[x][y][i]) state.setCellContents(x, y, i+1);
		}
		for (int[] group : groupToCellRef)
			for (int i = 1; i <= 9; i++)
			{
				int numPoss = 0;
				for (int cell : group) if (possMatrix[cell%9][cell/9][i-1]) numPoss++;
				if (numPoss == 1) for (int cell : group) if (possMatrix[cell%9][cell/9][i-1]) { state.setCellContents(cell%9, cell/9, i); break; }
			}
	}

	protected boolean doDefaultApplicationRaw(BoardState state)
	{
		BoardState noUpdate = state.getSingleParentState().copy();
		getDefApplication(state);
		ArrayList<Point> list = BoardState.getDifferenceLocations(noUpdate, state);
		if (list.size() == 0) state = noUpdate;
    	Legup.getInstance().getPuzzleModule().updateState(state);
		return !(list.size() == 0);
	}

}