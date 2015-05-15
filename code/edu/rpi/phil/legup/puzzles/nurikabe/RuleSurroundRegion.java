package edu.rpi.phil.legup.puzzles.nurikabe;

import java.awt.Point;
import java.util.Set;
import java.util.LinkedHashSet;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.puzzles.nurikabe.ContradictionTooManySpaces;
import edu.rpi.phil.legup.puzzles.nurikabe.ContradictionMultipleNumbers;


public class RuleSurroundRegion extends PuzzleRule
{
    private static final long serialVersionUID = 881143872L;

	 RuleSurroundRegion()
	 {
		setName("Surround Region");
		description = "All completed white regions must be surrounded by black.";
		image = new ImageIcon("images/nurikabe/rules/SurroundBlack.png");
	 }

	public String getImageName()
	{
		return "images/nurikabe/rules/SurroundBlack.png";
	}

  protected String checkRuleRaw(BoardState destBoardState)
  {
    Set<Contradiction> contras = new LinkedHashSet<Contradiction>();
    contras.add(new ContradictionTooManySpaces());

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
          boolean contraSatisfied = false;
          for (Contradiction c : contras) {
            if (c.checkContradictionRaw(modified) == null)
              contraSatisfied = true;
          }
          if (!contraSatisfied) return "Black cells must be placed around a completed region!";
        }
      }
    }
    return null;
  }
}
