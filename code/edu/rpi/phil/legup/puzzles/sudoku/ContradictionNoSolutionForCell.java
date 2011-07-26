package edu.rpi.phil.legup.puzzles.sudoku;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionNoSolutionForCell extends Contradiction
{

	 ContradictionNoSolutionForCell()
	 {
		setName("No Solution For Cell");
		description = "Process of elimination yields no valid numbers for an empty cell";
		image = new ImageIcon("images/sudoku/NoSolution.png");
	 }

	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    protected String checkContradictionRaw(BoardState state)
    {
    	boolean[][][] possMatrix = Sudoku.getPossMatrix(state);

    	for (int x = 0; x < 9; x++)
    		outer: for (int y = 0; y < 9; y++)
    			if (state.getCellContents(x,y) == Sudoku.CELL_UNKNOWN)
    		{
    			for (boolean bool : possMatrix[x][y])
    				if (bool) continue outer;
    			return null;
    		}

    	return "No such contradiction exists on this board";
    }
}
