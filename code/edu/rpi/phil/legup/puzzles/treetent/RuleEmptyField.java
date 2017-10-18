//
//  Rule4.java
//  LEGUP
//
//  Created by Drew Housten on Tues April 12 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//
//  Any cell that is not adjacent to a tree can be declared grass


package edu.rpi.phil.legup.puzzles.treetent;

import java.util.Set;
import java.util.HashSet;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Contradiction;

public class RuleEmptyField extends PuzzleRule
{
	static final long serialVersionUID = 9517L;
	public String getImageName() {return "images/treetent/noTreesAround.png";}
	public RuleEmptyField()
    {
		setName("Empty Field");
		//description = "Any cell not next to an unlinked tree can be marked grass.";
		description = "blank cells not adjacent to an unliked tree are grass.";
		//image = new ImageIcon("images/treetent/noTreesAround.png");
    }

	protected String checkRuleRaw(BoardState destBoardState) {
		Set<Contradiction> contras = new HashSet<Contradiction>();
		contras.add(new ContradictionNoTreeForTent());

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
					if (destBoardState.getCellContents(x, y) != TreeTent.CELL_GRASS) {
						return "Only Grass cells are allowed for this rule!";
					}

					BoardState modified = origBoardState.copy();
					modified.getBoardCells()[y][x] = TreeTent.CELL_TENT;
					for (Contradiction c : contras) {
						if (c.checkContradictionRaw(modified) != null)
							return "You can still add more tents to this row/column!";
					}
				}
			}
		}
		return null;
	}
}
