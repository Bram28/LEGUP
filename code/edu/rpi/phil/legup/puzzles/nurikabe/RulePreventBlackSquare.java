package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;

import java.awt.Point;
import java.util.Set;
import java.util.LinkedHashSet;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.puzzles.nurikabe.ContradictionNoNumber;

public class RulePreventBlackSquare extends PuzzleRule
{
	private static final long serialVersionUID = 28206759L;

	RulePreventBlackSquare()
	{
		setName("Prevent Black Square");
		description = "There cannot be a 2x2 square of black. (3 blacks = fill in last corner white)";
		image = new ImageIcon("images/nurikabe/rules/NoBlackSquare.png");
	}

	public String getImageName()
	{
		return "images/nurikabe/rules/NoBlackSquare.png";
	}

	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
	protected String checkRuleRaw(BoardState destBoardState)
  {
		Set<Contradiction> contras = new LinkedHashSet<Contradiction>();
		contras.add(new ContradictionBlackSquare());

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
						return "Only white cells are allowed for this rule!";
					}
					BoardState modified = origBoardState.copy();
					modified.getBoardCells()[y][x] = Nurikabe.CELL_BLACK;
					for (Contradiction c : contras) {
						if (c.checkContradictionRaw(modified) != null)
							return "White cell must be placed within a 2x2 box which contains 3 black cells.";
					}
				}
			}
		}
		return null;
	}
}
