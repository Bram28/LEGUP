package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.puzzles.nurikabe.Nurikabe;
import java.awt.Point;
import java.util.Vector;


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
	 * Checks if the black square at start can connect with the black square at end
	 * @param start A Point where the first black square is, or where the path should start
	 * @param end A Point where the second black square is, or where the path should end 
	 * @param width The width of the board
	 * @param height The height of the board
	 * @param state The current BoardState
	 * @param visited A list of all spaces that have been previously visited
	 * @return true if a path exists between the two points, false otherwise
	 */
	private boolean path(Point start, Point end, int width, int height, BoardState state, boolean[][] visited) {
		//if the distance from point start to point end is only 1.0 we know they are adjacent and there is a path
		if (start.distance(end) == 1.0) {
			return true;
		}
		
		//set this point as visited
		visited[start.x][start.y] = true;
		
		/*If a point adjacent to start is (1) in bounds, (2) either black or unknown, and
		 * (3) not yet visited, then we recursively check to see if a path exists from that point
		 * to the end point, if a path exists we return true. Otherwise we move on to the next
		 * adjecent point. If all options are exhausted and no path is found, we know the two points
		 * are isolated and can return false
		 */
		
		//check right
		if (start.x + 1 < width 
				&& (state.getCellContents(start.x+1, start.y) == Nurikabe.CELL_BLACK
					|| state.getCellContents(start.x+1, start.y) == Nurikabe.CELL_UNKNOWN)
				&& !visited[start.x+1][start.y]) {
			Point right = new Point(start.x+1, start.y);
			if(path(right, end, width, height, state, visited))
				return true;
		}
		
		//check left
		if (start.x - 1 > 0 
				&& (state.getCellContents(start.x-1, start.y) == Nurikabe.CELL_BLACK
					|| state.getCellContents(start.x-1, start.y) == Nurikabe.CELL_UNKNOWN)
				&& !visited[start.x-1][start.y]) {
			Point left = new Point(start.x-1, start.y);
			if(path(left, end, width, height, state, visited))
				return true;
		}
		
		//check up
		if (start.y + 1 < height 
				&& (state.getCellContents(start.x, start.y+1) == Nurikabe.CELL_BLACK
					|| state.getCellContents(start.x, start.y+1) == Nurikabe.CELL_UNKNOWN)
				&& !visited[start.x][start.y+1]) {
			Point up = new Point(start.x, start.y+1);
			if(path(up, end, width, height, state, visited))
				return true;
		}
		
		//check down
		if (start.y - 1 > 0 
				&& (state.getCellContents(start.x, start.y-1) == Nurikabe.CELL_BLACK
					|| state.getCellContents(start.x, start.y-1) == Nurikabe.CELL_UNKNOWN)
				&& !visited[start.x][start.y-1]) {
			Point down = new Point(start.x, start.y-1);
			if(path(down, end, width, height, state, visited))
				return true;
		}
		
		//if no paths exist then return false
		return false;
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
    	Vector<Point> blacks = new Vector<Point>();
    	
    	//find all blacks currently on the board
    	for (int x = 0; x < height; x++) {
    		for (int y = 0; y < width; y++) {
    			if (state.getCellContents(x, y) == Nurikabe.CELL_BLACK) {
    				blacks.add(new Point(x, y));
    			}
    		}
    	}
    	
    	//go through all blacks and check if one does not connect
    	for (int i = 0; i < blacks.size()-1; i++) {
    		System.out.println("call with (" + blacks.get(i).x + ", " + blacks.get(i).y + ")");
    		//if some connection cannot be made then a black cell is isolated and the contradiction was applied correctly
    		if (!path(blacks.get(i), blacks.get(i+1), width, height, state, new boolean[width][height])) {
    			System.out.println("No Connection from " + blacks.get(i) + " to " + blacks.get(i+1));
    			return null;
    		}
    	}
    	//if all black cells connect then there are no isolated cells and the contradiction was not applied correctly
    	return "ERROR";
    }
}