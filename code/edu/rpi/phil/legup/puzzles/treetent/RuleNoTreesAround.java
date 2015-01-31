//
//  Rule4.java
//  LEGUP
//
//  Created by Drew Housten on Tues April 12 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//
//  Any cell that is not adjacent to a tree can be declared grass


package edu.rpi.phil.legup.puzzles.treetent;

import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.awt.Point;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleNoTreesAround extends PuzzleRule
{
	static final long serialVersionUID = 9517L;
	public String getImageName() {return "images/treetent/noTreesAround.png";}
	public RuleNoTreesAround()
    {
		setName("Empty Field");
		description = "Any cell not next to an unlinked tree can be marked grass.";
		//image = new ImageIcon("images/treetent/noTreesAround.png");
    }
    
    private boolean hasLink(BoardState state, int x, int y)
    {
    	Point p = new Point(x,y);
    	ArrayList<Object> validLinks = state.getExtraData();
		for (int a = 0; a < validLinks.size(); ++a)
		{
			ExtraTreeTentLink e = (ExtraTreeTentLink)validLinks.get(a);
			if (e.pos1.equals(p) || e.pos2.equals(p))
			{
				return true;
			}					
		}
    	return false;
    }

    /**
     * Check if a tree is adjacent to this position
     * @param state the state of the board we're checking
     * @param x the x position we're checking
     * @param y the y position we're checking
     * @return true iff a tree is next to this position on the board
     */
    private boolean checkAdjacent(BoardState state, int x, int y)
    {
    	boolean rv = false;
    	int w = state.getWidth();
    	int h = state.getHeight();
    	
    	if (x > 0 && state.getCellContents(x-1,y) == TreeTent.CELL_TREE)
    		rv = !hasLink(state, x-1,y);
    	else if (x + 1 < w && state.getCellContents(x + 1,y) == TreeTent.CELL_TREE)
    		rv = !hasLink(state, x+1,y);
    	else if (y > 0 && state.getCellContents(x,y-1) == TreeTent.CELL_TREE)
    		rv = !hasLink(state, x,y-1);
    	else if (y + 1 < h && state.getCellContents(x,y+1) == TreeTent.CELL_TREE)
    		rv = !hasLink(state, x,y+1);

    	return rv;
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
						
						if (checkAdjacent(destBoardState,x,y))
						{
							error = "The cell at " + (char)(y + (int)'A') + "" + (x + 1)  
									+ " is next to a tree.";
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
		boolean connected;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();
		int tree_cells= 0;
		ArrayList <Object> destExtra = destBoardState.getExtraData();
		if (origBoardState != null && destBoardState.getParents().size() == 1)
		{
			for(int x = 0; x < width; ++x)
			{
				for(int y = 0; y<height;++y)
				{
					if(destBoardState.getCellContents(x,y)==TreeTent.CELL_UNKNOWN)
					{
						tree_cells=0;
						for(int i =-1;i<2;i++)
						{
							for(int j = -1;j<2;j++)
							{
								if(i==0 && j==0)
									continue;
								if((x+i)>=width || (x+i)<0)
									continue;
								if((y+j)>=height || (y+j)<0)
									continue;
								if( j!=0 && i!=0)
									continue;
									
								
								if(destBoardState.getCellContents(x+i,y+j)==TreeTent.CELL_TREE)
								{
									if(TreeTent.isLinked(destExtra, new Point(x+i,y+j)))
										continue;
									
									tree_cells++;
								}
							}
						}
						if(tree_cells==0)
						{
							System.out.println(x+" "+y);
							changed=true;
							destBoardState.setCellContents(x,y,TreeTent.CELL_GRASS);
						}
					}
				}
			}
		}
		
		String error = checkRuleRaw(destBoardState);
		if(error != null)
		{
			System.out.println(error);
			changed = false;
		}
		if(!changed)
		{
			destBoardState= origBoardState.copy();
		}
		return changed;
	}
}
