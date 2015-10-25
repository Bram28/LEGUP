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
}
