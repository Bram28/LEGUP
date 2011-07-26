//
//  Rule2.java
//  LEGUP
//
//  Created by Drew Housten on Tues April 12 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//
//  Any cell adjacent to a tent can be declared grass


package edu.rpi.phil.legup.puzzles.heyawake;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleWhiteAroundBlack extends PuzzleRule
{
    RuleWhiteAroundBlack()
    {
    	setName("White Around Black");
    	description = "Cells next to a black cell are white.";
    	image = new ImageIcon("images/heyawake/rules/WhiteAroundBlack.png");
    }
    

    public void print(){
	System.out.print(getName());
    }

    /**
     * Check if a tree is adjacent to this spot on the board
     * @param boardState the state of the board
     * @param x the x column number
     * @param y the y row number
     * @return true iff a tree is adjacent to this spot
     */
    private boolean checkAdjacent(BoardState boardState, int x, int y)
    {
    	boolean found = false;
    	
    	int xs[] = {
    					x - 1 >= 0 ? x - 1 : 0,
    					x,
    					x + 1 < boardState.getWidth() ? x + 1 : x
    				};
    	
    	int ys[] = {
				y - 1 >= 0 ? y - 1 : 0,
				y,
				y + 1 < boardState.getHeight() ? y + 1 : y
			};

    	for (int a = 0; a < xs.length && !found; ++a)
    	{
    		for (int b = 0; b < ys.length; ++b)
        	{
        		int nx = xs[a];
        		int ny = ys[b];
        		
        		if ((nx != x && ny == y) || (ny != y && nx == x))
        		{
        			if (boardState.getCellContents(nx,ny) == 2)
        			{
        				found = true;
        				break;
        			}
        		}
        	}
    	}
	
		return found;
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
						
						if (!checkAdjacent(destBoardState, x, y))
						{
							error = "There does not exist a black cell next to " 
								+ (char)(y + (int)'A') + "" + (x + 1) + ".";
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
    
    /**
     * Apply the default application of this rule
     * @param state the board we're using
     * @param pm the puzzle module we're using
     * @return true iff we have applied a rule correctly
     */
    protected boolean doDefaultApplicationRaw(BoardState destBoardState)
    {
    	BoardState origBoardState = destBoardState.getSingleParentState();
    	boolean changed = false;
    	
    	if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
    	{
	    	int w = destBoardState.getWidth();
	    	int h = destBoardState.getHeight();	    	
	    	
	    	for (int y = 0; y < h; ++y)
	    	{
	    		for (int x = 0; x < w; ++x)
	    		{
					int newState = destBoardState.getCellContents(x,y);
					
					
					if(newState == 2)
					{
						if(x + 1 < w)
						{
							if(destBoardState.getCellContents(x+1,y) == 0)
							{
								destBoardState.setCellContents(x+1,y, 1);
							}
							else if(destBoardState.getCellContents(x+1,y) == 2)
							{
								return false;
							}
						}
						if(x - 1 >= 0)
						{
							if(destBoardState.getCellContents(x-1,y) == 0)
							{
								destBoardState.setCellContents(x-1,y, 1);
							}
							else if(destBoardState.getCellContents(x-1,y) == 2)
							{
								return false;
							}
						}
						if(y+ 1 < h)
						{
							if(destBoardState.getCellContents(x,y+1) == 0)
							{
								destBoardState.setCellContents(x,y+1, 1);
							}
							else if(destBoardState.getCellContents(x,y+1) == 2)
							{
								return false;
							}
						}
						if(y - 1 >= 0)
						{
							if(destBoardState.getCellContents(x,y-1) == 0)
							{
								destBoardState.setCellContents(x,y-1, 1);
							}
							else if(destBoardState.getCellContents(x,y-1) == 2)
							{
								return false;
							}
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