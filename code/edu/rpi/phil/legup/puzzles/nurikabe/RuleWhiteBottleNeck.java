package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.ConnectedRegions;
import edu.rpi.phil.legup.Contradiction;
import java.util.LinkedHashSet;
import java.awt.Point;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class RuleWhiteBottleNeck extends PuzzleRule
{
  private static final long serialVersionUID = 450532374L;

  RuleWhiteBottleNeck()
  {
    setName("White Bottle Neck");
    description = "If a region needs more whites and there is only one path for the region to expand, then those unknowns must be white.";
    image = new ImageIcon("images/nurikabe/rules/OneUnknownWhite.png");
  }

  public String getImageName()
  {
    return "images/nurikabe/rules/OneUnknownWhite.png";
  }

  protected String checkRuleRaw(BoardState destBoardState)
  {
    Set<Contradiction> contras = new LinkedHashSet<Contradiction>();
    contras.add(new ContradictionNoNumber());
    contras.add(new ContradictionTooFewSpaces());

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

          int contrasSatisfied = 0;
          for (Contradiction c : contras) {
            if (c.checkContradictionRaw(modified) == null)
              contrasSatisfied++;
          }
          if (contrasSatisfied == 0)
            return "This is not the only way to fill up the region!";
        }
      }
    }

    return null;
  }
}
