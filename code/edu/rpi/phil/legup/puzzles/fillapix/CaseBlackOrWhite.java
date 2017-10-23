package edu.rpi.phil.legup.puzzles.fillapix;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Permutations;
import edu.rpi.phil.legup.PuzzleModule;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;

public class CaseBlackOrWhite extends CaseRule
{
	static final long serialVersionUID = 779450537L;
    public CaseBlackOrWhite() {
		setName("Black or White");
		description = "Every cell is either black or white.";
		image = new ImageIcon("images/fillapix/cases/BlackOrWhite.png");

		defaultApplicationText= "Select an unknown square.";
	}

	public String getImageName() {
		return "images/fillapix/cases/BlackOrWhite.png";
	}

	public String checkCaseRuleRaw(BoardState state)
	{
		String rv = null;
		BoardState parent = state.getSingleParentState();
		if (parent.getChildren().size() != 2) {
			rv = "This case rule can only be applied on a two-way split.";
		} else {
			BoardState one = parent.getChildren().get(0);
			BoardState two = parent.getChildren().get(1);

			ArrayList<Point> dif = BoardState.getDifferenceLocations(one,two);
			if (dif.size() > 1) {
				rv = "Your two-way split is only allowed to change a single cell with this rule.";
			} else if (dif.size() == 0) {
				rv = "Your two-way split must change a single cell with this rule.";
			} else {
				Point p = dif.get(0);

				if (!((Fillapix.isBlack(one.getCellContents(p.x,p.y)) &&
						Fillapix.isWhite(two.getCellContents(p.x,p.y))) ||
						(Fillapix.isBlack(two.getCellContents(p.x,p.y)) &&
								Fillapix.isWhite(one.getCellContents(p.x,p.y))))) {
					// if(LEGUP_Gui.LIGHT_UP_LEGACY == true)
					// {rv = "In this case rule, one state's cell must be white and the other a light.";}
					// else
					// {rv = "In this case rule, one state's cell must be light blue and the other a light.";}
				} else if (Fillapix.isUnknown(parent.getCellContents(p.x,p.y))) {
					rv = "The parent cell that you're applying the case rule on must be a blank cell.";
				}
			}
		}

		return rv;
		/*
		if (state.getChildren().size() < 2)
			return "This case rule can only be applied on a split transition";

		List<BoardState> states = state.getChildren();
		if (states.size() != 2) return "This case rule can only be applied to two child states";
		ArrayList<Point> dif = BoardState.getDifferenceLocations(states.get(0), states.get(1));
		if (dif.size() != 1)
			return "Case rule only applies to a split transition of one cell";
		Point difPoint = dif.get(0);

		if (state.getCellContents(difPoint.x, difPoint.y) != Fillapix.CELL_UNKNOWN)
			return "Case rule does not apply to changing values in a split, only making new ones";

		int val0 = states.get(0).getCellContents(difPoint.x, difPoint.y), val1 = states.get(1).getCellContents(difPoint.x, difPoint.y);

		if ((val0 == Fillapix.FILLED && val1 == Fillapix.EMPTY) || (val0 == Fillapix.EMPTY && val1 == Fillapix.FILLED))
			return null;

		return "Case rule only applies to splitting a single unknown cell into Black and White";*/
	}

	public boolean startDefaultApplicationRaw(BoardState state)
	{
		return true;
	}

	public boolean doDefaultApplicationRaw(BoardState state, PuzzleModule pm, Point location)
	{
		if(location.x < 0 || location.y < 0 || location.x >= state.getWidth( ) || location.y >= state.getHeight( ))
			return false;

		if(Fillapix.isUnknown(state.getCellContents(location.x, location.y))) {
			Vector<Integer> states = new Vector<Integer>();
			states.add(new Integer(Fillapix.CELL_BLACK));
			states.add(new Integer(Fillapix.CELL_WHITE));
			Permutations.permutationCell( state, location, states );
			state.setCaseSplitJustification(this);
			return true;
		}
		return false;
	}
}
