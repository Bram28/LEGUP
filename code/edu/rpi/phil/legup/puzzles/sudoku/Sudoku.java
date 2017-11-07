//
//  Sudoku.java
//  LEGUP
//
//  Created by Stan Bak on 12-05
//

package edu.rpi.phil.legup.puzzles.sudoku;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JDialog;

import edu.rpi.phil.legup.AI;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.CellPredicate;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.Endomorphism;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleGeneration;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;

public class Sudoku extends PuzzleModule
{
	public static int CELL_UNKNOWN = 0;
    public Map<String, Integer> getSelectableCells()
    {
        Map<String, Integer> tmp = new LinkedHashMap<String, Integer>();
        for(int i = 0; i <= 9; i++) {
        	tmp.put(String.format("%d", i), i);
        }
        return tmp;
    }
    public Map<String, Integer> getUnselectableCells(){
    	Map<String, Integer> tmp = new LinkedHashMap<String, Integer>();
    	return tmp;
    }
	public boolean hasLabels(){return false;}

    public static final Endomorphism<Point> normalizeToSubgrid = new Endomorphism<Point>() {
        @Override public Point apply(Point p) {
            BoardState state = Legup.getCurrentState();
            int w = state.getWidth(); int h = state.getHeight();
            Point q = CellPredicate.normalizeEdge.apply(p);
            if(q.x > -1 && q.y > -1) {
                // use integer division to normalize points to upper left corner of 3x3 grids
                q.x = q.x/3 * 3;
                q.y = q.y/3 * 3;
            }
            return q;
        }
    };

    public static CellPredicate inSameSubgrid(final Point p) {
        return new CellPredicate() {
            @Override public boolean check(BoardState s, int x, int y) {
                Point q = normalizeToSubgrid.apply(p);
                return (q.x > -1 && q.y > -1) &&
                        (x >= q.x && x < q.x+3) &&
                        (y >= q.y && y < q.y+3);
            }
        };
    }

	Vector <PuzzleRule> ruleList = new Vector <PuzzleRule>();
	Vector <Contradiction> contraList = new Vector <Contradiction>();
	Vector <CaseRule> caseList = new Vector <CaseRule>();

	private static final int[][] groups = new int[27][9], crossRef = new int[81][3];

	private static boolean[][][] annotations = new boolean[9][9][9];
	private static ArrayList<Vector<Integer> > validNums = new ArrayList<Vector<Integer> >();

	static
	{
		//The groups array contains references to each square in the entire grid (numbered from 0 to 80)

		//Go through row by row, starting with the topmost row
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				groups[i][j] = 9*i+j;

		//Go through column by column, starting with the leftmost column
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				groups[i+9][j] = 9*j+i;

		//Go through box by box, starting with the top left corner
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				groups[i+18][j] = 9*(3*(i/3)+(j/3))+3*(i%3)+(j%3);

		for (int i = 0; i < 81; i++)
		{
			int index = 0;
			for (int j = 0; j < 27; j++)
				for (int k = 0; k < 9; k++)
					if (groups[j][k] == i)
					{
						crossRef[i][index++] = j;
						break;
					}
		}
	}

	/**
	 *	Return the reference array for the groups in the Sudoku, containing the specific cells
	 *	0-8 = Rows, 9-17 = Cols, 18-26 = Boxes.  Each element is in the form 9*r+c
	 */
	static int[][] getGroups()
	{
		return groups;
	}

	/**
	 *	Return the cross reference array for individual cells
	 *	Each cell is contained in 3 groups, their indices (0-26) are the elements of the matrix
	 */
	static int[][] getCrossReference()
	{
		return crossRef;
	}

	/**
	 *	Returns a matrix of the form [x][y][v-1], designating if the value v is valid at cell (x,y)
	 */
	static boolean[][][] getPossMatrix(BoardState B)
	{
		boolean[][][] possMatrix = new boolean[9][9][9];
		for (int a = 0; a < 9; a++) for (int b = 0; b < 9; b++) for (int c = 0; c < 9; c++)
			possMatrix[a][b][c] = true;

		for (int x = 0; x < 9; x++) for (int y = 0; y < 9; y++)
			if (B.getCellContents(x, y) != Sudoku.CELL_UNKNOWN)
			{
				int index = B.getCellContents(x, y)-1;
				for (int i = 0; i < 9; i++) possMatrix[x][y][i] = false;
				for (int group : crossRef[y*9+x])
					for (int cell : groups[group])
						possMatrix[cell%9][cell/9][index] = false;
			}
		return possMatrix;
	}

