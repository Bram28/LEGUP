/**
*  Heyawake.java
*/

package edu.rpi.phil.legup.puzzles.heyawake;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;

import edu.rpi.phil.legup.BoardImage;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;

/**
 * Heyawake Puzzle class. Extends the puzzle module class to implement a Heyawake puzzle
 *
 * @author Matt Morrow
 * @version 1.2
 */
public class Heyawake extends PuzzleModule
{
	public static int CELL_WHITE = 1;
	public static int CELL_BLACK = 2;
	
	HeyawakeEditorBoardFrame boardEditor;
	
	public Heyawake(){
	}
	
	/**
	 * Take an action when the left mouse button is pressed
	 * @param state the current board state
	 * @param x the x position where the pressed event occured
	 * @param y the y position where the pressed event occured
	 */
	//public void mousePressedEvent(BoardState state, Point where){}
	
	/**
	 * Take an action when a left mouse drag (or click) event occurs
	 * @param state
	 * @param from
	 * @param to
	 */
	public void mouseDraggedEvent(BoardState state, Point from, Point to){}
	
	/**
	 * Draw any extra data for the board
	 * @param g the Graphics to draw with
	 * @param extraData the extra data of the current board state we're drawing
	 * @param bounds the bounds of the grid
	 * @param w the width (in boxes) of the puzzle
	 * @param h the height (in boxes) of the puzzle
	 */
	public void drawExtraData(Graphics gr, ArrayList<Object> extraData, Rectangle bounds, int w, int h)
	{
		Graphics2D g = (Graphics2D)gr;
		g.setColor(Color.black);
		
		//extra data is region[] regioncount int[][]
		if(extraData == null)
		{
			JOptionPane.showMessageDialog(null, "Error with extra data: null.");
		}
		else if(extraData.size() < 3)
		{
			JOptionPane.showMessageDialog(null, "Error with extra data: too few: " + extraData.size());
		}
		else
		{
			int regionCount = ((Integer)(extraData.get(1))).intValue();
			int[][] cellRegions = (int[][])(extraData.get(2));
			Region[] boardRegions = (Region[])(extraData.get(0));
			
			
			double dx = bounds.width / (double)w;
			double dy = bounds.height / (double)h;
			Stroke thin = new BasicStroke(1);
			Stroke thick = new BasicStroke(3);
			
			if(regionCount > 0)
			{
				for (int x = 1; x < w; ++x)
				{
					for (int y = 0; y < h; ++y)
					{
						if (cellRegions[y][x - 1] != cellRegions[y][x] )
							g.setStroke(thick);
						else
							g.setStroke(thin);
						
						g.drawLine((int)(bounds.x + (dx * x)), (int)(bounds.y + (dy * y)), (int)(bounds.x + (dx * x)),  (int)(bounds.y + (dy * (y + 1))));
					}
				}
				
				for (int x = 0; x < w; ++x)
				{
					for (int y = 1; y < h; ++y)
					{
						if (cellRegions[y-1][x] != cellRegions[y][x] )
							g.setStroke(thick);
						else
							g.setStroke(thin);
						
						g.drawLine((int)(bounds.x + (dx * x)), (int)(bounds.y + (dy * y)), (int)(bounds.x + (dx * (x+1))),  (int)(bounds.y + (dy * y)));
					}
				}
			}
			
			g.setStroke(thick);
			g.drawLine(bounds.x, bounds.y, bounds.x, bounds.y + bounds.height);
			g.drawLine(bounds.x + bounds.width, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height);
			g.drawLine(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y);
			g.drawLine(bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y + bounds.height);
			g.setStroke(thin);
			
			if(regionCount > 0)
			{
				boolean[] valueDrawn = new boolean[regionCount];
				for(int cnt = 0; cnt < regionCount; ++cnt)
					valueDrawn[cnt] = false;
				
				g.setColor(Color.RED);
				g.setFont(new Font("Arial",Font.BOLD,16));
				float fontsize = g.getFont().getSize2D();
				
				Vector<Region> stateRegions = Region.getRegions(boardRegions, regionCount);
				
				for (int y = 0; y < h; ++y)
				{
					for (int x = 0; x < w; ++x)
					{
						int cellRegion = cellRegions[y][x];
						if(cellRegion > -1)
						{
							if(!valueDrawn[cellRegion] && (stateRegions.elementAt(cellRegion)).getValue() != -1)
							{
								g.drawString(String.valueOf((stateRegions.elementAt(cellRegion)).getValue()), (float)(bounds.x + (dx * x)) + 4, (float)(bounds.y + (dy * y)) + fontsize);
								valueDrawn[cellRegion] = true;
							}
						}
					}
				}
				g.setFont(new Font("Arial",Font.PLAIN,16));
			}
		}
	}
	
