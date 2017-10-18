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
import java.util.Set;
import java.util.HashSet;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Contradiction;

public class RuleSurroundTentWithGrass extends PuzzleRule
{
	static final long serialVersionUID = 9513L;
	public String getImageName() {return "images/treetent/aroundTent.png";}
    public RuleSurroundTentWithGrass()
    {
    	setName("Surround Tent with Grass");
    	description = "Blank cells adjacent or diagonal to a tent are grass.";
    	//image = new ImageIcon("images/treetent/aroundTent.png");
    }

		protected String checkRuleRaw(BoardState destBoardState) {
			Set<Contradiction> contras = new HashSet<Contradiction>();
			contras.add(new ContradictionAdjacentTents());

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
								return "You must surround a tent with Grass cells!";
						}
					}
				}
			}
			return null;
		}
}
