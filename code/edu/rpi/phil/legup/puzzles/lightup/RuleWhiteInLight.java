package edu.rpi.phil.legup.puzzles.lightup;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleWhiteInLight extends PuzzleRule
{
	static final long serialVersionUID = 9502L;
	public String getImageName() {return "images/lightup/rules/WhiteInLight.png";}
	 RuleWhiteInLight()
	 {
		setName("White In Light");
		description = "Cells in light must be white.";
		image = new ImageIcon("images/lightup/rules/WhiteInLight.png");
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
		if (destBoardState.getParents().size() != 1)
		{
			error = "This rule only involves having a single branch!";
		}
		else
		{
			ArrayList<Object> extra = destBoardState.getExtraData();
			for (int y = 0; y < origBoardState.getHeight() && error == null; ++y)
			{
				for (int x = 0; x < origBoardState.getWidth(); ++x)
				{
					int origState = origBoardState.getCellContents(x,y);
					int newState = destBoardState.getCellContents(x,y);
					
					if (origState != newState)
					{
						changed = true;
						
						if (newState != LightUp.CELL_EMPTY || origState != LightUp.CELL_UNKNOWN)
						{
							error = "This rule only involves adding white cells!";
							break;
						}
						
						if(!extra.contains(new Point(x,y)))
						{
							error = "A white cell must be placed in an unknown lit cell.";
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
	    	
	    	int x = 0;
	    	int y = 0;
	    	if (origBoardState != null && destBoardState.getParents().size() == 1)
	    	{
	    		ArrayList<Object> extra = destBoardState.getExtraData();
	    		for(int cnt = 0; cnt < extra.size(); ++cnt)
	    		{
	    			x = ((Point)extra.get(cnt)).x;
	    			y = ((Point)extra.get(cnt)).y;
	    			if( destBoardState.getCellContents(x,y) == 0)
	    			{
	    				destBoardState.setCellContents(x,y, LightUp.CELL_EMPTY);
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
	 
}
//SVN Test