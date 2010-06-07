package edu.rpi.phil.legup;

import edu.rpi.phil.legup.newgui.LEGUP_Gui;
//import edu.rpi.phil.legup.*;
//import edu.rpi.phil.legup.PuzzleRule;
//import java.util.Vector;
//import edu.rpi.phil.legup.*;
import java.util.Vector;
//import java.lang.Integer;
import java.util.Random;
import java.util.ArrayList;
import java.awt.Point;

/**
 *	Updated 09/20/2008 by Daniel Ploch:
 *		AI rewired to apply to any puzzle, not just LightUp
 *		Also check PuzzleModule.obtainRuleOrder(int,int)
 */
public class AI implements Runnable {
	// private BoardState Current;
	private int steps;

	private int totSteps = 0;

	// private int hintsGiven = 0;
	private PuzzleModule ourPM;// for calling non-static puzzle methods

	private Legup ourLegup = Legup.getInstance(); // for calling non-static
													// legup methods!

	/** DP: Not necessary
	 *
	// private Vector<Vector<Integer>> previousRules = new
	// Vector<Vector<Integer>>();
	private String[] Labels = { "Bulbs", "White", "Unlit", "Corners", "lit",
			"Start", "Contradiction", "Guess" }; */

	/** DP: Also not necessary
	 *  If I don't get around to it, take this data to the LightUp class
	 *	 In a redefined obtainRuleOrder(int,int) method
	 *
	// Blah Blah Blah blindly indexing into arrays is bad
	// This is an array of array indexes to try, in order.
	// Usage is rules[lastRule][0-3]
	// This array should be puzzle-specific to Lightup, and there should be a
	// fuction in PuzzleModule to get the next rule to try. This is more
	// complicated to
	// implement than just putting a single new method, and I don't want to
	// accidentally break
	// something important just before I leave. - Eagle
	}; */

	private BoardState toRun;

	private int start = 0;

	private int contradiction = 1;

	private int guess = 2;

	private int normal = 3;

	private int lastCondition = start;
	private int lastRule = -1; // There hasn't been one yet

	/**
	 * Default constructor that doesn't do anything terrbly useful. it needs to
	 * be run on a board to do stuff
	 */
	public AI() {
	}

	/**
	 * Constructor that associates the AI with a particular puzzleModule
	 */
	public AI(PuzzleModule PM) {
		ourPM = PM;
		steps = 0;
	}

	/**
	 * Method to change the puzzleModule that we are using
	 *
	 * @param PM
	 *            the puzzleModule to change to
	 */
	public void setBoard(PuzzleModule PM) {
		ourPM = PM;
		steps = 0;
	}

	/**
	 * A simple method to automatically go through all of the LightUp boards for
	 * testing purposes Changing "LightUp" to another puzzle type will allow
	 * automated testing of other puzzles It is strongly reccommended that
	 * threading is disabled during testing, to avoid confusing the display
	 *
	 * @param PuzzleModule
	 *            must match the board list that we are testing
	 *
	 */
	public void test(PuzzleModule PM) {
		Vector boards = ourLegup.getConfig().getBoardsForPuzzle("Sudoku");
		this.setBoard(PM);
		for (int i = 0; i < boards.size(); i++) {
			lastRule = start;
			totSteps += steps;
			steps = 0;
			System.out.println("Puzzle " + i + " of " + boards.size());
			ourLegup.loadBoardFile((String) boards.get(i));
			// Legup puzzle 119 is enormously huge, and generates out-of-memory
			// errors
			if (i == 119) {
				System.out.println("Puzzle 119 skipped");
				continue;
			}
			this.stepToCompletion(ourLegup.getInitialBoardState());

		}
		System.out.flush();
		System.out.println("I'M DONE! YOU CAN WAKE UP NOW!");
		System.out.println("Total steps: " + totSteps);
	}

	/**
	 * Method to check if the AI has loaded a puzzle
	 *
	 * @return true if a PuzzleModule is loaded
	 */
	public boolean loaded() {
		return ourPM!=null;
	}

	/**
	 * Runs the AI from the given boardState to completion by repeatedly calling
	 * step
	 *
	 * @param Board
	 *            the board to start from
	 * @return The completed board
	 */
	public boolean stepToCompletion(BoardState Board) {
		toRun = Board;
		Thread testThread = new Thread(this, "AI thread");
		// finished = false;
		testThread.start();
		/*
		 * Uncomment this section to disable display updating during solving
		 * This is useful if you are running automated tests while (!finished) {
		 * try { Thread.sleep(1000); //testThread.join(1000);
		 * //System.out.println("Join timed out!"); } catch (Exception e) {
		 * System.err.println("Still not done! " + e); return false; } }
		 */

		return true;

	}

