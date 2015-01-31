package edu.rpi.phil.legup.puzzles.heyawake;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleOneUnknownWhite extends PuzzleRule
{	 
	private static final long serialVersionUID = 599105921L;
	
	RuleOneUnknownWhite()
	{
		setName("White Escape");
		description = "If there is one unknown next to a white region the unknown should be white.";
		image = new ImageIcon("images/heyawake/rules/WhiteAroundBlack.png");
	}
	
	public String getImageName()
	{
		return "images/heyawake/rules/WhiteAroundBlack.png";
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
		if (destBoardState.getParents().size() != 1)
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
						
						if (newState != Heyawake.CELL_WHITE || origState != 0)
						{
							error = "This rule only involves adding white cells!";
							break;
						}
						
						
						
						if(!white[y][x])
						{
							error = "White cells must be placed next to a white region without a number which needs more.";
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
	
	/**
	 * Determine which cells are valid for the given rule application
	 * @param state The previous board state
	 * @return A grid of booleans representing valid cells for the rule
	 */
	private boolean[][] determineWhite(BoardState state)
	{
		//Save width and heigh
		int width = state.getWidth();
		int height = state.getHeight();
		
		//Create a counter that will hold the number of white regions found
		int regioncount = 0;
		//Holds whether or not a cell has been visited
		//1 represents visited and white
		//0 not visited
		//- visited but not white
		int[][] visited = new int[height][width];
		//Booleans which hold whether or not a cell is valid for the rule
		boolean[][]white = new boolean[height][width];
		
		int temp;
		//For each cell
		for(int x = 0; x < width; ++x)
		{
			for(int y = 0; y < height; ++y)
			{
				//If the cell is white and we haven't visited it we need to loop through it and check it out
				if(state.getCellContents(x,y) == Heyawake.CELL_WHITE && visited[y][x] == 0)
				{
					//Since we have found a white region previously unvisited, add one to the region count
					++regioncount;
					//This loops through and returns the number of unknowns surrounding the region
					//If the loop finds a number then it returns -1 signifying that we can't apply the rule on this region
					//Since visited is by reference it should get updated
					temp = loopConnected(visited, state,x,y,width,height,regioncount);
					//If there is only 1 unknown around the region than it must be white
					if(temp == 1)
					{
						//This finds that unknown and sets it to be valid for the application
						setWhite(white, new boolean[height][width], state, x,y,width,height);
					}
				}
			}
		}
		//Return our results
		return white;
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
		
		//Set the cell to visited but not white
		visited[y][x] = -regioncount;
		
		//If the cell is unknown return that we found 1
		if(boardState.getCellContents(x,y) == Heyawake.CELL_UNKNOWN)
			return 1;
		
		//If the cell is black return nothing
		if(boardState.getCellContents(x,y) == Heyawake.CELL_BLACK)
			return 0;
		
		//A boolean to keep track of if we found a -1
		//We do this instead of returning so that all of the cells in the region still get marked as visited
		boolean foundnegative = false;
		
		//If the cell is a number return -1;
		
		//At this point we know the cell is white so set the cell to visited and white
		visited[y][x] = 1;
		
		//A counter of how many unknowns we have found
		int ret = 0;

		//Temporary integer
		int temp;
		
		//Essentially if there is a cell in the adjacent direction, see how many unknowns we can find from it
		//Return the sum of those unknowns if we didn't find a -1
		if(x+1 < width)
		{
			temp = loopConnected(visited, boardState, x+1, y, width, height, regioncount);
			ret += temp;
		}
		if(x-1 >= 0)
		{
			temp = loopConnected(visited, boardState, x-1, y, width, height, regioncount);
			ret += temp;
		}
		if(y+1 < height)
		{
			temp = loopConnected(visited, boardState, x, y+1, width, height, regioncount);
			ret += temp;
		}
		if(y-1 >= 0)
		{
			temp = loopConnected(visited, boardState, x, y-1, width, height, regioncount);
			ret += temp;
		}
		return ret;
	}

	private void setWhite(boolean[][] white, boolean[][] visited ,BoardState boardState, int x, int y, int width, int height)
	{
		if(visited[y][x] == true)
			return;
		visited[y][x] = true;
		
		if(boardState.getCellContents(x,y) == Heyawake.CELL_UNKNOWN)
		{
			white[y][x] = true;
			return;
		}
		if(boardState.getCellContents(x,y) != Heyawake.CELL_WHITE)
			return;

		
		if(x+1 < width)
		{
			setWhite(white, visited, boardState, x+1, y, width, height);
		}
		if(x-1 >= 0)
		{
			setWhite(white, visited, boardState, x-1, y, width, height);
		}
		if(y+1 < height)
		{
			setWhite(white, visited, boardState, x, y+1, width, height);
		}
		if(y-1 >= 0)
		{
			setWhite(white, visited, boardState, x, y-1, width, height);
		}
	}
	
	
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();
		
		
		if (origBoardState != null && destBoardState.getParents().size() == 1)
		{
			boolean[][] white = determineWhite(destBoardState);

			for(int x = 0; x < width; ++x)
			{
				for(int y = 0; y < height; ++y)
				{
					if(white[y][x])
					{
						changed = true;
						destBoardState.setCellContents(x,y,Heyawake.CELL_WHITE);   				
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
