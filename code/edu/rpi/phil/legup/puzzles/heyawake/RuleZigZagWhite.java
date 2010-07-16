package edu.rpi.phil.legup.puzzles.heyawake;

import java.util.Vector;
import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import java.util.ArrayList;
import java.awt.Point;

public class RuleZigZagWhite extends PuzzleRule
{
	RuleZigZagWhite()
	{
		name = "White forced from zigzag";
		description = "Cells next to a forced zigzag are white.";
		image = new ImageIcon("images/heyawake/rules/ZigZagWhite.png");
	}
	//Returns the dimensions of the region Not including white cells.
	protected Point getRegionDimension(BoardState origState, int cellregion)
	{
		Vector<CellLocation> cells;
		CellLocation tempcell;
		int rheight=0;
		int rwidth =0;
		int xmin,xmax,ymin,ymax;
		xmin = ymin = 1000000;
		xmax = ymax =-1000000;
		Region region = ((Region[])origState.getExtraData().get(0))[cellregion];
		cells = region.getCells();
		
		for(int c = 0; c<cells.size(); ++c)
		{
			if(origState.getCellContents(cells.get(c).getX(),cells.get(c).getY())==Heyawake.CELL_WHITE)
			{
				continue;
			}
			if(cells.get(c).getX()<xmin)
				xmin = cells.get(c).getX();
			if(cells.get(c).getY()<ymin)
				ymin = cells.get(c).getY();
			
			if(cells.get(c).getX()>xmax)
				xmax = cells.get(c).getX();
			if(cells.get(c).getY()>ymax)
				ymax = cells.get(c).getY();
		}
		rwidth = xmax-xmin;
		rheight = ymax-ymin;
		return new Point(rwidth,rheight);
		
	}
	//Returns the lowest x and y coordinates
	protected Point getRegionLocation(BoardState origState, int cellregion)
	{
		Vector<CellLocation> cells;
		CellLocation tempcell;
		int xmin,ymin;
		xmin = ymin = 1000000;
		Region region = ((Region[])origState.getExtraData().get(0))[cellregion];
		cells = region.getCells();
		for(int c = 0; c<cells.size(); ++c)
		{
			if(origState.getCellContents(cells.get(c).getX(),cells.get(c).getY())==Heyawake.CELL_WHITE)
			{
				continue;
			}
			if(cells.get(c).getX()<xmin)
				xmin = cells.get(c).getX();
			if(cells.get(c).getY()<ymin)
				ymin = cells.get(c).getY();
		}
		return new Point(xmin,ymin);
	}
	protected String checkRuleRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		String error = null;
		ArrayList<Object> extraData = destBoardState.getExtraData();
		int regionNum;
		int height = destBoardState.getHeight();
		int width = destBoardState.getWidth();
		boolean changed = false;
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
						if(newState!=Heyawake.CELL_WHITE || origState!=Heyawake.CELL_UNKNOWN)
						{
							error = "This rule only involves adding white cells.";
						}
						//Check adjacent cells to determine if the rule was applied properly
						//If no adjacent cell is in a zigzag region that it was applied improperly.
						for(int dx = -1; dx<2;dx++)
						{
							for(int dy = -1; dy<2;dy++)
							{
								if(dx!=0 && dy!=0)
									continue;
								if(dx==0 && dy==0)
									continue;
								if((dx+x>=width) || (dx+x<0))
									continue;
								if((dy+y>=height) || (dy+y<0))
									continue;
								
								regionNum =((int[][])(extraData.get(2)))[y+dy][x+dx];
								Region curRegion = ((Region[])extraData.get(0))[regionNum];
								CellLocation rSize = curRegion.getDimensions();
								if(rSize.getX()!=2 && rSize.getY()!=2)//One side has to be of size 2
								{
									error = "This rule can only be applied next to a 2xn region.";
								}
								else if(curRegion.getValue()!=rSize.getX() && curRegion.getValue()!=rSize.getY()) //The other side has to be size n
								{
									error = "This rule can only be applied next to a 2xn region.";
								}
							}
						}
						
					}
				}
			}
		}
		if (error == null && !changed)
		{
			error = "You must add a black cell to use this rule!";
		}			
					
		return error;
	}
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		Point temprSize;
		Point temprLocation;
		ArrayList<Object> extraData = origBoardState.getExtraData();
		Region curRegion;
		int height = destBoardState.getHeight();
		int width = destBoardState.getWidth();
		
		
		if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
		{
			int regioncount = ((Integer)origBoardState.getExtraData().get(1)).intValue();
			for( int r = 0; r< regioncount; r++)//loop through all the regions.
			{
				curRegion = ((Region[])extraData.get(0))[r];
				Point size = getRegionDimension(origBoardState,r);
				Point location = getRegionLocation(origBoardState,r);
				if( ((curRegion.getValue() == size.x+1) && (size.y==1)) || ((curRegion.getValue() == size.y+1) && (size.x==1)) )
				{//If a 2xN region is found then apply the rule.
					for(int x= location.x;x<=location.x+size.x;x++)
					{
						for(int y = location.y;y<=location.y+size.y;y++)
						{
							if((x==location.x) && (y == location.y))
								continue;
							if((x==location.x) && (y == location.y+size.y))
								continue;
							if((x==location.x+size.x) && (y == location.y))
								continue;
							if((x==location.x+size.x)&& (y == location.y+size.y))
								continue;
							if(size.x == 1)
							{
								if(x == location.x)
								{
									if(((x-1) >=0)&&(origBoardState.getCellContents(x-1,y)==Heyawake.CELL_UNKNOWN))
									{
										changed = true;
										destBoardState.setCellContents(x-1,y,Heyawake.CELL_WHITE);
									}
								}
								else
								{
									if(((x+1) <width)&&(origBoardState.getCellContents(x+1,y)==Heyawake.CELL_UNKNOWN))
									{
										changed = true;
										destBoardState.setCellContents(x+1,y,Heyawake.CELL_WHITE);
									}
								}
							}
							else
							{
								if(y == location.y)
								{
									if(((y-1) >=0)&&(origBoardState.getCellContents(x,y-1)==Heyawake.CELL_UNKNOWN))
									{
										changed = true;
										destBoardState.setCellContents(x,y-1,Heyawake.CELL_WHITE);
									}
								}
								else
								{
									if(((y+1) <height)&&(origBoardState.getCellContents(x,y+1)==Heyawake.CELL_UNKNOWN))
									{
										changed = true;
										destBoardState.setCellContents(x,y+1,Heyawake.CELL_WHITE);
									}
								}
							}
						}
					}
				}
			}
			
		}
		String error = checkRuleRaw(destBoardState);
			
		if (error == null)
		{
			changed = true;
			// valid change
		}
		if(!changed)
		{
			destBoardState = origBoardState.copy();
		}
			
		return changed;
	}
}
