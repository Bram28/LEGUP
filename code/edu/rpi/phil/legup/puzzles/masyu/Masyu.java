package edu.rpi.phil.legup.puzzles.masyu;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Vector;

import edu.rpi.phil.legup.BoardImage;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;


// TODO: Auto-generated Javadoc
/**
 * The Class Masyu.
 */
public class Masyu extends PuzzleModule
{
	/**
	 * The Constructor.
	 */
	public Masyu(){
	}
	
	/**
	 * Take an action when the left mouse button is pressed.
	 * 
	 * @param where the position where the pressed event occured
	 * @param state the current board state
	 */
	public void mousePressedEvent(BoardState state, Point where)
	{
		
	}
	
	/**
	 * Take an action when a left mouse drag (or click) event occurs.
	 * 
	 * @param to the to
	 * @param state the state
	 * @param from the from
	 */
	public void mouseDraggedEvent(BoardState state, Point from, Point to)
	{
		if (from.equals(to))
		{ // click
			int next = getNextCellValue(from.x,from.y,state);
			state.setCellContents(from.x,from.y,next);
		}
		else
		{ // drag, create link, or remove it
			int toVal = state.getCellContents(to.x, to.y);
			int fromVal = state.getCellContents(from.x, from.y);
			
			if(from.x == to.x && (from.y - to.y == 1 || from.y - to.y == -1))
			{
				if(to.y > from.y)
				{
					//dest is below
					if(((toVal & 1) == 1) == ((fromVal & 4) == 4) && ((toVal & 1) == 1))
					{
						//remove the link
						state.setCellContents(to.x, to.y, toVal ^ 1);
						state.setCellContents(from.x, from.y, fromVal ^ 4);
					}
					else
					{
						//add the link
						state.setCellContents(to.x, to.y, toVal | 1);
						state.setCellContents(from.x, from.y, fromVal | 4);
					}
				}
				else
				{
					//dest is above
					if(((toVal & 4) == 4) == ((fromVal & 1) == 1) && ((toVal & 4) == 4))
					{
						//remove the link
						state.setCellContents(to.x, to.y, toVal ^ 4);
						state.setCellContents(from.x, from.y, fromVal ^ 1);
					}
					else
					{
						//add the link
						state.setCellContents(to.x, to.y, toVal | 4);
						state.setCellContents(from.x, from.y, fromVal | 1);
					}
				}
			}
			else if(from.y == to.y && (from.x - to.x == 1 || from.x - to.x == -1))
			{
				if(to.x > from.x)
				{
					//dest is to the right
					if(((toVal & 8) == 8) == ((fromVal & 2) == 2) && ((toVal & 8) == 8))
					{
						//remove the link
						state.setCellContents(to.x, to.y, toVal ^ 8);
						state.setCellContents(from.x, from.y, fromVal ^ 2);
					}
					else
					{
						//add the link
						state.setCellContents(to.x, to.y, toVal | 8);
						state.setCellContents(from.x, from.y, fromVal | 2);
					}
				}
				else
				{
					//dest is to the left
					if(((toVal & 2) == 2) == ((fromVal & 8) == 8) && ((toVal & 2) == 2))
					{
						//remove the link
						state.setCellContents(to.x, to.y, toVal ^ 2);
						state.setCellContents(from.x, from.y, fromVal ^ 8);
					}
					else
					{
						//add the link
						state.setCellContents(to.x, to.y, toVal | 2);
						state.setCellContents(from.x, from.y, fromVal | 8);
					}
				}
			}
			else
			{
				//error
			}
		}
		
	}
	
	/**
	 * Gets the location of an image which is used by this.
	 * 
	 * @param cellValue the cell value
	 * 
	 * @return the image location
	 * 
	 * @see edu.rpi.phil.legup.PuzzleModule#getImageLocation(int)
	 */
	public String getImageLocation(int cellValue){
			return "images/masyu/" + Integer.valueOf(cellValue).toString() + ".gif";
	}
	
	/**
	 * Initializes the board, does nothing at this point.
	 * 
	 * @param state the state
	 * 
	 * @see edu.rpi.phil.legup.PuzzleModule#initBoard(edu.rpi.phil.legup.BoardState)
	 */
	public void initBoard(BoardState state)
	{
		
	}
	
