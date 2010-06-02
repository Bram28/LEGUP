package edu.rpi.phil.legup;

import java.util.Vector;

import javax.swing.ImageIcon;

/**
 * This is a generic merge rule which applies to most puzzles
 * 
 * You can merge any number of states to share their common information. This is a pretty
 * Common rules for most puzzles so it has been added automatically when you merge states
 * 
 * @author Stanley Bak
 *
 */
public final class RuleMerge extends PuzzleRule
{    
    private RuleMerge()
    {
    	name = "Merge Rule";
    	description = "Merge two or more states' common information.";
    	image = new ImageIcon("images/MergeRule.png");
    }
    
    /**
     * Return the error string, or null if it was applied correctly
     * @param state the state we're appling the rule
     * @return the error String, or null
     */
    protected String checkRuleRaw(BoardState state)
    {
    	if (state.getTransitionsTo().size() < 2)
    	{
    		return "To merge states correctly, there must be at least two parent states.";
    	}
    	else
    	{
    		Vector <BoardState> parents = state.getTransitionsTo();
    		
    		// make sure all of our information is in the parent
    		for (int i = 0; i < parents.size(); ++i)
    		{
    			BoardState parent = parents.get(i);
    			if(parent.getTransitionsFrom().size() > 1)
    				return "Parent nodes have other children!";
    			int w = state.getWidth();
    			int h = state.getHeight();
    			
    			for (int y = 0; y < h; ++y) for (int x = 0; x < w; ++x)
    			{
    				int myContents = state.getCellContents(x,y);
    				
    				if (myContents != PuzzleModule.CELL_UNKNOWN)
    				{
    					int parentContents = parent.getCellContents(x,y);
    					
    					if (myContents != parentContents) // contents vary!
    					{
    						return "The current cell at (" + (x+1) + ", " + (y+1) 
    							+ ") is different than parent #" + (i + 1) + "'s cell.";
    					}
    				}
    			}
    		}
    	}
    	
    	return null;
    }
    
    private static RuleMerge instance;
    /**
     * Retrieves the single instance of RuleMerge
     * @return The RuleMerge singleton
     */
    public static RuleMerge getInstance()
    {
    	if(instance == null)
    		instance = new RuleMerge();
    	return instance;
    }
}
