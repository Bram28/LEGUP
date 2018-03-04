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
import java.util.*;
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
		description = "If an unlinked tree is adjacent to only one blank cell and not adjacent to any unlinked tents, the blank cell must be a tent.";
	}

	protected String checkRuleRaw(BoardState destBoardState) {
		Point loneTree;
		ArrayList<ExtraTreeTentLink> validLinks = new ArrayList<ExtraTreeTentLink>();
		Set<Contradiction> contras = new HashSet<Contradiction>();
		contras.add(new ContradictionNoTentForTree());

		BoardState origBoardState = destBoardState.getSingleParentState();
		int width = origBoardState.getWidth();
		int height = origBoardState.getHeight();

		ArrayList<Object> extra = destBoardState.getExtraDataDelta();
		System.out.println(extra.size() + " last spot");
		//if(extra.size() > 0){  return "Links can't be verified by this rule!"; }
		// Check for only one branch

		if (destBoardState.getParents().size() != 1) {
			return "This rule only involves having a single branch!";
		}

		int numChanged = 0;
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
					ContradictionNoTentForTree c = new ContradictionNoTentForTree();
					if (c.checkContradictionRaw(modified) != null) {
						return "This is not the last camping spot next to a unlinked tree!";
					} else {
						loneTree = c.getLoneTree();
					}

					System.out.println(loneTree + " and " + new Point(x,y));
					ExtraTreeTentLink e = new ExtraTreeTentLink(new Point(x,y),loneTree);
					validLinks.add(e);
					numChanged += 1;


				}
			}
		}

		if(extra.size() > numChanged){ return "Links by itself can't be verified by this rule!";}
			for(int i = 0; i < extra.size(); i++){
				if(!validLinks.contains(extra.get(i))){
					return "There are incorrect links";
				}
			}
		return null;
	}
}
