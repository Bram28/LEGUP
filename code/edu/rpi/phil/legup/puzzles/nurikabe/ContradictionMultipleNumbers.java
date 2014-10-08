package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionMultipleNumbers extends Contradiction
{	 
    private static final long serialVersionUID = 326902345L;
	
	 ContradictionMultipleNumbers()
	 {
		setName("Multiple Numbers");
		description = "All white regions cannot have more than one number.";
		image = new ImageIcon("images/nurikabe/contradictions/MultipleNumbers.png");
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

//    	false = not checked, true = checked
    	boolean[][] neighbors = new boolean[height][width];
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			if(state.getCellContents(x,y) == Nurikabe.CELL_BLACK || state.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN)
    			{
    				neighbors[y][x] = true;
    			}
    		}
    	}
    	
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			if(!neighbors[y][x])
    			{
    				if(loopConnected(neighbors, state,x,y,width,height) > 1)
    					return null;
    			}
    		}
    	}
    	
    	error = "No regions with multiple numbers.";

		return error;
    }
    
    private int loopConnected(boolean[][] neighbors,BoardState boardState, int x, int y, int width, int height)
    {
    	int numcount = 0;
    	if(boardState.getCellContents(x,y) > 10)
    		++numcount;
    	neighbors[y][x] = true;
    	if(x+1 < width)
    	{
    		if(!neighbors[y][x+1])
    			numcount += loopConnected(neighbors, boardState, x+1, y, width, height);
    	}
    	if(x-1 >= 0)
    	{
    		if(!neighbors[y][x-1])
    			numcount += loopConnected(neighbors, boardState, x-1, y, width, height);
    	}
    	if(y+1 < height)
    	{
    		if(!neighbors[y+1][x])
    			numcount += loopConnected(neighbors, boardState, x, y+1, width, height);
    	}
    	if(y-1 >= 0)
    	{
    		if(!neighbors[y-1][x])
    			numcount += loopConnected(neighbors, boardState, x, y-1, width, height);
    	}
    	return numcount;
    }
}
