package edu.rpi.phil.legup.puzzles.heyawake;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionAdjacentBlacks extends Contradiction
{	 
    private static final long serialVersionUID = -23494650L;

	 ContradictionAdjacentBlacks()
	 {
		setName("Adjacent Black Cells");
		description = "No two blacks can be adjacent.";
		image = new ImageIcon("images/heyawake/contradictions/adjacentBlacks.png");
	 }
	 
	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    protected String checkContradictionRaw(BoardState state)
    {
    	String error = null;
    	int height = state.getHeight();
    	int width = state.getWidth();

    	// Check all black cells to see if they are adjacent to another black cell
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			if(state.getCellContents(x,y) == 2)
    			{
    				if(x+1 < width)
    				{
    					if(state.getCellContents(x+1,y) == 2)
    						return error;
    				}
    				if(y+1 < height)
    				{
    					if(state.getCellContents(x,y+1) == 2)
    						return error;
    				}
    			}
    		}
    	}
    	error = "No two black cells are adjacent to each other.";

		return error;
    }
}
