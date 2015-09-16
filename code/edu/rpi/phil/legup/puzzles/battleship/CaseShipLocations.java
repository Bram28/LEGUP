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
	private static final Set<Integer> waterSet;
	private static final Set<Integer> segmentSet;
    private static final Set<Integer> leftSegmentSet;
    private static final Set<Integer> rightSegmentSet;
    private static final Set<Integer> middleSegmentSet;
    private static final Set<Integer> topSegmentSet;
    private static final Set<Integer> bottomSegmentSet;
		
	static
	{
        waterSet = new LinkedHashSet<Integer>();
        segmentSet = new LinkedHashSet<Integer>();
        leftSegmentSet = new LinkedHashSet<Integer>();
        rightSegmentSet = new LinkedHashSet<Integer>();
        middleSegmentSet = new LinkedHashSet<Integer>();
        topSegmentSet = new LinkedHashSet<Integer>();
        bottomSegmentSet = new LinkedHashSet<Integer>();
        
        waterSet.add(BattleShip.CELL_WATER);
        waterSet.add(BattleShip.CELL_UNKNOWN);
        waterSet.add(PointSetAlgorithms.POINT_OUTSIDE);
        
        segmentSet.add(BattleShip.CELL_SEGMENT);
        segmentSet.add(BattleShip.CELL_UNKNOWN);
        
        leftSegmentSet.addAll(segmentSet);
        rightSegmentSet.addAll(segmentSet);
        middleSegmentSet.addAll(segmentSet);
        topSegmentSet.addAll(segmentSet);
        bottomSegmentSet.addAll(segmentSet);
        
        leftSegmentSet.add(BattleShip.CELL_LEFT_CAP);
        rightSegmentSet.add(BattleShip.CELL_RIGHT_CAP);
        middleSegmentSet.add(BattleShip.CELL_MIDDLE);
        topSegmentSet.add(BattleShip.CELL_TOP_CAP);
        bottomSegmentSet.add(BattleShip.CELL_BOTTOM_CAP);
	}

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
        
        Map<Point,Set<Integer>> horizontalShipMask = new LinkedHashMap<Point,Set<Integer>>();
        Map<Point,Set<Integer>> verticalShipMask = new LinkedHashMap<Point,Set<Integer>>();
                
        for (int i = -1; i <= currNumSelected; i++)
        {
        	Set<Integer> middleHorizSet;
        	Set<Integer> middleVertSet;
        	if (i == -1)
        	{
        		middleHorizSet = waterSet;
        		middleVertSet = waterSet;
        	}
        	else if (i == 0)
        	{
        		middleHorizSet = leftSegmentSet;
        		middleVertSet = topSegmentSet;
        	}
        	else if (i == currNumSelected - 1)
        	{
        		middleHorizSet = rightSegmentSet;
        		middleVertSet = bottomSegmentSet;
        	}
        	else if (i == currNumSelected)
        	{
        		middleHorizSet = waterSet;
        		middleVertSet = waterSet;
        	}
        	else
        	{
        		middleHorizSet = middleSegmentSet;
        		middleVertSet = middleSegmentSet;
        	}
        	horizontalShipMask.put(new Point(i, -1), waterSet);
        	horizontalShipMask.put(new Point(i,  0), middleHorizSet);
        	horizontalShipMask.put(new Point(i,  1), waterSet);
        	verticalShipMask.put(new Point(-1, i), waterSet);
        	verticalShipMask.put(new Point( 0, i), middleVertSet);
        	verticalShipMask.put(new Point( 1, i), waterSet);
        }
        
        Set<Point> horizShipLocations = PointSetAlgorithms.getPositionsForPointSet(cur.getBoardCells(), horizontalShipMask);
        Set<Point> vertShipLocations = PointSetAlgorithms.getPositionsForPointSet(cur.getBoardCells(), verticalShipMask);
        
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
