package edu.rpi.phil.legup.puzzles.nurikabe;

import java.awt.Point;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleOneUnknownRegion extends PuzzleRule
{	
    private static final long serialVersionUID = 450532374L;

	 RuleOneUnknownRegion()
	 {
		setName("Continue Region");
		description = "If there is one unknown next to a region and the region needs more whites, the unknown must be white.";
		image = new ImageIcon("images/nurikabe/rules/OneUnknownRegion.png");
	 }
	 
	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    protected String checkRuleRaw(BoardState destBoardState)
    {
    	String error = null;
    	boolean changed = false;
    	BoardState origBoardState = destBoardState.getSingleParentState();
    	
    	boolean[][] white = determineWhite(origBoardState);
    	
    	// Check for only one branch
		if (destBoardState.getTransitionsTo().size() != 1)
		{
			error = "This rule only involves having a single branch!";
		}
		else
		{
			
			for (int y = 0; y < origBoardState.getHeight() && error == null; ++y)
			{
				for (int x = 0; x < origBoardState.getWidth(); ++x)
				{
					int origState = origBoardState.getCellContents(x,y);
					int newState = destBoardState.getCellContents(x,y);
					
					if (origState != newState)
					{
						changed = true;
						
						if (newState != Nurikabe.CELL_WHITE || origState != 0)
						{
							error = "This rule only involves adding white cells!";
							break;
						}
						
						
						
						if(!white[y][x])
						{
							error = "White cells must be placed next to a region which needs more.";
							break;
						}
						
						
						
					}
				}
			}
			
			if (error == null && !changed)
			{
				error = "You must add a white cell to use this rule!";
			}
		}
		
		return error;
    }
    
    private boolean[][] determineWhite(BoardState state)
    {
    	int width = state.getWidth();
    	int height = state.getHeight();
    	//Holds what region the cell is in
    	boolean[][] whites = new boolean[width][height];
    	
    	Point temp;
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			if(!(state.getCellContents(x,y) == Nurikabe.CELL_BLACK || state.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN ))
    			{
    				temp = loopConnected(new boolean[width][height], state,x,y,width,height);
    				if(temp.y == 1 && temp.x > 0)
    				{
    					whites = setWhite(whites, new boolean[width][height], state, x,y,width,height);
    				}
    			}
    		}
    	}
    	
    	return whites;
    }
    
    //HACK:Uses a point to store 2 ints
    private Point loopConnected(boolean[][] neighbors,BoardState boardState, int x, int y, int width, int height)
    {
    	//x == how many desired whites - actual
    	//y == how many surrounding unknowns
    	Point ret = new Point(0,0);
    	if(neighbors[y][x] == true)
    		return ret;
    	neighbors[y][x] = true;
    	if(boardState.getCellContents(x,y) == Nurikabe.CELL_BLACK)
    		return ret;
    	if(boardState.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN)
    	{
    		++ret.y;
    		return ret;
    	}
    	
    	--ret.x;
    	if(boardState.getCellContents(x,y) > 0)
    		ret.x += boardState.getCellContents(x,y);
    	
    	Point temp;
    	if(x+1 < width)
    	{
    		temp = loopConnected(neighbors, boardState, x+1, y, width, height);
    		ret.x += temp.x;
    		ret.y += temp.y;
    	}
    	if(x-1 >= 0)
    	{
    		temp = loopConnected(neighbors, boardState, x-1, y, width, height);
    		ret.x += temp.x;
    		ret.y += temp.y;
    	}
    	if(y+1 < height)
    	{
    		temp = loopConnected(neighbors, boardState, x, y+1, width, height);
    		ret.x += temp.x;
    		ret.y += temp.y;
    	}
    	if(y-1 >= 0)
    	{
    		temp = loopConnected(neighbors, boardState, x, y-1, width, height);
    		ret.x += temp.x;
    		ret.y += temp.y;
    	}
    	return ret;
    }

    private boolean[][] setWhite(boolean[][] white, boolean[][] neighbors ,BoardState boardState, int x, int y, int width, int height)
    {
    	if(neighbors[y][x] == true)
    		return white;
    	neighbors[y][x] = true;
    	
    	if(boardState.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN)
    	{
    		white[y][x] = true;
    		return white;
    	}
    	if(boardState.getCellContents(x,y) == Nurikabe.CELL_BLACK)
    		return white;

    	
    	if(x+1 < width)
    	{
    		white = setWhite(white, neighbors, boardState, x+1, y, width, height);
    	}
    	if(x-1 >= 0)
    	{
    		white = setWhite(white, neighbors, boardState, x-1, y, width, height);
    	}
    	if(y+1 < height)
    	{
    		white = setWhite(white, neighbors, boardState, x, y+1, width, height);
    	}
    	if(y-1 >= 0)
    	{
    		white = setWhite(white, neighbors, boardState, x, y-1, width, height);
    	}
    	return white;
    }
    
    
    protected boolean doDefaultApplicationRaw(BoardState destBoardState)
    {
		BoardState origBoardState = destBoardState.getSingleParentState();
    	boolean changed = false;
    	int width = destBoardState.getWidth();
    	int height = destBoardState.getHeight();
    	
    	
    	if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
    	{
    		boolean[][] white = determineWhite(destBoardState);
    		
        	for(int x = 0; x < width; ++x)
        	{
        		for(int y = 0; y < height; ++y)
        		{
        			if(white[y][x])
        				destBoardState.setCellContents(x,y,Nurikabe.CELL_WHITE);   				
        		}
        	}
	    	String error = checkRuleRaw(destBoardState);
	    	
			if (error == null)
			{
				changed = true;
				// valid change
			}
    	}
    	
    	if(!changed)
    	{
    		destBoardState = origBoardState.copy();
    	}
	    	
	    return changed;
    }
}