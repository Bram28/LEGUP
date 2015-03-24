package edu.rpi.phil.legup.puzzles.sudoku;

import javax.swing.ImageIcon;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import javax.swing.JOptionPane;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.CellPredicate;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.Permutations;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.newgui.CaseRuleSelectionHelper;


public class CasePossibleCellsForNumber extends CaseRule
{
	private static final long serialVersionUID = 174002227L;

	private int currNumSelected = -1;

	public CasePossibleCellsForNumber()
	{
		setName("Possible Cells for Number");
		description = "There are a limited number of cells to place a given number, based on elimination";
		image = new ImageIcon("images/sudoku/PossibleValues.png");

		defaultApplicationText= "Select an unknown square.";
	}

	public String getImageName()
	{
		return "images/sudoku/PossibleValues.png";
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
        CellPredicate p = CellPredicate.union(CellPredicate.modifiableCell(), CellPredicate.edge(),
                                                CellPredicate.typeWhitelist(whiteListCells));
        final CaseRuleSelectionHelper crsh = new CaseRuleSelectionHelper(p);

        crsh.normalizePoint = Sudoku.normalizeToSubgrid;

        crsh.shouldHighlightCell = CellPredicate.union(CellPredicate.sameRowOrColumn(crsh.lastMousePosition),
                                                            Sudoku.inSameSubgrid(crsh.lastMousePosition));
        return crsh;
    }

  public BoardState autoGenerateCases(BoardState cur, Point pointSelected)
	{
		Contradiction contra = new ContradictionBoardStateViolated();
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
			}
		}

		return Legup.getCurrentState();
	}

	public String checkCaseRuleRaw(BoardState state)
	{
		BoardState parent = state.getSingleParentState();
		if (parent != null && parent.getChildren().size() < 2){
			return "This case rule can only be applied on a split transition";
		}
// 		Vector<BoardState> states = parent.getChildren();
// 		ArrayList<Point> dif = BoardState.getDifferenceLocations(states.get(0), states.get(1));
// 		if (dif.size() != 1){
// 			return "Case rule only applies to a split transition of one cell";
// 		}
// 		Point difPoint = dif.get(0);
//
// 		for (int i = 1; i < states.size()-1; i++)
// 		{
// 			dif = BoardState.getDifferenceLocations(states.get(i), states.get(i+1));
// 			if (dif.size() != 1 || !difPoint.equals(dif.get(0))){
// 				return "Case rule only applies to a split transition of one cell";
// 			}
// 		}
//
// 		if (parent.getCellContents(difPoint.x, difPoint.y) != Sudoku.CELL_UNKNOWN){
// 			return "Case rule does not apply to changing values in a split, only making new ones";
// 		}
// 		boolean[][][] possMatrix = Sudoku.getPossMatrix(parent);
// 		LinkedList<Integer> values = new LinkedList<Integer>();
// 		for (int i = 0; i < 9; i++){
// 			if (possMatrix[difPoint.x][difPoint.y][i]){
// 				values.add(new Integer(i+1));
// 			}
// 		}
// 		if (values.size() == 0){
// 			return "Cannot apply case rule to an invalid board state";
// 		}
//
// //		for (BoardState B : states)
// //		{
// //			Integer val = new Integer(B.getCellContents(difPoint.x, difPoint.y));
// ////			if (!values.remove(val)){
// ////				return "Case rule does not apply to invalid possibilities";
// ////			}
// //		}
// //
// //		if (values.size() > 0){
// //			return "Case rule invalid - not all possibilities have been accounted for";
// //		}
		return null;
	}

	public boolean startDefaultApplicationRaw(BoardState state)
	{
		return true;
	}

	public boolean doDefaultApplicationRaw(BoardState state, PuzzleModule pm ,Point location)
	{
    return true;
		// if(location.x < 0 || location.y < 0 || location.x >= state.getWidth( ) || location.y >= state.getHeight( )){
		// 	return false;
		// }
	//
	// 	if(state.getCellContents(location.x, location.y) == Sudoku.CELL_UNKNOWN)
	// 	{
	// 		Vector<Integer> states = new Vector<Integer>();
	// 		boolean[][][] possMatrix = Sudoku.getPossMatrix(state);
	// 		for (int i = 0; i < 9; i++) if (possMatrix[location.x][location.y][i]) states.add(new Integer(i+1));
  //
	// 		Permutations.permutationCell( state, location, states );
  //
	// 		state.setCaseSplitJustification(this);
  //
	// 		return true;
	// 	}
	// 	return false;
	}
}
