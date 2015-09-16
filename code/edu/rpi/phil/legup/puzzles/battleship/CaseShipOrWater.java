package edu.rpi.phil.legup.puzzles.battleship;

import java.awt.Point;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Legup;

public class CaseShipOrWater extends CaseRule {
	private static final long serialVersionUID = -8223546876387804878L;
	
	public CaseShipOrWater()
	{
		setName("Ship or Water");
		description = "A tile can either be a ship segment or water.";
	}

	
	public BoardState autoGenerateCases(BoardState cur, Point pointSelected)
	{		
		BoardState branchWater = cur.addTransitionFrom();
		branchWater.setCaseSplitJustification(this);
		branchWater.setCellContents(pointSelected.x, pointSelected.y, BattleShip.CELL_WATER);
		branchWater.endTransition();
		
		BoardState branchShip = cur.addTransitionFrom();
		branchShip.setCaseSplitJustification(this);
		branchShip.setCellContents(pointSelected.x, pointSelected.y, BattleShip.CELL_SEGMENT);
		branchShip.endTransition();
		
		return Legup.getCurrentState();
	}

	protected String checkCaseRuleRaw(BoardState state) {
		return null;
	}

	public String getImageName() {
		return "images/battleship/cases/ShipOrWater.png";
	}

}
