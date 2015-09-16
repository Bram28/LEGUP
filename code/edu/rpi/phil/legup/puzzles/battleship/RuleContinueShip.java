package edu.rpi.phil.legup.puzzles.battleship;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleContinueShip extends PuzzleRule
{
	private static final long serialVersionUID = 153790127149316292L;

	public RuleContinueShip()
	{
		setName("Continue ship");
		description = "Place a generic segment next to each ship end";
	}

	public String getImageName()
	{
		return "images/battleship/rules/ContinueShip.png";
	}

	protected String checkRuleRaw(BoardState state)
	{
		Set<Contradiction> contras = new LinkedHashSet<Contradiction>();
		contras.add(new ContradictionIncompleteShip());

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