	/**
	 * Finds a location to apply a rule on the board
	 *
	 * @param Board
	 */
	public String findRuleApplication(BoardState Board)
	{
		if (!LEGUP_Gui.profFlag(LEGUP_Gui.ALLOW_HINTS))
			return "Hints are not allowed in this proof mode.";

		String s = "Whoops!";
		int hintsGiven = Board.getHints();
		Board.addHint();
		// first, copy the board
		BoardState newBoard = Board.copy();
		// save our previous rule information
		int realLastRule = lastRule;
		// Then, step the copy
		// This try-catch statement exists to combat the Contradiction Problem:
		//   When the copy of the BoardState used by the tutor has no parentage, so when the AI.step() method
		//   tries to search for the last guess, and can't find it, there is a problem
		//   This exception helps separate unsolvable puzzles from the AI isolation problem
		try {	newBoard = this.step(newBoard); }
		catch (ProblemWithBoardStateCopyException pwbsce) { newBoard = pwbsce.defaultReturn; }
		// Then, find the rule that we used
		// Contradictions first
		ArrayList<Point> diff = BoardState.getDifferenceLocations(newBoard,
				Board);
		if (newBoard.leadsToContradiction()) {
			if (hintsGiven == 0)
				s = "Uh-oh, it appears we have a contradiction at hand";
			//else if (hintsGiven == 1)
				//s = "OOPS! This board leads to a contradiction! Better back up and try again";
		} else { // Check split
			if (newBoard.getCaseSplitJustification() != null
					|| newBoard.getJustification() == null) {

				s = "Looks like you'll have to guess";
				if (ourPM.checkBoardComplete(newBoard))
					s = "Your puzzle is finished, why do you continue to bother me?";
				else if (hintsGiven > 0) {
					s = "Why don't you try looking in the ";
					if (diff.get(0).y < Board.getHeight() / 2) {
						s = s.concat("upper ");
					} else {
						s = s.concat("lower ");
					}
					if (diff.get(0).x < Board.getWidth() / 2) {
						s = s.concat("left ");
					} else {
						s = s.concat("right ");
					}
					s = s.concat("corner of the board");
					Point center = new Point(Board.getHeight() / 2, Board
							.getWidth() / 2);
					Point UL = new Point(0, 0);
					Point UR = new Point(Board.getWidth(), 0);
					Point LL = new Point(0, Board.getHeight());
					Point LR = new Point(Board.getHeight(), Board.getWidth());
					if (diff.get(0).y < Board.getHeight() / 2) {
						if (diff.get(0).x < Board.getWidth() / 2) {
							// Upper left region
							Board.addHintCellRange(UL, center);
						} else {
							// Upper right region
							Board.addHintCellRange(UR, center);
						}
					} else {
						if (diff.get(0).x < Board.getWidth() / 2) {
							// Lower left region
							Board.addHintCellRange(center, LL);
						} else {
							// Lower right region
							Board.addHintCellRange(center, LR);
						}
					}
				}
			} else { // Get the rule that we applied

				if (hintsGiven == 0) { // First hint
					s = "There is a basic rule that you can apply. Try to find it!";
				} else if (hintsGiven == 1) { // second hint
					s = "Try looking in the ";
					if (diff.get(0).y < Board.getHeight() / 2) {
						s = s.concat("upper ");
					} else {
						s = s.concat("lower ");
					}
					if (diff.get(0).x < Board.getWidth() / 2) {
						s = s.concat("left ");
					} else {
						s = s.concat("right ");
					}
					s = s.concat("corner of the board");
					Point center = new Point(Board.getHeight() / 2, Board
							.getWidth() / 2);
					Point UL = new Point(0, 0);
					Point UR = new Point(Board.getWidth(), 0);
					Point LL = new Point(0, Board.getHeight());
					Point LR = new Point(Board.getHeight(), Board.getWidth());
					if (diff.get(0).y < Board.getHeight() / 2) {
						if (diff.get(0).x < Board.getWidth() / 2) {
							// Upper left region
							Board.addHintCellRange(UL, center);
						} else {
							// Upper right region
							Board.addHintCellRange(UR, center);
						}
					} else {
						if (diff.get(0).x < Board.getWidth() / 2) {
							// Lower left region
							Board.addHintCellRange(center, LL);
						} else {
							// Lower right region
							Board.addHintCellRange(center, LR);
						}
					}

				} else if (hintsGiven == 2) { // Third hint
					s = "Look for a place where you can "
							+ newBoard.getJustification();
				} else if (hintsGiven == 3) { // Fourth hint
					s = "Try looking at (" + diff.get(0).x + ","
							+ diff.get(0).y + ")";
					Board.setHintCell(diff.get(0));
				} else if (hintsGiven == 4) { // Fifth hint
					s = "You can apply " + newBoard.getJustification() + " at ";
					for (int i = 0; i < diff.size(); i++) {
						s = s.concat("(" + diff.get(i).x + "," + diff.get(i).y
								+ ")");
						Board.addHintCell(diff.get(i));
						if (i < diff.size() - 1) {
							s.concat(", ");
						}
						if (i == diff.size() - 2) {
							s.concat("and ");
						}
					}
				} else if (hintsGiven >= 5)
				{ // Sixth hint. Do the move for them
					if (LEGUP_Gui.profFlag(LEGUP_Gui.ALLOW_DEFAPP))
					{
						Board = step(Board);
						realLastRule = lastRule; // update the last rule that we applied
					}
					else s = "No more hints for you.  Start thinking and stop spamming the '?' button.";
				}
			}
		}
		Selection se = new Selection(Board, false);
		lastRule = realLastRule;
		ourLegup.getSelections().setSelection(se);
		return s;
	}

