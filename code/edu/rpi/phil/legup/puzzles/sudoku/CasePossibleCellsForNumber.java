package edu.rpi.phil.legup.puzzles.sudoku;

import javax.swing.ImageIcon;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.CellPredicate;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.newgui.CaseRuleSelectionHelper;


public class CasePossibleCellsForNumber extends CaseRule
{
	private static final long serialVersionUID = 174002227L;

	private int currNumSelected = -1;
	private int numCases;

	public CasePossibleCellsForNumber()
	{
		setName("Possible Cells for Number");
		description = "A number has a limited set of cells in which it can be placed.";
		image = new ImageIcon("images/sudoku/possible_cells_number.png");

		defaultApplicationText= "Select an unknown square.";
	}

	public String getImageName()
	{
		return "images/sudoku/possible_cells_number.png";
	}

    public CaseRuleSelectionHelper getSelectionHelper()
    {
        String[] possNums = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};

        String sNum = (String) JOptionPane.showInputDialog(null, "Choose a number...",
                                    "Possible Cells for Number Case Rule", JOptionPane.QUESTION_MESSAGE,
                                    null, possNums, possNums[0]);
        if (sNum != null) {
            currNumSelected = Integer.parseInt(sNum);
        }

        Set<Integer> whiteListCells = new HashSet<Integer>();
        for (int i = 1; i <= 9; i++) {
            whiteListCells.add(i);
        }
        CellPredicate p = CellPredicate.union(CellPredicate.modifiableCell, CellPredicate.edge,
                                                CellPredicate.typeWhitelist(whiteListCells));
        final CaseRuleSelectionHelper crsh = new CaseRuleSelectionHelper(p);

        crsh.normalizePoint = Sudoku.normalizeToSubgrid;

        crsh.shouldHighlightCell = CellPredicate.union(CellPredicate.sameRowOrColumn(crsh.lastMousePosition),
                                                            Sudoku.inSameSubgrid(crsh.lastMousePosition));
        return crsh;
    }

  public BoardState autoGenerateCases(BoardState cur, Point pointSelected)
	{
		numCases = 0;
		Contradiction contra = new ContradictionRepeatedNumber();
		int xLo, xHi, yLo, yHi;
		if (pointSelected.x < 0) {
			xLo = 0;
			xHi = 9;
			yLo = pointSelected.y;
			yHi = pointSelected.y + 1;
		} else if (pointSelected.y < 0) {
			xLo = pointSelected.x;
			xHi = pointSelected.x + 1;
			yLo = 0;
			yHi = 9;
		} else {
			xLo = (pointSelected.x/3) * 3;
			xHi = ((pointSelected.x/3) * 3) + 3;
			yLo = (pointSelected.y/3) * 3;
			yHi = ((pointSelected.y/3) * 3) + 3;
		}
		if (currNumSelected == -1) return cur;
		int num = currNumSelected;
		for (int x = xLo; x < xHi; x++) {
			for (int y = yLo; y < yHi; y++) {
				if (cur.getCellContents(x, y) != Sudoku.CELL_UNKNOWN) {
					continue;
				}
				BoardState modified = cur.copy();
				modified.getBoardCells()[y][x] = num;

				if (contra.checkContradictionRaw(modified) == null) {
					continue;
				}

				BoardState tmp = cur.addTransitionFrom();
				tmp.setCaseSplitJustification(this);
				tmp.setCellContents(x, y, num);
				tmp.endTransition();
				numCases+=1;
			}
		}

		return Legup.getCurrentState();
	}

	public boolean isSingleCase() {
		return numCases==1;
	}

	public String checkCaseRuleRaw(BoardState state)
	{
		BoardState parent = state.getSingleParentState();
		if (parent != null && parent.getChildren().size() < 2){
			return "This case rule can only be applied on a split transition";
		}
		return null;
	}

	public boolean startDefaultApplicationRaw(BoardState state)
	{
		return true;
	}

	public boolean doDefaultApplicationRaw(BoardState state, PuzzleModule pm ,Point location)
	{
    return true;

	}
}
