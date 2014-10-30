package edu.rpi.phil.legup.puzzles.fillapix;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;

import edu.rpi.phil.legup.AI;
import edu.rpi.phil.legup.BoardImage;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleGeneration;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;

public class Fillapix extends PuzzleModule
{
	public static int UNKNOWN = 0, FILLED = 1, EMPTY = 2;

    public List<String> getCellNames()
    { return Arrays.asList(new String[] {"blank", "filled", "empty"}); }
    public Set<Integer> getUnselectableCells()
    { return new HashSet(Arrays.asList(new Integer[] {})); }

	Vector <PuzzleRule> ruleList;
	Vector <Contradiction> contraList;
	Vector <CaseRule> caseList;

	public Fillapix()
	{
		ruleList = new Vector <PuzzleRule>();
		contraList = new Vector <Contradiction>();
		caseList = new Vector <CaseRule>();

		ruleList.add(new RuleForcedFill());
		ruleList.add(new RuleSharedCells());
		contraList.add(new ContradictionBoardStateViolated());
		caseList.add(new CaseWhiteOrBlack());
	}

	public void drawCell( Graphics2D g, int x, int y, BoardState state ){
		int val = state.getCellContents( x, y );
		// draw the background color
		if( val != 0 ){
			g.setColor( (val==1) ? Color.black : Color.white );
			g.fill( getCellBounds(x,y) );
		}
		// find the number to display
		int num = 10;  Point p = new Point(x, y);
		for (ExtraCellNumber ecn : getECNs(state))
			if( ecn.getPoint().equals(p) )
                        {
				num = ecn.getVal();
				break;
			}
		// set the text color
		fontColor = (val==1) ? Color.white : Color.black;
		// draw the number
		if( num < 10 )
			drawText( g, x, y, String.valueOf(num) );
	}

	public static ArrayList<ExtraCellNumber> getECNs(BoardState state)
	{
		for (int i = 0; i < state.getExtraData().size(); i++)
			if (!(state.getExtraData().get(i) instanceof ExtraCellNumber))
				state.getExtraData().remove(i--);

		ArrayList<ExtraCellNumber> arr = new ArrayList<ExtraCellNumber>();
		for (int i = 0; i < state.getExtraData().size(); i++) arr.add((ExtraCellNumber)state.getExtraData().get(i));

		return arr;
	}

	public void addExtra(ExtraCellNumber ecn, BoardState state)
	{
		state.addExtraData(ecn);
	}
	public void removeExtra(ExtraCellNumber ecn, BoardState state)
	{
		state.getExtraData().remove(ecn);
		state.boardDataChanged();
	}

	public void initBoard(BoardState state)
	{
		// blank board is a fine initial board for Fill-a-Pix
	}

