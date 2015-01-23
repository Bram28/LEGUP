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
		//boolean split;

		System.out.println("Check 1");
		// Check for only one branch
		if (destBoardState.getTransitionsTo().size() != 1)
		{
			error = "This rule only involves having a single branch!";
		}
		else
		{
			System.out.println("Check 2");
//			Vector<Point> changedPoints;
			for (int y = 0; y < height && error == null; ++y)
			{
				for (int x = 0; x < width; ++x)
				{
					int origState = origBoardState.getCellContents(x,y);
					int newState = destBoardState.getCellContents(x,y);

					if (origState != newState)
					{
						changed = true;

						System.out.println("Check 3");

						if (newState == Nurikabe.CELL_WHITE || newState == Nurikabe.CELL_UNKNOWN)
						{
							error = "You must either add all white or all black cells!";
							break;
						}
						else
						{
							// altBoard.setCellContents(x,y,Nurikabe.CELL_BLACK);
							// split = checkSplit(altBoard);
							// altBoard.setCellContents(x,y,Nurikabe.CELL_WHITE);
							// if(split==false)
							// {
							// 	error = "You must add a cell because of a bottleneck to use this rule!";
							// 	break;
							// }
//							if (newState == Nurikabe.CELL_BLACK)
//								usingBlack = true;
//							else
//								usingWhite = true;
//							changedPoints.pushBack(Point(x, y));
							System.out.println("Check 4");

							if (checkForBottleNeck(new Point(x, y), origBoardState, width, height) != true)
							{
								error = "The cell at (" + x + ", " + y + ") is not a bottle neck!";
								break;
							}
						}
					}
				}
			}

			System.out.println("Check 5");


			if (error == null && !changed)
			{
				error = "You must add either one or more black cells to use this rule!";
			}
		}
//		System.out.println(error);
		return error;
	}

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

//	private boolean touchingCell(BoardState board, Point p) {
//		int targetType = board.getCellContents(p.x, p.y);
//		bool touches = false;
//		if (board.getCellContents(p.x+1, p.y) == targetType ||
//				board.getCellContents(p.x-1, p.y) == targetType ||
//				board.getCellContents(p.x, p.y+1) == targetType ||
//				board.getCellContents(p.x, p.y-1) == targetType) {
//
//					return true;
//
//				}
//
//		else return false;
//	}

	// boolean checkSplit(BoardState board)
	// {
	// 	int tempSize=0;
	// 	int prevSize=0;
	// 	int parts = 0;
	// 	int height = board.getHeight();
	// 	int width  = board.getWidth();
	// 	for(int x=0; x<board.getWidth();x++)
	// 	{
	// 		for(int y = 0; y<board.getHeight();y++)
	// 		{
	// 			tempSize= loopConnected(new int[width][height],board,x,y);
	// 			if(tempSize<2)
	// 				continue;
	// 			if(prevSize==0)
	// 				prevSize=tempSize;
	// 			if(1<Math.abs(tempSize-prevSize))
	// 			{
	// 				System.out.println(x+" "+y+" , "+tempSize);
	// 				return true;
	// 			}
	// 		}
	// 	}
	// 	return false;
	// }
	// private int loopConnected(int[][] visited,BoardState boardState, int x, int y)
	// {
	// 	//If we have been here before return nothing
	// 	if(visited[x][y] == 1 || visited[x][y] == -1)
	// 		return 0;
	//
	// 	//Set the cell to visited but not white
	// 	visited[x][y] = -1;
	//
	// 	//If the cell is black return nothing
	// 	if(boardState.getCellContents(x,y) == Nurikabe.CELL_BLACK)
	// 		return 0;
	//
	// 	int width = boardState.getWidth();
	// 	int height= boardState.getHeight();
	//
	// 	//At this point we know the cell is white or unknown
	// 	visited[x][y] = 1;
	//
	// 	//A counter of how many unknowns/whites we have found
	// 	int ret;
	// 	if(boardState.getCellContents(x,y)>0)
	// 		ret = boardState.getCellContents(x,y)-1;
	// 	else if(boardState.getCellContents(x,y)==Nurikabe.CELL_WHITE)
	// 		ret = boardState.getCellContents(x,y)+1;
	// 	else
	// 		ret = 1;
	//
	// 	//Temporary integer
	// 	int temp;
	//
	// 	//Essentially if there is a cell in the adjacent direction, see how many unknowns we can find from it
	// 	//Return the sum of those unknowns if we didn't find a -1
	// 	if(x+1 < width)
	// 	{
	// 		temp = loopConnected(visited, boardState, x+1, y);
	// 		ret += temp;
	// 	}
	// 	if(x-1 >= 0)
	// 	{
	// 		temp = loopConnected(visited, boardState, x-1, y);
	// 		ret += temp;
	// 	}
	// 	if(y+1 < height)
	// 	{
	// 		temp = loopConnected(visited, boardState, x, y+1);
	// 		ret += temp;
	// 	}
	// 	if(y-1 >= 0)
	// 	{
	// 		temp = loopConnected(visited, boardState, x, y-1);
	// 		ret += temp;
	// 	}
	// 	return ret;
	// }

//	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
//	{
	// 	BoardState origBoardState = destBoardState.getSingleParentState();
	// 	boolean changed = false;
	// 	int width = destBoardState.getWidth();
	// 	int height = destBoardState.getHeight();
	// 	BoardState altBoard = destBoardState.copy();
	// 	boolean split=false;
	// 	String error = null;
	// 	if (origBoardState != null && destBoardState.getTransitionsTo().size() == 1)
	// 	{
	// 		for(int x= 0; x<width;x++)
	// 		{
	// 			if(split==true)
	// 				break;
	// 			for(int y = 0; y<height;y++)
	// 			{
	// 				if(origBoardState.getCellContents(x,y)==Nurikabe.CELL_UNKNOWN)
	// 				{
	// 					altBoard.setCellContents(x,y,Nurikabe.CELL_BLACK);
	// 					split = checkSplit(altBoard);
	// 					altBoard.setCellContents(x,y,Nurikabe.CELL_UNKNOWN);
	// 					if(split==true)
	// 					{
	// 						changed = true;
	// 						System.out.println(x+" "+y);
	// 						destBoardState.setCellContents(x,y,Nurikabe.CELL_WHITE);
	// 						break;
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}
	// 	error = checkRuleRaw(destBoardState);
	// 	changed = (error==null);
	//
	// 	if(changed==false)
	// 	{
	// 		destBoardState = origBoardState.copy();
	// 	}
	//
	// 	return changed;
	// }
}
