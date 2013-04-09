/**
 *  PuzzleModule.java
 **/

package edu.rpi.phil.legup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.rpi.phil.legup.newgui.TreeSelectionListener;
import edu.rpi.phil.legup.newgui.BoardDataChangeListener;

/**
 * Generic Puzzle class. All puzzles should extend this class and implement the
 * Puzzle specific functionality
 *
 * @author Drew Housten
 * @version 1.0
 *
 *	Update 09/20/08 by Daniel Ploch:
 *		To make the AI class more non-specific to LightUp puzzles, the PuzzleModule
 *		has been given a new obtainRuleOrder(int state, int rule) method, where the index
 *		Consult comments on this method for more details
 */
public abstract class PuzzleModule implements TreeSelectionListener, BoardDataChangeListener
{
	public static int CELL_UNKNOWN = 0;
	public int numAcceptableStates() {return 2;} //defined to be consistent with getNextCellValue()
	public int getNonunknownBlank() {return 1;}
	public boolean hasLabels(){return false;}
	protected static final Dimension cellSize = new Dimension(32,32);
	static final Color clear = new Color(0,0,0,0);
	public String name;
	public CaseRule defaultApplication;
	
	public PuzzleModule()
	{
		Legup.getInstance().getSelections().addTreeSelectionListener(this);
		BoardState.addCellChangeListener(this); 
	}
	/**
	 * Take an action when the left mouse button is pressed
	 * @param state the current board state
	 * @param x the x position where the pressed event occurred
	 * @param y the y position where the pressed event occurred
	 */
	public void mousePressedEvent(BoardState state, Point where)
	{
		int next = getNextCellValue(where.x,where.y,state);
		state.setCellContents(where.x,where.y,next);
	}

	/**
	 * Is this puzzle provably solved in any of these board states?
	 * @param states The set of board states to check
	 * @return true iff there is a proven path from the root to the solution
	 */
	public boolean checkProof(BoardState initialState)
	{
		boolean rv = false;

		BoardState endstate = initialState.getFinalState();
		if(endstate != null)
		{
			rv = checkBoardComplete(endstate);
		}
		if(rv)
			endstate.setAsSolution();
		return rv;
	}
	
	//de-serializes extradata, intended to be implemented in every module that has extradata
	public Object extraDataFromString(String str)
	{
		return null;
	}
	
	public boolean checkBoardComplete(BoardState finalstate)
	{
		boolean ret = true;
		//Loop through and see if all cells are filled

		int width = finalstate.getWidth();
		int height = finalstate.getHeight();

		for(int x = 0; x < width; ++x)
		{
			for(int y = 0; y < height; ++y)
			{
				if(finalstate.getCellContents(x,y) == PuzzleModule.CELL_UNKNOWN)
				{
					ret = false;
					break;
				}
			}
		}

		return ret;
	}

	/**
	 * Take an action when a left mouse drag (or click) event occurs
	 * @param state
	 * @param from
	 * @param to
	 */
	public void mouseDraggedEvent(BoardState state, Point from, Point to)
	{
	}

	/**
	 *	Take an action when a left mouse click event occurs on a Label
	 */
	public void labelPressedEvent(BoardState state, int index, int side)
	{

	}

	/**
	 * Initialize the board
	 * This is the function that gets called by the editor to
	 * intialize the board for each puzzle type
	 * @param board the board to initialize
	 */
	public void initBoard(BoardState board)
	{

	}

