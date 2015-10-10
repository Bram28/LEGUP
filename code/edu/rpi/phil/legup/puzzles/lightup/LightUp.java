//
//  LightUp.java
//  LEGUP

package edu.rpi.phil.legup.puzzles.lightup;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.rpi.phil.legup.BoardImage;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Selection;
import edu.rpi.phil.legup.puzzles.treetent.CaseLinkTree; //avoid duplicating helper functions


public class LightUp extends PuzzleModule
{
	public static int CELL_UNKNOWN = 0;
	public static int CELL_LIGHT = 1;
	public static int CELL_EMPTY = 2; //formerly CELL_BLANK
	public static int CELL_BLOCK0 = 10; //blocks with numbers
	public static int CELL_BLOCK1 = 11;
	public static int CELL_BLOCK2 = 12;
	public static int CELL_BLOCK3 = 13;
	public static int CELL_BLOCK4 = 14;
	public static int CELL_BLOCK = 15; //the blank black block

    public Map<String, Integer> getSelectableCells()
    {
        Map<String, Integer> tmp = new LinkedHashMap<String, Integer>();
        tmp.put("blank", CELL_UNKNOWN);
        tmp.put("light", CELL_LIGHT);
        tmp.put("empty", CELL_EMPTY);
        return tmp;
    }
    public Map<String, Integer> getUnselectableCells()
    {
        Map<String, Integer> tmp = new LinkedHashMap<String, Integer>();
        tmp.put("CELL_BLOCK0", CELL_BLOCK0);
        tmp.put("CELL_BLOCK1", CELL_BLOCK1);
        tmp.put("CELL_BLOCK2", CELL_BLOCK2);
        tmp.put("CELL_BLOCK3", CELL_BLOCK3);
        tmp.put("CELL_BLOCK4", CELL_BLOCK4);
        tmp.put("CELL_BLOCK", CELL_BLOCK);
        return tmp;
    }
	public int getNonunknownBlank() {return 2;} //the index into getStateName of empty

	public LightUp(){
	}

	public boolean checkBoardComplete(BoardState finalstate)
	{
		boolean[][] litup = new boolean[finalstate.getHeight()][finalstate.getWidth()];
		determineLight(finalstate, litup);
		//Loop through and see if all cells are filled

		int width = finalstate.getWidth();
		int height = finalstate.getHeight();

		for(int x = 0; x < width; ++x)
			for(int y = 0; y < height; ++y)
				if(finalstate.getCellContents(x,y) < 10 && !litup[y][x])
					return false;
		return true;
	}

	public void mousePressedEvent(BoardState state, Point where)
	{
		int next = getNextCellValue(where.x,where.y,state);
		state.setCellContents(where.x,where.y,next);
		fillLight(state);
	}

	public String getImageLocation(int cellValue){
		if (cellValue == CELL_UNKNOWN){
			return "images/unknown.gif";
		} else if (cellValue == CELL_LIGHT){
			return "images/lightup/light.png";
		} else if (cellValue == CELL_EMPTY){
			return "images/lightup/empty.gif";
		} else if (cellValue >= 10 && cellValue < 15){
			return "images/lightup/" + (cellValue-10)+".gif";
		} else if(cellValue == 15){
			return "images/lightup/black.gif";
		} else {
			return "images/unknown.gif";
		}
	}

	public void initBoard(BoardState state)
	{
	}

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

	/*public int getAbsoluteNextCellValue(int x, int y, BoardState boardState)
	{
		int contents = boardState.getCellContents(x,y);
		int rv = CELL_UNKNOWN;

		if (contents == CELL_UNKNOWN)
		{
			rv = CELL_LIGHT;
		}
		else if (contents == CELL_LIGHT)
		{
			rv = CELL_EMPTY;
		}
		else if (contents == CELL_EMPTY)
		{
			rv = CELL_BLOCK0;
		}
		else if (contents == CELL_BLOCK0)
		{
			rv = CELL_BLOCK1;
		}
		else if (contents == CELL_BLOCK1)
		{
			rv = CELL_BLOCK2;
		}
		else if (contents == CELL_BLOCK2)
		{
			rv = CELL_BLOCK3;
		}
		else if (contents == CELL_BLOCK3)
		{
			rv = CELL_BLOCK4;
		}
		else if (contents == CELL_BLOCK4)
		{
			rv = CELL_BLOCK;
		}

		return rv;
	}*/

	public boolean checkGoal(BoardState currentBoard, BoardState goalBoard){
		return currentBoard.compareBoard(goalBoard);
	}

