package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.puzzles.nurikabe.Nurikabe;

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
	  * @return true if the black cell is isolated
	  */
//	private boolean contradictionCheckerHelper(BoardState state, boolean[][] visited, int x, int y, int width, int height) {
//		int blackCount = 0;
//		for(int dx = -1;dx<2;dx++){
//			{
//				for(int dy = -1; dy<2;dy++)
//				{
//					if(dx!=0 && dy!=0)
//						continue;
//					if((dx+x>=width) || (dx+x<0))
//						continue;
//					if((dy+y>=height) || (dy+y<0))
//						continue;
//					if(state.getCellContents(x+dx,y+dy)==Nurikabe.CELL_WHITE)
//						blackCount++;
//				}
//			}
//			if(blackCount == 4)
//			{
//				return true;
//			}
//		}
//		return false;
//	}
	
	/**
	 * Basically, this will recursively check all possible paths for black from the initial (x, y), if it runs into
	 * a white than it is considered a barrier, running into 4 whites would mean that this black is isolated
	 */
	private int countWhiteBounds(BoardState state, boolean[][] visited, int x, int y, int width, int height) {
		int whiteCount = 0;
		if(state.getCellContents(x, y) == Nurikabe.CELL_BLACK && !visited[x][y]){
			return 0;
		}
		visited[x][y] = true;
		if(state.getCellContents(x, y) == Nurikabe.CELL_WHITE) {
			//base case
			return 1;
		}
		if(x+1 < width) {
			whiteCount += countWhiteBounds(state, visited, x+1, y, width, height);
		}
		if(x-1 > 0){
			whiteCount += countWhiteBounds(state, visited, x-1, y, width, height);
		}
		if(y+1 < height){
			whiteCount += countWhiteBounds(state, visited, x, y+1, width, height);
		}
		if(y-1 > 0){
			whiteCount += countWhiteBounds(state, visited, x, y-1, width, height);
		}
		return whiteCount;
	}
	
	
	/*
	 * All of the following helper functions will return a false if white (blocked), or true if black (there is a path)
	 */
//	private boolean checkAbove(BoardState state, boolean[][] visited, int x, int y, int width, int height) {
//		if (y < 0) {
//			//if it is at the upper bound than return false (the path is blocked going up)
//			return false;
//		}
//		if (state.getCellContents(x, y) == Nurikabe.CELL_WHITE) {
//			//if the cell is white than the path is blocked (return false)
//			visited[x][y] = true;
//			return false;
//		} else if (state.getCellContents(x, y) == Nurikabe.CELL_BLACK) {
//			//if the cell is black then there is a path, however we say that this black cell is not visited
//			//so that we can check it for paths to other black cells
//			return true;
//		} else {
//			//getting here means the cell is unknown, meaning there could still be a path up so we recursively check that.
//			visited[x][y] = true;
//			checkAbove(state, visited, x, y-1, width, height);
//		}
//	}
//	private boolean checkBelow(BoardState state, boolean[][] visited, int x, int y, int width, int height) {
//		if (state.getCellContents == )
//	}
//	private boolean checkRight(BoardState state, boolean[][] visited, int x, int y, int width, int height) {
//		
//	}
//	private boolean checkLeft(BoardState state, boolean[][] visited, int x, int y, int width, int height) {
//		
//	}
	
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
    	boolean first = true;
    	boolean checked[][] = new boolean[height][width];
    	boolean visited[][] = new boolean[width][height];
    	for(int i = 0; i < width; i++){
    		for(int j = 0; j < height; j++){
    			//recursive wrapper
    			if (state.getCellContents(i, j) == Nurikabe.CELL_BLACK){
    				visited[i][j] = true;
    				int numBarriers = countWhiteBounds(state, visited, i, j, width, height);
    				System.out.println("numBarriers = at (" + i + ", " + j + ") = " + numBarriers);
    				if (numBarriers > 3){
    					return null;
    				}
    			}
    		}
    	}
        return "ERROR";
    }
}