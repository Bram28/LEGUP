package edu.rpi.phil.legup.puzzles.lightup;

import edu.rpi.phil.legup.BoardState;

public class NumberedBlock
{
	private int bulbs = 0;
	private int blanks = 0;
	private int unknowns = 0;
	private int value;
	private int blocks = 0;
	private int lit = 0;
	
	private int width;
	private int height;
	
	NumberedBlock(BoardState state, int x, int y)
	{
		width = state.getWidth();
		height = state.getHeight();
		boolean[][] litup = new boolean[height][width];
		LightUp.determineLight(state, litup);
		
		if(x < width && y < height )
		{
			if(state.getCellContents(x, y) >= 10 && state.getCellContents(x, y) < 15)
			{
				this.value = state.getCellContents(x, y) - 10;
				
				if(x > 0)
				{
					if(state.getCellContents(x - 1, y) == LightUp.CELL_LIGHT)
						++bulbs;
					else if(state.getCellContents(x - 1, y) == LightUp.CELL_UNKNOWN && litup[y][x-1])
						++lit;
					else if(state.getCellContents(x - 1, y) == LightUp.CELL_BLANK )
						++blanks;
					else if(state.getCellContents(x - 1, y) >= 10)
						++blocks;
					else
						++unknowns;
				}
				else
					++blocks;
				
				if(x < width - 1)
				{
					if(state.getCellContents(x + 1, y) == LightUp.CELL_LIGHT)
						++bulbs;
					else if(state.getCellContents(x + 1, y) == LightUp.CELL_UNKNOWN && litup[y][x+1])
						++lit;
					else if(state.getCellContents(x + 1, y) == LightUp.CELL_BLANK )
						++blanks;
					else if(state.getCellContents(x + 1, y) >= 10)
						++blocks;
					else
						++unknowns;
				}
				else
					++blocks;
				
				if(y > 0)
				{
					if(state.getCellContents(x, y - 1) == LightUp.CELL_LIGHT)
						++bulbs;
					else if(state.getCellContents(x, y - 1) == LightUp.CELL_UNKNOWN && litup[y-1][x])
						++lit;
					else if(state.getCellContents(x, y - 1) == LightUp.CELL_BLANK )
						++blanks;
					else if(state.getCellContents(x, y-1) >= 10)
						++blocks;
					else
						++unknowns;
				}
				else
					++blocks;
				
				if(y < height - 1)
				{
					if(state.getCellContents(x, y + 1) == LightUp.CELL_LIGHT)
						++bulbs;
					else if(state.getCellContents(x, y + 1) == LightUp.CELL_UNKNOWN && litup[y+1][x])
						++lit;
					else if(state.getCellContents(x, y + 1) == LightUp.CELL_BLANK )
						++blanks;
					else if(state.getCellContents(x, y+1) >= 10)
						++blocks;
					else //unknown && unlit
						++unknowns;
				}
				else
					++blocks;
			}
			else
			{
				throw new IllegalStateException();
			}
		}
		else
		{
			throw new IndexOutOfBoundsException();
		}
	}
	
	public int getUnNeededBlanks()
	{
		return unknowns - (value - bulbs);
	}
	
	public int getRemainingBulbs()
	{
		return (value - bulbs);
	}
}
