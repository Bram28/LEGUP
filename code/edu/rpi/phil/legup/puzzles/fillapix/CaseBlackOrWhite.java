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
					rv = "In this case rule, one state's cell must be white and the other black.";
				} else if (Fillapix.isUnknown(parent.getCellContents(p.x,p.y))) {
					rv = "The parent cell that you're applying the case rule on must be a blank cell.";
				}
			}
		}
		return rv;
	}

	/*
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
	*/

}
