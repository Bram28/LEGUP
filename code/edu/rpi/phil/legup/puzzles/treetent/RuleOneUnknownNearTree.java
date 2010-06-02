//
//  Rule5.java
//  LEGUP
//
//  Created by Drew Housten on Tues April 12 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//
//  If a tree has only one unknown cell adjacent to it, it is a tent


package edu.rpi.phil.legup.puzzles.treetent;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleOneUnknownNearTree extends PuzzleRule{

	
	public RuleOneUnknownNearTree()
    {
		name = "Last Camping Spot";
		description = "If there is one unknown cell next to a tentless unlinked tree, it is a tent.";
		image = new ImageIcon("images/treetent/oneTentPosition.png");
    }

    /**
     * Check if a tree at a certain position forces a tent at another one (there are no other 
     * possiblities)
     * @param state the original BoardState (before we added the tent)
     * @param treeX the tree's x position
     * @param treeY the tree's y position
     * @param tentX the proposed tent's x position
     * @param tentY the proposed tent's y position
     * @return true iff a tent is forced
     */
    private boolean checkTreeForcesTent(BoardState state, int treeX, int treeY, int tentX, int tentY)
	{
		int numUnknown = 0;
		boolean foundTentPos = false;
		int w = state.getWidth();
		int h = state.getHeight();
		ArrayList<Object> links = state.getExtraData();

		// Check Left
		if (treeX > 0)
		{
			if (state.getCellContents(treeX - 1, treeY) == TreeTent.CELL_TENT && !TreeTent.isLinked(links, new Point(treeX -1,treeY)))
			{
				return false;
			}

			if (state.getCellContents(treeX - 1, treeY) == TreeTent.CELL_UNKNOWN)
			{
				if (treeX - 1 == tentX && treeY == tentY)
				{
					foundTentPos = true;
				}
				numUnknown++;
			}
		}

		// Check Up
		if (treeY > 0)
		{
			if (state.getCellContents(treeX, treeY - 1) == TreeTent.CELL_TENT && !TreeTent.isLinked(links, new Point(treeX,treeY-1)))
			{
				return false;
			}

			if (state.getCellContents(treeX, treeY - 1) == TreeTent.CELL_UNKNOWN)
			{
				if (treeX == tentX && treeY - 1 == tentY)
				{
					foundTentPos = true;
				}
				numUnknown++;
			}
		}

		// Check Down
		if (treeY + 1 < h)
		{
			if (state.getCellContents(treeX, treeY + 1) == TreeTent.CELL_TENT && !TreeTent.isLinked(links, new Point(treeX,treeY+1)))
			{
				return false;
			}

			if (state.getCellContents(treeX, treeY + 1) == TreeTent.CELL_UNKNOWN)
			{
				if (treeX == tentX && treeY + 1 == tentY)
				{
					foundTentPos = true;
				}
				numUnknown++;
			}
		} 
	
		// Check Right
		if (treeX + 1 < w)
		{
			if (state.getCellContents(treeX + 1, treeY) == TreeTent.CELL_TENT && !TreeTent.isLinked(links, new Point(treeX + 1,treeY)))
			{
				return false;
			}

			if (state.getCellContents(treeX + 1, treeY) == TreeTent.CELL_UNKNOWN)
			{
				if (treeX + 1 == tentX && treeY == tentY)
				{
					foundTentPos = true;
				}
				numUnknown++;
			}
		} 

		return (numUnknown == 1 && foundTentPos);
    }

    /**
     * Check if there is a tree at this position
     * @param state the state we're concerned with
     * @param x the x position we're checking
     * @param y the y position we're checking
     * @return true iff there is a tree at this state, false if there isn't, or we're out of bounds
     */
    private boolean isTree(BoardState state, int x, int y)
    {
    	int w = state.getWidth();
    	int h = state.getHeight();
    	
    	if (x >= 0 && x < w && y >= 0 && y < h)
    	{
    		return (state.getCellContents(x,y) == TreeTent.CELL_TREE) && !TreeTent.isLinked(state.getExtraData(), new Point(x,y));
    	}
    	return false;
    }
    
    /**
     * Check if inserting a tent at the given position is a legal application of the rule
     * @param original the original board state
     * @param x the x position we're inserting at
     * @param y the y position we're inserting at
     * @return true iff it's a legal application of this rule
     */
    private boolean checkLegal(BoardState original, int x, int y)
    {
    	boolean rv = false;
    	
    	if (isTree(original,x+1,y) && checkTreeForcesTent(original,x+1,y,x,y))
    		rv = true;
    	else if (isTree(original,x-1,y) && checkTreeForcesTent(original,x-1,y,x,y))
    		rv = true;
    	else if (isTree(original,x,y+1) && checkTreeForcesTent(original,x,y+1,x,y))
    		rv = true;
    	else if (isTree(original,x,y-1) && checkTreeForcesTent(original,x,y-1,x,y))
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
							error = "This rule only involves adding one or more tents!";
							break;
						}
						
						if (!checkLegal(origBoardState,x,y))
						{
							error = "The tent at " + (char)(y + (int)'A') + "" + (x + 1)  
									+ " is not forced by any adjacent tree.";
							break;
						}
					}
				}
			}
			
			if (error == null && !changed)
			{
				error = "You must add one or more tents to use this rule!";
			}
		}
		
		return error;
	}
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		boolean connected;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();
		int empty_cells= 0;
		ArrayList <Object> destExtra = destBoardState.getExtraData();
		if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
		{
			for(int x = 0; x < width; ++x)
			{
				for(int y = 0; y<height;++y)
				{
					connected= false;
					for(int i = 0; i<destExtra.size();i++)
					{
						ExtraTreeTentLink e = (ExtraTreeTentLink)destExtra.get(x);
						Point tree = e.pos1;
						Point tent = e.pos2;
						int state = destBoardState.getCellContents(tree.x,tree.y);
						if (state == TreeTent.CELL_TENT)
						{ // swap them
							Point temp = tree;
							tree = tent;
							tent = temp;
						}
						if(tree.x==x && tree.y==y)
						{
							connected =true;
							break;
						}
					}
					if(connected!=true&& destBoardState.getCellContents(x,y)==TreeTent.CELL_TREE)
					{
						empty_cells=0;
						for(int i =-1;i<2;i++)
						{
							for(int j = -1;j<2;j++)
							{
								if(i==0 && j==0)
									continue;
								if(x+i>=width || x+i<=0)
									continue;
								if(y+j>=height || y+j<=0)
									continue;
								if(destBoardState.getCellContents(x+i,y+j)==0)
								{
									empty_cells++;
								}
							}
						}
						if(empty_cells==1)
						{
							for(int i =-1;i<2;i++)
							{
								for(int j = -1;j<2;j++)
								{
									if(i==0 && j==0)
										continue;
									if(x+i>=width || x+i<=0)
										continue;
									if(y+j>=height || y+j<=0)
										continue;
									if(destBoardState.getCellContents(x+i,y+j)==0)
									{
										destBoardState.setCellContents(x+i, y+j, TreeTent.CELL_TENT);
									}
								}
							}
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
