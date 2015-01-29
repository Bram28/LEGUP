package edu.rpi.phil.legup.puzzles.nurikabe;

import java.awt.Point;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleSurroundRegion extends PuzzleRule
{
    private static final long serialVersionUID = 881143872L;

	 RuleSurroundRegion()
	 {
		setName("Surround Region");
		description = "All completed white regions must be surrounded by black.";
		image = new ImageIcon("images/nurikabe/rules/SurroundBlack.png");
	 }

	public String getImageName()
	{
		return "images/nurikabe/rules/SurroundBlack.png";
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

    	int[][] surrounding = determineSurrounding(origBoardState);

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

						if (newState != Nurikabe.CELL_BLACK || origState != 0)
						{
							error = "This rule only involves adding black cells!";
							break;
						}



						if(surrounding[y][x] != Nurikabe.CELL_BLACK && destBoardState.getCellContents(x,y) == Nurikabe.CELL_BLACK)
						{
							error = "Black cells must be placed around a completed region.";
							break;
						}



					}
				}
			}

			if (error == null && !changed)
			{
				error = "You must add a black cell to use this rule!";
			}
		}

		return error;
    }

    private int[][] determineSurrounding(BoardState state)
    {
    	int width = state.getWidth();
    	int height = state.getHeight();
    	int[][] surrounding = new int[width][height];
    	boolean[][] neighbors = new boolean[width][height];
    	boolean[][]surrounded = new boolean[width][height];

    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			surrounding[y][x] = state.getCellContents(x,y);
    			if(state.getCellContents(x,y) == Nurikabe.CELL_BLACK || state.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN )
    			{
    				neighbors[y][x] = true;
    				if(state.getCellContents(x,y)==Nurikabe.CELL_BLACK)
    					surrounded[y][x]=true;
    			}
    		}
    	}

    	Point temp = new Point();
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			if(!neighbors[y][x])
    			{
    				temp = loopConnected(neighbors, state,x,y,width,height);
    				if(temp.y == 0)
    				{
    					;//return "Different contradiction found. No number found in a region.";
    				}
    				else if(temp.y == -1)
    				{
    					;//return "Different contradiction found. Too many numbers in a region";
    				}
    				else if(temp.y == temp.x)
    					surroundRegion(surrounding,surrounded, state, x,y, width, height);
    			}
    		}
    	}

    	return surrounding;
    }

    //HACK: This uses a point in order to return 2 ints
    private Point loopConnected(boolean[][] neighbors,BoardState boardState, int x, int y, int width, int height)
    {
    	Point numcount = new Point(0,0);
    	Point temp = new Point();
    	if(boardState.getCellContents(x,y) > 10)
    	{
    		numcount.x += 1;
    		numcount.y = boardState.getCellContents(x,y) - 10;
    	}
    	else if(boardState.getCellContents(x,y) == Nurikabe.CELL_WHITE)
    		numcount.x += 1;
    	else if(boardState.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN)
    		numcount.x += 1;
    	neighbors[y][x] = true;
    	if(x+1 < width)
    	{
    		if(!neighbors[y][x+1])
    		{
    			temp = loopConnected(neighbors, boardState, x+1, y, width, height);
    			numcount.x += temp.x;
    			if(temp.y != 0)
    			{
    				if(numcount.y == 0)
    					numcount.y = temp.y;
    				else
    					numcount.y = -1;
    			}
    		}
    	}
    	if(x-1 >= 0)
    	{
    		if(!neighbors[y][x-1])
    		{
    			temp = loopConnected(neighbors, boardState, x-1, y, width, height);
				numcount.x += temp.x;
				if(temp.y != 0)
				{
					if(numcount.y == 0)
						numcount.y = temp.y;
					else
						numcount.y = -1;
				}
    		}
    	}
    	if(y+1 < height)
    	{
    		if(!neighbors[y+1][x])
    		{
    			temp = loopConnected(neighbors, boardState, x, y+1, width, height);
				numcount.x += temp.x;
				if(temp.y != 0)
				{
					if(numcount.y == 0)
						numcount.y = temp.y;
					else
						numcount.y = -1;
				}
    		}
    	}
    	if(y-1 >= 0)
    	{
    		if(!neighbors[y-1][x])
    		{
    			temp = loopConnected(neighbors, boardState, x, y-1, width, height);
				numcount.x += temp.x;
				if(temp.y != 0)
				{
					if(numcount.y == 0)
						numcount.y = temp.y;
					else
						numcount.y = -1;
				}
    		}
    	}
    	return numcount;
    }


    private void surroundRegion(int[][] surrounding, boolean[][] surrounded, BoardState boardState, int x, int y, int width, int height)
    {
    	surrounded[y][x] = true;
    	if(x+1 < width)
    	{
    		if(!surrounded[y][x+1])
    		{
    			if(boardState.getCellContents(x+1,y) == Nurikabe.CELL_UNKNOWN)
    			{
    				surrounding[y][x+1] = Nurikabe.CELL_BLACK;
    				surrounded[y][x+1] = true;
    			}
    			if(boardState.getCellContents(x+1,y) == Nurikabe.CELL_WHITE || boardState.getCellContents(x+1,y) >0)
    				surroundRegion(surrounding, surrounded, boardState,x+1,y,width,height);
    		}
    	}
    	if(x-1 >= 0)
    	{
    		if(!surrounded[y][x-1])
    		{
    			if(boardState.getCellContents(x-1,y) == Nurikabe.CELL_UNKNOWN)
    			{
    				surrounding[y][x-1] = Nurikabe.CELL_BLACK;
    				surrounded[y][x-1]=true;
    			}
    			if(boardState.getCellContents(x-1,y) == Nurikabe.CELL_WHITE || boardState.getCellContents(x-1,y) >0)
    				surroundRegion(surrounding, surrounded, boardState,x-1,y,width,height);
    		}
    	}
    	if(y+1 < height)
    	{
    		if(!surrounded[y+1][x])
    		{
    			if(boardState.getCellContents(x,y+1) == Nurikabe.CELL_UNKNOWN)
    			{
    				surrounding[y+1][x] = Nurikabe.CELL_BLACK;
    				surrounded[y+1][x]=true;
    			}
    			if(boardState.getCellContents(x,y+1) == Nurikabe.CELL_WHITE || boardState.getCellContents(x,y+1) >0)
    				surroundRegion(surrounding, surrounded, boardState,x,y+1,width,height);
    		}
    	}
    	if(y-1 >= 0)
    	{
    		if(!surrounded[y-1][x])
    		{
    			if(boardState.getCellContents(x,y-1) == Nurikabe.CELL_UNKNOWN)
    			{
    				surrounding[y-1][x] = Nurikabe.CELL_BLACK;
    				surrounded[y-1][x]=true;
    			}
    			if(boardState.getCellContents(x,y-1) == Nurikabe.CELL_WHITE || boardState.getCellContents(x,y-1) >0)
    				surroundRegion(surrounding, surrounded, boardState,x,y-1,width,height);
    		}
    	}
    }

    protected boolean doDefaultApplicationRaw(BoardState destBoardState)
    {
		BoardState origBoardState = destBoardState.getSingleParentState();
    	boolean changed = false;
    	int width = destBoardState.getWidth();
    	int height = destBoardState.getHeight();


    	if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
    	{
    		int[][] surround = determineSurrounding(destBoardState);

        	for(int x = 0; x < width; ++x)
        	{
        		for(int y = 0; y < height; ++y)
        		{
        			if(surround[y][x] == Nurikabe.CELL_BLACK)
        				destBoardState.setCellContents(x,y,Nurikabe.CELL_BLACK);
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
