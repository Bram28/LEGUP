package edu.rpi.phil.legup.puzzles.lightup;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionTooManyBulbs extends Contradiction
{	 
	
	 ContradictionTooManyBulbs()
	 {
		setName("Too Many Bulbs");
		description = "There cannot be more bulbs around a block than its number states.";
		image = new ImageIcon("images/lightup/contradictions/TooManyBulbs.png");
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
    	int cellvalue = 0;
    	int bulbs = 0;

    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			cellvalue = state.getCellContents(x,y);
    			if(cellvalue >= 10  && cellvalue < 15)
    			{
    				bulbs = 0;
    				if(x > 0)
    					if(state.getCellContents(x-1, y) == LightUp.CELL_LIGHT)
    						++bulbs;
    				if(x < width - 1)
    					if(state.getCellContents(x+1, y) == LightUp.CELL_LIGHT)
    						++bulbs;
    				if(y > 0)
    					if(state.getCellContents(x, y-1) == LightUp.CELL_LIGHT)
    						++bulbs;
    				if(y < height - 1)
    					if(state.getCellContents(x, y+1) == LightUp.CELL_LIGHT)
    						++bulbs;
    					
    				if(bulbs > cellvalue - 10)
    					return null;
    			}
    		}
    	}
    	
    	error = "No block with too many bulbs exists.";

		return error;
    }
}
