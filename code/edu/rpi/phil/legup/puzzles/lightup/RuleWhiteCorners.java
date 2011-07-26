package edu.rpi.phil.legup.puzzles.lightup;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleWhiteCorners extends PuzzleRule
{	 
	 RuleWhiteCorners()
	 {
		setName("White Corners");
		description = "Cells on the corners of a number must be white if they would prevent the number from acheiving its lights.";
		image = new ImageIcon("images/lightup/rules/WhiteCorners.png");
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
    	int width = destBoardState.getWidth();
    	int height = destBoardState.getHeight();
    	boolean foundvalidblock = false;
    	NumberedBlock block;
    	boolean[][] litup = new boolean[height][width];
    	LightUp.determineLight(destBoardState, litup);
    	int blanks;
    	
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
					foundvalidblock = false;
					
					if (origState != newState)
					{
						changed = true;
						
						if (newState != LightUp.CELL_BLANK || origState != LightUp.CELL_UNKNOWN)
						{
							error = "This rule only involves adding white cells!";
							break;
						}
						
						foundvalidblock = this.willBlockLights(destBoardState, x, y, litup);
						
						
						
						if(!foundvalidblock)
						{
							error = "A white cell must be placed because if it was a light it would block a number.";
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
	    	NumberedBlock block;
	    	boolean[][] litup = new boolean[height][width];
	    	LightUp.determineLight(destBoardState, litup);
	    	
	    	if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
	    	{
	    		for (int y = 0; y < origBoardState.getHeight(); ++y)
				{
					for (int x = 0; x < origBoardState.getWidth(); ++x)
					{
						if(destBoardState.getCellContents(x,y) == LightUp.CELL_UNKNOWN)
						{
							if(this.willBlockLights(destBoardState, x, y, litup))
							{
								destBoardState.setCellContents(x, y, LightUp.CELL_BLANK);
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
	    	}
	    	
	    	if(!changed)
	    	{
	    		destBoardState = origBoardState.copy();
	    	}
		    	
		    return changed;
	  }
	 
	 protected boolean willBlockLights(BoardState state, int x, int y, boolean[][] litup)
	 {
		 int width = state.getWidth();
		 int height = state.getHeight();
		 NumberedBlock block;
		 int blanks;
		 
		 if(x > 0)
			{
				if(y > 0)
				{
					if(state.getCellContents(x-1,y-1)>= 10 && state.getCellContents(x-1,y-1)< 15)
					{
						block = new NumberedBlock(state,x-1,y-1);
						blanks = block.getUnNeededBlanks();
						if(state.getCellContents(x, y-1) == LightUp.CELL_UNKNOWN && !litup[y-1][x])
							--blanks;
						if(state.getCellContents(x-1, y) == LightUp.CELL_UNKNOWN && !litup[y][x-1])
							--blanks;
						if(blanks < 0)
							return true;
					}
				}
				if(y < height - 1)
				{
					if(state.getCellContents(x-1,y+1)>= 10 && state.getCellContents(x-1,y+1)< 15)
					{
						block = new NumberedBlock(state,x-1,y+1);
						blanks = block.getUnNeededBlanks();
						if(state.getCellContents(x, y+1) == LightUp.CELL_UNKNOWN && !litup[y+1][x])
							--blanks;
						if(state.getCellContents(x-1, y) == LightUp.CELL_UNKNOWN && !litup[y][x-1])
							--blanks;
						if(blanks < 0)
							return true;
					}
				}
			}

			if(x < width - 1)
			{
				if(y > 0)
				{
					if(state.getCellContents(x+1,y-1)>= 10 && state.getCellContents(x+1,y-1)< 15)
					{
						block = new NumberedBlock(state,x+1,y-1);
						blanks = block.getUnNeededBlanks();
						if(state.getCellContents(x, y-1) == LightUp.CELL_UNKNOWN && !litup[y-1][x])
							--blanks;
						if(state.getCellContents(x+1, y) == LightUp.CELL_UNKNOWN && !litup[y][x+1])
							--blanks;
						if(blanks < 0)
							return true;
					}
				}
				if(y < height - 1)
				{
					if(state.getCellContents(x+1,y+1)>= 10 && state.getCellContents(x+1,y+1)< 15)
					{
						block = new NumberedBlock(state,x+1,y+1);
						blanks = block.getUnNeededBlanks();
						if(state.getCellContents(x, y+1) == LightUp.CELL_UNKNOWN && !litup[y+1][x])
							--blanks;
						if(state.getCellContents(x+1, y) == LightUp.CELL_UNKNOWN && !litup[y][x+1])
							--blanks;
						if(blanks < 0)
							return true;
					}
				}
			}
			return false;
	 }
}
