package edu.rpi.phil.legup.puzzles.nurikabe;

import java.util.ArrayList;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleBetweenRegions extends PuzzleRule
{	 
	private static final long serialVersionUID = 830456717L;
	
	 RuleBetweenRegions()
	 {
		setName("Black Between Regions");
		description = "Any unknowns between two regions must be black.";
		image = new ImageIcon("images/nurikabe/rules/BetweenRegions.png");
	 }
		
	public String getImageName()
	{
		return "images/nurikabe/rules/BetweenRegions.png";
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
		
		boolean[][] between = determineBetween(origBoardState);
		
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
						
						
						
						if(!between[y][x])
						{
							error = "Black cells must be placed between two different regions with numbers.";
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
	
	private boolean[][] determineBetween(BoardState state)
	{
		int width = state.getWidth();
		int height = state.getHeight();
		int regioncount = 1;
		//Holds what region the cell is in
		int[][] regions = new int[width][height];
		//Booleans which hold whether or not a cell is valid for the rule
		boolean[][]between = new boolean[width][height];
		
		for(int x = 0; x < width; ++x)
		{
			for(int y = 0; y < height; ++y)
			{
				if(!(state.getCellContents(x,y) == Nurikabe.CELL_BLACK || state.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN ) && regions[y][x] == 0)
				{
					if(loopConnected(state, new boolean[width][height],x,y,width,height))
					{
						regions = setRegionConnected(regioncount, regions, state, x,y,width,height);
						++regioncount;
					}
					else
					{
						regions = setRegionConnected(-1, regions, state, x,y,width,height);
					}
				}
			}
		}
		
		ArrayList<Integer> surrounding = new ArrayList<Integer>();
		for(int x = 0; x < width; ++x)
		{
			for(int y = 0; y < height; ++y)
			{
				if(regions[y][x] == 0)
				{
					surrounding.clear();
					if(x+1 < width)
					{
						if(regions[y][x+1]>0)
							surrounding.add(regions[y][x+1]);
					}
					if(x-1 >= 0)
					{
						if(regions[y][x-1]>0)
							surrounding.add(regions[y][x-1]);
					}
					if(y+1 < height)
					{
						if(regions[y+1][x]>0)
							surrounding.add(regions[y+1][x]);
					}
					if(y-1 >= 0)
					{
						if(regions[y-1][x]>0)
							surrounding.add(regions[y-1][x]);
					}
					
					
					if(surrounding.size() > 1)
					{
						int last = surrounding.get(0);
						for(int cnt = 1; cnt < surrounding.size(); ++cnt)
						{
							if(surrounding.get(cnt) != last)
							{
								between[y][x] = true;
								break;
							}
						}
					}
				}
			}
		}
		
		return between;
	}
	
	//This might go into an infinite loop
	//Needs to check if it has been somewhere
	private boolean loopConnected(BoardState boardState, boolean[][] visited,int x, int y, int width, int height)
	{
		if(boardState.getCellContents(x,y) > 0)
			return true;
		else if(!(boardState.getCellContents(x,y) == Nurikabe.CELL_WHITE))
			return false;
		if(visited[x][y]==true)
			return false;
		visited[x][y]=true;
		if(x+1 < width)
		{
			if(loopConnected( boardState, visited,x+1, y, width, height))
				return true;
		}
		if(x-1 >= 0)
		{
			if(loopConnected( boardState, visited,x-1, y, width, height))
				return true;
		}
		if(y+1 < height)
		{
			if(loopConnected( boardState, visited,x, y+1, width, height))
				return true;
		}
		if(y-1 >= 0)
		{
			if(loopConnected( boardState, visited,x, y-1, width, height))
				return true;
		}
		return false;
	}

	private int[][] setRegionConnected(int number, int[][] regions ,BoardState boardState, int x, int y, int width, int height)
	{
		if(!(boardState.getCellContents(x,y) == Nurikabe.CELL_WHITE || boardState.getCellContents(x,y) > 0))
			return regions;
		if(regions[y][x] != 0)
			return regions;
		regions[y][x] = number;
		if(x+1 < width)
		{
			regions = setRegionConnected(number, regions, boardState, x+1, y, width, height);
		}
		if(x-1 >= 0)
		{
			regions = setRegionConnected(number, regions, boardState, x-1, y, width, height);
		}
		if(y+1 < height)
		{
			regions = setRegionConnected(number, regions, boardState, x, y+1, width, height);
		}
		if(y-1 >= 0)
		{
			regions = setRegionConnected(number, regions, boardState, x, y-1, width, height);
		}
		return regions;
	}
	
	
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();
		
		
		if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
		{
			boolean[][] between = determineBetween(destBoardState);
			
			for(int x = 0; x < width; ++x)
			{
				for(int y = 0; y < height; ++y)
				{
					if(between[y][x])
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