	/**
	 * Get all the images (as strings to the image path) used by this puzzle in the center part.
	 * 
	 * @return an array of strings to image paths
	 */
	public BoardImage[] getAllCenterImages()
	{
		BoardImage[] s = new BoardImage[48]; 
		{
				for(int x = 0; x < 48; ++x)
					s[x] = new BoardImage("images/masyu/" + Integer.valueOf(x).toString() + ".gif",x);
				
		}
		
		return s;
	}
	
	/**
	 * Get all the images (as strings to the image path) used by this puzzle in the border part.
	 * 
	 * @return an array of strings to image paths
	 */
	public BoardImage[] getAllBorderImages()
	{
		BoardImage[] s = new BoardImage[0];
		return s;
	}
	
	/**
	 * Get the next label value if we're at this one (like the numbers around the border)
	 * This is used when we're creating puzzles.
	 * 
	 * @param curValue the current value of the label
	 * 
	 * @return the next value of the label
	 */
	public int getNextLabelValue(int curValue)
	{
		return 0;
	}
	
	/**
	 * Gets the next cell value when creating puzzles.
	 * 
	 * @param y the y
	 * @param x the x
	 * @param boardState the board state
	 * 
	 * @return the absolute next cell value
	 * 
	 * @see edu.rpi.phil.legup.PuzzleModule#getAbsoluteNextCellValue(int, int, edu.rpi.phil.legup.BoardState)
	 */
	public int getAbsoluteNextCellValue(int x, int y, BoardState boardState)
	{
		//TODO: Break apart, comment, figure out perhaps how to optimize
		//also, perhaps if in the previous cell already have a line prevent removal
		int contents = boardState.getCellContents(x,y);
		int rv = contents;
		boolean bu,br,bd,bl;
		bu = br = bd = bl = false;
		if(x > 0)
		{
			bl = ((boardState.getCellContents(x-1, y) & 2) == 2);
		}
		if(x < boardState.getWidth() - 1)
		{
			br = ((boardState.getCellContents(x+1, y) & 8) == 8);
		}
		if(y > 0)
		{
			bu = ((boardState.getCellContents(x, y-1) & 4) == 4);
		}
		if(y < boardState.getHeight() - 1)
		{
			bd = ((boardState.getCellContents(x, y+1) & 1) == 1);
		}
		
		int newcontents = (contents & 48);
		if(bu)
			newcontents = newcontents | 1;
		if(br)
			newcontents = newcontents | 2;
		if(bd)
			newcontents = newcontents | 4;
		if(bl)
			newcontents = newcontents | 8;
		
		int temp = contents;
		int start = contents & 15;
		int ty;
		for(int cnt = 0; cnt < 16; ++cnt)
		{
			ty = (cnt + start) % 16;
			temp = newcontents | ty;
			if(temp != contents)
			{
				rv = temp;
				break;
			}
		}
		
		return rv;
	}
	
	/**
	 * Gets next cell value when modifying the puzzle.
	 * 
	 * @param y the y
	 * @param boardState the board state
	 * @param x the x
	 * 
	 * @return the next cell value
	 * 
	 * @see edu.rpi.phil.legup.PuzzleModule#getNextCellValue(int, int, edu.rpi.phil.legup.BoardState)
	 */
	public int getNextCellValue(int x, int y, BoardState boardState)
	{
		
		
		int contents = boardState.getCellContents(x,y);
		boolean on, os, oe, ow;
		BoardAccessor ba = new BoardAccessor(boardState,boardState.getSingleParentState(), 0,x,y);
 
		//return false if no parent state
		int parent = ba.getOrigCell(0, 0);
  		on = Masyu.hasNorth(parent);
  		oe = Masyu.hasEast(parent);
  		os = Masyu.hasSouth(parent);
  		ow = Masyu.hasWest(parent);
  		
  			
 
		int value = contents & 15;
		boolean valid = true;
		do {
			valid = true;
			value++;
			value %= 16;
			if(on && !hasNorth(value))
				valid = false;
			if(oe && !hasEast(value))
				valid = false;
			if(os && !hasSouth(value))
				valid = false;
			if(ow && !hasWest(value))
				valid = false;
					
			
		} while(!valid);
		if(isWhite(contents))
			value += WHITE;
		else if(isBlack(contents))
			value += BLACK;
		
		return value;
	}
	