	private static final int[][] rules = { { 1, 2, 0, 3 }, // Rules to try after SurroundBulbs (White, SelfLit, Bulbs, Corners)
			{ 2, 0, 3, 1 }, // Rules to try after SurroundWhite (SelfLit, Bulbs, Corners, White). White is actually unneeded
			{ 1, 0, 3, 2 }, // Rules to try after OnlySelfLit (White, Bulbs, Corners, SelfLit)
			{ 0, 2, 1, 3 }, // Rules to try after WhitCorners (Bulbs, SelfLit, White, Lit)
			{ 1, 3, 0, 2 }, // Rules to try after Start (White, Corners, Bulbs, SelfLit)
			{ 0, 3, 2, 1 }, // Rules to try after Contradition (Bulbs, Corners, SelfLit, White)
			{ 1, 0, 3, 2 } }; // Rules to try after Guess (White, Bulbs, Corners, SelfLit}

	//	DP: Added 09/20/2008

	public int[] obtainRuleOrder(int state, int rule)
	{
		if (state == 3) // Normal
			return rules[rule];
		else // Default state rules
			return rules[state+4];
	}

	public Vector <PuzzleRule> getRules(){
		Vector <PuzzleRule>ruleList = new Vector <PuzzleRule>();
		ruleList.add(new RuleFinishWithBulbs());
		ruleList.add(new RuleFinishWithEmpty());
		ruleList.add(new RuleMustLight());
		ruleList.add(new RuleEmptyCorners());
		ruleList.add(new RuleWhiteInLight());
		return ruleList;
	}

	 /**
	 * Gets a list of Contradictions associated with this puzzle
	 *
	 * @return A Vector of Contradictions
	 */
	public Vector <Contradiction> getContradictions()
	{
		Vector <Contradiction>contradictionList = new Vector <Contradiction>();
		contradictionList.add(new ContradictionTooFewBulbs());
		contradictionList.add(new ContradictionTooManyBulbs());
		contradictionList.add(new ContradictionBulbsInPath());
		contradictionList.add(new ContradictionNoLight());
		return contradictionList;
	}

	public Vector <CaseRule> getCaseRules()
	{
		Vector <CaseRule> caseRules = new Vector <CaseRule>();
		caseRules.add(new CaseLightOrEmpty());
		caseRules.add(new CaseSatisfyNumber());

		return caseRules;
	}


	public boolean checkValidBoardState(BoardState boardState)
	{
		BoardState clone = new BoardState(boardState);
		boardState = clone.addTransitionFrom();

		Vector<Contradiction> contras = getContradictions();

		for (Contradiction con : contras) if (con.checkContradiction(clone) == null) return false;
		return true;
	}

	private final Color light = new Color(255,255,0,63);
	public void drawExtraData(Graphics gr, ArrayList<Object> extraData, ArrayList<Object> extraDataDelta, Rectangle bounds, int w, int h)
	{
		Graphics2D g = (Graphics2D)gr;
		g.setColor( light );

		Dimension d = this.getImageSize();
		int imW = d.width;
		int imH = d.height;

		if(extraData.size() > 0)
		{
			for(int cnt = 0; cnt < extraData.size(); ++cnt)
			{
				g.fillRect(imW + ((Point)extraData.get(cnt)).x * imW, imH + ((Point)extraData.get(cnt)).y * imH, imW, imH);
			}
		}
	}

	public void treeSelectionChanged(ArrayList <Selection> newSelection)
	{
		if(newSelection.size() != 0)
		{
			BoardState b = newSelection.get(0).getState();
			fillLight(b);
		}
	}

	public void boardDataChanged(BoardState state)
	{
		fillLight(state);
	}

	public static void fillLight(BoardState state)
	{
		ArrayList<Object> extra = state.getExtraData();
		extra.clear();
		boolean[][] litup = new boolean[state.getHeight()][state.getWidth()];
		determineLight(state, litup);
		for (int y = 0; y < state.getHeight(); ++y)
		{
			for (int x = 0; x < state.getWidth(); ++x)
			{
				if(litup[y][x])
					extra.add(new Point(x,y));
			}
		}
	}

