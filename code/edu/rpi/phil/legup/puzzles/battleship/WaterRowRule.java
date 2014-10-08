package edu.rpi.phil.legup.puzzles.battleship;

import edu.rpi.phil.legup.*;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class WaterRowRule extends PuzzleRule
{
	static final long serialVersionUID = 853810334L;

	private static final BattleShip battleship = new BattleShip();

	public WaterRowRule()
	{
		setName("Row/Column Deduction Rule");
		description = "When all the ship/water cells have been accounted for, fill in the rest of the unknowns appropriately";
		image = new ImageIcon("images/battleship/labelforce.png");
	}

	protected String checkRuleRaw(BoardState state)
	{
		if (state.getTransitionsFrom().size() > 1)
			return "Rule does not apply to Merge cases";

		BoardState parent = state.getSingleParentState();
		ArrayList<Point> diffs1 = BoardState.getDifferenceLocations(parent, state);
		for (Point p : diffs1)
		{
			if (parent.getCellContents(p.x, p.y) != BattleShip.CELL_UNKNOWN)
				return "You cannot change cells to apply this rule!";
			if (battleship.isConcreteShipPart(state.getCellContents(p.x, p.y)))
				return "You cannot choose specific ship parts when applying this rule";
		}

		BoardState clone = state.copy();
		BoardState labrat = clone.addTransitionFrom();
		doDefaultApplicationRaw(labrat);
		ArrayList<Point> diffs2 = BoardState.getDifferenceLocations(state, labrat);
		for (Point p : diffs2)
		{
			if (labrat.getCellContents(p.x, p.y) == BattleShip.CELL_UNKNOWN)
				return "Cell change at ("+p.x+", "+p.y+") is not deducible";
			if (state.getCellContents(p.x, p.y) != BattleShip.CELL_UNKNOWN)
				return "Cell change at ("+p.x+", "+p.y+") is incorrect";
		}

		return null;
	}

	protected boolean doDefaultApplicationRaw(BoardState state)
	{
		boolean updated = false;
		BoardState noUpdate = state.getSingleParentState().copy();

		for (int i = 0; i < state.getWidth(); i++) battleship.labelPressedEvent(state, i, BoardState.LABEL_BOTTOM);
		for (int i = 0; i < state.getHeight(); i++) battleship.labelPressedEvent(state, i, BoardState.LABEL_RIGHT);

		if (!updated)
			state = noUpdate;

    	Legup.getInstance().getPuzzleModule().updateState(state);

		return updated;
	}

}