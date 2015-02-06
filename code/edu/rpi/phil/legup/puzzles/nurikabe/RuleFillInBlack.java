package edu.rpi.phil.legup.puzzles.nurikabe;

import java.awt.Point;
import java.util.Set;
import java.util.LinkedHashSet;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.puzzles.nurikabe.ContradictionNoNumber;

public class RuleFillInBlack extends PuzzleRule
{
	private static final long serialVersionUID = 729976023L;

	RuleFillInBlack()
	{
		setName("Fill In Black");
		description = "If there an unknown region surrounded by black, it must be black.";
		image = new ImageIcon("images/nurikabe/rules/FillInBlack.png");
	}

	public String getImageName()
	{
		return "images/nurikabe/rules/FillInBlack.png";
	}

	protected String checkRuleRaw(BoardState destBoardState)
	{
		Set<Contradiction> contras = new LinkedHashSet<Contradiction>();
		contras.add(new ContradictionNoNumber());

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
							return "Black cells must be placed in a region of black cells!";
					}
				}
			}
		}
		return null;
	}
}