	protected static void determineLight(BoardState state, boolean[][] litup)
	{
		int width = state.getWidth();
		int height = state.getHeight();
		for (int y = 0; y < height; ++y)
		{
			for (int x = 0; x < width; ++x)
			{
				if(state.getCellContents(x,y) == CELL_LIGHT)
				{
					litup[y][x] = true;
					if(x > 0)
					{
						for(int tempx = x - 1; tempx >= 0; --tempx)
						{
							if(state.getCellContents(tempx,y) > 2 || state.getCellContents(tempx,y) == 1)
								break;
							else
								litup[y][tempx] = true;
						}
					}

					if(x < width-1)
					{
						for(int tempx = x+1; tempx < width; ++tempx)
						{
							if(state.getCellContents(tempx,y) > 2 || state.getCellContents(tempx,y) == 1)
								break;
							else
								litup[y][tempx] = true;
						}
					}

					if(y > 0)
					{
						for(int tempy = y-1; tempy >= 0; --tempy)
						{
							if(state.getCellContents(x,tempy) > 2 || state.getCellContents(x,tempy) == 1)
								break;
							else
								litup[tempy][x] = true;
						}
					}

					if(y < height-1)
					{
						for(int tempy = y + 1; tempy < height; ++tempy)
						{
							if(state.getCellContents(x,tempy) > 2 || state.getCellContents(x,tempy) == 1)
								break;
							else
								litup[tempy][x] = true;
						}
					}
				}
			}
		}
	}
	/* AI stuff */
	public BoardState guess(BoardState Board) {
		// out of forced moves, need to guess
		Point guess = GenerateBestGuess(Board);
		// guess, if we found one
		if (guess.x != -1 && guess.y != -1) {
			BoardState Parent = Board.getSingleParentState();
			BoardState CaseLight = Board;
			BoardState CaseBlank = Parent.addTransitionFrom();
			CaseLight.setCellContents(guess.x, guess.y, CELL_LIGHT);
			fillLight(CaseLight);
			CaseBlank.setCellContents(guess.x, guess.y, CELL_EMPTY);
			fillLight(CaseBlank);
			Parent.setCaseSplitJustification(new CaseLightOrEmpty());
			//System.out.println("Guessed at "+guess.x+","+guess.y);

			return CaseLight;
		}
		// if we didn't then the board is full, and we are finished (thus, the returned board will be the same as the one we were given
		System.out.println("Statement: Your puzzle has been solved already. Why do you persist?");
		return Board;
	}



	private Point GenerateBestGuess(BoardState Board) {
		// Lightup requires a bit more guessing, since the places we can stick lights aren't very restricted
		// We will start with trying to find un-filled black squares, and then move on to the unfilled square
		// that will fill the most other unfilled squares.
		int currentX=-1;
		int currentY=-1;
		//double prob = 0;
		int information = 0;
		int width = Board.getHeight();
		int height = Board.getWidth();
		// search for best square
		for (int r = 0; r < height; r++ ) {
			for (int c = 0; c < width; c++) {
				// If the cell we are looking at is a black square, we want to find the number of open squares
				// preference is given to squares with the best ratio of available to filled squares
				if (Board.getCellContents(r,c) == CELL_UNKNOWN) {
					int currentInfo = InfoGained(r,c,Board,5);
					if (currentInfo > information) {
						currentX = r;
						currentY = c;
						information = currentInfo;
					}
				}
			}
		}

		// now we try to return again, this time with the empty square that will fill the most other squares
		if (currentX != -1 && currentY != -1) {
			return new Point (currentX, currentY);
		}
		//And if that fails, then there are no more squares to fill
		System.out.println("No more squares!");
		return new Point (currentX, currentY);
	}
	// function for finding blank squares around a black square
	private Point FindEmpty (int r, int c, BoardState Board) {
		int width = Board.getHeight();
		int height = Board.getWidth();
		//Up
		if (r > 0) {
			if (Board.getCellContents(r-1, c) == CELL_UNKNOWN) {
				return new Point(r-1,c);
			}
		}
		//Left
		if (c > 0) {
			if (Board.getCellContents(r, c-1) == CELL_UNKNOWN) {
				return new Point(r,c-1);
			}
		}
		//down
		if (r < height-1) {
			if (Board.getCellContents(r+1, c) == CELL_UNKNOWN) {
				return new Point(r+1,c);
			}
		}
		//Right
		if (c < width) {
			if (Board.getCellContents(r, c+1) == CELL_UNKNOWN) {
				return new Point(r,c+1);
			}
		}
		// If we havn't returned by now, something is seriously wrong
		return new Point(r,c);
	}


