//
//  Fillapix.java
//  LEGUP

package edu.rpi.phil.legup.puzzles.fillapix;

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
import edu.rpi.phil.legup.newgui.LEGUP_Gui;
// import edu.rpi.phil.legup.puzzles.treetent.CaseLinkTree; //avoid duplicating helper functions

public class Fillapix extends PuzzleModule {
	// Cells can be unknown, black, white, black with a clue, or white with a clue
	// Values are stored depending on the type of cell it is
	// First click: cell turns black
	// Second click: cell turns white
	// Third click: cell resets to unknown

	// Only one value can be stored in the board
	// Therefore cells with clues are a little tricky
	// To capture both the color and the clue number, a special number system is implemented

	// natural values for cells with clues. Mod 10 gives the clue number
	public static int CELL_ZERO = 10;
	public static int CELL_ONE = 11;
	public static int CELL_TWO = 12;
	public static int CELL_THREE = 13;
	public static int CELL_FOUR = 14;
	public static int CELL_FIVE = 15;
	public static int CELL_SIX = 16;
	public static int CELL_SEVEN = 17;
	public static int CELL_EIGHT = 18;
	public static int CELL_NINE = 19;

	// cell colors
	public static int CELL_UNKNOWN = -50;
	public static int CELL_BLACK = 20;
	public static int CELL_WHITE = 30;

	// unknown unknown: -50
	// unknown black: -30
	// unknown white: -20
	// clues with unknown cells: 10, 11, 12, 13, 14, 15, 16, 17, 18, 19
	// clues with black cells: 30, 31, 32, 33, 34, 35, 36, 37, 38, 39
	// clues with white cells: 60, 61, 62, 63, 64, 65, 66, 67, 68, 69

	public Map<String, Integer> getSelectableCells() {
		Map<String, Integer> tmp = new LinkedHashMap<String, Integer>();
		tmp.put("blank", CELL_UNKNOWN);
		tmp.put("black", CELL_BLACK);
		tmp.put("white", CELL_WHITE);
		return tmp;
	}

	public Map<String, Integer> getUnselectableCells() {
		return null;
	}

	public static boolean isUnknown(int value) {
		return (value == -50 || value == 10 || value == 11 || value == 12 || value == 13 || value == 14
				|| value == 15 || value == 16 || value == 17 || value == 18 || value == 19);
	}

	public static boolean isBlack(int value) {
		return (value == -30 || value == 30 || value == 31 || value == 32 || value == 33 || value == 34
				|| value == 35 || value == 36 || value == 37 || value == 38 || value == 39);
	}

	public static boolean isWhite(int value) {
		return (value == -20 || value == 60 || value == 61 || value == 62 || value == 63 || value == 64
				|| value == 65 || value == 66 || value == 67 || value == 68 || value == 69);
	}

	protected boolean inBounds(int width, int height, int x, int y) {
		return (((0 <= x) && (x < width)) && ((0 <= y) && (y < height)));
	}

	public boolean isCellWithClue(int value) {
		return value > 0;
	}

	public Fillapix() {
	}