	/**
	 * Runs the AI one move into the future. Returns the boardstate after the
	 * move.  First checks the current board for contradictions and then applies
	 * "basic" rules.  If the basic rule doesn't work it asks the module to make a
	 * guess.  
	 * @param Board
	 *            the BoardState to start from
	 * @return the Boardstate after the move
	 */
	public BoardState step(BoardState Board) {
		if (steps % 100 == 99) {
			System.out.println("step " + steps);
		}
		System.out.println("Started step");
		Board = Board.addTransitionFrom();
		Legup.getInstance().getSelections().setSelection(new Selection(Board, false));
		steps += 1;
		// Check for contradictions
		boolean Contradiction = false;
		Vector<Contradiction> contras = ourPM.getContradictions();
		for (int i = 0; i < contras.size(); i++) {
			String error = contras.get(i).checkContradiction(Board);
			System.out.println("Checking "+ contras.get(i).name + ": " +
			 error);
			Contradiction = (error == null);
			if (Contradiction) {
				System.out.println("Contradiction!");
				Board.setJustification(contras.get(i));
				break;
			}
		}
		if (Contradiction) {
			// Do contradiction stuff. Hunt down the last guess, choose the
			// other path, and continue
			// System.out.println("Contradiction!");
			// previousRules.get(lastRule).set(contradiction,
			// (previousRules.get(lastRule).get(contradiction) +1) );
			lastCondition = contradiction;
			BoardState Search = Board;
			/*
			 * While the search hasn't found our last guess
			 */
			// System.out.println("Getting parent state");
			boolean searching = true;
			BoardState newPath = Board;
			while (searching) {
				/*
				 * while (Search.getCaseSplitJustification() == null) { Search =
				 * Search.getSingleParentState(); }
				 */
				 System.out.println("Searching...");
				 System.out.println(Board);
				while (Search != null && Search.getTransitionsFrom().size() < 2) {
					Search = Search.getSingleParentState();
				}
				if (Search == null)
					throw new ProblemWithBoardStateCopyException(Board);
				// search is now the parent of the split

				// hunt down the first child that does not lead to a
				// contradiction
				Vector<BoardState> Children = Search.getTransitionsFrom();
				boolean found = false;
				for (int i = 0; i < Children.size(); i++) {
					BoardState child = Children.get(i);
					if (!(child.leadsToContradiction())) {
						newPath = child;
						found = true;
						break;
					}
				}
				// if we found a new path
				if (found) {
					searching = false;
				} else {
					// go one level higher with Search
					Search = Search.getSingleParentState();
					// and then let the loop run again
				}
			}
			// now that newPath is the first valid path that we have found...
			Selection path = new Selection(newPath, false);
			Legup.getInstance().getSelections().setSelection(path);
			return newPath;
		}

		// done checking for contradictions
		// Check for forced moves (default rule applications)

		boolean modified = false;
		Vector<PuzzleRule> Rules = ourPM.getRules();
		// Purposeful Order
		int[] order = ourPM.obtainRuleOrder(lastCondition, lastRule);
		// Sanity Check
		if (order.length != Rules.size()) {
			// Complain bitterly
			System.out.println("order is " + order.length + ", but Rules is "
					+ Rules.size());
		}

		/*
		 * Random Order for (int i = 0; i < order.length; i++) { order[i] = i; }
		 * order = randomize(order);
		 */
		for (int j = 0; j < order.length; j++) {
			int i = order[j];
			modified = Rules.get(i).doDefaultApplication(Board);
			System.out.println(i);
			if (modified) {
				// This is for keeping track of order of rule applications
				// previousRules.get(lastRule).set(i,
				// (previousRules.get(lastRule).get(i) +1) );
				lastRule = i;
				lastCondition = normal;
				System.out.println(Rules.get(i).getName());
				Board.setJustification(Rules.get(i));
				return Board;
			}
			// System.out.println("No places to apply "+Rules.get(i).getName());
		}
		// System.outprintln("Guessing");
		// This is for keeping track of order of rule applications
		// previousRules.get(lastRule).set(guess,
		// (previousRules.get(lastRule).get(guess) +1) );
		lastCondition = guess;
		BoardState Guessed = ourPM.guess(Board);
		if (Guessed == Board) {
			// No guess possible, because the puzzle is finished or
			// the guessing function has not been implemented for this PM
			// System.out.println("Statement: Your puzzle has been solved");
			// System.out.println("Alternately, you have not told me how to
			// guess on these types of puzzles");
		}
		return Guessed;
	}

