package edu.rpi.phil.legup.puzzles.heyawake;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionWhiteArea extends Contradiction
{	 
    private static final long serialVersionUID = 173001424L;

	 ContradictionWhiteArea()
	 {
		setName("White Area Connected");
		description = "All whites must be connected together.";
		image = new ImageIcon("images/heyawake/contradictions/WhiteArea.png");
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
    	int startx = -1;
    	int starty = -1;
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			neighbors[y][x] = false;
    			if(state.getCellContents(x,y) != 2 && startx == -1)
    			{
    				startx = x;
    				starty = y;
    			}
    			else if(state.getCellContents(x,y) == 2)
    			{
    				neighbors[y][x] = true;
    			}
    		}
    	}
    	if(startx > -1)
    	{
	    	loopConnected(neighbors, state, startx, starty, width, height);
	    	for(int x = 0; x < width; ++x)
	    	{
	    		for(int y = 0; y < height; ++y)
	    		{
	    			if(!neighbors[y][x])
	    				return error;
	    		}
	    	}
    	}
    	
    	error = "White cells are not isolated.";

		return error;
    }
    
    private boolean[][] loopConnected(boolean[][] neighbors,BoardState boardState, int x, int y, int width, int height)
    {
    	neighbors[y][x] = true;
    	if(x+1 < width)
    	{
    		if(!neighbors[y][x+1])
    			neighbors = loopConnected(neighbors, boardState, x+1, y, width, height);
    	}
    	if(x-1 >= 0)
    	{
    		if(!neighbors[y][x-1])
    			neighbors = loopConnected(neighbors, boardState, x-1, y, width, height);
    	}
    	if(y+1 < height)
    	{
    		if(!neighbors[y+1][x])
    			neighbors = loopConnected(neighbors, boardState, x, y+1, width, height);
    	}
    	if(y-1 >= 0)
    	{
    		if(!neighbors[y-1][x])
    			neighbors = loopConnected(neighbors, boardState, x, y-1, width, height);
    	}
    	return neighbors;
    }
}
