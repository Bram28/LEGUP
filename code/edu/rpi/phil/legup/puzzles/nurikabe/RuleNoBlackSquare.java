package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleNoBlackSquare extends PuzzleRule
{	 
	private static final long serialVersionUID = 28206759L;
	
	 RuleNoBlackSquare()
	 {
		setName("Prevent Black Square");
		description = "There cannot be a 2x2 square of black. (3 blacks = fill in last corner white)";
		image = new ImageIcon("images/nurikabe/rules/NoBlackSquare.png");
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
						
						if(!checkPreventsSquare(destBoardState, x,y, destBoardState.getWidth(), destBoardState.getHeight()))
						{
							error = "White cell must be placed within a 2x2 box which contains 3 black cells.";
							break;
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
	 
	 protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	  {
		 BoardState origBoardState = destBoardState.getSingleParentState();
	    	boolean changed = false;
	    	int width = destBoardState.getWidth();
	    	int height = destBoardState.getHeight();
	    	
	    	
	    	if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
	    	{
	        	for(int x = 0; x < width - 1; ++x)
	        	{
	        		for(int y = 0; y < height - 1; ++y)
	        		{
	        			if(destBoardState.getCellContents(x,y) == Nurikabe.CELL_BLACK && destBoardState.getCellContents(x + 1,y) == Nurikabe.CELL_BLACK && destBoardState.getCellContents(x,y + 1) == Nurikabe.CELL_BLACK)
	        			{
	        				if(destBoardState.getCellContents(x+1,y+1)>0)
	        					continue;
	        				destBoardState.setCellContents(x + 1,y + 1, Nurikabe.CELL_WHITE);
	        			}
	        			else if(destBoardState.getCellContents(x,y) == Nurikabe.CELL_BLACK && destBoardState.getCellContents(x + 1,y) == Nurikabe.CELL_BLACK && destBoardState.getCellContents(x+1,y + 1) == Nurikabe.CELL_BLACK)
	        			{
	        				if(destBoardState.getCellContents(x,y+1)>0)
	        					continue;
	        				destBoardState.setCellContents(x,y + 1, Nurikabe.CELL_WHITE);
	        			}
	        			else if(destBoardState.getCellContents(x,y) == Nurikabe.CELL_BLACK && destBoardState.getCellContents(x + 1,y + 1) == Nurikabe.CELL_BLACK && destBoardState.getCellContents(x,y + 1) == Nurikabe.CELL_BLACK)
	        			{
	        				if(destBoardState.getCellContents(x+1,y)>0)
	        					continue;
	        				destBoardState.setCellContents(x + 1,y, Nurikabe.CELL_WHITE);
	        			}
	        			else if(destBoardState.getCellContents(x+1,y+1) == Nurikabe.CELL_BLACK && destBoardState.getCellContents(x + 1,y) == Nurikabe.CELL_BLACK && destBoardState.getCellContents(x,y + 1) == Nurikabe.CELL_BLACK)
	        			{
	        				if(destBoardState.getCellContents(x,y)>0)
	        					continue;
	        				destBoardState.setCellContents(x,y, Nurikabe.CELL_WHITE);
	        			}      				
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
	 
	 
	 private boolean checkPreventsSquare(BoardState state, int x, int y, int width, int height)
	 {
		 boolean ret = false;
		 
		 if(x > 0)
		 {
			 if(y > 0)
				 ret = ret | (state.getCellContents(x - 1,y) == Nurikabe.CELL_BLACK && state.getCellContents(x - 1,y - 1) == Nurikabe.CELL_BLACK && state.getCellContents(x,y - 1) == Nurikabe.CELL_BLACK);
			 if(y < height - 1)
				 ret = ret | (state.getCellContents(x - 1,y) == Nurikabe.CELL_BLACK && state.getCellContents(x - 1,y + 1) == Nurikabe.CELL_BLACK && state.getCellContents(x,y + 1) == Nurikabe.CELL_BLACK);
		 }
		 if(x < width - 1)
		 {
			 if(y > 0)
				 ret = ret | (state.getCellContents(x + 1,y) == Nurikabe.CELL_BLACK && state.getCellContents(x + 1,y - 1) == Nurikabe.CELL_BLACK && state.getCellContents(x,y - 1) == Nurikabe.CELL_BLACK);
			 if(y < height - 1)
				 ret = ret | (state.getCellContents(x + 1,y) == Nurikabe.CELL_BLACK && state.getCellContents(x + 1,y + 1) == Nurikabe.CELL_BLACK && state.getCellContents(x,y + 1) == Nurikabe.CELL_BLACK);
		 }
		 
		 return ret;
	 }
}
