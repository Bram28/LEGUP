package edu.rpi.phil.legup.puzzles.battleship;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.rpi.phil.legup.*;
import edu.rpi.phil.legup.puzzles.nurikabe.ContradictionIsolatedBlack;
import edu.rpi.phil.legup.puzzles.nurikabe.Nurikabe;

public class RulePreventAdjacentShips extends PuzzleRule
{
	static final long serialVersionUID = 853810334L;
	
    private static final BattleShip battleship = new BattleShip();

	public RulePreventAdjacentShips()
	{
		setName("Row/Column Deduction Rule");
		description = "When all the ship/water cells have been accounted for, fill in the rest of the unknowns appropriately";
	}

	public String getImageName()
	{
		return "images/defaultRule.png";
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
					for (Contradiction c : contras) {
						if (c.checkContradictionRaw(modified) != null)
							return "At least one of these tiles is not forced to be water!";
					}
				}
			}
		}
		return null;
	}

}