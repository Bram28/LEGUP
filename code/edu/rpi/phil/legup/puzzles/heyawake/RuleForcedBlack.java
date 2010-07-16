package edu.rpi.phil.legup.puzzles.heyawake;

import java.util.Vector;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import java.awt.Point;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
public class RuleForcedBlack extends PuzzleRule
{
	RuleForcedBlack()
	{
		name = "Black Path";
		description = "A path of length 2*n-1 has only one configuration.";
		image = new ImageIcon("images/heyawake/rules/WhiteAroundBlack.png");
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
	protected int getRegionValue(BoardState origState,int cellregion)
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
		int value = region.getValue();
		for(int c = 0; c<cells.size(); ++c)
		{
			if(origState.getCellContents(cells.get(c).getX(),cells.get(c).getY())==Heyawake.CELL_BLACK)
			{
				value--;
			}
		}
		return value;
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
			if(origState.getCellContents(cells.get(c).getX(),cells.get(c).getY())==Heyawake.CELL_UNKNOWN)
			{
				if((cells.get(c).getX()<xmin)&&(cells.get(c).getY()<ymin))
				{
					xmin = cells.get(c).getX();
					ymin = cells.get(c).getY();
				}
			}
		}
		return new Point(xmin,ymin);
	}
	protected String checkRuleRaw(BoardState destBoardState)
	{
		String error = null;
		boolean changed = false;
		int regionNum;
		BoardState origBoardState = destBoardState.getSingleParentState();
		int height =origBoardState.getHeight();
		int width = origBoardState.getWidth();
		ArrayList<Object> extraData = origBoardState.getExtraData();
		Region curRegion;
		Point dimPoint;
		Point locPoint;
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
						if (newState == Heyawake.CELL_UNKNOWN || origState != Heyawake.CELL_UNKNOWN)
						{
							error = "This rule only involves adding black and white cells!";
							break;
						}
						
						if(!checkRegionHasClue(origBoardState, ((int[][])origBoardState.getExtraData().get(2))[y][x]))
						{
							error = "Rule cannot be applied to regions without a designated number.";
							break;
						}
						int maxconnect=0;
						
						regionNum = ((int[][])origBoardState.getExtraData().get(2))[y][x];
						curRegion = ((Region[])extraData.get(0))[regionNum];
						Vector<CellLocation> cells = curRegion.getCells();
						for(int i = 0; i<cells.size();i++)
						{
							int curconnect;
							if((curconnect=loopConnected(new int[width][height], origBoardState,x,y,regionNum))>maxconnect)
								maxconnect = curconnect;
						}
						int regionValue = getRegionValue(origBoardState,regionNum);
						if(maxconnect ==((2*regionValue)-1))
						{
							BoardState altBoard = origBoardState.copy();
							find_pattern(altBoard,regionNum,regionValue);
							if(altBoard.getCellContents(x,y)!=destBoardState.getCellContents(x,y))
							{
								error = "Rule you must fill a region with a specific pattern of black squares, and surround those squares with white.";
								break;
							}
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
	protected boolean find_pattern(BoardState board, int regionNum, int value)
	{
		Point cell = getRegionLocation(board,regionNum);
		if(cell.x==1000000 || cell.y==1000000)
			return false;
		if(make_black(board.copy(),cell.x,cell.y,value-1,regionNum)==true)
		{
			make_black(board,cell.x,cell.y,value-1,regionNum);
		}
		else
		{
			board.setCellContents(cell.x,cell.y,Heyawake.CELL_WHITE);
			cell = getRegionLocation(board,regionNum);
			if(cell.x==1000000 || cell.y==1000000)
				return false;
			make_black(board,cell.x,cell.y,value-1,regionNum);
		}
		return true;
	}
	protected boolean make_black(BoardState board, int x, int y, int value,int regionNum)
	{
		int width = board.getWidth();
		int height =board.getHeight();
		int curReg;
		board.setCellContents(x,y,Heyawake.CELL_BLACK);
		for(int i = 0;i<2;i++)
		{
			for(int j = 0; j<2;j++)
			{
				if((i+x)>=width || (i+x)<0)
					continue;
				if((j+y)>=height || (j+y)<0)
					continue;
				if(i!=0 && j!=0)
					continue;
				if(i==0 && j==0)
					continue;
				curReg = ((int[][])board.getExtraData().get(2))[y+j][x+i];
				if(board.getCellContents(x+i,y+j)==Heyawake.CELL_UNKNOWN && (curReg==regionNum))
				{
					board.setCellContents(x+i,y+j,Heyawake.CELL_WHITE);
				}
			}
		}
		
		if(value==0)
		{
			Point cell = getRegionLocation(board,regionNum);
			if(cell.x==1000000 || cell.y==1000000)
				return true;
			else
				return false;
		}
		else
			return find_pattern(board,regionNum,value);
	}
	//Returns the number of connected unknown cells.
	private int loopConnected(int[][] visited,BoardState boardState, int x, int y,int region)
	{
		//If we have been here before return nothing
		if(visited[x][y] == 1)
			return 0;
		
		//Set the cell to visited but not white
		visited[x][y] = 1;
		
		//If the cell is black return nothing
		if(boardState.getCellContents(x,y) == Heyawake.CELL_BLACK)
			return 0;
		if(boardState.getCellContents(x,y) == Heyawake.CELL_WHITE)
			return 0;
		if(region!= ((int[][])boardState.getExtraData().get(2))[y][x])
			return 0;
		
		int width = boardState.getWidth();
		int height= boardState.getHeight();
		
		//A counter of how many unknowns/whites we have found
		int ret = 1;

		//Temporary integer
		int temp;
		
		//Essentially if there is a cell in the adjacent direction, see how many unknowns we can find from it
		//Return the sum of those unknowns if we didn't find a -1
		if(x+1 < width)
		{
			temp = loopConnected(visited, boardState, x+1, y,region);
			ret += temp;
		}
		if(x-1 >= 0)
		{
			temp = loopConnected(visited, boardState, x-1, y,region);
			ret += temp;
		}
		if(y+1 < height)
		{
			temp = loopConnected(visited, boardState, x, y+1,region);
			ret += temp;
		}
		if(y-1 >= 0)
		{
			temp = loopConnected(visited, boardState, x, y-1,region);
			ret += temp;
		}
		return ret;
	}
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		Point temprSize;
		Point temprLocation;
		ArrayList<Object> extraData = origBoardState.getExtraData();
		Region curRegion;
		int height = origBoardState.getHeight();
		int width = origBoardState.getWidth();
		System.out.println("Yo");
		if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
		{
			int regioncount = ((Integer)origBoardState.getExtraData().get(1)).intValue();
			for( int r = 0; r< regioncount; r++)
			{
				curRegion = ((Region[])extraData.get(0))[r];
				Point size = getRegionDimension(destBoardState,r);
				Point location = getRegionLocation(destBoardState,r);
				int value = getRegionValue(destBoardState,r);
				Vector<CellLocation> cells = curRegion.getCells();
				int maxconnect = 0;
				for(int i = 0; i<cells.size();i++)
				{
					int curconnect;
					if((curconnect=loopConnected(new int[width][height], origBoardState,cells.get(i).x,cells.get(i).y,r))>maxconnect)
						maxconnect = curconnect;
				}
				int regionValue = getRegionValue(origBoardState,r);
				if(maxconnect ==((2*regionValue)-1))
				{
					if(find_pattern(destBoardState.copy(),r,regionValue)==true)
					{
						find_pattern(destBoardState,r,regionValue);
						changed =true;
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
