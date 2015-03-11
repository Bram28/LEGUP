package edu.rpi.phil.legup.puzzles.battleship;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleRule;

public class RuleSegmentType extends PuzzleRule
{
	private static final long serialVersionUID = 153790127149316292L;

	public RuleSegmentType()
	{
		setName("Segment Type");
		description = "Choose the type of segment a generic ship tile is.";
	}

	public String getImageName()
	{
		return "images/defaultRule.png";
	}

	protected String checkRuleRaw(BoardState state)
	{
		Set<Contradiction> contras = new LinkedHashSet<Contradiction>();
		contras.add(new ContradictionAdjacentShips());
		contras.add(new ContradictionMalformedShip());

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
					if (origBoardState.getCellContents(x, y) != BattleShip.CELL_SEGMENT)
						return "This rule can only be applied to generic segments!";
					if (!BattleShip.isShip(state.getCellContents(x, y))) {
						return "This rule can only determine ship segments!";
					}
					BoardState modified = origBoardState.copy();
					for (int curType = BattleShip.CELL_LEFT_CAP; curType <= BattleShip.CELL_MIDDLE; curType++ )
					{
						if (curType != state.getCellContents(x, y))
						{
							modified.getBoardCells()[y][x] = curType;
							boolean contradicts = false;
							for (Contradiction c : contras)
							{
								if (c.checkContradictionRaw(modified) == null)
									contradicts = true;
							}
							if (!contradicts)
							{
								System.out.println(curType);
								return "At least one of these tiles is not forced to be this segment type!";
							}
						}
					}
				}
			}
		}
		return null;
	}

}
