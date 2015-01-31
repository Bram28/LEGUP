package edu.rpi.phil.legup.puzzles.fillapix;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Permutations;
import edu.rpi.phil.legup.PuzzleModule;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;

public class CaseWhiteOrBlack extends CaseRule
{
	static final long serialVersionUID = 779450537L;
	
    public CaseWhiteOrBlack()
	{
		setName("White or Black");
		description = "An unknown cell can only be white or black";
		image = new ImageIcon("images/fillapix/whiteorblack.png");

		defaultApplicationText= "Select an unknown square.";
	}

	public String getImageName()
	{
		return "images/fillapix/whiteorblack.png";
	}

	public String checkCaseRuleRaw(BoardState state)
	{
		if (state.getChildren().size() < 2)
			return "This case rule can only be applied on a split transition";

		Vector<BoardState> states = state.getChildren();
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

		return "Case rule only applies to splitting a single unknown cell into Black and White";
	}

	public boolean startDefaultApplicationRaw(BoardState state)
	{
		return true;
	}

	public boolean doDefaultApplicationRaw(BoardState state, PuzzleModule pm, Point location)
	{
		if(location.x < 0 || location.y < 0 || location.x >= state.getWidth( ) || location.y >= state.getHeight( ))
			return false;

		if(state.getCellContents(location.x, location.y) == Fillapix.CELL_UNKNOWN)
		{
			Vector<Integer> states = new Vector<Integer>();
			states.add(new Integer(Fillapix.FILLED));
			states.add(new Integer(Fillapix.EMPTY));

			Permutations.permutationCell( state, location, states );

			state.setCaseSplitJustification(this);

			return true;
		}

		return false;
	}

}