	private static final int[] dim = {15, 15, 15, 15};
	public BoardState generatePuzzle(int difficulty, JFrame host)
	{
		BoardState solution = null;
		int diff = -1;

		JDialog loadPane = new JDialog(host, "Loading...", false);
		loadPane.setBounds(150, 150, 250, 50);
		loadPane.setVisible(true);

		int attmpt = 0;
		while (diff < difficulty)
		{
			attmpt++;
			loadPane.setTitle("Loading: Initializing Attempt " + attmpt);
			loadPane.repaint();

			diff = -1;
			solution = new BoardState(dim[difficulty], dim[difficulty]);
			solution.setVirtual(true);
			randomSeed(solution);

			for (int i = PuzzleGeneration.OPTIMAL; i > difficulty; i--) solution = clusterMechanics(solution);
			finalizeBoard(solution);

			ArrayList<Point> toRemove = new ArrayList<Point>();
			for (int x = 0; x < solution.getWidth(); x++) for (int y = 0; y < solution.getHeight(); y++)
			{
				solution.setModifiableCell(x, y, true);
				toRemove.add(new Point(x, y));
			}

			int DIFF = computeDifficulty(solution);
			if (DIFF > difficulty) { continue;	}

			int numRemovals = 1; //solution.getWidth();
			while (toRemove.size() > 0)
			{
				ArrayList<ExtraCellNumber> removals = new ArrayList<ExtraCellNumber>(numRemovals);
				for (int i = 0; i < numRemovals; i++)
				{
					Point p = toRemove.remove((int)(Math.random()*toRemove.size()));
					removals.add(getECNAt(p, solution));
				}

				for (ExtraCellNumber ecn : removals) removeExtra(ecn, solution);

				int prevDiff = diff;
				diff = computeDifficulty(solution);
				while (diff > difficulty)
				{
					addExtra(removals.remove(0), solution);
					diff = computeDifficulty(solution);
					double prct = (1 - ((toRemove.size() * 1.0 + removals.size()) / (solution.getWidth() * solution.getHeight())))*100;
					loadPane.setTitle("Loading: " + (int)prct + "% on Attempt " + attmpt);
					loadPane.repaint();
				}

				double prct = (1 - ((toRemove.size() * 1.0) / (solution.getWidth() * solution.getHeight())))*100;
				loadPane.setTitle("Loading: " + (int)prct + "% on Attempt " + attmpt);
				loadPane.repaint();
				if (diff > difficulty) diff = prevDiff;
			}
		}

		loadPane.dispose();

		return solution;
	}
	private void randomSeed(BoardState state)
	{
		for (int x = 0; x < state.getWidth(); x++) for (int y = 0; y < state.getHeight(); y++)
		{
			if (Math.random() < 0.5) state.setCellContents(x, y, FILLED);
			else state.setCellContents(x, y, EMPTY);
		}
	}
	private static double probAbsorb = 0.5;
	private BoardState clusterMechanics(BoardState state)
	{
		BoardState result = state.copy();

		for (int x = 0; x < state.getWidth(); x++) for (int y = 0; y < state.getHeight(); y++) if (Math.random() < probAbsorb)
		{
			ExtraCellNumber ecn = new ExtraCellNumber();
			ecn.setPoint(new Point(x, y));
			int nBlack = ecn.getBlackCells(state).size(), nWhite = ecn.getWhiteCells(state).size();

			boolean goBlack = (Math.random() < (nBlack * 1.0) / (nBlack + nWhite));
			if (goBlack) result.setCellContents(x, y, FILLED); else result.setCellContents(x, y, EMPTY);
		}

		return result;
	}
	private void finalizeBoard(BoardState state)
	{
		for (int x = 0; x < state.getWidth(); x++) for (int y = 0; y < state.getHeight(); y++)
		{
			ExtraCellNumber ecn = new ExtraCellNumber();
			ecn.setPoint(new Point(x, y));
			ecn.setVal(ecn.getBlackCells(state).size());
			addExtra(ecn, state);
		}
		for (int x = 0; x < state.getWidth(); x++) for (int y = 0; y < state.getHeight(); y++) state.setCellContents(x, y, UNKNOWN);
	}

	public int computeDifficulty(BoardState state)
	{
		BoardState copy = state.copy();

		AI ai = new AI(this);
		boolean unique = ai.completeReturnUnique(copy);

		if (!unique) return PuzzleGeneration.UNSOLVEABLE;

		// Difficulty is classified by move intensity:
		// If the move is RuleForcedFill and reveals at least 3 squares or 1/20 of the remaining, it is Easy
		// If the move is RuleForcedFill and reveals at least 2 squares of 1/30 of the remaining, it is Medium
		//	It is also Medium if the RuleSharedCells rule occurs, but never twice within 5 turns, and always revealing at least 10 squares or 1/6 of the remaining
		// If guessing is not required, it is hard
		// If the puzzle requires guessing, it is optimal

		int runningDifficulty = PuzzleGeneration.EASY;

		BoardState test = copy;
		int lastShared = 5;
		while (test != null)
		{
			if (test.getTransitionsFrom().size() == 0) test = null;
			else if (test.getTransitionsFrom().size() >= 2) return PuzzleGeneration.OPTIMAL;
			else
			{
				BoardState prev = test;
				test = test.getTransitionsFrom().get(0);
				if (runningDifficulty == PuzzleGeneration.EASY)
				{
					Object just = test.getJustification();
					if (just instanceof RuleForcedFill)
					{
						int unknown1 = countUnknown(prev), unknown2 = countUnknown(test);
						if (unknown1-unknown2 < 2 && (unknown1-unknown2)*30 < unknown1)
							runningDifficulty = PuzzleGeneration.HARD;
						else if (unknown1-unknown2 < 3 && (unknown1-unknown2)*20 < unknown1)
							runningDifficulty = PuzzleGeneration.NORMAL;
					}
					else if (just instanceof RuleSharedCells)
					{
						runningDifficulty = PuzzleGeneration.NORMAL;
						lastShared = 0;
					}
				}
				else if (runningDifficulty == PuzzleGeneration.NORMAL)
				{
					if (lastShared < 5) lastShared++;
					Object just = test.getJustification();
					if (just instanceof RuleForcedFill)
					{
						int unknown1 = countUnknown(prev), unknown2 = countUnknown(test);
						if (unknown1-unknown2 < 2 && (unknown1-unknown2)*40 < unknown1)
							runningDifficulty = PuzzleGeneration.HARD;
					}
					else if (just instanceof RuleSharedCells)
						if (lastShared < 5) runningDifficulty = PuzzleGeneration.HARD;
				}
			}
		}
		return runningDifficulty;
	}
	private int countUnknown(BoardState board)
	{
		int tot = 0;
		for (int x = 0; x < board.getWidth(); x++) for (int y = 0; y < board.getHeight(); y++) if (board.getCellContents(x, y) == CELL_UNKNOWN) tot++;
		return tot;
	}

