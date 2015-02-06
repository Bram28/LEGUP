package edu.rpi.phil.legup.puzzles.nurikabe;

import java.awt.Point;
import java.util.Set;
import java.util.LinkedHashSet;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.puzzles.nurikabe.ContradictionIsolatedBlack;

public class RuleFillInWhite extends PuzzleRule
{
	private static final long serialVersionUID = -2761216044763518050L;

	RuleFillInWhite()
	{
		setName("Fill In White");
		description = "If there an unknown region surrounded by white, it must be white.";
		image = new ImageIcon("images/nurikabe/rules/FillInWhite.png");
	}

	public String getImageName()
	{
		return "images/nurikabe/rules/FillInWhite.png";
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
					if (destBoardState.getCellContents(x, y) != Nurikabe.CELL_WHITE) {
						return "Only black cells are allowed for this rule!";
					}
					BoardState modified = origBoardState.copy();
					modified.getBoardCells()[y][x] = Nurikabe.CELL_BLACK;
					for (Contradiction c : contras) {
						if (c.checkContradictionRaw(modified) != null)
							return "White cells must be placed inside of a region of white cells!";
					}
				}
			}
		}
		return null;
	}
}
