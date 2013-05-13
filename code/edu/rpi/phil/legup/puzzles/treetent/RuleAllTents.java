//
//  Rule2.java
//  LEGUP
//
//  Created by Drew Housten on Tues April 12 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//
//  If there are a number of unknown cells in any row or column that equals
//  the number of tents left to place, then they can be declared a tent


package edu.rpi.phil.legup.puzzles.treetent;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleAllTents extends PuzzleRule{

	public String getImageName() {return "images/treetent/finishTent.png";}
	public RuleAllTents()
    {
		setName("Finish Tents");
		description = "Tents can be added to finish a row or column that has one open spot per required tent.";
		//image = new ImageIcon("images/treetent/finishTent.png");
    }

    
    /**
     * Count the number of occurances of a certain cell type in a row or column
     * @param state the BoardState we're counting on
     * @param givenX true iff we're counting a column (we're given an x), false = row
     * @param type the type of cell we're counting, like TreeTent.CELL_GRASS
     * @param num the column or row number
     * @return the occrances of the cell type in the given row or column
     */
    private int countCell(BoardState state, boolean givenX, int type, int num)
    {
    	int rv = 0;
    	
    	int x = 0, y = 0;
    	
    	if (givenX)
    		x = num;
    	else
    		y = num;
    	
    	while (true)
    	{	
    		if (state.getCellContents(x,y) == type)
    			++rv;
    		
    		if (givenX)
    		{
    			++y;
    			if (y == state.getHeight())
    				break;
    		}
    		else
    		{
    			++x;
    			if (x == state.getWidth())
    				break;
    		}
    	}
    		
    	return rv;
    }
    
    /**
     * Check if adding a tent here is a valid application of this rule
     * @param state the destination board state
     * @param x the x position of the new tent
     * @param y the y position of the new tent
     * @return true iff we have a valid application of the rule
     */
    public boolean checkLegal(BoardState state, int x, int y)
    {
    	boolean rv = false;
    	int numRow = TreeTent.translateNumTents(state.getLabel(BoardState.LABEL_RIGHT, y));
    	int numCol = TreeTent.translateNumTents(state.getLabel(BoardState.LABEL_BOTTOM, x));
    	
    	int spotsCol = countCell(state,true,TreeTent.CELL_UNKNOWN,x);
    	int spotsRow = countCell(state,false,TreeTent.CELL_UNKNOWN,y);
    	
    	int tentsCol = countCell(state,true,TreeTent.CELL_TENT,x);
    	int tentsRow = countCell(state,false,TreeTent.CELL_TENT,y);
    	
    	if (numRow == spotsRow + tentsRow || numCol == spotsCol + tentsCol)
    		rv = true;
    	
    	return rv;
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
		else if (!destBoardState.getExtraData().equals(origBoardState.getExtraData()))
		{
			error = "This rule does not involve changing tree-tent links.";
		}
		else
		{
			// For each cell, check if the row or column has a sufficient number of tents in it
			
			for (int y = 0; y < origBoardState.getHeight() && error == null; ++y)
			{
				for (int x = 0; x < origBoardState.getWidth(); ++x)
				{
					int origState = origBoardState.getCellContents(x,y);
					int newState = destBoardState.getCellContents(x,y);
					
					if (origState != newState)
					{
						changed = true;						
						
						if (newState != TreeTent.CELL_TENT || origState != TreeTent.CELL_UNKNOWN)
						{
							error = "This rule only involves adding tents!";
							break;
						}
						
						if (!checkLegal(destBoardState,x,y))
						{
							error = "Neither the row nor column at " 
								+ String.valueOf((char)(y + (int)'A')) 
								+ "" + (x + 1) + " has its maximum number of non-tents.";
							break;
						}
					}
				}
			}
			
			if (error == null && !changed)
			{
				error = "You must add some tents to use this rule2!";
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
	        	for(int x = 0; x < width; ++x)
	        	{
	        		int num_empty = 0;
	    			int total = TreeTent.translateNumTents(destBoardState.getLabel(destBoardState.LABEL_BOTTOM, x));
	        		for(int y = 0; y < height; ++y)
	    			{
	    				if(destBoardState.getCellContents(x, y)==0 || destBoardState.getCellContents(x, y)==TreeTent.CELL_TENT)
	    				{
	    					num_empty++;
	    				}
	    			}
	        		if(total==num_empty)
		    		{
		    			//System.out.println(total + "=" + num_empty);
		    			for(int y = 0; y < height; ++y)
		    			{
		    				if(destBoardState.getCellContents(x, y)==0)
		    				{
		    					destBoardState.setCellContents(x, y, TreeTent.CELL_TENT);
		    					changed = true;
		    				}
		    			}
		    		}
	        	}
	        	for(int y = 0; y < height; ++y)
	        	{
	        		int num_empty = 0;
	    			int total = TreeTent.translateNumTents(destBoardState.getLabel(destBoardState.LABEL_RIGHT, y));
	        		for(int x = 0; x < width; ++x)
	    			{
	    				if(destBoardState.getCellContents(x, y)==TreeTent.CELL_UNKNOWN || destBoardState.getCellContents(x, y)==TreeTent.CELL_TENT)
	    				{
	    					num_empty++;
	    				}
	    			}
	        		if(total==num_empty && total>0)
		    		{
		    			for(int x = 0; x < width; ++x)
		    			{
		    				if(destBoardState.getCellContents(x, y)==TreeTent.CELL_UNKNOWN)
		    				{
		    					destBoardState.setCellContents(x, y, TreeTent.CELL_TENT);
		    					changed = true;
		    				}
		    			}
		    		}
	        	}
	        	
		    	
		    	
				
	    	}
	    	String error = checkRuleRaw(destBoardState);
	    	if (error != null)
				{
					System.out.println(error);
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