	/**
	 * Get all the images (as strings to the image path) used by this puzzle in the center part
	 * @return an array of strings to image paths
	 */
	public BoardImage[] getAllCenterImages()
	{
		BoardImage[] s = new BoardImage[33];

		for (int i = 0; i <= 10; i++) for (int j = 0; j <= 2; j++)
			s[11*j+i] = new BoardImage("images/fillapix/cellval["+i+"]["+j+"].GIF", 11*j+i);

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

	public void directModify(int x, int y, BoardState state)
	{
		ExtraCellNumber ecn = getECNAt(new Point(x, y), state);

		if (ecn != null)
		{
   	 	if (ecn.getVal() == 9) removeExtra(ecn, state);
			else ecn.setVal(ecn.getVal()+1);
		}
                
		else
		{
			ecn = new ExtraCellNumber();
			ecn.setPoint(new Point(x, y));
			ecn.setVal(0);

			addExtra(ecn, state);
		}
          
          
	}
	private ExtraCellNumber getECNAt(Point p, BoardState state)
	{
		for (ExtraCellNumber ecn : getECNs(state)) if (ecn.getPoint().equals(p)) return ecn;
		return null;
	}

        public void mousePressedEvent(BoardState state, Point where)
	{
            	BoardState state2 = state.getSingleParentState();
                int next2=state2.getCellContents(where.x, where.y);
                //only alter the cell if it has not been altered in a previous state
                if (next2==0)
                {
                    int next = getNextCellValue(where.x,where.y,state);
                    state.setCellContents(where.x,where.y,next);
                }
             else
                {}

		
	}
        
	public boolean checkGoal(BoardState currentBoard, BoardState goalBoard)
	{
		return currentBoard.compareBoard(goalBoard);
	}

	public boolean checkBoardComplete(BoardState state)
	{
		if (!checkValidBoardState(state)) return false;
		for (int x = 0; x < state.getWidth(); x++) for (int y = 0; y < state.getHeight(); y++) if (state.getCellContents(x, y) == UNKNOWN) return false;
		return true;
	}

	public Vector<PuzzleRule> getRules()
	{
		return ruleList;
	}

	public Vector<Contradiction> getContradictions()
	{
		return contraList;
	}

	public Vector<CaseRule> getCaseRules()
	{
		return caseList;
	}

	public boolean checkValidBoardState(BoardState boardState)
	{
		for (ExtraCellNumber ecn : getECNs(boardState)) if (!ecn.valid(boardState)) return false;
		return true;
	}

	// Static clone of local method, may need renaming
	public static boolean s_checkValidBoardState(BoardState boardState)
	{
		return (new Fillapix()).checkValidBoardState(boardState);
	}

	/**
	 * Daniel Ploch 09/30/2008
	 * Locates squares with least # of possible solutions, and chooses one at random
	 */
	public BoardState guess(BoardState B)
	{
		Point toGuess = null;

		int w = B.getWidth(), h = B.getHeight();

		outer: for (int x = 0; x < w; x++) for (int y = 0; y < h; y++) if (B.getCellContents(x, y) == Fillapix.UNKNOWN)
		{
			toGuess = new Point(x, y);
			break;
		}

		if (toGuess == null) // Board is full, gameover
			return B;

		BoardState parent = B.getSingleParentState();

		B.setCellContents(toGuess.x, toGuess.y, FILLED);
		BoardState white = parent.addTransitionFrom();
		white.setCellContents(toGuess.x, toGuess.y, EMPTY);

		parent.setCaseSplitJustification(getCaseRules().get(0));

		return B;
	}
}
