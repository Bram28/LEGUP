//
//  Rule1.java
//  LEGUP
//
//  Created by Drew Housten on Tues April 12 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//
//  If the number of tents in a row or column have been declared, any
//  other cell in that row or column can be declared grass



package edu.rpi.phil.legup.puzzles.treetent;
import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleAllGrass extends PuzzleRule
{
	static final long serialVersionUID = 9511L;
	public String getImageName() {return "images/treetent/finishGrass.png";}
    public RuleAllGrass()
    {
    	setName("Finish Grass");
    	description = "Grass can be added to finish a row or column that has reached its tent limit.";
    	//image = new ImageIcon("images/treetent/finishGrass.png");
    }


    private boolean checkRow(BoardState boardState, int y)
    {
		int width = boardState.getWidth();
		int label = boardState.getLabel(BoardState.LABEL_RIGHT, y);
		int numTents = TreeTent.translateNumTents(label);
		
		//Iterate through each cell in this row
		for (int i = 0; i < width; i++)
		{    
			//If the cell is a tent, subtract from the total tents remaining
			if (boardState.getCellContents(i,y) == 2)
			{
			    numTents--;
			}
		}
	
		//True, if no more tents are remaining.
		return numTents == 0;
    }

    private boolean checkCol(BoardState boardState, int x)
    {
		int height = boardState.getHeight();
		int numTents = TreeTent.translateNumTents(boardState.getLabel(BoardState.LABEL_BOTTOM, x));
		
		//Iterate through each cell in this column
		for (int i = 0; i < height; i++)
		{
			if (boardState.getCellContents(x,i) == 2)
			{
			    numTents--;
			}
		}
		
		//True, if no more tents are remaining
		return numTents == 0;
    }

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
						
						if (newState != TreeTent.CELL_GRASS || origState != TreeTent.CELL_UNKNOWN)
						{
							error = "This rule only involves adding grass!";
							break;
						}
						
						if (!checkRow(destBoardState, y) && !checkCol(destBoardState, x))
						{
							error = "Neither the row nor column at " + (char)(y + (int)'A') + "" + (x + 1)  
									+ " has its maximum number of tents.";
							break;
						}
					}
				}
			}
			
			if (error == null && !changed)
			{
				error = "You must add grass to use this rule!";
			}
		}
		
		TreeTent.setAnnotations(destBoardState);
		
		return error;
	}
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
	    	int width = destBoardState.getWidth();
	    	int height = destBoardState.getHeight();
	    	
	    	if (origBoardState != null && destBoardState.getParents().size() == 1)
	    	{
	        	for(int x = 0; x < width; ++x)
	        	{
	        		int num_empty = 0;
	    			int total = TreeTent.translateNumTents(destBoardState.getLabel(BoardState.LABEL_BOTTOM, x));
	        		for(int y = 0; y < height; ++y)
	    			{
	    				if(destBoardState.getCellContents(x, y)==TreeTent.CELL_TENT)
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
		    					destBoardState.setCellContents(x, y, TreeTent.CELL_GRASS);
		    					changed = true;
		    				}
		    			}
		    		}
	        	}
	        	for(int y = 0; y < height; ++y)
	        	{
	        		int num_empty = 0;
	    			int total = TreeTent.translateNumTents(destBoardState.getLabel(BoardState.LABEL_RIGHT, y));
	        		for(int x = 0; x < width; ++x)
	    			{
	    				if(destBoardState.getCellContents(x, y)==TreeTent.CELL_TENT)
	    				{
	    					num_empty++;
	    				}
	    			}
	        		if(total==num_empty)
		    		{
		    			for(int x = 0; x < width; ++x)
		    			{
		    				if(destBoardState.getCellContents(x, y)==0)
		    				{
		    					destBoardState.setCellContents(x, y, TreeTent.CELL_GRASS);
		    					changed = true;
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
	    	}
	    	
	    	if(!changed)
	    	{
	    		destBoardState = origBoardState.copy();
	    	}
		    	
		    return changed;
	}
}
