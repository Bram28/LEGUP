package edu.rpi.phil.legup.puzzles.heyawake;

import java.util.Vector;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import java.awt.Point;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
public class Rule3x3 extends PuzzleRule
{
	private static final long serialVersionUID = 141809156L;

	Rule3x3()
	{
		setName("3x3");
		description = "One possible combination in a region 3x3 with a value of 5";
		image = new ImageIcon("images/heyawake/rules/FillRoomBlack.png");
	}
	public String getImageName()
	{
		return "images/heyawake/rules/FillRoomBlack.png";
	}
	protected boolean checkRegionHasClue(BoardState state, int cellregion)
	{
		Region region = ((Region[])state.getExtraData().get(0))[cellregion];
		if(region.getValue() < 0)
			return false;
		return true;
	}
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
		String error = null;
		boolean changed = false;
		int regionNum;
		BoardState origBoardState = destBoardState.getSingleParentState();
		ArrayList<Object> extraData = origBoardState.getExtraData();
		Region curRegion;
		Point tempPoint;
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
						
						if (newState != 2 || origState != 0)
						{
							error = "This rule only involves adding black cells!";
							break;
						}
						
						if(!checkRegionHasClue(origBoardState, ((int[][])origBoardState.getExtraData().get(2))[y][x]))
						{
							error = "Rule cannot be applied to regions without a designated number.";
							break;
						}
						regionNum =((int[][])(extraData.get(2)))[y][x];
						curRegion = ((Region[])extraData.get(0))[regionNum];
						if(curRegion.getValue()!=5)
						{
							error = "Rule cannot be applied to regions without a value of 5.";
						}
						tempPoint= getRegionDimension(origBoardState,regionNum);
						if(tempPoint.x!=2 || tempPoint.y!=2)
						{
							error = "Rule can only be applied to regions with dimension 3x3.";
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
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		Point temprSize;
		Point temprLocation;
		ArrayList<Object> extraData = origBoardState.getExtraData();
		Region curRegion;
		
		if (origBoardState != null && destBoardState.getParents().size() == 1)
		{
			int regioncount = ((Integer)origBoardState.getExtraData().get(1)).intValue();
			for( int r = 0; r< regioncount; r++)
			{
				curRegion = ((Region[])extraData.get(0))[r];
				if(curRegion.getValue() == 5)
				{
					Point size = getRegionDimension(destBoardState,r);
					Point location = getRegionLocation(destBoardState,r);
					if(size.x == 2 && size.y == 2)
					{
						if(origBoardState.getCellContents(location.x,location.y)==Heyawake.CELL_UNKNOWN)
						{
							changed = true;
							destBoardState.setCellContents(location.x,location.y,Heyawake.CELL_BLACK);
							destBoardState.setCellContents(location.x+2,location.y,Heyawake.CELL_BLACK);
							destBoardState.setCellContents(location.x+1,location.y+1,Heyawake.CELL_BLACK);
							destBoardState.setCellContents(location.x,location.y+2,Heyawake.CELL_BLACK);
							destBoardState.setCellContents(location.x+2,location.y+2,Heyawake.CELL_BLACK);
						}
						
					}
				}
			}
		}
			
			
		String error = checkRuleRaw(destBoardState);
		if (error != null)
		{
			System.out.println(error+" "+changed);
			changed = false;
			// valid change
		}
		if(!changed)
		{
			destBoardState = origBoardState.copy();
		}
			
		return changed;	
			
	}		
			
			
			
			
			
}
