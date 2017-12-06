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
import java.util.*;
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

			ArrayList<Object> extra = destBoardState.getExtraDataDelta();
			if(extra.size() > 0){  return "Links can't be verified by this rule!"; }

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
}
