package edu.rpi.phil.legup.puzzles.nurikabe;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleFillInWhite extends PuzzleRule
{
	private static final long serialVersionUID = -2761216044763518050L;

	RuleFillInWhite()
	{
		setName("Fill In White");
		description = "If there an unknown region surrounded by white, it must be white.";
		image = new ImageIcon("images/nurikabe/rules/FillInWhite.png");
	}
	
	public String getImageName()
	{
		return "images/nurikabe/rules/FillInWhite.png";
	}
	
	protected String checkRuleRaw(BoardState destBoardState)
	{
		String error = null;
		boolean changed = false;
		BoardState origBoardState = destBoardState.getSingleParentState();
		
		int[][] filled = determineRegions(origBoardState);
		
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
					
						if(filled[y][x] == -1 && destBoardState.getCellContents(x,y) != Nurikabe.CELL_WHITE)
						{
							error="White cells must be placed inside of a region of white cells.";
						}
						else if (filled[y][x] != -1)
						{
							error = "Cannot place cells outside a region of white cells";
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
	
	private int[][] determineRegions(BoardState state)
	{
		int width = state.getWidth();
    	int height = state.getHeight();
    	boolean[][] visited = new boolean[width][height];
    	int[][] filled = new int[width][height];
    	
    	ArrayList<Point> regions = new ArrayList<Point>();
    	
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			filled[y][x] = 0;
    			
    			if(state.getCellContents(x,y) == Nurikabe.CELL_WHITE || state.getCellContents(x,y) > 10)
    			{
    				visited[y][x] = true;
    				filled[y][x] = Nurikabe.CELL_WHITE;
    			}
    		}
    	}
    	
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			if(!visited[y][x])
    			{
    				getRegionSize(visited, state, x, y, width, height, regions);
    				
    				//check if the region contains any black cells
    				boolean hasBlackCells = false;
    				for (int i = 0; i < regions.size(); i++)
    				{
    					int regionX = regions.get(i).x;
    					int regionY = regions.get(i).y;
    					if (state.getCellContents(regionX , regionY) == Nurikabe.CELL_BLACK)
    					{
    						hasBlackCells = true;
    						break;
    					}
    				}
    				
    				//If the region has no black cells, then this region can be filled with white cells
    				if (hasBlackCells == false)
    				{
	    				for (int i = 0; i < regions.size(); i++)
	    				{
	    					filled[regions.get(i).y][regions.get(i).x] = -1;
	    				}
    				}
    				
    				//erase all the contents
    				regions.clear();
    			}
    		}
    	}
    		
    	return filled;
	}
	private int getRegionSize(boolean[][] visited, BoardState boardState, int x, int y, int width, int height, ArrayList<Point> regions)
    {
    	int numcount = 0;
    	if(boardState.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN)
    		numcount += 1;
    
    	visited[y][x] = true;
    	regions.add(new Point(x, y));
    	
    	if(x+1 < width)
    	{
    		if(!visited[y][x+1])
    		{
    			numcount += getRegionSize(visited, boardState, x+1, y, width, height, regions);
    		}
    	}
    	if(x-1 >= 0)
    	{
    		if(!visited[y][x-1])
    		{
    			numcount += getRegionSize(visited, boardState, x-1, y, width, height, regions);
    		}
    	}
    	if(y+1 < height)
    	{
    		if(!visited[y+1][x])
    		{
    			numcount += getRegionSize(visited, boardState, x, y+1, width, height, regions);
    		}
    	}
    	if(y-1 >= 0)
    	{
    		if(!visited[y-1][x])
    		{
    			numcount += getRegionSize(visited, boardState, x, y-1, width, height, regions);
    		}
    	}
    	return numcount;
    }
	
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();
		int whiteCount;
		
		if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
		{
			
			for(int x = 0; x < width; ++x)
			{
				for(int y = 0; y < height; ++y)
				{
					whiteCount = 0;
					if(origBoardState.getCellContents(x,y)!=Nurikabe.CELL_UNKNOWN)
						continue;
					for(int dx = -1;dx<2;dx++)
					{
						for(int dy = -1; dy<2;dy++)
						{
							if(dx!=0 && dy!=0)
								continue;
							if((dx+x>=width) || (dx+x<0))
								continue;
							if((dy+y>=height) || (dy+y<0))
								continue;
							if(origBoardState.getCellContents(x+dx,y+dy)==Nurikabe.CELL_WHITE)
								whiteCount++;
						}
					}
					if(whiteCount==4)
					{
						changed = true;
						destBoardState.setCellContents(x,y,Nurikabe.CELL_WHITE);
					}		
				}
			}
			
			
		}
		String error = checkRuleRaw(destBoardState);
		if(error!=null)
		{
			changed = false;
			System.out.println(error);
		}
		if(!changed)
		{
			destBoardState = origBoardState.copy();
		}
			
		return changed;
	}

}