	/**
	 * Checks to see if the current board can result in the goal.
	 * 
	 * @param currentBoard the current board
	 * @param goalBoard the goal board
	 * 
	 * @return true, if check goal
	 * 
	 * @see edu.rpi.phil.legup.PuzzleModule#checkGoal(edu.rpi.phil.legup.BoardState, edu.rpi.phil.legup.BoardState)
	 */
	public boolean checkGoal(BoardState currentBoard, BoardState goalBoard){
		//TODO: Multiple solutions?
		return currentBoard.compareBoard(goalBoard);
	}
	
	/**
	 * Gets a list of all the rules.
	 * 
	 * @return the rules
	 * 
	 * @see edu.rpi.phil.legup.PuzzleModule#getRules()
	 */
	public Vector<PuzzleRule> getRules(){
		Vector<PuzzleRule> ruleList = new Vector<PuzzleRule>();
		ruleList.add(new RuleFinishPath());
		ruleList.add(new RuleBlackEdge());
		ruleList.add(new RuleWhiteEdge());
		ruleList.add(new RuleNearWhite());
		ruleList.add(new RuleOnlyOneChoice());
		return ruleList;
	}
	
	 /**
 	 * Gets a list of Contradictions associated with this puzzle.
 	 * 
 	 * @return A Vector of Contradictions
 	 */   
	public Vector <Contradiction> getContradictions()
	{
		Vector <Contradiction> contradictionList = new Vector <Contradiction>();
		contradictionList.add(new ContradictionOnly2());
		//contradictionList.add(new ContradictionWhite());
		contradictionList.add(new ContradictionBlack());
		contradictionList.add(new ContradictionBadLooping());
		contradictionList.add(new ContradictionNoOptions());
		return contradictionList;
	}
	
	/**
	 * Gets list of Case Rules for this project.
	 * 
	 * @return the case rules
	 * 
	 * @see edu.rpi.phil.legup.PuzzleModule#getCaseRules()
	 */
	public Vector <CaseRule> getCaseRules()
	{
		Vector <CaseRule> caseRules = new Vector <CaseRule>();
		caseRules.add(new CaseWhiteSplit());
		caseRules.add(new CaseBlackSplit());
		caseRules.add(new CaseNormalSplit());
		return caseRules;
	}

	/**
	 * Checks to see if the current board state is valid.
	 * 
	 * @param boardState the board state
	 * @return true, if check valid board state
	 * @see edu.rpi.phil.legup.PuzzleModule#checkValidBoardState(edu.rpi.phil.legup.BoardState)
	 */
	public boolean checkValidBoardState(BoardState boardState){
	int height = boardState.getHeight();
	int width = boardState.getWidth();
	
	
		
		//TODO - write the code to check this
	
		return (height + width) > 0;
	}
	
	//helper functions
	/**
	 * Checks for north.
	 * @param val the value of the cell
	 * @return true, if cell value given goes north
	 */
	public static final boolean hasNorth(int val)
	{
		return (val & 1) != 0;
	}
	
	/**
	 * Checks for east.
	 * 
	 * @param val the value of the cell
	 * 
	 * @return true, if cell value given goes east
	 */
	public static final boolean hasEast(int val)
	{
		return (val & 2) != 0;
	}
	
	/**
	 * Checks for south.
	 * 
	 * @param val the value of the cell
	 * 
	 * @return true, if cell value given goes south
	 */
	public static final boolean hasSouth(int val)
	{
		return (val & 4) != 0;
	}
	
	public static final boolean isBlank(int val)
	{
		return val < 16;
	}
	public static final boolean isWhite(int val)
	{
		return (val & 0x10) != 0;
	}
	public static final boolean isBlack(int val)
	{
		return (val & 0x20) != 0;
	}
	
	/**
	 * Checks for west.
	 * 
	 * @param val the value of the cell
	 * @return true, if cell value given goes west
	 */
	public static final boolean hasWest(int val)
	{
		return (val & 8) != 0;
	}
	