	public String getImageLocation(int cellValue){
		if (cellValue == 0){
			return "images/heyawake/unknown.gif";
		} else if (cellValue == 1){
		    return "images/heyawake/white.gif";
		} else if (cellValue == 2){
		    return "images/heyawake/black.gif";
		}
		else{
		    return "images/heyawake/unknown.gif";
		}	
	}
	
	public void initBoard(BoardState state)
	{
		int w  = state.getWidth();
		int h = state.getHeight();

		if(state.getExtraData().size() < 3)
		{
			state.getExtraData().clear();
			state.addExtraData(new Region[0]);
			state.addExtraData(Integer.valueOf(0));
			int[][] temp = new int[h][w];
			for (int y = 0; y < h; ++y)
			{
				for (int x = 0; x < w; ++x)
				{
					temp[y][x] = -1;
				}
			}
			state.addExtraData(temp);
		}
	}
    
    /**
     * Get all the images (as strings to the image path) used by this puzzle in the center part
     * @return an array of strings to image paths
     */
    public BoardImage[] getAllCenterImages()
    {
    	BoardImage[] s = 
    	{
    			new BoardImage("images/heyawake/unknown.gif",0),
    			new BoardImage("images/heyawake/white.gif",1),
    			new BoardImage("images/heyawake/black.gif",2),
    			
    	};
    	
    	return s;
    }
    
    /**
     * Get all the images (as strings to the image path) used by this puzzle in the border part
     * @return an array of strings to image paths
     */
    public BoardImage[] getAllBorderImages()
    {
    	BoardImage[] s = new BoardImage[0];
    	return s;
    }
	
	/**
     * Get the next label value if we're at this one (like the numbers around the border)
     * This is used when we're creating puzzles
     * 
     * @param curValue the current value of the label
     * @return the next value of the label
     */
    public int getNextLabelValue(int curValue)
    {
    	return 0;
    }
	
	public int getAbsoluteNextCellValue(int x, int y, BoardState boardState)
    {
    	int contents = boardState.getCellContents(x,y);
    	int rv = CELL_UNKNOWN;
    	
		if (contents == CELL_UNKNOWN)
		{
			rv = CELL_WHITE;
		}
		else if (contents == CELL_WHITE)
		{
			rv = CELL_BLACK;
		}
		else if (contents == CELL_BLACK)
		{
			rv = CELL_UNKNOWN;
		}
		
		return rv;
    }
	
    public int getNextCellValue(int x, int y, BoardState boardState)
    {
    	int contents = boardState.getCellContents(x,y);
    	
		if (contents == CELL_UNKNOWN)
		{
			return CELL_WHITE;
		}
		else if (contents == CELL_WHITE)
		{
			return CELL_BLACK;
		}
		else if (contents == CELL_BLACK)
		{
			return CELL_UNKNOWN;
		}
		else
		{
			return CELL_UNKNOWN;
		}
    }
	
	public boolean checkGoal(BoardState currentBoard, BoardState goalBoard){
		return currentBoard.compareBoard(goalBoard);
	}
	
	public Vector <PuzzleRule> getRules(){
		Vector <PuzzleRule> ruleList = new Vector <PuzzleRule>();
		//ruleList.add(new PuzzleRule());
		
		ruleList.add(new RuleFillRoomWhite());
		ruleList.add(new RuleFillRoomBlack());
		ruleList.add(new RuleWhiteAroundBlack());
		ruleList.add(new RuleOneUnknownWhite());
		ruleList.add(new RulePreventWhiteLine());
		ruleList.add(new RuleOneRow());
		ruleList.add(new Rule3x3());
		ruleList.add(new Rule3OnWall());
		ruleList.add(new Rule2InCorner());
		ruleList.add(new RuleZigZagWhite());
		ruleList.add(new RuleBottleNeck());
		ruleList.add(new RuleForcedBlack());
		return ruleList;
	}
	
