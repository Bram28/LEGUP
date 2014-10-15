package edu.rpi.phil.legup.puzzles.nurikabe;

import java.awt.Point;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleOneUnknownRegion extends PuzzleRule
{	
	
	 RuleOneUnknownRegion()
	 {
		setName("Continue White");
		description = "If there is one unknown next to a region and the region needs more whites, the unknown must be white.";
		image = new ImageIcon("images/nurikabe/rules/OneUnknownWhite.png");
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
						
						
						
						if(!white[x][y])
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
    					checkForMultiple(whites, x, y, -1, -1, state, width, height);
    				}
    			}
    		}
    	}
    	
    	return whites;
    }
    
    /**
     * Recursivly checks all spots around any possible white area to see if multiple spots could be considered white
     * @param white grid of all values that could be set to white based on continue white rule
     * @param x the current x coordinate
     * @param y the current y coordinate
     * @param preX the previous x coordinate
     * @param preY the previous y coordinate
     * @param boardState the current boardstate
     * @param width the width of the board
     * @param height the height of the board
     */
    private void checkForMultiple(boolean[][] white, int x, int y, int preX, int preY, BoardState boardState, int width, int height) {
    	int openIndex = multLoopConnected(x, y, preX, preY, boardState, width, height);
    	if (openIndex <= 0)
    	{
    		return;
    	}
    	
    	if (openIndex == 1)
    	{
    		white[x+1][y] = true;
    		checkForMultiple(white, x+1, y, x, y, boardState, width, height);
    		return;
    	}
    	if (openIndex == 2)
    	{
    		white[x][y+1] = true;
    		checkForMultiple(white, x, y+1, x, y, boardState, width, height);
    		return;
    	}
    	if (openIndex == 3)
    	{
    		white[x-1][y] = true;
    		checkForMultiple(white, x-1, y, x, y, boardState, width, height);
    		return;
    	}
    	if (openIndex == 4)
    	{
    		white[x][y-1] = true;
    		checkForMultiple(white, x, y-1, x, y, boardState, width, height);
    		return;
    	}
    }
    
    /**
     * helper function for check for multiple
     * @param x the current x coordinate
     * @param y the current y coordinate
     * @param preX the previous x coordinate
     * @param preY the previous y coordinate
     * @param boardState the current boardstate
     * @param width the width of the board
     * @param height the height of the board
     * @return 0 if there are no open adjacent spaces
     * @return -1 if there are multiple open adjacent spaces
     * @return 1 if there is a single open adjacent space to the right of the current cell
     * @return 2 if there is a single open adjacent space below the current cell
     * @return 3 if there is a single open adjacent space to the left of the current cell
     * @return 4 if there is a single open adjacent space above the current cell
     */
    private int multLoopConnected(int x, int y, int preX, int preY, BoardState boardState, int width, int height) {
    	int openAdjacentSpaces = 0;
    	
    	int openIndex = 0;
    	if (x+1 < width)
    	{
        	if ((boardState.getCellContents(x+1, y) == Nurikabe.CELL_UNKNOWN)
        			&& (x+1 != preX || y != preY))
        		openIndex = 1;
    	}
    	if (y+1 < height)
    	{
    		if ((boardState.getCellContents(x, y+1) == Nurikabe.CELL_UNKNOWN)
    			&& (x != preX || y+1 != preY))
    		{
    			if (openIndex > 0){
    				openIndex = -1;
    			} else {
    				openIndex = 2;
    			}
    		}
    	}
    	if (x-1 >= 0)
    	{
    		if ((boardState.getCellContents(x-1, y) == Nurikabe.CELL_UNKNOWN)
    			&& (x-1 != preX || y != preY)) 
    		{
    			if (openIndex > 0){
    				openIndex = -1;
    			} else {
    				openIndex = 3;
    			}
    		}
    	}
    	if (y-1 >= 0){
    		if ((boardState.getCellContents(x, y-1) == Nurikabe.CELL_UNKNOWN)
        			&& (x != preX || y-1 != preY))
    		{
    			if (openIndex > 0){
    				openIndex = -1;
    			} else {
    				openIndex = 4;
    			}
    		}
    	}
    		
    	return openIndex;
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