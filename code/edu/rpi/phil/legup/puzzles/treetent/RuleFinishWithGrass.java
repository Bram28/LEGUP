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
import java.util.Set;
import java.util.*;
import java.util.HashSet;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Contradiction;

public class RuleFinishWithGrass extends PuzzleRule
{
	static final long serialVersionUID = 9511L;
	public String getImageName() {return "images/treetent/finishGrass.png";}
    public RuleFinishWithGrass()
    {
    	setName("Finish with Grass");
    	description = "Grass can be added to finish a row or column that has reached its tent limit.";
    	//image = new ImageIcon("images/treetent/finishGrass.png");
    }

		protected String checkRuleRaw(BoardState destBoardState) {
			Set<Contradiction> contras = new HashSet<Contradiction>();
			contras.add(new ContradictionTooManyTents());

			BoardState origBoardState = destBoardState.getSingleParentState();
			int width = origBoardState.getWidth();
			int height = origBoardState.getHeight();

			// Check for only one branch
			if (destBoardState.getParents().size() != 1) {
				return "This rule only involves having a single branch!";
			}

			ArrayList<Object> extra = destBoardState.getExtraDataDelta();
			if(extra.size() > 0){  return "Links can't be verified by this rule!"; }

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
