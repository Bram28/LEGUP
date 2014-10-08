package edu.rpi.phil.legup.puzzles.masyu;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleFinishPath extends PuzzleRule
{	 
	private static final long serialVersionUID = 246674040L;
	
	
	/**
	 * Rule to make a path continuous between different cells.
	 */
	 RuleFinishPath()
	 {
		setName("Continue Path");
		description = "Connected cells - there exists only one path.";
		image = new ImageIcon("images/masyu/Rules/RuleFinishPath.png");
	 }
	 
	 /**
     * Checks to see if the rule was correctly applied
     * For this rule, for each added there must be a line on the other side
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
			/*
			for (int y = 0; y < origBoardState.getHeight() && error == null; ++y)
			{
				for (int x = 0; x < origBoardState.getWidth(); ++x)
				{
					int origState = origBoardState.getCellContents(x,y);
					int newState = destBoardState.getCellContents(x,y);
					
					if (origState != newState)
					{
						changed = true;
						
						//determine just what was changed
						int amount = origState ^ newState;
						
						// check for invalid line removals
						if(!Masyu.onlyAdds(newState,origState))
							error = "You cannot remove lines!";
						
						// check each difference - make it matches on the other side
						if(Masyu.hasNorth(amount))
							if(!Masyu.hasSouth(origBoardState.getCellContents(x,y-1)))
								error = "You must have a matching line on the other side";
						if(Masyu.hasSouth(amount))
							if(!Masyu.hasNorth(origBoardState.getCellContents(x,y+1)))
								error = "You must have a matching line on the other side";
						if(Masyu.hasEast(amount))
							if(!Masyu.hasWest(origBoardState.getCellContents(x+1,y)))
								error = "You must have a matching line on the other side";
						if(Masyu.hasWest(amount))
							if(!Masyu.hasEast(origBoardState.getCellContents(x-1,y)))
								error = "You must have a matching line on the other side";
					}
				}
			}
			*/
			if (error == null && !changed)
			{
				error = "You must change something to use this rule!";
			}
		}
		
		return error;
    }
   
     
    /**
     * Tries to apply the rule everywhere and returns true if it can do so.
     * 
     * @author Bryan
     * @param destBoardState the board to work with
     * @param pm the puzzle module
     * @see edu.rpi.phil.legup.PuzzleRule#doDefaultApplicationRaw(edu.rpi.phil.legup.BoardState, edu.rpi.phil.legup.PuzzleModule)
     */
    protected boolean doDefaultApplicationRaw(BoardState destBoardState)
    {
		BoardState origBoardState = destBoardState.getSingleParentState();
    	boolean changed = false;
    	int width = destBoardState.getWidth();
    	int height = destBoardState.getHeight();
    	
    	int destValue;
    	
    	if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
    	{
    		/*for(int x = 0; x < width; x++)
    		{
    			for(int y = 0; y < height; y++)
    			{
    				destValue = destBoardState.getCellContents(x,y);
    				int oldDestValue = destValue;
    				
    				
    				if(x < width-1 && Masyu.hasWest(origBoardState.getCellContents(x+1,y)))
    					destValue |= Masyu.EAST;
    				if(x > 0 && Masyu.hasEast(origBoardState.getCellContents(x-1,y)))
    					destValue |= Masyu.WEST;
    				if(y < height-1 && Masyu.hasNorth(origBoardState.getCellContents(x, y+1)))
    					destValue |= Masyu.SOUTH;
    				if(y > 0 && Masyu.hasSouth(origBoardState.getCellContents(x, y-1)))
    					destValue |= Masyu.NORTH;
    				//update if changed
    				if(oldDestValue != destValue)
    				{
    					destBoardState.setCellContents(x, y, destValue);
    					changed = true;
    				}
    			}
    		}*/
    	}
    	
    	if(!changed)
    	{
    		destBoardState = origBoardState.copy();
    	}
	    	
	    return changed;
    }
}
