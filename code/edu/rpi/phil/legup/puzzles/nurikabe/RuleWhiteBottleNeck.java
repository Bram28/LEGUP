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
    image = new ImageIcon("images/nurikabe/rules/OneUnknownRegion.png");
  }

  public String getImageName()
  {
    return "images/nurikabe/rules/OneUnknownRegion.png";
  }

  protected String checkRuleRaw(BoardState destBoardState)
  {
    Contradiction contraTooFew = new ContradictionTooFewSpaces();
    Contradiction contraTooMany = new ContradictionTooManySpaces();

    BoardState origBoardState = destBoardState.getSingleParentState();
    int width = origBoardState.getWidth();
    int height = origBoardState.getHeight();

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (destBoardState.getCellContents(x, y) !=
        origBoardState.getCellContents(x, y)) {
          if (destBoardState.getCellContents(x, y) != Nurikabe.CELL_WHITE) {
            return "Only white cells are allowed for this rule!";
          }
          BoardState modified = origBoardState.copy();
          modified.getBoardCells()[y][x]=Nurikabe.CELL_BLACK;

          if (contraTooFew.checkContradictionRaw(modified) != null)
            return "This is not the only way to fill up the region!";
          if (contraTooMany.checkContradictionRaw(modified) == null) {
            return "Placing that amound of white tiles creates a region that is too large!";
          }
        }
      }
    }

    return null;
  }
}
