package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.ConnectedRegions;
import java.awt.Point;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class RuleBottleNeck extends PuzzleRule
{
	private static final long serialVersionUID = 787962510L;

	RuleBottleNeck()
	{
		setName("Black Bottle Neck");
		description = "If there is only one path for a black square to continue than follow that path.";
		image = new ImageIcon("images/nurikabe/rules/OneUnknownBlack.png");
	}
	public String getImageName()
	{
		return "images/nurikabe/rules/OneUnknownBlack.png";
	}
	protected String checkRuleRaw(BoardState destBoardState)
	{
		String error = null;
		boolean changed = false;
		BoardState origBoardState = destBoardState.getSingleParentState();
		BoardState altBoard = destBoardState.copy();
		int width = origBoardState.getWidth();
		int height = origBoardState.getHeight();

		// Check for only one branch
		if (destBoardState.getTransitionsTo().size() != 1)
		{
			error = "This rule only involves having a single branch!";
		}
		else
		{
			for (int y = 0; y < height && error == null; ++y)
			{
				for (int x = 0; x < width; ++x)
				{
					int origState = origBoardState.getCellContents(x,y);
					int newState = destBoardState.getCellContents(x,y);

					if (origState != newState)
					{
						changed = true;
						if (newState == Nurikabe.CELL_WHITE || newState == Nurikabe.CELL_UNKNOWN)
						{
							error = "You must either add all white or all black cells!";
							break;
						}
						else
						{
							if (checkForBottleNeck(new Point(x, y), origBoardState, width, height) != true)
							{
								error = "The cell at (" + x + ", " + y + ") is not a bottle neck!";
								break;
							}
						}
					}
				}
			}

			if (error == null && !changed)
			{
				error = "You must add either one or more black cells to use this rule!";
			}
		}
		return error;
	}

	//returns true if the rule is applied correctly, false otherwise
	private boolean checkForBottleNeck(Point target, BoardState state, int width, int height)
	{
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
    	//The target cell is where we are checking for a bottle neck
    	//We mark it as white, and if that creates 2 discrete regions we know there is a bottle neck
    	cells[target.x][target.y] = Nurikabe.CELL_WHITE;
    	
    	//Find all regions
    	List<Set<Point>> regions = ConnectedRegions.getConnectedRegions(Nurikabe.CELL_WHITE, cells, width, height);

    	//If there are 2 sepearate regions both containing black then the contradiction was applied correctly
    	int numRegionsWithBlack = 0;
    	for(Set<Point> region: regions) {
    		if (ConnectedRegions.regionContains(Nurikabe.CELL_BLACK, cells, region)) {
    			numRegionsWithBlack++;
    		}
    	}
    	if (numRegionsWithBlack > 1) return true;
    	else return false;
	}
}