	/**
	 * Method used for automated puzzle generation.  Needs to be overwritten in subclasses.
	 */
	public BoardState generatePuzzle(int difficulty, JFrame host)
	{
		return null;
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

	/**
	 * Get the next call value of all of them (so if we're editing tree tent, for example, we can
	 * change to and from trees)
	 *
	 * @param x Column coordinate of the cell
	 * @param y Row coordinate of the cell
	 * @param boardState BoardState that the cell should be looked up in
	 * @return The next cell value
	 */
	public int getAbsoluteNextCellValue(int x, int y, BoardState boardState)
	{
		int rv = 0;

		if (boardState.getCellContents(x,y) == 0)
			rv = 1;

		return rv;
	}

	/**
	 * Gets the next cell value for a specified cell in a boardstate
	 *
	 * @param x Column coordinate of the cell
	 * @param y Row coordinate of the cell
	 * @param boardState BoardState that the cell should be looked up in
	 * @return The next cell value
	 */
	public int getNextCellValue(int x, int y, BoardState boardState)
	{
		int rv = 0;
		
		if (boardState.getCellContents(x,y) == 0)
		   rv = 1;
		return rv;
	}
	//Helper function for user-enterable tile types, for indexing with 0->n
	//defined in the abstract class to be consistent with getNextCellValue()
	public /*abstract*/ String getStateName(int state)
	{
		if(state == 0)return "false";
		else if(state == 1)return "true";
		else return null;
	}
	//inverse function needed to reliably map 0->n to arbitrary puzzle-defined values
	public /*abstract*/ int getStateNumber(String state)
	{
		if(state == "false")return 0;
		else if(state == "true")return 1;
		else return CELL_UNKNOWN;
	}
	
	public void boardDataChanged(BoardState state)
	{
		
	}
	
	public void treeSelectionChanged(ArrayList <Selection> newSelection)
	{
		
	}
	
	/**
	 * Checks if the current board is the goal board
	 *
	 * @param currentBoard The current board state
	 * @param goalBoard The goal board state
	 * @return True if the goal has been reached
	 */
	public boolean checkGoal(BoardState currentBoard, BoardState goalBoard){
		return currentBoard.compareBoard(goalBoard);
	}

	/**
	 *	Returns the rules of the PuzzleModule in the order they should be applied to the AI
	 *	state = {0:Start, 1:Contradiction, 2:Guess, 3:Normal}
	 *			Normal - The last rule was applied successfully
	 *	rule = The index of PuzzleRule as specified by the return of the getRules() method
	 *			 that was most recently applied to the PuzzleModule by AI
	 *
	 *	The return of this method will be used for applying further rules
	 *	The first element of the array will be applied next
	 *	Assume obtainRuleOrder(a,b)==getRules().size()  If not, someone screwed up
	 */
	public int[] obtainRuleOrder(int state, int rule)
	{
		// Default Return = regular rule order
		int[] rv = new int[getRules().size()];
		for (int i = 0; i < rv.length; rv[i]=i++);
		return rv;
	}

	/**
	 * Gets a list of puzzle rules associated with this puzzle
	 *
	 * @return A Vector of PuzzleRules
	 */
	public abstract Vector<PuzzleRule> getRules();

	/**
	 * Gets a list of Contradictions associated with this puzzle
	 *
	 * @return A Vector of Contradictions
	 */
	public abstract Vector <Contradiction> getContradictions();

	/**
	 * Get a list of the case rules applicable for the puzzle
	 * @return a Vector of CaseRules
	 */
	public abstract Vector<CaseRule> getCaseRules();


	/**
	 * Checks if a board state is valid according to the rules of the puzzle
	 *
	 * @return True if the board state is valid
	 */
	public boolean checkValidBoardState(BoardState boardState){
		return true;
	}

	/**
	 * Get the forced dimension for this puzzle, or null if there isn't a forced dimension
	 * @return the size the puzzle must be, or null if the size is allowed to vary
	 */
	public Dimension getForcedDimension()
	{
		return null;
	}

	/**
	 * Call the extra data editor for this puzzle
	 * @return whether or not the initialization was successful
	 */
	public boolean editExtraData(BoardState boardState, edu.rpi.phil.legup.editor.PuzzleEditor peditor)
	{
		JOptionPane.showMessageDialog(null,"This puzzle type has no extra data to edit.");
		return false;
	}

	public BoardState importPuzzle(String filename)
	{
		System.out.println(filename);
		try
		{
			int width = 0;
			int height = 0;
			Dimension d = getForcedDimension();
			if(d != null)
			{
				width = d.width;
				height = d.height;
			}

			//Loop through file and get size and data

			Vector<Vector<Integer>> cells = new Vector<Vector<Integer>>();

			FileReader file = new FileReader(filename);
			BufferedReader bf = new BufferedReader(file);

			String line;
			while((line = bf.readLine()) != null)
			{
				line = line.trim( );
				if(line.length( ) == 0)
					continue;
				String[] row = line.split( "," );
				if(row.length != width && width != 0)
					return null;
				else
				{
					width = row.length;
					Vector<Integer> rowCells = new Vector<Integer>();
					for(int i = 0; i < width; ++ i )
					{
						rowCells.add( Integer.parseInt( row[i] ) );
					}
					cells.add( rowCells );
				}
			}
			bf.close( );
			file.close( );

			if(cells.size( ) == 0 || (cells.size() != height && height != 0))
				return null;

			BoardState state = new BoardState(cells.size(), width);
			for(int y = 0; y < cells.size(); ++y)
			{
				for(int x = 0; x < cells.get( y ).size( ); ++x)
				{
					state.setCellContents( x, y, cells.get( y ).get( x ) );
				}
			}


			return state;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	/* AI stuff */
	/**
	 * @param Board the boardstate to make a guess on
	 * @return the board after the guess has been made
	 */
	public BoardState guess(BoardState B) {
		// by default, just return what we were passed in
		return B;
	}

	public Justification getRuleByName(String name)
	{
		if(name == null)
			return null;

		if(RuleMerge.getInstance().getName().compareTo(name) == 0)
			return RuleMerge.getInstance();

		for(PuzzleRule p : getRules())
		{
			if(p.getName().compareTo(name) == 0)
				return p;
		}

		for(Contradiction p : getContradictions())
		{
			if(p.getName().compareTo(name) == 0)
				return p;
		}
		return null;
	}

	public CaseRule getCaseRuleByName(String name)
	{
		if(name == null)
			return null;

		for(CaseRule p : getCaseRules())
		{
			if(p.getName().compareTo(name) == 0)
				return p;
		}

		return null;
	}

	/**
	 * Performs any final processing on a state that has been altered
	 * @param state
	 */
	public void updateState(BoardState state){}



	/**
	 * Get all the images (as strings to the image path) used by this puzzle in the center part
	 * @return an array of strings to image paths
	 */
	public BoardImage[] getAllCenterImages()
	{
		BoardImage[] s = new BoardImage[0];

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

	public Dimension getImageSize()
	{
		return cellSize;
	}

	/**
	 *	Separate method, intended for overwriting when extra data needs to be incorporated
	 */
	public String getImageLocation(int x, int y, BoardState boardState)
	{
		return getImageLocation(boardState.getCellContents(x, y));
	}

	/**
	 * Gets the image location for the cellValue
	 * Method is not used if the overloaded method is overwritten
	 *
	 * @return A location for the image
	 */
	public String getImageLocation(int cellValue)
	{
		if (cellValue == 0)
			return "images/blank.gif";
		else
			return "images/unknown.gif";
	}

	protected Font font = new Font( "Arial", Font.BOLD, 20 );
	protected Color fontColor = Color.black;
	protected void drawText( Graphics2D g, int x, int y, String text ){
		g.setColor( fontColor );
		g.setFont( font );
		FontMetrics fm = g.getFontMetrics();
		int w = ( cellSize.width - fm.stringWidth(text) ) / 2;
		int h = ( cellSize.height - fm.getAscent() ) / 2 + fm.getAscent();
		g.drawString( text,
			cellSize.width * (x + 1) + w,
			cellSize.height * (y + 1) + h );
	}

	protected void drawImage( Graphics2D g, int x, int y, Image img ){
		g.drawImage( img,
			cellSize.width * (x + 1), cellSize.height * (y + 1),
			cellSize.width, cellSize.height, null );
	}

	protected Rectangle getCellBounds( int x, int y ){
		return new Rectangle(
			cellSize.width * (x + 1), cellSize.height * (y + 1),
			cellSize.width, cellSize.height );
	}

	/**
	 * Drawing methods
	 */
	public void drawCell(Graphics2D g, int x, int y, BoardState state)
	{
		drawCell( g, x, y, state.getCellContents(x, y) );
	}

	public void drawCell( Graphics2D g, int x, int y, int state ){
		String imagePath = getImageLocation(state);
		Image i = new ImageIcon(imagePath).getImage();
		drawImage(g,x,y,i);
	}

	/**
	 * Draw the grid for the puzzle in the specified coords
	 * @param g the Graphics to draw with
	 * @param bounds the bounds of the grid
	 * @param w the width (in boxes) of the puzzle
	 * @param h the height (in boxes) of the puzzle
	 */
	public void drawGrid(Graphics g, Rectangle bounds, int w, int h)
	{
		g.setColor(Color.gray);

		double dx = bounds.width / (double)w;
		double dy = bounds.height / (double)h;

		// draw vertical lines
		for (int x = 0; x <= w; ++x)
		{
			int drawX = bounds.x + (int)(x * dx);
			g.drawLine(drawX, bounds.y, drawX,bounds.y + bounds.height);
		}

		// draw horizontal lines
		for (int y = 0; y <= h; ++y)
		{
			int drawY = bounds.y + (int)(y * dy);

			g.drawLine(bounds.x, drawY, bounds.x + bounds.width, drawY);
		}
	}

	/**
	 * Draw any extra data for the board
	 * @param g the Graphics to draw with
	 * @param extraData the extra data of the current board state we're drawing
	 * @param bounds the bounds of the grid
	 * @param w the width (in boxes) of the puzzle
	 * @param h the height (in boxes) of the puzzle
	 */
	public void drawExtraData(Graphics g, ArrayList<Object> extraData, ArrayList<Object> extraDataDelta, Rectangle bounds, int w, int h)
	{

	}

	// Default behavior for borders should be to draw nothing, let individual
	// puzzles implement this if they chose. Borders may be removed in the
	// near future, in favor of including borders as part of the board.

	public void drawLeftLabel(Graphics2D g, int val, int x, int y)
	{/*
		String imagePath = getImageLocation(val);
		Image i = new ImageIcon(imagePath).getImage();
		drawImage(g,x,y,i);*/
	}

	public void drawRightLabel(Graphics2D g, int val, int x, int y)
	{/*
		String imagePath = getImageLocation(val);
		Image i = new ImageIcon(imagePath).getImage();
		drawImage(g,x,y,i);*/
	}

	public void drawTopLabel(Graphics2D g, int val, int x, int y)
	{/*
		String imagePath = getImageLocation(val);
		Image i = new ImageIcon(imagePath).getImage();
		drawImage(g,x,y,i);*/
	}

	public void drawBottomLabel(Graphics2D g, int val, int x, int y)
	{/*
		String imagePath = getImageLocation(val);
		Image i = new ImageIcon(imagePath).getImage();
		drawImage(g,x,y,i);*/
	}
}