	//finds the number of squares that will be filled by putting a light in an unfilled square
	private int InfoGained (int r, int c, BoardState Board, int direction) {
		int information =0;
		int height = Board.getHeight();
		int width = Board.getWidth();
		final int squareWeight = 1; // base points for a square.
		final int startingWeight = 10; // bonuses for the starting square being near black squares.
		final double blackWeight = .5; // bonus for a non-starting square being adjaceted to a black square.
		// ending conditions: running off the side of the board, running into a black square
		if ((r < 0) || (r >= width) || (c < 0) || (c >= height)) {
			return 0;
		}
		if (Board.getCellContents(r,c) > 3) {
			return 0;
		}

		// attempts to condense everything into a single number, "Information gained by moving here"
		// Points for filling in this square
		if (Board.getCellContents(r,c) == CELL_UNKNOWN) {
				information += squareWeight;
		}
		// Points for black squares we are adjacent to
		if (r > 1) {
			if ((10 < Board.getCellContents((r-1), c)) && (Board.getCellContents((r-1), c) < 15)) {
				information += blackWeight;
				if(direction == 5) {
					information += startingWeight;
				}
			}
		}
		if (r < (width-1)) {
			if ((10 < Board.getCellContents((r+1), c)) && (Board.getCellContents((r+1), c) < 15)) {
				information += blackWeight;
				if(direction == 5) {
					information += startingWeight;
				}
			}
		}
		if (c > 1) {
			if ((10 < Board.getCellContents(r, (c-1))) && (Board.getCellContents(r, (c-1)) < 15)) {
				information += blackWeight;
				if(direction == 5) {
					information += startingWeight;
				}
			}
		}
		if (c < (height-1)) {
			if ((10 < Board.getCellContents(r, (c+1))) && (Board.getCellContents(r, (c+1)) < 15)) {
				information += blackWeight;
				if(direction == 5) {
					information += startingWeight;
				}
			}
		}
		// recursively find all other squares that we cast on
		if (direction == 5) {
			return information +InfoGained(r+1,c,Board,1) +
								InfoGained(r,c+1,Board,2) +
								InfoGained(r-1,c,Board,3) +
								InfoGained(r,c-1,Board,4);
		}
		if (direction == 1) {
			return information + InfoGained(r+1,c,Board,1);
		}
		if (direction == 2) {
			return information + InfoGained(r,c+1,Board,2);
		}
		if (direction == 3) {
			return information + InfoGained(r-1,c,Board,3);
		}
		if (direction == 4) {
			return information + InfoGained(r,c-1,Board,4);
		}
		// If we get here, things are bad. We shouldn't guess here.
		return -1;

	}

	static Vector<Point> findDiagonalNumbers(BoardState state, Point at)
	{
		Vector<Point> ret = new Vector<Point>();
		int x;
		int y;
		if(at.x > 0)
		{
			x = at.x - 1;
			if(at.y > 0)
			{
				y = at.y - 1;
				if(cellIsNumber(state, new Point(x, y)))
					ret.add(new Point(x,y));
			}

			if(at.y < state.getHeight() - 1)
			{
				y = at.y + 1;
				if(cellIsNumber(state, new Point(x, y)))
					ret.add(new Point(x,y));
			}
		}

		if(at.x < state.getWidth() - 1)
		{
			x = at.x + 1;
			if(at.y > 0)
			{
				y = at.y - 1;
				if(cellIsNumber(state, new Point(x, y)))
					ret.add(new Point(x,y));
			}

			if(at.y < state.getHeight() - 1)
			{
				y = at.y + 1;
				if(cellIsNumber(state, new Point(x, y)))
					ret.add(new Point(x,y));
			}
		}

		return ret;
	}

	static Vector<Point> findAdjacentNumbers(BoardState state, Point at)
	{
		Vector<Point> ret = new Vector<Point>();
		int x = at.x;
		int y = at.y;
		if(at.x > 0)
		{
			x = at.x - 1;
			if(cellIsNumber(state, new Point(x, y)))
				ret.add(new Point(x,y));
		}

		if(at.x < state.getWidth() - 1)
		{
			x = at.x + 1;
			if(cellIsNumber(state, new Point(x, y)))
				ret.add(new Point(x,y));
		}

		x = at.x;

		if(at.y > 0)
		{
			y = at.y - 1;
			if(cellIsNumber(state, new Point(x, y)))
				ret.add(new Point(x,y));
		}

		if(at.y < state.getHeight() - 1)
		{
			y = at.y + 1;
			if(cellIsNumber(state, new Point(x, y)))
				ret.add(new Point(x,y));
		}

		return ret;
	}

	static boolean cellIsNumber(BoardState state, Point at)
	{
		return state.getCellContents(at.x, at.y) >= 10 || state.getCellContents(at.x, at.y) < 15;
	}

	public void updateState(BoardState state)
	{
		LightUp.fillLight(state);
	}
}
