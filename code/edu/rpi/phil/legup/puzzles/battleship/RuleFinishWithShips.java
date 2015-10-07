package edu.rpi.phil.legup.puzzles.battleship;

import edu.rpi.phil.legup.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.ImageIcon;

public class RuleFinishWithShips extends PuzzleRule
{
	static final long serialVersionUID = 853810334L;

	public RuleFinishWithShips()
	{
		setName("Finish with ships");
		description = "When the remaining unknowns in a row/column must be ship segments to satisfy the label, fill them in accordingly.";
	}

	public String getImageName()
	{
		return "images/defaultRule.png";
	}

	protected String checkRuleRaw(BoardState state)
	{
		Set<Contradiction> contras = new LinkedHashSet<Contradiction>();
		contras.add(new ContradictionTooFewRowCol());

		BoardState origBoardState = state.getSingleParentState();
		int width = origBoardState.getWidth();
		int height = origBoardState.getHeight();

		// Check for only one branch
		if (state.getParents().size() != 1)
		{
			return "This rule only involves having a single branch!";
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (state.getCellContents(x, y) !=
						origBoardState.getCellContents(x, y)) {
					if (state.getCellContents(x, y) != BattleShip.CELL_SEGMENT) {
						return "This rule can only prove generic ship segments!";
					}
					BoardState modified = origBoardState.copy();
					modified.getBoardCells()[y][x] = BattleShip.CELL_WATER;
					boolean contradicts = false;
					for (Contradiction c : contras)
					{
						if (c.checkContradictionRaw(modified) == null)
							contradicts = true;
					}
					if (!contradicts)
						return "At least one of these tiles is not forced to be a ship segment!";
				}
			}
		}
		return null;
	}

}