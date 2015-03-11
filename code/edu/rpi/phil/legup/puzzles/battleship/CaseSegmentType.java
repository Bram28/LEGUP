package edu.rpi.phil.legup.puzzles.battleship;

import java.awt.Point;
import java.util.Vector;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.newgui.CaseRuleSelectionHelper;

public class CaseSegmentType extends CaseRule {
	private static final long serialVersionUID = -683278713957257834L;
	public int crshMode(){return CaseRuleSelectionHelper.MODE_TILETYPE;}
	public Vector<Integer> crshTileType()
	{
		Vector<Integer> ret = new Vector<Integer>();
		ret.add(BattleShip.CELL_SEGMENT);
		return ret;
	}
	
	public CaseSegmentType()
	{
		setName("Ship Type");
		description = "A ship segment can be one of several types.";
	}

	
	public BoardState autoGenerateCases(BoardState cur, Point pointSelected)
	{
		Vector<Integer> types = new Vector<Integer>();
		types.add(BattleShip.CELL_LEFT_CAP);
		types.add(BattleShip.CELL_RIGHT_CAP);
		types.add(BattleShip.CELL_BOTTOM_CAP);
		types.add(BattleShip.CELL_TOP_CAP);
		types.add(BattleShip.CELL_SUBMARINE);
		types.add(BattleShip.CELL_MIDDLE);
		for (int curType : types)
		{
			BoardState branch = cur.addTransitionFrom();
			branch.setCaseSplitJustification(this);
			branch.setCellContents(pointSelected.x, pointSelected.y, curType);
			branch.endTransition();
		}
		
		return Legup.getCurrentState();
	}

	protected String checkCaseRuleRaw(BoardState state) {
		return null;
	}

	public String getImageName() {
		return "images/battleship/SegmentType.png";
	}

}