	// a simple function to randomize arrays, used for testing rule application
	// order
	private int[] randomize(int[] toRand) {
		Random r = new Random();
		for (int i = 0; i < toRand.length; i++) {
			int swap = r.nextInt(toRand.length);
			int temp = toRand[i];
			toRand[i] = toRand[swap];
			toRand[swap] = temp;
		}
		return toRand;
	}

	// Completes the puzzle, which includes finishing all un-checked guess branches even when a solution is found.
	// Returns false if multiple distinct solutions are found
	public boolean completeReturnUnique(BoardState start)
	{
		BoardState myBoard = start;
		while (!ourPM.checkBoardComplete(myBoard) || contradiction(myBoard)) myBoard = step(myBoard);

		// Puzzle is now solved, scan for completeness:

		return scanCompleteness(start, myBoard);
	}
	private boolean scanCompleteness(BoardState board, BoardState uniqueSolution)
	{
		BoardState choice = board;

		while (true)
		{
			if (choice.getTransitionsFrom().size() == 1) choice = choice.getTransitionsFrom().get(0);
			else if (choice.getTransitionsFrom().size() == 0)
			{
				if (ourPM.checkBoardComplete(choice)) return (choice == uniqueSolution);
				else if (contradiction(choice)) return true;
				else step(choice);
			}
			else
			{
				for (BoardState B : choice.getTransitionsFrom()) if (!scanCompleteness(B, uniqueSolution)) return false;
				return true;
			}
		}
	}

	// The thread function. Steps through a board until it is done
	public void run() {
		BoardState myBoard = toRun;
		while (!ourPM.checkBoardComplete(myBoard) || contradiction(myBoard)) myBoard = step(myBoard);
		// More AIs should act like HK-47
		// System.out.println("Statement: Your puzzle is compleated, Meatbag");
		/*
		 * This is for printing the rule application frequencies
		 * System.out.print("\t"); for(int i = 0; i < Labels.length; i++) {
		 * System.out.print("\t" + Labels[i]); } System.out.println(); for(int i =
		 * 0; i < Labels.length; i++) { if (i != Labels.length-1) {
		 * System.out.print(Labels[i] + "\t\t"); } else {
		 * System.out.print(Labels[i] + "\t"); } //for(int j = 0; j <
		 * previousRules.get(i).size(); j++) { //
		 * System.out.print(previousRules.get(i).get(j) + "\t"); //}
		 * System.out.println(); }
		 */
		// Uncomment this for automated testing
		// finished = true;
	}

	// Encapsulated contradition checking
	private boolean contradiction(BoardState board) {
		boolean contra;
		board = board.addTransitionFrom();
		Vector<Contradiction> contras = ourPM.getContradictions();
		for (int i = 0; i < contras.size(); i++) {
			String error = contras.get(i).checkContradiction(board);
			//System.out.println("Checking "+ contras.get(i).name + ": " + error);
			contra = (error == null);
			if (contra) {
				//System.out.println("Contradiction!");
				board.getSingleParentState().getTransitionsFrom().clear();
				return true;
			}
		}
		board.getSingleParentState().getTransitionsFrom().clear();
		return false;
	}

}

/**
 *	Created 10/01/2008 by Daniel Ploch
 *	Used in AI.step() method to combat the Contradiction problem with the AI Tutor
 */
class ProblemWithBoardStateCopyException extends RuntimeException
{

	BoardState defaultReturn;

	ProblemWithBoardStateCopyException(BoardState defaultReturn)
	{
		super("Corrupt Data - Puzzle is Unsolvable");
		this.defaultReturn = defaultReturn;
	}

}
