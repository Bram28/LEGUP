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
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Contradiction;

public class RuleLastCampingSpot extends PuzzleRule
{
	static final long serialVersionUID = 9518L;
	public String getImageName() {return "images/treetent/oneTentPosition.png";}
	public RuleLastCampingSpot()
    {
		setName("Last Camping Spot");
		description = "If there is one unknown cell next to a tentless unlinked tree, it is a tent which must be linked to the tree.";
		//image = new ImageIcon("images/treetent/oneTentPosition.png");
    }

		protected String checkRuleRaw(BoardState destBoardState) {
			Set<Contradiction> contras = new HashSet<Contradiction>();
			contras.add(new ContradictionNoTentForTree());

			BoardState origBoardState = destBoardState.getSingleParentState();
			int width = origBoardState.getWidth();
			int height = origBoardState.getHeight();

			// Check for only one branch
			if (destBoardState.getParents().size() != 1) {
				return "This rule only involves having a single branch!";
			}

			for (int y = 0; y < height; ++y) {
				for (int x = 0; x < width; ++x) {
					int origState = origBoardState.getCellContents(x,y);
					int newState = destBoardState.getCellContents(x,y);
					if (origState != newState) {
						if (destBoardState.getCellContents(x, y) != TreeTent.CELL_TENT) {
							return "Only Tent cells are allowed for this rule!";
						}

						BoardState modified = origBoardState.copy();
						modified.getBoardCells()[y][x] = TreeTent.CELL_GRASS;
						for (Contradiction c : contras) {
							if (c.checkContradictionRaw(modified) != null)
								return "This is not the last camping spot next to a unlinked tree!";
						}
					}
				}
			}
			return null;
		}