	 /**
     * Gets a list of Contradictions associated with this puzzle
     *
     * @return A Vector of Contradictions
     */   
    public Vector <Contradiction> getContradictions()
    {
		Vector <Contradiction> contradictionList = new Vector <Contradiction>();
		//contradictionList.add(new Contradiction());
		contradictionList.add(new ContradictionAdjacentBlacks());
		contradictionList.add(new ContradictionWhiteArea());
		contradictionList.add(new ContradictionWhiteLine());
		contradictionList.add(new ContradictionRoomTooFull());
		contradictionList.add(new ContradictionRoomTooEmpty());
		
		return contradictionList;
    }
    
    public Vector <CaseRule> getCaseRules()
    {
    	Vector <CaseRule> caseRules = new Vector <CaseRule>();
    	
    	caseRules.add(new CaseBlackOrWhite());
    	caseRules.add(new CaseZigZag());
    	
    	return caseRules;
    }

    public boolean checkValidBoardState(BoardState boardState)
    {
		int height = boardState.getHeight();
		int width = boardState.getWidth();
	
		if(!checkWhiteConnected(boardState, width, height))
		{
			System.out.println("All the whites are not connected.");
			return false;
		}
		return checkWhiteConnected(boardState, width, height) && checkBlackAdjacent(boardState, width, height) && checkRoomTooEmpty(boardState) && checkWhiteLine(boardState, width, height);
	}
    