	/**
	 * Checks to see that all walls in oldValue are in newValue
	 * 
	 * @param newValue The new value for a cell
	 * @param oldValue The old value for a cell
	 * @return T iff all walls in oldValue are in newValue
	 */
	public static final boolean onlyAdds(int newValue, int oldValue)
	{
		return (!hasNorth(oldValue) || hasNorth(newValue)) &&
			   (!hasSouth(oldValue) || hasSouth(newValue)) &&
			   (!hasEast(oldValue) || hasEast(newValue)) &&
			   (!hasWest(oldValue) || hasWest(newValue));
	}
	public static String checkDirections(Checker c, BoardAccessor ba)
	{
		String error = null;
		int origDir = ba.getDir();
		int origVal = ba.getOrigCell(0,0);
		int newVal = ba.getOrigCell(0, 0);
		for(int i = 0; i < 4 && error == null;i++)
		{
			ba.turn(BoardAccessor.EAST);
			ba.setDir(i);
			
			if(!ba.hasDir(origVal, BoardAccessor.NORTH) && ba.hasDir(newVal, BoardAccessor.NORTH))
				error = c.check(ba);
		}
		
		ba.setDir(origDir);
		return error;
	}
	public static String checkDirections(Checker c, BoardState destBoardState, int x, int y, int amount)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		String error = null;
		BoardAccessor ba = new BoardAccessor(destBoardState, origBoardState, 0,x,y);
		for(int i = 0; i < 4 && error == null; i++)
		{
			ba.setDir(i);
			if((amount & (1 << i)) != 0) //has that new branch
				error = c.check(ba);
		}
		return error;
	}
	public static boolean addDirections(Adder c, BoardState destBoardState, int x, int y)
	{
		String error = null;
		BoardState origBoardState = destBoardState.getSingleParentState();
		BoardAccessor ba = new BoardAccessor(destBoardState, origBoardState,0,x,y);
		int amount,newAmount = destBoardState.getCellContents(x, y);
		amount = newAmount;
		for(int i = 0; i < 4 && error == null; i++)
		{
			ba.setDir(i);
			
			//don't try to add already added walls
			if(ba.hasDir(ba.getOrigCell(0, 0), BoardAccessor.NORTH))
			{	  	
				continue;
			}
			if(c.canAdd(ba))
			{
				newAmount |= (1 << i);
				//break;
			}
			error = (error==null)?c.getError():null;
		}
		if(newAmount != amount)
		{
			destBoardState.setCellContents(x, y, newAmount);
			return true;
		}
		return false;
	}
	
	/** Constant bit for NORTH
	 */
	static final int NORTH = 1;
	/** Constant bit for EAST
	 */
	static final int EAST = 2;
	/** Constant bit for SOUTH
	 */
	static final int SOUTH = 4;
	/** Constant bit for WEST;
	 */
	static final int WEST = 8;
	static final int WHITE = 0x10;
	static final int BLACK = 0x20;

	private BasicStroke medium = new BasicStroke(2);
	public void drawCell( Graphics2D g, int x, int y, int state ){
		// draws the components of a cell based on bitwise flags
		int sx = cellSize.width * (x+1);
		int sy = cellSize.height * (y+1);
		// draw pearls
		if( !isBlank(state) ){
			g.setColor( isBlack(state) ? Color.black : Color.white );
			g.fillOval( sx+1, sy+1, cellSize.width-2, cellSize.height-2 );
			g.setColor( Color.black );
			g.drawOval( sx+1, sy+1, cellSize.width-2, cellSize.height-2 );
		}
		// draw lines
		g.setColor( Color.blue );
		Stroke s = g.getStroke();
		g.setStroke( medium );
		if( hasWest(state) )
			g.drawLine( sx+cellSize.width/2, sy+cellSize.height/2,
				sx+1, sy+cellSize.height/2 );
		if( hasSouth(state) )
			g.drawLine( sx+cellSize.width/2, sy+cellSize.height/2,
				sx+cellSize.width/2, sy+cellSize.height-1 );
		if( hasEast(state) )
			g.drawLine( sx+cellSize.width/2, sy+cellSize.height/2,
				sx+cellSize.width-1, sy+cellSize.height/2 );
		if( hasNorth(state) )
			g.drawLine( sx+cellSize.width/2, sy+cellSize.height/2,
				sx+cellSize.width/2, sy+1 );
		g.setStroke(s);
	}

}
