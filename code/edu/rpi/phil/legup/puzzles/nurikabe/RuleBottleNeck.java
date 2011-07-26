package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleBottleNeck extends PuzzleRule
{
	RuleBottleNeck()
	{
		setName("Bottle Neck");
		description = "Cells that are white bottlenecks must be white.";
		image = new ImageIcon("images/nurikabe/rules/NoBlackSquare.png");
	}
	protected String checkRuleRaw(BoardState destBoardState)
	{
		String error = null;
		boolean changed = false;
		BoardState origBoardState = destBoardState.getSingleParentState();
		BoardState altBoard = destBoardState.copy();
		boolean split;
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
						
						if (newState != Nurikabe.CELL_WHITE || origState != Nurikabe.CELL_UNKNOWN)
						{
							error = "This rule only involves adding white cells!";
							break;
						}
						else
						{
							altBoard.setCellContents(x,y,Nurikabe.CELL_BLACK);
							split = checkSplit(altBoard);
							altBoard.setCellContents(x,y,Nurikabe.CELL_WHITE);
							if(split==false)
							{
								error = "You must add a white cell because of a bottleneck to use this rule!";
								break;
							}
						}
					}
				}
			}
			
			if (error == null && !changed)
			{
				error = "You must add a white cell to use this rule!";
			}
		}
		System.out.println(error);
		return error;
	}
	boolean checkSplit(BoardState board)
	{
		int tempSize=0;
		int prevSize=0;
		int parts = 0;
		int height = board.getHeight();
		int width  = board.getWidth();
		for(int x=0; x<board.getWidth();x++)
		{
			for(int y = 0; y<board.getHeight();y++)
			{
				tempSize= loopConnected(new int[width][height],board,x,y);
				if(tempSize<2)
					continue;
				if(prevSize==0)
					prevSize=tempSize;
				if(1<Math.abs(tempSize-prevSize))
				{
					System.out.println(x+" "+y+" , "+tempSize);
					return true;
				}
			}
		}
		return false;
	}
	private int loopConnected(int[][] visited,BoardState boardState, int x, int y)
	{
		//If we have been here before return nothing
		if(visited[x][y] == 1 || visited[x][y] == -1)
			return 0;
		
		//Set the cell to visited but not white
		visited[x][y] = -1;
		
		//If the cell is black return nothing
		if(boardState.getCellContents(x,y) == Nurikabe.CELL_BLACK)
			return 0;
		
		int width = boardState.getWidth();
		int height= boardState.getHeight();
		
		//At this point we know the cell is white or unknown
		visited[x][y] = 1;
		
		//A counter of how many unknowns/whites we have found
		int ret;
		if(boardState.getCellContents(x,y)>0)
			ret = boardState.getCellContents(x,y)-1;
		else if(boardState.getCellContents(x,y)==Nurikabe.CELL_WHITE)
			ret = boardState.getCellContents(x,y)+1;
		else
			ret = 1;

		//Temporary integer
		int temp;
		
		//Essentially if there is a cell in the adjacent direction, see how many unknowns we can find from it
		//Return the sum of those unknowns if we didn't find a -1
		if(x+1 < width)
		{
			temp = loopConnected(visited, boardState, x+1, y);
			ret += temp;
		}
		if(x-1 >= 0)
		{
			temp = loopConnected(visited, boardState, x-1, y);
			ret += temp;
		}
		if(y+1 < height)
		{
			temp = loopConnected(visited, boardState, x, y+1);
			ret += temp;
		}
		if(y-1 >= 0)
		{
			temp = loopConnected(visited, boardState, x, y-1);
			ret += temp;
		}
		return ret;
	}
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();
		BoardState altBoard = destBoardState.copy();
		boolean split=false;
		String error = null;
		if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
		{
			for(int x= 0; x<width;x++)
			{
				if(split==true)
					break;
				for(int y = 0; y<height;y++)
				{
					if(origBoardState.getCellContents(x,y)==Nurikabe.CELL_UNKNOWN)
					{
						altBoard.setCellContents(x,y,Nurikabe.CELL_BLACK);
						split = checkSplit(altBoard);
						altBoard.setCellContents(x,y,Nurikabe.CELL_UNKNOWN);
						if(split==true)
						{
							changed = true;
							System.out.println(x+" "+y);
							destBoardState.setCellContents(x,y,Nurikabe.CELL_WHITE);
							break;
						}
					}
				}
			}
		}
		error = checkRuleRaw(destBoardState);
		changed = (error==null);
		
		if(changed==false)
		{
			destBoardState = origBoardState.copy();
		}
			
		return changed;
	}
}