	/**
	 * Marks possible numbers that could fit in each cell.
	 * @param B
	 */
	static void setAnnotations(BoardState B)
	{
		//obtain a large truth table of values that may or may not fit in the cell
		annotations = getPossMatrix(B);

		//obtain all the numbers that could potentially fit each cell
		validNums.clear();
		for (int y = 0; y < 9; y++)
		{
			for (int x = 0; x < 9; x++)
			{
				//Determine which numbers will work
				Vector<Integer> tempNums = new Vector<Integer>();
				for (int i = 0; i < 9; i++)
				{
					if (annotations[x][y][i])
						tempNums.add(i + 1);
				}
				validNums.add(tempNums);
			}
		}
	}

	public Sudoku()
	{
		name = "Sudoku";

		ruleList.add(new RuleLastCellForNumber());
		ruleList.add(new RuleLastNumberForCell());
		//ruleList.add(new RuleAdvancedDeduction());
		contraList.add(new ContradictionRepeatedNumber());
		contraList.add(new ContradictionNoSolutionForCell());
		caseList.add(new CasePossibleNumbersForCell());
		caseList.add(new CasePossibleCellsForNumber());
	}

	public BoardState generatePuzzle(int difficulty, JFrame host)
	{
		BoardState solution = null;
		int diff = -1;

		JDialog loadPane = new JDialog(host, "Loading...", false);
		loadPane.setBounds(150, 150, 250, 50);
		loadPane.setVisible(true);

		while (diff < difficulty)
		{
			diff = -1;
			solution = new BoardState(9, 9);

			seedValue(solution, 1, 0);

			ArrayList<Point> toRemove = new ArrayList<Point>();
			for (int x = 0; x < 9; x++) for (int y = 0; y < 9; y++) toRemove.add(new Point(x, y));

			while (toRemove.size() > 0)
			{
				Point rand = toRemove.remove((int)(Math.random()*toRemove.size()));
				Point rand2 = new Point(8 - rand.x, 8 - rand.y); toRemove.remove(rand2);

				int stolen = solution.getCellContents(rand.x, rand.y);
				int stolen2 = solution.getCellContents(rand2.x, rand2.y);
				solution.setCellContents(rand.x, rand.y, CELL_UNKNOWN);
				solution.setCellContents(rand2.x, rand2.y, CELL_UNKNOWN);

				int prevDiff = diff;
				diff = computeDifficulty(solution);
				if (diff > difficulty)
				{
					solution.setCellContents(rand.x, rand.y, stolen);
					solution.setCellContents(rand2.x, rand2.y, stolen2);
					solution.setModifiableCell(rand.x, rand.y, false);
					solution.setModifiableCell(rand2.x, rand2.y, false);
					diff = prevDiff;
				}
				else
				{
					solution.setModifiableCell(rand.x, rand.y, true);
					solution.setModifiableCell(rand2.x, rand2.y, true);
				}
			}
		}

		loadPane.dispose();

		return solution;
	}
	private boolean seedValue(BoardState openBoard, int value, int row)
	{
		ArrayList<Integer> pot = new ArrayList<Integer>(9);
		for (int x = 0; x < 9; x++) if (openBoard.getCellContents(x, row) == CELL_UNKNOWN) pot.add(new Integer(x));

		if (pot.size() > 1)
		{
			boolean[][][] mat = getPossMatrix(openBoard);
			for (int x = 0; x < pot.size(); x++) if (!mat[pot.get(x).intValue()][row][value-1]) pot.remove(x--);
		}

		while (pot.size() > 0)
		{
			Integer i = pot.remove((int)(Math.random()*pot.size()));

			openBoard.setCellContents(i.intValue(), row, value);

			if (row < 8 && !seedValue(openBoard, value, row+1)) openBoard.setCellContents(i.intValue(), row, CELL_UNKNOWN);
			else if (row == 8 && value < 9 && !seedValue(openBoard, value+1, 0)) openBoard.setCellContents(i.intValue(), row, CELL_UNKNOWN);
			else return true;
		}

		return false;
	}
	private int computeDifficulty(BoardState board)
	{
		BoardState copy = board.copy();

		AI ai = new AI(this);
		boolean unique = ai.completeReturnUnique(copy);

		if (!unique) return PuzzleGeneration.UNSOLVEABLE;

		// Difficulty is classified by move intensity:
		// If the move is ForcedLocation/Deduction and reveals at least 6/3 squares or 1/3 / 1/5 of the remaining, it is easy
		// If the move is ForcedDeduction or, ForcedLocation with less revelation, the move is Medium
		//	However, for a puzzle to be medium, there cannot be a sequence of 4 moves or more with an average number of numbers per turn less than 1.5,
		//	unless at the start of the sequence < 20 squares remain
		// If the puzzle requires guessing, it is optimal
		// If the puzzle does not require guessing, but is too hard for medium, it is hard.  A puzzle with no guessing but with advanced deduction is hard.

		if (unique)
		{
			int runningDifficulty = PuzzleGeneration.EASY;

			BoardState test = copy;
			int[] medTally = new int[4];
			int medCount = 0;
			int medInd = 0;
			while (test != null)
			{
				if (test.getChildren().size() == 0) test = null;
				else if (test.getChildren().size() >= 2) return PuzzleGeneration.OPTIMAL;
				else
				{
					BoardState prev = test;
					test = test.getChildren().get(0);
					if (runningDifficulty == PuzzleGeneration.EASY)
					{
						Object just = test.getJustification();
						if (just instanceof RuleLastCellForNumber)
						{
							int unknown1 = countUnknown(prev), unknown2 = countUnknown(test);
							if (unknown1-unknown2 < 6 && (unknown1-unknown2)*3 < unknown1)
								runningDifficulty = PuzzleGeneration.NORMAL;
						}
						else if (just instanceof RuleLastNumberForCell)
						{
							int unknown1 = countUnknown(prev), unknown2 = countUnknown(test);
							if (unknown1-unknown2 < 3 && (unknown2-unknown1)*5 < unknown1)
								runningDifficulty = PuzzleGeneration.NORMAL;
						}
						else if (just instanceof RuleAdvancedDeduction)
							runningDifficulty = PuzzleGeneration.HARD;
					}
					else if (runningDifficulty == PuzzleGeneration.NORMAL)
					{
						Object just = test.getJustification();
						if (just instanceof RuleLastCellForNumber || just instanceof RuleLastNumberForCell)
						{
							int unknown1 = countUnknown(prev), unknown2 = countUnknown(test);

							if (unknown1 > 20)
							{
								int diff = unknown1-unknown2;
								medTally[medInd] = diff;
								if (++medCount > 4) medCount = 4;
								if (++medInd == 4) medInd = 0;

								if (medCount == 4)
								{
									double avg = 0;
									for (int i = 0; i < 4; i++) avg += medTally[i];
									if (avg/4 < 1.5) runningDifficulty = PuzzleGeneration.HARD;
								}
							}
						}
						else if (just instanceof RuleAdvancedDeduction)
							runningDifficulty = PuzzleGeneration.HARD;
					}
				}
			}
			return runningDifficulty;
		}
		else return PuzzleGeneration.UNSOLVEABLE;
	}
	private int countUnknown(BoardState board)
	{
		int tot = 0;
		for (int x = 0; x < 9; x++) for (int y = 0; y < 9; y++) if (board.getCellContents(x, y) == CELL_UNKNOWN) tot++;
		return tot;
	}

