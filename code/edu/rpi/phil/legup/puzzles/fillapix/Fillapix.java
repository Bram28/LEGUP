//
//  Fillapix.java
//  LEGUP

package edu.rpi.phil.legup.puzzles.fillapix;

import java.awt.*;
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
import edu.rpi.phil.legup.newgui.Board;
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
		// FIX LATER
		Map<String, Integer> tmp = new LinkedHashMap<>();
		tmp.put("blank", 50);
		tmp.put("black", 70);
		tmp.put("white", 100);
		return tmp;
	}

	public Map<String, Integer> getUnselectableCells() {
		Map<String, Integer> tmp = new LinkedHashMap<>();
		tmp.put("hmm", -100); // BOGUS VALUE, EVERYTHING'S SELECTABLE
		return tmp;
	}

	// position of cell can be found from line of integers
	// x position: integer/row_size
	// y position: integer%row_size
	public Map<Integer, Boolean> initializedCells;
	public void initializeCell(int key) {
		initializedCells.put(key, true);
	}

	public static boolean isUnknown(int value) {
		return (value == -50 || value == 50 || (value/10)==1);
	}

	public static boolean isBlack(int value) {
		return (value == -30 || value == 70 || (value/10)==3);
	}

	public static boolean isWhite(int value) {
		return (value == -20 || value == 100 || (value/10)==6);
	}

	public static boolean inBounds(int width, int height, int x, int y) {
		return (((0 <= x) && (x < width)) && ((0 <= y) && (y < height)));
	}

	public static boolean hasClue(int value) {
		return value!=-50 && value!=-30 && value!=-20 && value!=50 && value!=70 && value!=100;
	}

	public Fillapix() {
		initializedCells = new LinkedHashMap<>();
	}

	public void drawCell( Graphics2D g, int x, int y, int state ){
		Color textColor = Color.black;
		if (isUnknown(state)) {
			g.setColor(Color.lightGray);
		} else if (isBlack(state)) {
			textColor = Color.white;
			g.setColor(Color.black);
		} else if (isWhite(state)) {
			g.setColor(Color.white);
		} else {
			System.out.println("This state shouldn't exist. It's impossible. It's preposterous");
		}
		/*
		if (state > 0) {
			drawText( g, x, y, String.valueOf(state%10) );
		}*/

		// draw the number
		if (state!=50 && state!=70 && state!=100 && state!=0) {
			drawText(g, x, y, String.valueOf(state%10), textColor);
		} else {
			drawText(g, x, y, "", textColor);
		}
	}

	public void drawCell( Graphics2D g, int x, int y, BoardState state ){
		// make sure the user can click on the cell
		int key = (x*state.getWidth())+y;
		if (initializedCells.get(key) == null) {
			state.setModifiableCell(x,y, true);
			initializeCell(key);
		}

		int val = state.getCellContents( x, y );
		// draw the background color
		Color textColor = Color.black;
		g.setColor(Color.lightGray);
		if (isUnknown(val)) {
		} else if (isBlack(val)) {
			textColor = Color.white;
			g.setColor(Color.black);
		} else if (isWhite(val)) {
			g.setColor(Color.white);
		}
		g.fill(getCellBounds(x,y));

		// draw the number
		if (val!=50 && val!=70 && val!=100 && val!=0) {
			drawText(g, x, y, String.valueOf(val%10), textColor);
		} else {
			drawText(g, x, y, "", textColor);
		}
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
				if (hasClue(value)) {
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
		/*
		BoardState clone = new BoardState(boardState);
		boardState = clone.addTransitionFrom();
		Vector<Contradiction> contras = getContradictions();
		for (Contradiction con : contras) if (con.checkContradiction(clone) == null) return false;
		return true;
		*/
		return true;
	}

	public void treeSelectionChanged(ArrayList <Selection> newSelection) {
		/*
		if(newSelection.size() != 0) {
			BoardState b = newSelection.get(0).getState();
			// fillLight(b); SOMETHING ELSE SHOULD PROBABLY GO HERE
		}
		*/
	}

	public void boardDataChanged(BoardState state) {
		// fillLight(state); HERE TOO
	}
}