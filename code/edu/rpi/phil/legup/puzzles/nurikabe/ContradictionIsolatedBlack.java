package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.ConnectedRegions;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.puzzles.nurikabe.Nurikabe;
import java.awt.Point;
import java.util.List;
import java.util.Set;


public class ContradictionIsolatedBlack extends Contradiction
{
    private static final long serialVersionUID = 450786104L;


	ContradictionIsolatedBlack()
	{
		setName("Black cells cannot be isolated");
		description = "There must still be a possibility to connect every Black cell";
		image = new ImageIcon("images/nurikabe/contradictions/BlackArea.png");
	}

	public String getImageName()
	{
		return "images/nurikabe/contradictions/BlackArea.png";
	}

	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    protected String checkContradictionRaw(BoardState state)
    {
    	int height = state.getHeight();
    	int width = state.getWidth();

    	//Put all cells into array for connected regions method
    	int[][] cells = new int[width][height];
    	for (int x = 0; x < width; x++) {
    		for (int y = 0; y < height; y++) {
    			if (state.getCellContents(x, y) == Nurikabe.CELL_UNKNOWN || state.getCellContents(x, y) == Nurikabe.CELL_BLACK) {
        			cells[x][y] = state.getCellContents(x, y);
    			} else {
    				cells[x][y] = Nurikabe.CELL_WHITE;
    			}
    		}
    	}

    	//Find all regions
    	List<Set<Point>> regions = ConnectedRegions.getConnectedRegions(Nurikabe.CELL_WHITE, cells, width, height);

    	//If there are 2 sepearate regions both containing black then the contradiction was applied correctly
    	int numRegionsWithBlack = 0;
    	for(Set<Point> region: regions) {
    		if (ConnectedRegions.regionContains(Nurikabe.CELL_BLACK, cells, region)) {
    			numRegionsWithBlack++;
    		}
    	}
    	if (numRegionsWithBlack > 1) return null;
    	else return "Contradiction applied incorrectly. No isolated Blacks.";
    }
}
