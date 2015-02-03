package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.ConnectedRegions;
import java.util.List;
import java.util.Set;
import java.awt.Point;

public class ContradictionNoNumber extends Contradiction
{
    private static final long serialVersionUID = 16944369L;

	 ContradictionNoNumber()
	 {
		setName("No Number");
		description = "All enclosed white regions must have a number.";
		image = new ImageIcon("images/nurikabe/contradictions/NoNumber.png");
	 }

	public String getImageName()
	{
		return "images/nurikabe/contradictions/NoNumber.png";
	}

	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    public String checkContradictionRaw(BoardState state)
    {
    	int height = state.getHeight();
    	int width = state.getWidth();
      int[][] cells = new int[height][width];

      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          cells[y][x] = state.getCellContents(x, y);
        }
      }
      List<Set<Point>> regions = ConnectedRegions.getConnectedRegions(Nurikabe.CELL_BLACK, cells, width, height);
      for (Set<Point> region : regions)
      {
        if (!ConnectedRegions.regionContains(Nurikabe.CELL_WHITE, cells, region)) continue;
        boolean haveNumber = false;
        for (Point p : region)
        {
          if (state.getCellContents(p.x, p.y) > 10)
          {
            haveNumber = true;
            break;
          }
        }
        if (!haveNumber) return null;
      }

      return "No white regions (contains >1 white cell) without a number.";
    }
}