  //   /**
  //    * Check if a tree at a certain position forces a tent at another one (there are no other
  //    * possiblities)
  //    * @param state the original BoardState (before we added the tent)
  //    * @param treeX the tree's x position
  //    * @param treeY the tree's y position
  //    * @param tentX the proposed tent's x position
  //    * @param tentY the proposed tent's y position
  //    * @return true iff a tent is forced
  //    */
  //   private boolean checkTreeForcesTent(BoardState state, int treeX, int treeY, int tentX, int tentY)
	// {
	// 	int numUnknown = 0;
	// 	boolean foundTentPos = false;
	// 	int w = state.getWidth();
	// 	int h = state.getHeight();
	// 	ArrayList<Object> links = state.getExtraData();
	//
	// 	// Check Left
	// 	if (treeX > 0)
	// 	{
	// 		if (state.getCellContents(treeX - 1, treeY) == TreeTent.CELL_TENT && !TreeTent.isLinked(links, new Point(treeX -1,treeY)))
	// 		{
	// 			return false;
	// 		}
	//
	// 		if (state.getCellContents(treeX - 1, treeY) == TreeTent.CELL_UNKNOWN)
	// 		{
	// 			if (treeX - 1 == tentX && treeY == tentY)
	// 			{
	// 				foundTentPos = true;
	// 			}
	// 			numUnknown++;
	// 		}
	// 	}
	//
	// 	// Check Up
	// 	if (treeY > 0)
	// 	{
	// 		if (state.getCellContents(treeX, treeY - 1) == TreeTent.CELL_TENT && !TreeTent.isLinked(links, new Point(treeX,treeY-1)))
	// 		{
	// 			return false;
	// 		}
	//
	// 		if (state.getCellContents(treeX, treeY - 1) == TreeTent.CELL_UNKNOWN)
	// 		{
	// 			if (treeX == tentX && treeY - 1 == tentY)
	// 			{
	// 				foundTentPos = true;
	// 			}
	// 			numUnknown++;
	// 		}
	// 	}
	//
	// 	// Check Down
	// 	if (treeY + 1 < h)
	// 	{
	// 		if (state.getCellContents(treeX, treeY + 1) == TreeTent.CELL_TENT && !TreeTent.isLinked(links, new Point(treeX,treeY+1)))
	// 		{
	// 			return false;
	// 		}
	//
	// 		if (state.getCellContents(treeX, treeY + 1) == TreeTent.CELL_UNKNOWN)
	// 		{
	// 			if (treeX == tentX && treeY + 1 == tentY)
	// 			{
	// 				foundTentPos = true;
	// 			}
	// 			numUnknown++;
	// 		}
	// 	}
	//
	// 	// Check Right
	// 	if (treeX + 1 < w)
	// 	{
	// 		if (state.getCellContents(treeX + 1, treeY) == TreeTent.CELL_TENT && !TreeTent.isLinked(links, new Point(treeX + 1,treeY)))
	// 		{
	// 			return false;
	// 		}
	//
	// 		if (state.getCellContents(treeX + 1, treeY) == TreeTent.CELL_UNKNOWN)
	// 		{
	// 			if (treeX + 1 == tentX && treeY == tentY)
	// 			{
	// 				foundTentPos = true;
	// 			}
	// 			numUnknown++;
	// 		}
	// 	}
	//
	// 	return (numUnknown == 1 && foundTentPos);
  //   }
	//
  //   /**
  //    * Check if there is a tree at this position
  //    * @param state the state we're concerned with
  //    * @param x the x position we're checking
  //    * @param y the y position we're checking
  //    * @return true iff there is a tree at this state, false if there isn't, or we're out of bounds
  //    */
  //   private boolean isTree(BoardState state, int x, int y)
  //   {
  //   	int w = state.getWidth();
  //   	int h = state.getHeight();
	//
  //   	//Make sure the current position is within bounds
  //   	if (x >= 0 && x < w && y >= 0 && y < h)
  //   	{
  //   		//Make sure the tree at the position is unlinked
  //   		return (state.getCellContents(x,y) == TreeTent.CELL_TREE) && !TreeTent.isLinked(state.getExtraData(), new Point(x,y));
  //   	}
	//
  //   	//The position is out of bounds
  //   	return false;
  //   }
	//
  //   /**
  //    * Check if inserting a tent at the given position is a legal application of the rule
  //    * @param original the original board state
  //    * @param x the x position we're inserting at
  //    * @param y the y position we're inserting at
  //    * @return true iff it's a legal application of this rule
  //    */
  //   private boolean checkLegal(BoardState original, int x, int y)
  //   {
  //   	boolean rv = false;
	//
  //   	if (isTree(original,x+1,y) && checkTreeForcesTent(original,x+1,y,x,y))
  //   		rv = true;
  //   	else if (isTree(original,x-1,y) && checkTreeForcesTent(original,x-1,y,x,y))
  //   		rv = true;
  //   	else if (isTree(original,x,y+1) && checkTreeForcesTent(original,x,y+1,x,y))
  //   		rv = true;
  //   	else if (isTree(original,x,y-1) && checkTreeForcesTent(original,x,y-1,x,y))
  //   		rv = true;
	//
  //   	return rv;
  //   }
	//
  //   protected String checkRuleRaw(BoardState destBoardState)
  //   {
  //   	String error = null;
  //   	boolean changed = false;
  //   	BoardState origBoardState = destBoardState.getSingleParentState();
  //   	int numTentsAdded = 0;
	//
  //   	// Check for only one branch
	// 	if (destBoardState.getParents().size() != 1)
	// 	{
	// 		error = "This rule only involves having a single branch!";
	// 	}
	// 	/*else if(destBoardState.getExtraData().equals(origBoardState.getExtraData()))
	// 	{
	// 		error = "This rule requires links to be added for every tent added.";
	// 	}*/
	// 	else if(!TreeTent.noInvalidLinks(destBoardState))
	// 	{
	// 		error = "There is an invalid link in this state";
	// 	}
	// 	else
	// 	{
	// 		for (int y = 0; y < origBoardState.getHeight() && error == null; ++y)
	// 		{
	// 			for (int x = 0; x < origBoardState.getWidth(); ++x)
	// 			{
	// 				int origState = origBoardState.getCellContents(x,y);
	// 				int newState = destBoardState.getCellContents(x,y);
	//
	// 				if (origState != newState)
	// 				{
	// 					changed = true;
	//
	// 					if (newState != TreeTent.CELL_TENT || origState != TreeTent.CELL_UNKNOWN)
	// 					{
	// 						error = "This rule only involves adding and linking tents!";
	// 						break;
	// 					}
	//
	// 					numTentsAdded++;
	//
	// 					if (!checkLegal(origBoardState,x,y))
	// 					{
	// 						error = "The tent at " + (char)(y + (int)'A') + "" + (x + 1)
	// 								+ " is not forced by any adjacent tree.";
	// 						break;
	// 					}
	// 				}
	// 			}
	// 		}
	// 		if((destBoardState.getExtraData().size() - origBoardState.getExtraData().size()) != numTentsAdded)
	// 		{
	// 			error = "This rule requires links to be added for every tent added.";
	// 		}
	//
	// 		if (error == null && !changed)
	// 		{
	// 			error = "You must add one or more tents to use this rule!";
	// 		}
	// 	}
	//
	// 	TreeTent.setAnnotations(destBoardState);
	//
	// 	return error;
	// }
	// protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	// {
	// 	BoardState origBoardState = destBoardState.getSingleParentState();
	// 	boolean changed = false;
	// 	boolean connected;
	// 	int width = destBoardState.getWidth();
	// 	int height = destBoardState.getHeight();
	// 	int empty_cells= 0;
	// 	ArrayList <Object> destExtra = destBoardState.getExtraData();
	// 	ArrayList <Object> origExtra = origBoardState.getExtraData();
	// 	if (origBoardState != null && origBoardState.getParents().size() == 1)
	// 	{
	// 		for(int x = 0; x < width; ++x)
	// 		{
	// 			for(int y = 0; y<height;++y)
	// 			{
	//
	// 				if(TreeTent.isLinked(destExtra, new Point(x,y)))
	// 					continue;
	// 				if(origBoardState.getCellContents(x,y)==TreeTent.CELL_TREE)
	// 				{
	// 					empty_cells=0;
	// 					for(int i =-1;i<2;i++)
	// 					{
	// 						for(int j = -1;j<2;j++)
	// 						{
	// 							if(i==0 && j==0)
	// 								continue;
	// 							if((x+i)>=width || (x+i)<0)
	// 								continue;
	// 							if((y+j)>=height || (y+j)<0)
	// 								continue;
	// 							if(i!=0 && j!=0)
	// 								continue;
	//
	// 							if(TreeTent.isLinked(origExtra, new Point(x+i,y+j)))
	// 								continue;
	// 							if(origBoardState.getCellContents(x+i,y+j)==0)
	// 							{
	// 								empty_cells++;
	// 							}
	// 						}
	// 					}
	// 					if(empty_cells==1)
	// 					{
	// 						for(int i =-1;i<2;i++)
	// 						{
	// 							for(int j = -1;j<2;j++)
	// 							{
	// 								if(i==0 && j==0)
	// 									continue;
	// 								if((x+i)>=width || (x+i)<0)
	// 									continue;
	// 								if((y+j)>=height || (y+j)<0)
	// 									continue;
	// 								if(i!=0 && j!=0)
	// 									continue;
	// 								if(origBoardState.getCellContents(x+i,y+j)==0)
	// 								{
	// 									changed = true;
	// 									destBoardState.setCellContents(x+i, y+j, TreeTent.CELL_TENT);
	// 									destExtra.add((Object) new ExtraTreeTentLink(new Point(x,y), new Point(x+i,y+j)));
	// 									destBoardState.setExtraData(destExtra);
	// 								}
	// 							}
	// 						}
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}
	// 	String error = checkRuleRaw(destBoardState);
	// 	if(error != null)
	// 	{
	// 		System.out.println(error);
	// 		changed = false;
	// 	}
	// 	if(!changed)
	// 	{
	// 		destBoardState= origBoardState.copy();
	// 	}
	// 	return changed;
	// }
}
