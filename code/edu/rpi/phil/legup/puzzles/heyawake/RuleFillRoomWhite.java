//
//  Rule2.java
//  LEGUP
//
//  Created by Drew Housten on Tues April 12 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//
//  Any cell adjacent to a tent can be declared grass


package edu.rpi.phil.legup.puzzles.heyawake;

import java.util.Vector;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleFillRoomWhite extends PuzzleRule{

    RuleFillRoomWhite()
    {
    	name = "Finish Room White";
    	description = "Remaining cells are white if number of black cells equals the clue.";
    	image = new ImageIcon("images/heyawake/rules/FillRoomWhite.png");
    }


    public void print(){
	System.out.print(name);
    }

    /**
     * Check if a tree is adjacent to this spot on the board
     * @param boardState the state of the board
     * @param x the x column number
     * @param y the y row number
     * @return true iff a tree is adjacent to this spot
     */
    
    protected boolean checkRegionHasClue(BoardState state, int cellregion)
    {
    	Region region = ((Region[])state.getExtraData().get(0))[cellregion];
    	if(region.getValue() < 0)
    		return false;
    	return true;
    }
    
    protected boolean checkBlackEqualedClue(BoardState state, int cellregion)
    {
    	int countblack, countwhite, countunknown, cellval;
    	countblack = countwhite = countunknown = 0 ;
    	Vector<CellLocation> cells;
    	CellLocation tempcell;
    	
    	Region region = ((Region[])state.getExtraData().get(0))[cellregion];
    	cells = region.getCells();
		if(cells.size() > 0)
		{
    		for(int c = 0; c < cells.size(); ++c)
    		{
    			tempcell = (CellLocation)cells.get(c);
    			cellval = state.getCellContents(tempcell.x, tempcell.y);
    			if(cellval == 1)
    			{
    				++countwhite;
    			}
    			else if(cellval == 2)
    			{
    				++countblack;
    			}
    			else
    			{
    				++countunknown;
    			}
    		}
    		if(countblack == region.getValue() && region.getValue() > -1)
    		{
    			return true;
    		}
		}
		
    	return false;
    }
    

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
						
						if (newState != 1 || origState != 0)
						{
							error = "This rule only involves adding white cells!";
							break;
						}
						
						if(!checkRegionHasClue(origBoardState, ((int[][])origBoardState.getExtraData().get(2))[y][x]))
						{
							error = "Rule cannot be applied to regions without a designated number.";
							break;
						}
						
						if(!checkBlackEqualedClue(origBoardState, ((int[][])origBoardState.getExtraData().get(2))[y][x]))
						{
							error = "A region had too many unknowns.";
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
    	
    	if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
    	{
	    	int regioncount = ((Integer)origBoardState.getExtraData().get(1)).intValue();
	    	for( int x = 0; x< regioncount; ++x)
	    	{
	    		if(checkBlackEqualedClue(destBoardState, x))
	    		{
	    			Region region = ((Region[])destBoardState.getExtraData().get(0))[x];
	    			Vector<CellLocation> cells = region.getCells();
	    			for(int y =0; y < cells.size(); ++y)
	    			{
	    				if(destBoardState.getCellContents(cells.get(y).x,cells.get(y).y) == 0)
	    				{
	    					destBoardState.setCellContents(cells.get(y).x,cells.get(y).y,1);
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
}