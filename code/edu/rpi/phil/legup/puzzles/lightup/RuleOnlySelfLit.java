package edu.rpi.phil.legup.puzzles.lightup;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleOnlySelfLit extends PuzzleRule
{
	static final long serialVersionUID = 3220052913694553750L;
	public String getImageName() {return "images/lightup/rules/MustLight.png";}
	RuleOnlySelfLit()
	{
		setName("Must Light");
		description = "A cell must be a light if it is the only cell to be able to light another.";
	}
	
	//Returns a point which is the only way to light up this cell
	private Point findOneSource(BoardState boardState, boolean[][] litup, int x, int y)
	{
		if(litup[y][x] || boardState.getCellContents(x, y) == LightUp.CELL_LIGHT || boardState.getCellContents(x, y) >= 10)
			return null;
		
		Point ret = null;
		
		int ix = x; //This is just x so we can check this cell
		while(ix >= 0)
		{
			if(boardState.getCellContents(ix, y) >= 10)
				break;
			if(!litup[y][ix] && boardState.getCellContents(ix, y) == LightUp.CELL_UNKNOWN)
			{
				if(ret != null)
					return null;
				ret = new Point(ix,y);
			}
			--ix;
		}
		
		ix = x + 1;
		while(ix < boardState.getWidth())
		{
			if(boardState.getCellContents(ix, y) >= 10)
				break;
			if(!litup[y][ix] && boardState.getCellContents(ix, y) == LightUp.CELL_UNKNOWN)
			{
				if(ret != null)
					return null;
				ret = new Point(ix,y);
			}
			++ix;
		}
		
		int iy = y - 1;
		while(iy >= 0)
		{
			if(boardState.getCellContents(x, iy) >= 10)
				break;
			if(!litup[iy][x] && boardState.getCellContents(x, iy) == LightUp.CELL_UNKNOWN)
			{
				if(ret != null)
					return null;
				ret = new Point(x,iy);
			}
			--iy;
		}
		
		iy = y + 1;
		while(iy < boardState.getHeight())
		{
			if(boardState.getCellContents(x, iy) >= 10)
				break;
			if(!litup[iy][x] && boardState.getCellContents(x, iy) == LightUp.CELL_UNKNOWN)
			{
				if(ret != null)
					return null;
				ret = new Point(x,iy);
			}
			++iy;
		}
		
		return ret;
	}
	
	private ArrayList<Point> findAllOneSources(BoardState boardState, boolean[][] litup)
	{
		ArrayList<Point> points = new ArrayList<Point>();
		for(int x = 0; x < boardState.getWidth(); ++x)
		{
			for(int y = 0; y < boardState.getHeight(); ++y)
			{
				Point p = findOneSource(boardState, litup, x, y);
				if(p != null)
					points.add(p);
			}
		}
		return points;
	}

	protected String checkRuleRaw(BoardState destBoardState)
	{
		String error = null;
		boolean changed = false;
		BoardState origBoardState = destBoardState.getSingleParentState();
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();

		boolean[][] litup = new boolean[origBoardState.getHeight()][origBoardState
				.getWidth()];
		LightUp.determineLight(origBoardState, litup);

		// Check for only one branch
		if (destBoardState.getTransitionsTo().size() != 1)
		{
			return "This rule only involves having a single branch!";
		}
		else
		{
			ArrayList<Point> points = findAllOneSources(origBoardState, litup);
			if(points.size() == 0)
				return "All blanks are either lit or can be lit from more than 1 source.";
			for (int y = 0; y < origBoardState.getHeight() && error == null; ++y)
			{
				for (int x = 0; x < origBoardState.getWidth(); ++x)
				{
					int origState = origBoardState.getCellContents(x, y);
					int newState = destBoardState.getCellContents(x, y);

					if (origState != newState)
					{
						changed = true;

						if (newState != LightUp.CELL_LIGHT
								|| origState != LightUp.CELL_UNKNOWN)
						{
							return "This rule only involves adding light bulbs!";
						}
						if (litup[y][x])
						{
							return "The cell cannot be previously lit!";
						}
						boolean issource = false;
						for(int i = 0; i < points.size(); ++i)
						{
							if(points.get(i).x == x && points.get(i).y == y)
							{
								issource = true;
								break;
							}
						}
						
						if(!issource)
							return "A light is incorrectly marked as being the only possible source for a cell.";
					}
				}
			}

			if (error == null && !changed)
			{
				return "You must add a bulb to use this rule!";
			}
		}

		return error;
	}

	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();
		boolean foundopening;
		boolean[][] litup = new boolean[origBoardState.getHeight()][origBoardState
				.getWidth()];
		LightUp.determineLight(origBoardState, litup);

		if (origBoardState != null
				&& destBoardState.getTransitionsTo().size() == 1)
		{
			ArrayList<Point> points = findAllOneSources(destBoardState, litup);
			if(points.size() == 0)
			{
				return false;
			}
			else
			{
				for(int i = 0; i < points.size(); ++i)
				{
					destBoardState.setCellContents(points.get(i).x, points.get(i).y, LightUp.CELL_LIGHT);
				}
			}

			String error = checkRuleRaw(destBoardState);

			if (error == null)
			{
				changed = true;
				// valid change
			}
		}

		if (!changed)
		{
			destBoardState = origBoardState.copy();
		}
		else
		{
			LightUp.fillLight(destBoardState);
		}

		return changed;
	}
}
