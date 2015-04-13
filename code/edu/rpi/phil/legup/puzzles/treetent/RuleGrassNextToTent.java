//
//  Rule2.java
//  LEGUP
//
//  Created by Drew Housten on Tues April 12 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//
//  Any cell adjacent to a tent can be declared grass


package edu.rpi.phil.legup.puzzles.treetent;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleGrassNextToTent extends PuzzleRule
{
	static final long serialVersionUID = 9513L;
	public String getImageName() {return "images/treetent/aroundTent.png";}
    public RuleGrassNextToTent()
    {
    	setName("Surround Tent with Grass");
    	description = "Cells next to a tent are grass.";
    	//image = new ImageIcon("images/treetent/aroundTent.png");
    }


    public void print()
    {
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

    	//clamp the adjacent cell coordinates to the board
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

    	//loop through all adjacent and diagonal cells from the current cells
    	for (int a = 0; a < xs.length && !found; ++a)
    	{
    		for (int b = 0; b < ys.length; ++b)
        	{
    			//Get the cell's coordinates
        		int nx = xs[a];
        		int ny = ys[b];

        		//Skip if the cell is the same as the current one
        		if (nx != x || ny != y)
        		{
        			if (boardState.getCellContents(nx,ny) == TreeTent.CELL_TENT)
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

						if (!checkAdjacent(destBoardState, x, y))
						{
							error = "There does not exist a tent next to "
								+ (char)(y + (int)'A') + "" + (x + 1) + ".";
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
				for(int y = 0; y<height;++y)
				{
					if(destBoardState.getCellContents(x,y)==TreeTent.CELL_TENT)
					{
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


								if(destBoardState.getCellContents(x+i,y+j)==0)
								{
									destBoardState.setCellContents(x+i, y+j, TreeTent.CELL_GRASS);
									changed=true;
								}
							}
						}
					}
				}
			}
		}
		String error = checkRuleRaw(destBoardState);
		System.out.println(error);
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
