package edu.rpi.phil.legup.puzzles.heyawake;

import java.util.Vector;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import java.awt.Point;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
public class RuleOneRow extends PuzzleRule
{
	private static final long serialVersionUID = 480985280L;

	RuleOneRow()
	{
		setName("One Row");
		description = "One possible combination in a region 1x(n+1)";
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
						
						tempPoint= getRegionDimension(origBoardState,regionNum);
						if(tempPoint.x!=0 && tempPoint.y!=0)
						{
							error = "Rule cannot be applied to region without a single row/column.";
							break;
						}
						if((tempPoint.x+1)!=(2*curRegion.getValue()-1) && (tempPoint.y+1)!=(2*curRegion.getValue()-1))
						{
							error = "Rule cannot be applied to region without a dimension of 2*value-1.";
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
				temprSize= getRegionDimension(origBoardState,r);
				temprLocation = getRegionLocation(origBoardState,r);
				curRegion = ((Region[])extraData.get(0))[r];
				
				if(curRegion.getValue()<=0)
					continue;
				if(temprSize.x==0 || temprSize.y == 0)
					System.out.println(temprSize+" "+temprLocation+" "+(2*curRegion.getValue()-1));
				if((temprSize.x == 0) && ((temprSize.y+1) ==(2*curRegion.getValue()-1)))
				{
					for(int y = temprLocation.y; (y-temprLocation.y)<=temprSize.y;y+=2)
					{
						changed = true;
						destBoardState.setCellContents(temprLocation.x,y,Heyawake.CELL_BLACK);
					}
				}
				else if((temprSize.y ==0) && ((temprSize.x+1) ==(2*curRegion.getValue()-1)))
				{
					for(int x = temprLocation.x; (x-temprLocation.x)<=temprSize.x;x+=2)
					{
						destBoardState.setCellContents(x,temprLocation.y,Heyawake.CELL_BLACK);
						changed = true;
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
			System.out.println("what");
			destBoardState = origBoardState.copy();
		}
			
		return changed;
	}
}
