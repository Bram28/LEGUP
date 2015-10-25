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
import java.util.Set;
import java.util.HashSet;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Contradiction;

public class RuleFinishWithTents extends PuzzleRule
{
	static final long serialVersionUID = 9512L;
	public String getImageName() {return "images/treetent/finishTent.png";}
	public RuleFinishWithTents()
    {
		setName("Finish with Tents");
		description = "Tents can be added to finish a row or column that has one open spot per required tent.";
		//image = new ImageIcon("images/treetent/finishTent.png");
    }

		protected String checkRuleRaw(BoardState destBoardState) {
			Set<Contradiction> contras = new HashSet<Contradiction>();
			contras.add(new ContradictionTooFewTents());

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
								return "You can must add more tents to this row/column!";
						}
					}
				}
			}
			return null;
		}

  //   /**
  //    * Count the number of occurances of a certain cell type in a row or column
  //    * @param state the BoardState we're counting on
  //    * @param givenX true iff we're counting a column (we're given an x), false = row
  //    * @param type the type of cell we're counting, like TreeTent.CELL_GRASS
  //    * @param num the column or row number
  //    * @return the occrances of the cell type in the given row or column
  //    */
  //   private int countCell(BoardState state, boolean givenX, int type, int num)
  //   {
  //   	int rv = 0;
	//
  //   	int x = 0, y = 0;
	//
  //   	if (givenX)
  //   		x = num;
  //   	else
  //   		y = num;
	//
  //   	while (true)
  //   	{
  //   		if (state.getCellContents(x,y) == type)
  //   			++rv;
	//
  //   		if (givenX)
  //   		{
  //   			++y;
  //   			if (y == state.getHeight())
  //   				break;
  //   		}
  //   		else
  //   		{
  //   			++x;
  //   			if (x == state.getWidth())
  //   				break;
  //   		}
  //   	}
	//
  //   	return rv;
  //   }
	//
  //   /**
  //    * Check if adding a tent here is a valid application of this rule
  //    * @param state the destination board state
  //    * @param x the x position of the new tent
  //    * @param y the y position of the new tent
  //    * @return true iff we have a valid application of the rule
  //    */
  //   public boolean checkLegal(BoardState state, int x, int y)
  //   {
  //   	boolean rv = false;
	//
  //   	//Total number of tents that can exist in a row or column
  //   	int numRow = TreeTent.translateNumTents(state.getLabel(BoardState.LABEL_RIGHT, y));
  //   	int numCol = TreeTent.translateNumTents(state.getLabel(BoardState.LABEL_BOTTOM, x));
	//
  //   	//Current number of unknown spots which can be filled
  //   	int spotsCol = countCell(state,true,TreeTent.CELL_UNKNOWN,x);
  //   	int spotsRow = countCell(state,false,TreeTent.CELL_UNKNOWN,y);
	//
  //   	//Current number of tents
  //   	int tentsCol = countCell(state,true,TreeTent.CELL_TENT,x);
  //   	int tentsRow = countCell(state,false,TreeTent.CELL_TENT,y);
	//
  //   	//Total number of tents = Number of unknown cells + Current number of tents
  //   	//There is enough unknown spaces to be filled up with tents
  //   	if (numRow == spotsRow + tentsRow || numCol == spotsCol + tentsCol)
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
	//
  //   	// Check for only one branch
	// 	if (destBoardState.getParents().size() != 1)
	// 	{
	// 		error = "This rule only involves having a single branch!";
	// 	}
	// 	else if (!destBoardState.getExtraData().equals(origBoardState.getExtraData()))
	// 	{
	// 		error = "This rule does not involve changing tree-tent links.";
	// 	}
	// 	else
	// 	{
	// 		// For each cell, check if the row or column has a sufficient number of tents in it
	//
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
	// 						error = "This rule only involves adding tents!";
	// 						break;
	// 					}
	//
	// 					if (!checkLegal(destBoardState,x,y))
	// 					{
	// 						error = "Neither the row nor column at "
	// 							+ String.valueOf((char)(y + (int)'A'))
	// 							+ "" + (x + 1) + " has its maximum number of non-tents.";
	// 						break;
	// 					}
	// 				}
	// 			}
	// 		}
	//
	// 		if (error == null && !changed)
	// 		{
	// 			error = "You must add some tents to use this rule2!";
	// 		}
	// 	}
	//
	// 	TreeTent.setAnnotations(destBoardState);
	//
	// 	return error;
	// }
	//
	//
	// protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	// {
	// 	 	BoardState origBoardState = destBoardState.getSingleParentState();
	//     	boolean changed = false;
	//     	int width = destBoardState.getWidth();
	//     	int height = destBoardState.getHeight();
	//
	//     	if (origBoardState != null && destBoardState.getParents().size() == 1)
	//     	{
	//         	for(int x = 0; x < width; ++x)
	//         	{
	//         		int num_empty = 0;
	//     			int total = TreeTent.translateNumTents(destBoardState.getLabel(BoardState.LABEL_BOTTOM, x));
	//         		for(int y = 0; y < height; ++y)
	//     			{
	//     				if(destBoardState.getCellContents(x, y)==0 || destBoardState.getCellContents(x, y)==TreeTent.CELL_TENT)
	//     				{
	//     					num_empty++;
	//     				}
	//     			}
	//         		if(total==num_empty)
	// 	    		{
	// 	    			//System.out.println(total + "=" + num_empty);
	// 	    			for(int y = 0; y < height; ++y)
	// 	    			{
	// 	    				if(destBoardState.getCellContents(x, y)==0)
	// 	    				{
	// 	    					destBoardState.setCellContents(x, y, TreeTent.CELL_TENT);
	// 	    					changed = true;
	// 	    				}
	// 	    			}
	// 	    		}
	//         	}
	//         	for(int y = 0; y < height; ++y)
	//         	{
	//         		int num_empty = 0;
	//     			int total = TreeTent.translateNumTents(destBoardState.getLabel(BoardState.LABEL_RIGHT, y));
	//         		for(int x = 0; x < width; ++x)
	//     			{
	//     				if(destBoardState.getCellContents(x, y)==TreeTent.CELL_UNKNOWN || destBoardState.getCellContents(x, y)==TreeTent.CELL_TENT)
	//     				{
	//     					num_empty++;
	//     				}
	//     			}
	//         		if(total==num_empty && total>0)
	// 	    		{
	// 	    			for(int x = 0; x < width; ++x)
	// 	    			{
	// 	    				if(destBoardState.getCellContents(x, y)==TreeTent.CELL_UNKNOWN)
	// 	    				{
	// 	    					destBoardState.setCellContents(x, y, TreeTent.CELL_TENT);
	// 	    					changed = true;
	// 	    				}
	// 	    			}
	// 	    		}
	//         	}
	//
	//
	//
	//
	//     	}
	//     	String error = checkRuleRaw(destBoardState);
	//     	if (error != null)
	// 			{
	// 				System.out.println(error);
	// 				changed = false;
	// 				// valid change
	// 			}
	//     	if(!changed)
	//     	{
	//     		destBoardState = origBoardState.copy();
	//     	}
	//
	// 	    return changed;
	//   }
}