	public boolean checkGoal(BoardState currentBoard, BoardState goalBoard)
	{
		return currentBoard.compareBoard(goalBoard);
	}

	public boolean checkBoardComplete(BoardState state)
	{
		if (!checkValidBoardState(state)) return false;
		for (int x = 0; x < 9; x++) for (int y = 0; y < 9; y++) if (state.getCellContents(x, y) == Sudoku.CELL_UNKNOWN) return false;
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
		int height = boardState.getHeight();
		int width = boardState.getWidth();

		if (width != 9 || height != 9)
			return false;
		else
		{
			HashSet<Integer> set = new HashSet<Integer>();
			for (int[] cells : groups)
			{
				set.clear();
				for (int cell : cells)
				{
					int val = boardState.getCellContents(cell%9, cell/9);
					if (val != CELL_UNKNOWN && !set.add(new Integer(val)))
						return false;
				}
			}
		}

		return true;
	}

	// Static clone of local method, may need renaming
	public static boolean s_checkValidBoardState(BoardState boardState)
	{
		int height = boardState.getHeight();
		int width = boardState.getWidth();

		if (width != 9 || height != 9)
			return false;
		else
		{
			HashSet<Integer> set = new HashSet<Integer>();
			for (int[] cells : groups)
			{
				set.clear();
				for (int cell : cells)
				{
					int val = boardState.getCellContents(cell%9, cell/9);
					if (val != CELL_UNKNOWN && !set.add(new Integer(val)))
						return false;
				}
			}
		}

		return true;
	}

