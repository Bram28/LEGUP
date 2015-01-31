package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.ConnectedRegions;
import edu.rpi.phil.legup.Contradiction;
import java.util.LinkedHashSet;
import java.util.Set;

public class RuleBlackBottleNeck extends PuzzleRule
{
	private static final long serialVersionUID = 787962510L;

	RuleBlackBottleNeck()
	{
		setName("Black Bottle Neck");
		description = "If there is only one path for a black to escape, then those unknowns must be white.";
		image = new ImageIcon("images/nurikabe/rules/OneUnknownBlack.png");
	}

	public String getImageName()
	{
		return "images/nurikabe/rules/OneUnknownBlack.png";
	}

	protected String checkRuleRaw(BoardState destBoardState)
	{
		Set<Contradiction> contras = new LinkedHashSet<Contradiction>();
		contras.add(new ContradictionIsolatedBlack());

		BoardState origBoardState = destBoardState.getSingleParentState();
		int width = origBoardState.getWidth();
		int height = origBoardState.getHeight();

		// Check for only one branch
		if (destBoardState.getParents().size() != 1)
		{
			return "This rule only involves having a single branch!";
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (destBoardState.getCellContents(x, y) !=
						origBoardState.getCellContents(x, y)) {
					if (destBoardState.getCellContents(x, y) != Nurikabe.CELL_BLACK) {
						return "Only black cells are allowed for this rule!";
					}
					BoardState modified = origBoardState.copy();
					modified.getBoardCells()[y][x] = Nurikabe.CELL_WHITE;
					for (Contradiction c : contras) {
						if (c.checkContradictionRaw(modified) != null)
							return "This is not the only way for black to escape!";
					}
				}
			}
		}
		return null;
	}

}
