package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import java.awt.Point;

public class RuleCornerBlack extends PuzzleRule
{
	
	RuleCornerBlack()
	{
		name = "Corner Black";
		description = "If there is only one white square connected to unkowns and one more white is needed then the angles of that white square are black";
		image = new ImageIcon("images/nurikabe/rules/OneUnknownBlack.png");
	}
	protected String checkRuleRaw(BoardState destBoardState)
	{
		String error = null;
		boolean changed = false;
		BoardState origBoardState = destBoardState.getSingleParentState();
		
		int[][] black = determineBlack(origBoardState);
		
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
						if(black[x][y]!=2)
						{
							error = "Black cells must be placed on the corners of a whtie cell.";
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
	private int[][] determineBlack(BoardState state)
	{
		//Save width and heigh
		int width = state.getWidth();
		int height = state.getHeight();
		
		//Create a counter that will hold the number of black regions found
		//Holds whether or not a cell has been visited
		//1 represents visited and black
		//0 not visited
		//- visited but not black
		//Booleans which hold whether or not a cell is valid for the rule
		int[][] black = new int[width][height];
		
		Point temp;
		for(int x = 0; x<width;x++)
		{
			for(int y = 0; y<height; y++)
			{
				temp =loopConnected(new boolean[width][height],state,x,y,width,height);
				
				if((temp.x==2) && (temp.y==1) && (countSize(new boolean[width][height],state,x,y)==1))
				{
					setBlack(black, new boolean[width][height], state, x,y,width,height);
				}
				
			}
		}
		
		return black;
	}
	private void setBlack(int[][] black, boolean[][] visited, BoardState boardState, int x, int y, int width, int height)
	{
		if(visited[x][y] == true)
			return;
		visited[x][y] = true;
		
		if(boardState.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN)
		{
			if(x>0 && ((boardState.getCellContents(x-1,y)==Nurikabe.CELL_WHITE)||(boardState.getCellContents(x-1,y)>0)))
			{
				if(y>0)
					black[x][y-1]++;
				if(y<height-2)
					black[x][y+1]++;
			}
			if(x<width-2  && ((boardState.getCellContents(x+1,y)==Nurikabe.CELL_WHITE)||(boardState.getCellContents(x+1,y)>0)))
			{
				if(y>0)
					black[x][y-1]++;
				if(y<height-2)
					black[x][y+1]++;
			}
			if(y>0  && ((boardState.getCellContents(x,y-1)==Nurikabe.CELL_WHITE)||(boardState.getCellContents(x,y-1)>0)))
			{
				if(x>0)
					black[x-1][y]++;
				if(x<width-2)
					black[x+1][y]++;
			}
			if(y<height-2  && ((boardState.getCellContents(x,y+1)==Nurikabe.CELL_WHITE)||(boardState.getCellContents(x,y+1)>0)))
			{
				if(x>0)
					black[x-1][y]++;
				if(x<width-2)
					black[x+1][y]++;
			}
			return;
		}
		if(boardState.getCellContents(x,y) ==Nurikabe.CELL_BLACK)
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
	private int countSize(boolean [][] visited, BoardState boardState,int x, int y)
	{
		//If we have been here before return nothing
		int width = boardState.getWidth();
		int height = boardState.getHeight();
		int retVal = 0;
		if(visited[x][y] == true)
			return retVal;
		
		//Set the cell to visited but not white
		visited[x][y] = true;
		
		//If the cell is unknown return that we found 1
		if(boardState.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN)
		{
			return retVal;
		}
		//If the cell is black return nothing
		if(boardState.getCellContents(x,y) == Nurikabe.CELL_BLACK)
		{
			return retVal;
		}
		if(boardState.getCellContents(x,y)>0)
			retVal+=boardState.getCellContents(x,y)-1;
		if(boardState.getCellContents(x,y) == Nurikabe.CELL_WHITE)
			retVal--;
		int temp;
		//Essentially if there is a cell in the adjacent direction, see how many unknowns we can find from it
		//Return the sum of those unknowns if we didn't find a -1
		if(x+1 < width)
		{
			temp = countSize(visited, boardState, x+1, y);
			retVal+=temp;
		}
		if(x-1 >= 0)
		{
			temp = countSize(visited, boardState, x-1, y);
			retVal+=temp;
		}
		if(y+1 < height)
		{
			temp = countSize(visited, boardState, x, y+1);
			retVal+=temp;
		}
		if(y-1 >= 0)
		{
			temp = countSize(visited, boardState, x, y-1);
			retVal+=temp;
		}
		return retVal;
	}
	private Point loopConnected(boolean[][] visited,BoardState boardState, int x, int y, int width, int height)
	{
		//If we have been here before return nothing
		Point retP = new Point(0,0);
		if(visited[x][y] == true)
		{
			
			return retP;
		}
		//Set the cell to visited but not white
		visited[x][y] = true;
		
		//If the cell is unknown return that we found 1
		if(boardState.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN)
		{
			retP.x++;
			return retP;
		}
		//If the cell is black return nothing
		if(boardState.getCellContents(x,y) == Nurikabe.CELL_BLACK)
		{
			return retP;
		}

		//Temporary Point
		Point temp;
		int adjCounter=0;
		
		if(x+1 < width)
		{
			temp = loopConnected(visited, boardState, x+1, y, width, height);
			retP.x += temp.x;
			retP.y += temp.y;
			if(boardState.getCellContents(x+1,y)==Nurikabe.CELL_UNKNOWN)
				adjCounter++;
		}
		if(x > 0)
		{
			temp = loopConnected(visited, boardState, x-1, y, width, height);
			retP.x += temp.x;
			retP.y += temp.y;
			if(boardState.getCellContents(x-1,y)==Nurikabe.CELL_UNKNOWN)
				adjCounter++;
		}
		if(y+1 < height)
		{
			temp = loopConnected(visited, boardState, x, y+1, width, height);
			retP.x += temp.x;
			retP.y += temp.y;
			if(boardState.getCellContents(x,y+1)==Nurikabe.CELL_UNKNOWN)
				adjCounter++;
		}
		if(y > 0)
		{
			temp = loopConnected(visited, boardState, x, y-1, width, height);
			retP.x += temp.x;
			retP.y += temp.y;
			if(boardState.getCellContents(x,y-1)==Nurikabe.CELL_UNKNOWN)
				adjCounter++;
		}
		
		if(adjCounter>0)
			retP.y++;
		return retP;
	}
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();
		
		
		if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
		{
			int[][] black = determineBlack(origBoardState);

			for(int x = 0; x < width; ++x)
			{
				for(int y = 0; y < height; ++y)
				{
					if(black[x][y]==2)// && origBoardState.getCellContents(x,y)==Nurikabe.CELL_UNKNOWN)
					{
						changed=true;
						destBoardState.setCellContents(x,y,Nurikabe.CELL_BLACK);   				
					}
				}
			}
			
		}
		String error = checkRuleRaw(destBoardState);
			
		if(error != null)
		{
			System.out.println(error);
			changed = false;
			// valid change
		}
		if(changed==false)
		{
			destBoardState = origBoardState.copy();
		}
			
		return changed;
	}
}