    public boolean checkWhiteConnected(BoardState boardState, int width, int height)
    {
    	//false = not checked, true = checked
    	boolean[][] neighbors = new boolean[height][width];
    	int startx = -1;
    	int starty = -1;
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			neighbors[y][x] = false;
    			if(boardState.getCellContents(x,y) != 2 && startx == -1)
    			{
    				startx = x;
    				starty = y;
    			}
    			else if(boardState.getCellContents(x,y) == 2)
    			{
    				neighbors[y][x] = true;
    			}
    		}
    	}
    	if(startx > -1)
    	{
	    	loopConnected(neighbors, boardState, startx, starty, width, height);
	    	for(int x = 0; x < width; ++x)
	    	{
	    		for(int y = 0; y < height; ++y)
	    		{
	    			if(!neighbors[y][x])
	    				return false;
	    		}
	    	}
    	}
    	
    	return true;
    }
    
    private boolean[][] loopConnected(boolean[][] neighbors,BoardState boardState, int x, int y, int width, int height)
    {
    	neighbors[y][x] = true;
    	if(x+1 < width)
    	{
    		if(!neighbors[y][x+1])
    			neighbors = loopConnected(neighbors, boardState, x+1, y, width, height);
    	}
    	if(x-1 >= 0)
    	{
    		if(!neighbors[y][x-1])
    			neighbors = loopConnected(neighbors, boardState, x-1, y, width, height);
    	}
    	if(y+1 < height)
    	{
    		if(!neighbors[y+1][x])
    			neighbors = loopConnected(neighbors, boardState, x, y+1, width, height);
    	}
    	if(y-1 >= 0)
    	{
    		if(!neighbors[y-1][x])
    			neighbors = loopConnected(neighbors, boardState, x, y-1, width, height);
    	}
    	return neighbors;
    }
    
    public boolean checkBlackAdjacent(BoardState boardState, int width, int height)
    {
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			if(boardState.getCellContents(x,y) == 2)
    			{
    				if(x+1 < width)
    				{
    					if(boardState.getCellContents(x+1,y) == 2)
    						return false;
    				}
    				if(y+1 < height)
    				{
    					if(boardState.getCellContents(x,y+1) == 2)
    						return false;
    				}
    			}
    		}
    	}
    	return true;
    }
    
    public boolean checkWhiteLine(BoardState boardState, int width, int height)
    {
    	int[][] arrayacross= new int[height][width];
    	int[][] arraydown = new int[height][width];
    	int[][]cellRegions = (int[][])boardState.getExtraData().get(2);
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			arrayacross[y][x] = arraydown[y][x] = 0;
    		}
    	}
    	
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			if(boardState.getCellContents(x,y) == 1)
    			{
    				if(x+1 < width)
    				{
    					if(boardState.getCellContents(x+1,y) == 1)
    					{
    						if( cellRegions[y][x] != cellRegions[y][x+1])
    							arrayacross[y][x+1] = arrayacross[y][x] + 1;
    						else
    							arrayacross[y][x+1] = arrayacross[y][x];
    					}
    				}
    				if(y+1 < height)
    				{
    					if(boardState.getCellContents(x,y+1) == 1)
    					{
    						if( cellRegions[y][x] != cellRegions[y+1][x])
    							arrayacross[y+1][x] = arrayacross[y][x] + 1;
    						else
    							arrayacross[y+1][x] = arrayacross[y][x];
    					}
    				}
    			}
    		}
    	}
    	
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			if(arrayacross[y][x] > 1 || arraydown[y][x] > 1)
    				return false;
    		}
    	}
    	return true;
    }
    
    public boolean checkRoomTooEmpty(BoardState boardState)
    {
    	int countwhite, countblack, countunknown, cellval;
    	Vector<CellLocation> cells;
    	CellLocation tempcell;
    	Region[] regions = (Region[])boardState.getExtraData().get(0);
    	int regionCount = ((Integer)(boardState.getExtraData().get(1))).intValue();
    	for(int indx = 0; indx < regionCount; ++indx)
    	{
    		countwhite = countblack = countunknown = 0;
    		cells = regions[indx].getCells();
    		if(cells.size() > 0)
    		{
	    		for(int c = 0; c < cells.size(); ++c)
	    		{
	    			tempcell = cells.get(c);
	    			cellval = boardState.getCellContents(tempcell.x, tempcell.y);
	    			if(cellval == CELL_WHITE)
	    			{
	    				++countwhite;
	    			}
	    			else if(cellval == CELL_BLACK)
	    			{
	    				++countblack;
	    			}
	    			else
	    			{
	    				++countunknown;
	    			}
	    		}
	    		if(countblack + countunknown < regions[indx].getValue() && regions[indx].getValue() > -1)
	    		{
	    			return false;
	    		}
    		}
    		
    	}
    	
    	return true;
    }
    
    public boolean checkRoomTooFull(BoardState boardState)
    {
    	int countwhite, countblack, countunknown, cellval;
    	Vector<CellLocation> cells;
    	CellLocation tempcell;
    	Region[] regions = (Region[])boardState.getExtraData().get(0);
    	int regionCount = ((Integer)(boardState.getExtraData().get(1))).intValue();
    	for(int indx = 0; indx < regionCount; ++indx)
    	{
    		countwhite = countblack = countunknown = 0;
    		cells = regions[indx].getCells();
    		if(cells.size() > 0)
    		{
	    		for(int c = 0; c < cells.size(); ++c)
	    		{
	    			tempcell = cells.get(c);
	    			cellval = boardState.getCellContents(tempcell.x, tempcell.y);
	    			if(cellval == CELL_WHITE)
	    			{
	    				++countwhite;
	    			}
	    			else if(cellval == CELL_BLACK)
	    			{
	    				++countblack;
	    			}
	    			else
	    			{
	    				++countunknown;
	    			}
	    		}
	    		if(countblack > regions[indx].getValue() && regions[indx].getValue() > -1)
	    		{
	    			return false;
	    		}
    		}
    		
    	}
    	
    	return true;
    }
    
    public boolean checkRoomCount(BoardState boardState)
    {
    	int countwhite, countblack, countunknown, cellval;
    	Vector<CellLocation> cells;
    	CellLocation tempcell;
    	Region[] regions = (Region[])boardState.getExtraData().get(0);
    	int regionCount = ((Integer)(boardState.getExtraData().get(1))).intValue();
    	for(int indx = 0; indx < regionCount; ++indx)
    	{
    		countwhite = countblack = countunknown = 0;
    		cells = regions[indx].getCells();
    		if(cells.size() > 0)
    		{
	    		for(int c = 0; c < cells.size(); ++c)
	    		{
	    			tempcell = cells.get(c);
	    			cellval = boardState.getCellContents(tempcell.x, tempcell.y);
	    			if(cellval == CELL_WHITE)
	    			{
	    				++countwhite;
	    			}
	    			else if(cellval == CELL_BLACK)
	    			{
	    				++countblack;
	    			}
	    			else
	    			{
	    				++countunknown;
	    			}
	    		}
	    		if(countblack + countunknown < regions[indx].getValue() && regions[indx].getValue() > -1)
	    		{
	    			return false;
	    		}
	    		if(countblack > regions[indx].getValue() && regions[indx].getValue() > -1)
	    		{
	    			return false;
	    		}
    		}
    		
    	}
    	
    	return true;
    }
    
    public boolean editExtraData(BoardState boardState, edu.rpi.phil.legup.editor.PuzzleEditor peditor)
    {
    	if(boardEditor == null)
    	{
	    	boardEditor = new HeyawakeEditorBoardFrame(boardState, this, peditor);
	    	peditor.setEditorVisible(false);
	    	
	    	//if trying modal use this:
	    	//comment out line 503 HeyawakeEditorBoardFrame
	    	//boardEditor.setModal(true);
	    	//boardEditor.setVisible(true);
	    	//JOptionPane.showMessageDialog(null, "Done."); //this should pop up only after the window has closed
	    	//return false;
	    	
	    	//if non-modal use this:
	    	//Also, the on window close needs to let the puzzle editor know that the extra data editor is finished
	    	//uncomment line 503 HeyawakeEditorBoardFrame
	    	boardEditor.setVisible(true);
    	}
    	if(!boardEditor.isVisible())
    	{
    		boardEditor.curState = boardState;
    		boardEditor.setVisible(true);
    	}
    	return true;
    }
    public BoardState guess(BoardState Board) 
    {
		// out of forced moves, need to guess
		Point guess = GenerateBestGuess(Board);
		// guess, if we found one
		if (guess.x != -1 && guess.y != -1) 
		{
			BoardState Parent = Board.getSingleParentState();
			BoardState CaseBlack = Board;
			BoardState CaseWhite = Parent.addTransitionFrom();
			CaseBlack.setCellContents(guess.x, guess.y, CELL_BLACK);
			CaseWhite.setCellContents(guess.x, guess.y, CELL_WHITE);
			Parent.setCaseSplitJustification(new CaseBlackOrWhite());
			System.out.println("Guessed at "+guess.x+","+guess.y);
			//Legup.setSelection(CaseTent,false);
			return CaseBlack;
		}
		// if we didn't then the board is full, and we are finished (thus, the returned board will be the same as the one we were given
		System.out.println("Statement: Your puzzle has been solved already. Why do you persist?");
		return Board;
	}
    private Point GenerateBestGuess(BoardState Board) {
		// this should more properly be some kind of ranking system whereby different
		// conditions scored points and the highest scoring square was chosen.
		// until there is more time to actually watch the AI, it scores based on closeness
		// to a probability. In the future, it might include points for having only one extra
		// free space or something like that.
		int currentX=-1;
		int currentY=-1;
		int height = Board.getHeight();
		int width = Board.getWidth();
		double currentOff = Double.POSITIVE_INFINITY;
		double BESTPROB = .25;
		Region bestRegion=null;
		//Create a counter that will hold the number of white regions found
		int regioncount = 0;
		//Booleans which hold whether or not a cell is valid for the rule
		boolean[][]white = new boolean[width][height];
		ArrayList<Object> extraData = Board.getExtraData();
		Region[] regions = (Region[])extraData.get(0);
		Region curRegion;
		CellLocation curCell;
		Point tempPoint;
		Point temp;
		//For each cell
		for(int r = 0;r<regions.length;r++)
		{
			curRegion = regions[r];
			int cUnknown=0;
			for(int c = 0; c<curRegion.getCells().size();c++)
			{
				curCell = curRegion.getCells().get(c);
				if(Board.getCellContents(curCell.getX(),curCell.getY()) == CELL_UNKNOWN)
				{
					cUnknown++;
					
				}
			}
			if(cUnknown==0)
				continue;
			double myProb;
			if(curRegion.getValue()>0)
				myProb = (cUnknown/curRegion.getValue());
			else
				myProb = 10000;
			double myOff = Math.abs(BESTPROB-myProb);
			if(myOff<currentOff)
			{
				
				bestRegion = curRegion;
				currentOff=myOff;
			}
		}
		if(bestRegion == null)
		{
			return new Point(currentX,currentY);
		}
		System.out.println("Updated");
		for(int c = 0; c<bestRegion.getCells().size();c++)
		{
			curCell = bestRegion.getCells().get(c);
			System.out.println(curCell);
			if(Board.getCellContents(curCell.getX(),curCell.getY()) == CELL_UNKNOWN)
			{
				currentX=curCell.getX();
				currentY=curCell.getY();
				break;
			}
		}
		return new Point(currentX,currentY);
	}
}
