package edu.rpi.phil.legup.puzzles.battleship;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.rpi.phil.legup.*;

public class RuleSurroundShip extends PuzzleRule
{
	private static final long serialVersionUID = 853810334L;

	public RuleSurroundShip()
	{
		setName("Surround ship");
		description = "Set tiles to water around ship segments";
	}

	public String getImageName()
	{
		return "images/battleship/rules/SurroundShip.png";
	}

	protected String checkRuleRaw(BoardState state)
	{
		Set<Contradiction> contras = new LinkedHashSet<Contradiction>();
		contras.add(new ContradictionAdjacentShips());

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
					if (state.getCellContents(x, y) != BattleShip.CELL_WATER) {
						return "This rule can only prove water tiles!";
					}
					BoardState modified = origBoardState.copy();
					modified.getBoardCells()[y][x] = BattleShip.CELL_SEGMENT;
					boolean contradicts = false;
					for (Contradiction c : contras)
					{
						if (c.checkContradictionRaw(modified) == null)
							contradicts = true;
					}
					if (!contradicts)
						return "At least one of these tiles is not forced to be water!";
				}
			}
		}
		return null;
	}

}