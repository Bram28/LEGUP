package edu.rpi.phil.legup.puzzles.battleship;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.CellPredicate;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PointSetAlgorithms;
import edu.rpi.phil.legup.newgui.CaseRuleSelectionHelper;
import edu.rpi.phil.legup.puzzles.sudoku.Sudoku;

public class CaseShipLocations extends CaseRule {

	private static final long serialVersionUID = 8581077747266144558L;

	public String getImageName() {
		return "images/defaultRule.png";
	}
	
	public CaseRuleSelectionHelper getSelectionHelper()
    {
        CellPredicate allTiles = new CellPredicate() {
        	@Override public boolean check(BoardState s, int x, int y) {
                return true;
            }
		};
		
        final CaseRuleSelectionHelper crsh = new CaseRuleSelectionHelper(allTiles);
        
        crsh.shouldHighlightCell = new CellPredicate() {
        	@Override public boolean check(BoardState s, int x, int y) {
                return false;
            }
		};
		
		return crsh;
		
    }
	
	public BoardState autoGenerateCases(BoardState cur, Point pointSelected)
	{
		int currNumSelected;
		Contradiction contra = new ContradictionTooManyRowCol();
		
		String[] possNums = {"1", "2", "3", "4"};

        String sNum = (String) JOptionPane.showInputDialog(null, "Choose a ship size...",
                                    "Possible Sizes for Ship Case Rule", JOptionPane.QUESTION_MESSAGE,
                                    null, possNums, possNums[0]);
        if (sNum != null) {
        	try
        	{
        		currNumSelected = Integer.parseInt(sNum);
        	}
            catch(NumberFormatException e)
            {
            	currNumSelected = 4;
            }
        }
        else
        {
        	return cur;
        }
        
        Set<Point> horizShipLocations = BattleShip.possibleHorizontalShipLocations(cur, currNumSelected);
        Set<Point> vertShipLocations = BattleShip.possibleVerticalShipLocations(cur, currNumSelected);
        
        for (Point curPoint : horizShipLocations)
        {
        	BoardState transition = cur.copy();
        	transition.setCaseSplitJustification(this);
        	int x = curPoint.x;
        	int y = curPoint.y;
        	for (int i = 0; i < currNumSelected; i++)
        	{
        		int value = BattleShip.CELL_MIDDLE;
        		if (i == 0)
        			value = BattleShip.CELL_LEFT_CAP;
        		else if (i == currNumSelected - 1)
        			value = BattleShip.CELL_RIGHT_CAP;
        		
        		transition.setCellContents(x + i, y, value);
        	}
        	if (contra.checkContradictionRaw(transition) != null && !transition.compareBoard(cur))
        	{
        		BoardState caseTransition = cur.addTransitionFrom();
        		caseTransition.setCaseSplitJustification(this);
            	for (int i = 0; i < currNumSelected; i++)
            	{
            		int value = BattleShip.CELL_MIDDLE;
            		if (i == 0)
            			value = BattleShip.CELL_LEFT_CAP;
            		else if (i == currNumSelected - 1)
            			value = BattleShip.CELL_RIGHT_CAP;
            		
            		caseTransition.setCellContents(x + i, y, value);
            	}
        		caseTransition.endTransition();
        	}
        	transition.deleteState();
        	transition = null;
        }
        for (Point curPoint : vertShipLocations)
        {
        	BoardState transition = cur.copy();
        	int x = curPoint.x;
        	int y = curPoint.y;
        	for (int i = 0; i < currNumSelected; i++)
        	{
        		int value = BattleShip.CELL_MIDDLE;
        		if (i == 0)
        			value = BattleShip.CELL_TOP_CAP;
        		else if (i == currNumSelected - 1)
        			value = BattleShip.CELL_BOTTOM_CAP;
        		
        		transition.setCellContents(x, y + i, value);
        	}
        	if (contra.checkContradictionRaw(transition) != null && !transition.compareBoard(cur))
//            if (true)
        	{
        		BoardState caseTransition = cur.addTransitionFrom();
        		caseTransition.setCaseSplitJustification(this);
            	for (int i = 0; i < currNumSelected; i++)
            	{
            		int value = BattleShip.CELL_MIDDLE;
            		if (i == 0)
            			value = BattleShip.CELL_TOP_CAP;
            		else if (i == currNumSelected - 1)
            			value = BattleShip.CELL_BOTTOM_CAP;
            		
            		caseTransition.setCellContents(x, y + i, value);
            	}
        		caseTransition.endTransition();
        	}
        	transition.deleteState();
        	transition = null;
        }
        
        return cur;
        
	}

	protected String checkCaseRuleRaw(BoardState state) {
		BoardState parent = state.getSingleParentState();
		if (parent != null && parent.getChildren().size() < 1){
			return "This case rule can only be applied on a split transition";
		}
		return null;
	}

}