	/**
	 * Draw the current cell in the puzzle.
	 * @param g the Graphics to draw with
	 * @param x the x-coordinate of the cell
	 * @param y the y-coordinate of the cell
	 * @param state the current number in the box
	 */
	public void drawCell( Graphics2D g, int x, int y, int state ){
		if( state > 0 && state < 10 )
			drawText( g, x, y, String.valueOf(state) );
		else
		{
			if (!drawAnnotations)
				return;

			//puzzle has just begun
			if (validNums.size() == 0)
				return;

			//draw annotations once a case rule has been pressed
			int cellIndex = x + y * 9;
			int totalNumbers = validNums.get(cellIndex).size();
			for (int i = 0; i < totalNumbers; i++)
			{
				int number = validNums.get(cellIndex).get(i);
				drawSmallText(g, x, y, String.valueOf(number), totalNumbers, i);
			}
		}
	}

	/**
	 * Draw the grid for the puzzle in the specified coords
	 * @param gr the Graphics to draw with
	 * @param bounds the bounds of the grid
	 * @param w the width (in boxes) of the puzzle
	 * @param h the height (in boxes) of the puzzle
	 */
	public void drawGrid(Graphics gr, Rectangle bounds, int w, int h)
	{
		Graphics2D g = (Graphics2D)gr;
				g.setColor(Color.black);

		double dx = bounds.width / (double)w;
		double dy = bounds.height / (double)h;
		Stroke thin = new BasicStroke(1);
		Stroke thick = new BasicStroke(2);

		// draw vertical lines
		for (int x = 0; x <= w; ++x)
		{
			int drawX = bounds.x + (int)(x * dx);

			if (x % 3 == 0)
				g.setStroke(thick);
			else
				g.setStroke(thin);

			g.drawLine(drawX, bounds.y, drawX,bounds.y + bounds.height);
		}

		// draw horizontal lines
		for (int y = 0; y <= h; ++y)
		{
						int drawY = bounds.y + (int)(y * dy);

			if (y % 3 == 0)
				g.setStroke(thick);
			else
				g.setStroke(thin);

			g.drawLine(bounds.x, drawY, bounds.x + bounds.width, drawY);
		}
	}

	/**
	 * Get the forced dimension for this puzzle, or null if there isn't a forced dimension
	 * @return the size the puzzle must be, or null if the size is allowed to vary
	 */
	public Dimension getForcedDimension()
	{
		return new Dimension(9,9);
	}

	/**
	 * Daniel Ploch 09/30/2008
	 * Locates squares with least # of possible solutions, and chooses one at random
	 */
	public BoardState guess(BoardState B)
	{
		Vector<Point> bestGuesses = new Vector<Point>();
		int bestGuess = 9;

		boolean[][][] possMatrix = getPossMatrix(B);
		for (int x = 0; x < 9; x++) for (int y = 0; y < 9; y++)
		{
			int numPoss = 0;
			for (boolean poss : possMatrix[x][y]) if (poss) numPoss++;
			if (numPoss != 0)
				if (numPoss == bestGuess)
					bestGuesses.add(new Point(x, y));
				else if (numPoss == 1)
				{
					// Flaw in design: If this is the case, AI shouldn't be guessing
					throw new IllegalStateException("Another algorithmic error?  I hate my life even more.");
				}
				else if (numPoss < bestGuess)
				{
					bestGuess = numPoss;
					bestGuesses.clear();
					bestGuesses.add(new Point(x, y));
				}
		}

		if (bestGuesses.size() == 0) // Board is full, gameover
			return B;

		Point randomLoc = bestGuesses.get(0);
		BoardState parent = B.getSingleParentState();
		List<BoardState> cases = new ArrayList<BoardState>();
		cases.add(B);

		for (int i = 1; i < bestGuess; i++)
			cases.add(parent.addTransitionFrom());

		int index = 0;
		for (int i = 0; i < 9; i++)
			if (possMatrix[randomLoc.x][randomLoc.y][i])
				cases.get(index++).setCellContents(randomLoc.x, randomLoc.y, i+1);

		parent.setCaseSplitJustification(getCaseRules().get(0));

		return B;
	}
}