	public boolean checkBoardComplete(BoardState finalstate) {
		boolean[][] colored = new boolean[finalstate.getHeight()][finalstate.getWidth()];
		int width = finalstate.getWidth();
		int height = finalstate.getHeight();

		//Loop through and see if all cells are filled
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				int value = finalstate.getCellContents(x, y);
				// return false if the cell is still marked as unknown
				if (isUnknown(value)) {
					return false;
				}
				// check that the numbers are satisfied
				// don't look at cells that don't have clues in them
				if (value != -30 && value != -20) {
					// count the number of black cells
					int numBlackCells = 0;
					for (int i = -1; i < 2; ++i) {
						for (int j = -1; j < 2; ++j) {
							int xpos = x + i;
							int ypos = y + j;
							if (inBounds(width, height, xpos, ypos)) {
								if (isBlack(finalstate.getCellContents(x, y))) {
									numBlackCells += 1;
								}
							}
						}
					}
					if (numBlackCells != value) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void mousePressedEvent(BoardState state, Point where) {

		int value = state.getCellContents(where.x, where.y);
		if (isUnknown(value)) {
			value += CELL_BLACK;
		} else if (isBlack(value)) {
			value += CELL_WHITE;
		} else if (isWhite(value)) {
			value += CELL_UNKNOWN;
		} else {
			String error = "The value in the cell is outside the set of possible values.";
		}
		state.setCellContents(where.x, where.y, value);
	}

	public String getImageLocation(int cellValue) {
		if (cellValue == -50) { // unknown unknown
			return "images/unknown.gif";
		} else if (cellValue == -30) { // unknown black
			return "images/fillapix/unknownBlack.gif";
		} else if (cellValue == -20) { // unknown white
			return "images/fillapix/unknownWhite.gif";
		}
		// unknown cells with a clue
		else if (cellValue == 10) {
			return "images/fillapix/unknown0.gif";
		} else if (cellValue == 11) {
			return "images/fillapix/unknown1.gif";
		} else if (cellValue == 12) {
			return "images/fillapix/unknown2.gif";
		} else if (cellValue == 13) {
			return "images/fillapix/unknown3.gif";
		} else if (cellValue == 14) {
			return "images/fillapix/unknown4.gif";
		} else if (cellValue == 15) {
			return "images/fillapix/unknown5.gif";
		} else if (cellValue == 16) {
			return "images/fillapix/unknown6.gif";
		} else if (cellValue == 17) {
			return "images/fillapix/unknown7.gif";
		} else if (cellValue == 18) {
			return "images/fillapix/unknown8.gif";
		} else if (cellValue == 19) {
			return "images/fillapix/unknown9.gif";
			// black cells with a clue
		} else if (cellValue == 30) {
			return "images/fillapix/black0.gif";
		} else if (cellValue == 31) {
			return "images/fillapix/black1.gif";
		} else if (cellValue == 32) {
			return "images/fillapix/black2.gif";
		} else if (cellValue == 33) {
			return "images/fillapix/black3.gif";
		} else if (cellValue == 34) {
			return "images/fillapix/black4.gif";
		} else if (cellValue == 35) {
			return "images/fillapix/black5.gif";
		} else if (cellValue == 36) {
			return "images/fillapix/black6.gif";
		} else if (cellValue == 37) {
			return "images/fillapix/black7.gif";
		} else if (cellValue == 38) {
			return "images/fillapix/black8.gif";
		} else if (cellValue == 39) {
			return "images/fillapix/black9.gif";
			// white cells with a clue
		} else if (cellValue == 60) {
			return "images/fillapix/white0.gif";
		} else if (cellValue == 61) {
			return "images/fillapix/white1.gif";
		} else if (cellValue == 62) {
			return "images/fillapix/white2.gif";
		} else if (cellValue == 63) {
			return "images/fillapix/white3.gif";
		} else if (cellValue == 64) {
			return "images/fillapix/white4.gif";
		} else if (cellValue == 65) {
			return "images/fillapix/white5.gif";
		} else if (cellValue == 66) {
			return "images/fillapix/white6.gif";
		} else if (cellValue == 67) {
			return "images/fillapix/white7.gif";
		} else if (cellValue == 68) {
			return "images/fillapix/white8.gif";
		} else if (cellValue == 69) {
			return "images/fillapix/white9.gif";
		}
		return "";
	}

	public void initBoard(BoardState state) {
	}

	/**
	 * Get all the images (as strings to the image path) used by this puzzle in the center part
	 * @return an array of strings to image paths
	 */
	public BoardImage[] getAllCenterImages() {
		BoardImage[] s = new BoardImage[0];
		return s;
	}

	/**
 	* Get all the images (as strings to the image path) used by this puzzle in the border part
 	* @return an array of strings to image paths
 	*/
	public BoardImage[] getAllBorderImages() {
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
	public int getNextLabelValue(int curValue) {
		return 0;
	}

	public boolean checkGoal(BoardState currentBoard, BoardState goalBoard){
		return currentBoard.compareBoard(goalBoard);
	}

	// SHOULD FIX THIS SO THAT IT APPLIES TO FILLAPIX AND NOT LIGHTUP I'M NOT SURE WHAT VALUES GO HERE QUITE YET
	private static final int[][] rules = { { 1, 2, 0, 3 }, // Rules to try after SurroundBulbs (White, SelfLit, Bulbs, Corners)
		{ 2, 0, 3, 1 }, // Rules to try after SurroundWhite (SelfLit, Bulbs, Corners, White). White is actually unneeded
		{ 1, 0, 3, 2 }, // Rules to try after OnlySelfLit (White, Bulbs, Corners, SelfLit)
		{ 0, 2, 1, 3 }, // Rules to try after WhitCorners (Bulbs, SelfLit, White, Lit)
		{ 1, 3, 0, 2 }, // Rules to try after Start (White, Corners, Bulbs, SelfLit)
		{ 0, 3, 2, 1 }, // Rules to try after Contradition (Bulbs, Corners, SelfLit, White)
		{ 1, 0, 3, 2 } }; // Rules to try after Guess (White, Bulbs, Corners, SelfLit}

	public int[] obtainRuleOrder(int state, int rule) {
		if (state == 3) // Normal
		return rules[rule];
		else // Default state rules
		return rules[state+4];
	}

	public Vector <PuzzleRule> getRules() {
		Vector <PuzzleRule>ruleList = new Vector <PuzzleRule>();
		ruleList.add(new RuleFinishWithBlack());
		ruleList.add(new RuleFinishWithWhite());
		return ruleList;
	}

	/**
	 * Gets a list of Contradictions associated with this puzzle
	 *
	 * @return A Vector of Contradictions
	 */
	public Vector <Contradiction> getContradictions() {
		Vector <Contradiction>contradictionList = new Vector <Contradiction>();
		contradictionList.add(new ContradictionTooFewBlackCells());
		contradictionList.add(new ContradictionTooManyBlackCells());
		return contradictionList;
	}

	public Vector <CaseRule> getCaseRules() {
		Vector <CaseRule> caseRules = new Vector <CaseRule>();
		caseRules.add(new CaseBlackOrWhite());
		return caseRules;
	}


	public boolean checkValidBoardState(BoardState boardState) {
		BoardState clone = new BoardState(boardState);
		boardState = clone.addTransitionFrom();
		Vector<Contradiction> contras = getContradictions();
		for (Contradiction con : contras) if (con.checkContradiction(clone) == null) return false;
		return true;
	}

	// CAN PROBABLY REMOVE THIS DRAWEXTRADATA FUNCTION I DON'T THINK FILLAPIX USES IT
	private final Color light = new Color(255,255,0,63);
	public void drawExtraData(Graphics gr, ArrayList<Object> extraData, ArrayList<Object> extraDataDelta, Rectangle bounds, int w, int h) {
		/*
		Graphics2D g = (Graphics2D)gr;
		g.setColor( light );

		Dimension d = this.getImageSize();
		int imW = d.width;
		int imH = d.height;

		if(extraData.size() > 0) {
			for(int cnt = 0; cnt < extraData.size(); ++cnt) {
				g.fillRect(imW + ((Point)extraData.get(cnt)).x * imW, imH + ((Point)extraData.get(cnt)).y * imH, imW, imH);
			}
		}
		*/
	}

	public void treeSelectionChanged(ArrayList <Selection> newSelection) {
		if(newSelection.size() != 0) {
			BoardState b = newSelection.get(0).getState();
			// fillLight(b); SOMETHING ELSE SHOULD PROBABLY GO HERE
		}
	}

	public void boardDataChanged(BoardState state) {
		// fillLight(state); HERE TOO
	}

	// I DON'T THINK WE NEED THIS FUNCTION EITHER
	public static void fillLight(BoardState state) {
		/*
		ArrayList<Object> extra = state.getExtraData();
		extra.clear();
		boolean[][] litup = new boolean[state.getHeight()][state.getWidth()];
		// determineLight(state, litup);
		for (int y = 0; y < state.getHeight(); ++y) {
			for (int x = 0; x < state.getWidth(); ++x) {
				if(litup[y][x])
					extra.add(new Point(x,y));
			}
		}
		*/
	}

	// WE PROBABLY DON'T NEED THIS EITHER
	protected static void determineLight(BoardState state, boolean[][] litup) {
		/*
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
		}*/
	}

	/* AI stuff */
	public BoardState guess(BoardState Board) {
		/*
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
		*/
		// if we didn't then the board is full, and we are finished (thus, the returned board will be the same as the one we were given
		System.out.println("Statement: Your puzzle has been solved already. Why do you persist?"); // hahahaha
		return Board;
	}


	private Point GenerateBestGuess(BoardState Board) {
		// Lightup requires a bit more guessing, since the places we can stick lights aren't very restricted
		// We will start with trying to find un-filled black squares, and then move on to the unfilled square
		// that will fill the most other unfilled squares.
		int currentX=-1;
		int currentY=-1;
		/*
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
		System.out.println("No more squares!"); */
		return new Point (currentX, currentY);
	}

	// DON'T THINK WE NEED THIS
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
		int information = 0;
		/*
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
			*/
		return -1;
	}

	// ?? WHAT'S THIS FOR
	static Vector<Point> findDiagonalNumbers(BoardState state, Point at) {
		Vector<Point> ret = new Vector<Point>();
		int x;
		int y;
		if(at.x > 0) {
			x = at.x - 1;
			if(at.y > 0) {
				y = at.y - 1;
				if(cellIsNumber(state, new Point(x, y))) ret.add(new Point(x,y));
			}

			if(at.y < state.getHeight() - 1) {
				y = at.y + 1;
				if(cellIsNumber(state, new Point(x, y))) ret.add(new Point(x,y));
			}
		}

		if(at.x < state.getWidth() - 1) {
			x = at.x + 1;
			if(at.y > 0) {
				y = at.y - 1;
				if(cellIsNumber(state, new Point(x, y))) ret.add(new Point(x,y));
			}

			if(at.y < state.getHeight() - 1) {
				y = at.y + 1;
				if(cellIsNumber(state, new Point(x, y))) ret.add(new Point(x,y));
			}
		}

		return ret;
	}

	// ?? WHAT IS THIS FOR
	static Vector<Point> findAdjacentNumbers(BoardState state, Point at)
	{
		Vector<Point> ret = new Vector<Point>();
		int x = at.x;
		int y = at.y;
		if(at.x > 0) {
			x = at.x - 1;
			if(cellIsNumber(state, new Point(x, y)))
			ret.add(new Point(x,y));
		}

		if(at.x < state.getWidth() - 1) {
			x = at.x + 1;
			if(cellIsNumber(state, new Point(x, y)))
			ret.add(new Point(x,y));
		}

		x = at.x;

		if(at.y > 0) {
			y = at.y - 1;
			if(cellIsNumber(state, new Point(x, y)))
			ret.add(new Point(x,y));
		}

		if(at.y < state.getHeight() - 1) {
			y = at.y + 1;
			if(cellIsNumber(state, new Point(x, y)))
			ret.add(new Point(x,y));
		}

		return ret;
	}

	static boolean cellIsNumber(BoardState state, Point at) {
		return state.getCellContents(at.x, at.y) < 0;
		// return state.getCellContents(at.x, at.y) >= 10 || state.getCellContents(at.x, at.y) < 15;
	}

	public void updateState(BoardState state) {
		//LightUp.fillLight(state);
	}
}