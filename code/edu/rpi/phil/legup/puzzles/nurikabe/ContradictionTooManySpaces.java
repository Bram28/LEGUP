package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;
import java.awt.Point;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.ConnectedRegions;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionTooManySpaces extends Contradiction
{
    private static final long serialVersionUID = 657974076L;

	 ContradictionTooManySpaces()
	 {
		setName("Too Many Spaces");
		description = "A region cannot contain more spaces than its number.";
		image = new ImageIcon("images/nurikabe/contradictions/TooManySpaces.png");
	 }

	public String getImageName()
	{
		return "images/nurikabe/contradictions/TooManySpaces.png";
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

    	//Put all cells into array for connected regions method
    	int[][] cells = new int[height][width];
    	for (int x = 0; x < width; x++) {
    		for (int y = 0; y < height; y++) {
    			if (state.getCellContents(x, y) == Nurikabe.CELL_UNKNOWN || state.getCellContents(x, y) == Nurikabe.CELL_BLACK) {
        		cells[y][x] = state.getCellContents(x, y);
    			} else {
    				cells[y][x] = Nurikabe.CELL_WHITE;
    			}
    		}
    	}

    	//Find all regions
      LinkedHashSet<Integer> boundryCells = new LinkedHashSet<Integer>();
      boundryCells.add(Nurikabe.CELL_UNKNOWN);
      boundryCells.add(Nurikabe.CELL_BLACK);
    	List<Set<Point>> regions = ConnectedRegions.getConnectedRegions(boundryCells, cells, width, height);

      for (Set<Point> r : regions) {
        int smallestNum = -1;
        boolean numFound = false;
        for (Point p : r) {
          int contents = state.getCellContents(p.x, p.y);
          if (contents > 10) {
            //if the cell content is a number
            if (!numFound) {
              numFound = true;
              smallestNum = contents-10;
            } else if (contents < smallestNum) {
              smallestNum = contents-10;
            }
          }
        }
        if (numFound && r.size() > smallestNum) {
          return null;
        }
      }

      return "There are not too many spaces in any region.";

//     	String error = null;
//     	int height = state.getHeight();
//     	int width = state.getWidth();
//
// //    	false = not checked, true = checked
//     	boolean[][] neighbors = new boolean[height][width];
//     	for(int x = 0; x < width; ++x)
//     	{
//     		for(int y = 0; y < height; ++y)
//     		{
//     			if(state.getCellContents(x,y) == Nurikabe.CELL_BLACK || state.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN )
//     			{
//     				neighbors[y][x] = true;
//     			}
//     		}
//     	}
//     	Point temp = new Point();
//     	for(int x = 0; x < width; ++x)
//     	{
//     		for(int y = 0; y < height; ++y)
//     		{
//     			if(!neighbors[y][x])
//     			{
//     				temp = loopConnected(neighbors, state,x,y,width,height);
//     				if(temp.y == 0)
//     				{
//     					;//return "Different contradiction found. No number found in a region.";
//     				}
//     				else if(temp.y == -1)
//     				{
//     					return "Different contradiction found. Too many numbers in a region";
//     				}
//     				else if(temp.y < temp.x)
//     					return null;
//     			}
//     		}
//     	}
//
//     	error = "There are not too many spaces in a region.";
//
// 		return error;
    }

    // //HACK: This uses a point in order to return 2 ints
    // private Point loopConnected(boolean[][] neighbors,BoardState boardState, int x, int y, int width, int height)
    // {
    // 	Point numcount = new Point(0,0);
    // 	Point temp = new Point();
    // 	if(boardState.getCellContents(x,y) - 10 > 0 )
    // 	{
    // 		numcount.x += 1;
    // 		numcount.y = boardState.getCellContents(x,y) - 10;
    // 	}
    // 	else if(boardState.getCellContents(x,y) == Nurikabe.CELL_WHITE)
    // 		numcount.x += 1;
    // 	neighbors[y][x] = true;
    // 	if(x+1 < width)
    // 	{
    // 		if(!neighbors[y][x+1])
    // 		{
    // 			temp = loopConnected(neighbors, boardState, x+1, y, width, height);
    // 			numcount.x += temp.x;
    // 			if(temp.y != 0)
    // 			{
    // 				if(numcount.y == 0)
    // 					numcount.y = temp.y;
    // 				else
    // 					numcount.y = -1;
    // 			}
    // 		}
    // 	}
    // 	if(x-1 >= 0)
    // 	{
    // 		if(!neighbors[y][x-1])
    // 		{
    // 			temp = loopConnected(neighbors, boardState, x-1, y, width, height);
		// 		numcount.x += temp.x;
		// 		if(temp.y != 0)
		// 		{
		// 			if(numcount.y == 0)
		// 				numcount.y = temp.y;
		// 			else
		// 				numcount.y = -1;
		// 		}
    // 		}
    // 	}
    // 	if(y+1 < height)
    // 	{
    // 		if(!neighbors[y+1][x])
    // 		{
    // 			temp = loopConnected(neighbors, boardState, x, y+1, width, height);
		// 		numcount.x += temp.x;
		// 		if(temp.y != 0)
		// 		{
		// 			if(numcount.y == 0)
		// 				numcount.y = temp.y;
		// 			else
		// 				numcount.y = -1;
		// 		}
    // 		}
    // 	}
    // 	if(y-1 >= 0)
    // 	{
    // 		if(!neighbors[y-1][x])
    // 		{
    // 			temp = loopConnected(neighbors, boardState, x, y-1, width, height);
		// 		numcount.x += temp.x;
		// 		if(temp.y != 0)
		// 		{
		// 			if(numcount.y == 0)
		// 				numcount.y = temp.y;
		// 			else
		// 				numcount.y = -1;
		// 		}
    // 		}
    // 	}
    // 	return numcount;
    // }
}
