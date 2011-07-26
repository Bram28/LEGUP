package edu.rpi.phil.legup.puzzles.nurikabe;

import java.awt.Point;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleUnknownSurrounded extends PuzzleRule
{
	RuleUnknownSurrounded()
	{
		setName("Fill In Black");
		description = "If there is one uknown surrounded by black, it must be black.";
		image = new ImageIcon("images/nurikabe/rules/FillInBlack.png");
	}
	protected String checkRuleRaw(BoardState destBoardState)
	{
		String error = null;
		boolean changed = false;
		BoardState origBoardState = destBoardState.getSingleParentState();
		int width = origBoardState.getWidth();
		int height = origBoardState.getHeight();
		
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
						
						
						for(int dx = -1;dx<2;dx++)
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
								
								if(origBoardState.getCellContents(x+dx,y+dy)!=Nurikabe.CELL_BLACK)
								{
									error="Black cells must be placed inside of a region of black cells.";
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
		}
		
		return error;
	}
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();
		int blackCount;
		
		if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
		{
			
			for(int x = 0; x < width; ++x)
			{
				for(int y = 0; y < height; ++y)
				{
					blackCount = 0;
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
							if(origBoardState.getCellContents(x+dx,y+dy)==Nurikabe.CELL_BLACK)
								blackCount++;
						}
					}
					if(blackCount==4)
					{
						changed = true;
						destBoardState.setCellContents(x,y,Nurikabe.CELL_BLACK);
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
