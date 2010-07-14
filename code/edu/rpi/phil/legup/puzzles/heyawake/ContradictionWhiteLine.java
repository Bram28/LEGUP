package edu.rpi.phil.legup.puzzles.heyawake;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionWhiteLine extends Contradiction
{	 
	 ContradictionWhiteLine()
	 {
		name = "White Line Too Long";
		description = "Line of white cells cannot exceed 2 rooms.";
		image = new ImageIcon("images/heyawake/contradictions/WhiteLine.png");
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
    	int width  = state.getWidth();

    	int[][] arrayacross = new int[height][width];
    	int[][] arraydown   = new int[height][width];
    	int[][] cellRegions = (int[][])state.getExtraData().get(2);
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			arrayacross[y][x] = arraydown[y][x] = 0;
    		}
    	}
    	
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			if(state.getCellContents(x,y) == 1)
    			{
    				if(x+1 < width)
    				{
    					if(state.getCellContents(x+1,y) == 1)
    					{
    						if( cellRegions[y][x] != cellRegions[y][x+1])
    							arrayacross[y][x+1] = arrayacross[y][x] + 1;
    						else
    							arrayacross[y][x+1] = arrayacross[y][x];
    					}
    				}
    				if(y+1 < height)
    				{
    					if(state.getCellContents(x,y+1) == 1)
    					{
    						if( cellRegions[y][x] != cellRegions[y+1][x])
    							arraydown[y+1][x] = arraydown[y][x] + 1;
    						else
    							arraydown[y+1][x] = arraydown[y][x];
    					}
    				}
    			}
    		}
    	}
    	
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			if(arrayacross[y][x] > 1 || arraydown[y][x] > 1)
    				return error;
    		}
    	}
    	
    	error = "A line of white cells does not exceed two rooms.";

		return error;
    }
}
