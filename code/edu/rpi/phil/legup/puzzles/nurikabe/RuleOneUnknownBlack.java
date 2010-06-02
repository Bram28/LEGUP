package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleOneUnknownBlack extends PuzzleRule
{	 
	
	 RuleOneUnknownBlack()
	 {
		name = "Continue Black Area";
		description = "If there is one unknown next to a black region, the unknown should also be black.";
		image = new ImageIcon("images/nurikabe/rules/OneUnknownBlack.png");
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
    	
    	boolean[][] black = determineBlack(origBoardState);
    	
    	// Check for only one branch
		if (destBoardState.getTransitionsTo().size() != 1)
		{
			error = "This rule only involves having a single branch!";
		}
		else if(black == null)
		{
			error = "This rule is only applicable if more than one black region exists.";
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
						
						
						
						if(!black[y][x])
						{
							error = "Black cells must be placed next to a black region which needs more.";
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
    
    /**
     * Determine which cells are valid for the given rule application
     * @param state The previous board state
     * @return A grid of booleans representing valid cells for the rule
     */
    private boolean[][] determineBlack(BoardState state)
    {
    	//Save width and heigh
    	int width = state.getWidth();
    	int height = state.getHeight();
    	
    	//Create a counter that will hold the number of black regions found
    	int regioncount = 0;
    	//Holds whether or not a cell has been visited
    	//1 represents visited and black
    	//0 not visited
    	//- visited but not black
    	int[][] visited = new int[width][height];
    	//Booleans which hold whether or not a cell is valid for the rule
    	boolean[][]black = new boolean[width][height];
    	
    	int temp;
    	//For each cell
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			//If the cell is black and we haven't visited it we need to loop through it and check it out
    			if(state.getCellContents(x,y) == Nurikabe.CELL_BLACK && visited[y][x] == 0)
    			{
    				//Since we have found a black region previously unvisited, add one to the region count
    				++regioncount;
    				//This loops through and returns the number of unknowns surrounding the region
    				//Since visited is by reference it should get updated
    				temp = loopConnected(visited, state,x,y,width,height,regioncount);
    				//If there is only 1 unknown around the region than it must be black
    				if(temp == 1)
    				{
    					//This finds that unknown and sets it to be valid for the application
    					setBlack(black, new boolean[width][height], state, x,y,width,height);
    				}
    			}
    		}
    	}
    	//If we found more than one black region, the cells we found are valid for appplication
    	if(regioncount > 1)
    		//So return our results
    		return black;
    	//If there is zero or one black regions than our findings are not valid
    	//This is because the goal of this rule is to make it so that all regions must not be prevented from connecting to eachother
    	//With one region there may not be any other cells that it must connect to(i.e. the region spans the entire board
    	//So we return a null
    	else
    		return null;
    }
    
    /**
     * Loops the adjacency region based off of a cell and counts the number of unknowns around the region
     * @param visited A grid of booleans representing what has been searched
     * @param boardState The board state
     * @param x The x position of the starting cell
     * @param y The y position of the starting cell
     * @param width The width of the board
     * @param height The height of the board
     * @return Returns the number of unknowns around the region
     */
    private int loopConnected(int[][] visited,BoardState boardState, int x, int y, int width, int height, int regioncount)
    {
    	//If we have been here before return nothing
    	if(visited[y][x] == 1 || visited[y][x] == -regioncount)
    		return 0;
    	
    	//Set the cell to visited but not black
    	visited[y][x] = -regioncount;
    	
    	//If the cell is unknown return that we found 1
    	if(boardState.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN)
    		return 1;
    	
    	//If the cell is white return nothing
    	if(boardState.getCellContents(x,y) != Nurikabe.CELL_BLACK)
    		return 0;
    	
    	//At this point we know the cell is black so set the cell to visited and black
    	visited[y][x] = 1;
    	
    	//A counter of how many unknowns we have found
    	int ret = 0;
    	
    	//Essentially if there is a cell in the adjacent direction, see how many unknowns we can find from it
    	//Return the sum of those unknowns
    	if(x+1 < width)
    	{
    		ret += loopConnected(visited, boardState, x+1, y, width, height, regioncount);
    	}
    	if(x-1 >= 0)
    	{
    		ret += loopConnected(visited, boardState, x-1, y, width, height, regioncount);
    	}
    	if(y+1 < height)
    	{
    		ret += loopConnected(visited, boardState, x, y+1, width, height, regioncount);
    	}
    	if(y-1 >= 0)
    	{
    		ret += loopConnected(visited, boardState, x, y-1, width, height, regioncount);
    	}
    	return ret;
    }

    private void setBlack(boolean[][] black, boolean[][] visited ,BoardState boardState, int x, int y, int width, int height)
    {
    	if(visited[y][x] == true)
    		return;
    	visited[y][x] = true;
    	
    	if(boardState.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN)
    	{
    		black[y][x] = true;
    		return;
    	}
    	if(boardState.getCellContents(x,y) != Nurikabe.CELL_BLACK)
    		return;

    	
    	if(x+1 < width)
    	{
    		setBlack(black, visited, boardState, x+1, y, width, height);
    	}
    	if(x-1 >= 0)
    	{
    		setBlack(black, visited, boardState, x-1, y, width, height);
    	}
    	if(y+1 < height)
    	{
    		setBlack(black, visited, boardState, x, y+1, width, height);
    	}
    	if(y-1 >= 0)
    	{
    		setBlack(black, visited, boardState, x, y-1, width, height);
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
    		boolean[][] black = determineBlack(destBoardState);
    		if(black != null)
    		{
	        	for(int x = 0; x < width; ++x)
	        	{
	        		for(int y = 0; y < height; ++y)
	        		{
	        			if(black[y][x])
	        				destBoardState.setCellContents(x,y,Nurikabe.CELL_BLACK);   				
	        		}
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