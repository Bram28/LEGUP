package edu.rpi.phil.legup.puzzles.heyawake;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
public class CaseZigZag extends CaseRule
{	
	private static final long serialVersionUID = 279685258L;
	
    public CaseZigZag()
	{
		setName("Region Zigzag");
		description = "A region with 2x(n) has only two configurations";
		image = new ImageIcon("images/heyawake/cases/BlackOrWhite.png");
	}
	public String getImageName()
	{
		return "images/heyawake/cases/BlackOrWhite.png";
	}

	public String checkCaseRuleRaw(BoardState state)
	{
		String rv = null;
		ArrayList<Object> extraData = state.getExtraData();
		int regionNum;
		int height = state.getHeight();
    	int width = state.getWidth();
		if (state.getChildren().size() != 2)
		{
			rv = "This case rule can only be applied on a two-way split.";
		}
		else
		{
			BoardState one = state.getChildren().get(0);
			BoardState two = state.getChildren().get(1);
						
			ArrayList<Point> dif = BoardState.getDifferenceLocations(one,two);
			Point p = dif.get(0);
			regionNum =((int[][])(extraData.get(2)))[p.y][p.x];
			Region curRegion = ((Region[])extraData.get(0))[regionNum];
			CellLocation rSize = curRegion.getDimensions();
			
			if(rSize.getX()!=2 && rSize.getY()!=2)
			{
				rv = "Your two-way split is only allowed to change a 2xn region.";
			}
			else if(curRegion.getValue()!=rSize.getX() && curRegion.getValue()!=rSize.getY()) 
			{
				rv = "Your two-way split is only allowed to change a 2xn region.";
			}
			else
			{
				
				for(int i = 0; i <dif.size();i++)
				{
					if(rv!=null)
						break;
					p = dif.get(i);
					if(regionNum!=((int[][])(extraData.get(2)))[p.y][p.x])
					{
						rv="Your two-way split is only allowed to change a single region.";
					}
					if (!((one.getCellContents(p.x,p.y) == Heyawake.CELL_BLACK && 
						two.getCellContents(p.x,p.y) == Heyawake.CELL_WHITE) ||
						(two.getCellContents(p.x,p.y) == Heyawake.CELL_BLACK && 
							one.getCellContents(p.x,p.y) == Heyawake.CELL_WHITE)))
					{
						rv = "In this case rule, one state's cell must be white and the other black.";
					}
					else if (state.getCellContents(p.x,p.y) != Heyawake.CELL_UNKNOWN)
					{
						rv = "The parent cells that you're applying the case rule on must be blank cells.";
					}
					if(one.getCellContents(p.x,p.y)==Heyawake.CELL_BLACK)
					{
						if(p.x-1 >=0)
						{
							if((one.getCellContents(p.x-1,p.y)==Heyawake.CELL_BLACK)&& (regionNum==((int[][])(extraData.get(2)))[p.y][p.x-1]))
								rv ="In this case rule, there must be a zigzag pattern.";
						}
						if(p.x+1 < width)
    					{
    						if((one.getCellContents(p.x+1,p.y) == Heyawake.CELL_BLACK)&& (regionNum==((int[][])(extraData.get(2)))[p.y][p.x+1]))
    							rv ="In this case rule, there must be a zigzag pattern.";
    					}
    					if(p.y+1 < height)
    					{
    						if((one.getCellContents(p.x,p.y+1) == Heyawake.CELL_BLACK)&& (regionNum==((int[][])(extraData.get(2)))[p.y+1][p.x]))
    							rv ="In this case rule, there must be a zigzag pattern.";
    					}
    					if(p.y-1 >=0)
    					{
    						if((one.getCellContents(p.x,p.y-1)==Heyawake.CELL_BLACK)&& (regionNum==((int[][])(extraData.get(2)))[p.y-1][p.x]))
    							rv ="In this case rule, there must be a zigzag pattern.";
    					}
					}
				}
			}
			
		}
			
		return rv;
	}
}
