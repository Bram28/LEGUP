//
//  BattleShip.java
//  LEGUP
//
//  Created by Drew Housten on Wed April 27 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//


package edu.rpi.phil.legup.puzzles.battleship;
import edu.rpi.phil.legup.*;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class BattleShip extends PuzzleModule
{
	static final long serialVersionUID = 532393951L;
	
	public static int[] numShips = {4, 3, 2, 1};

	public static final int CELL_UNKNOWN = 0;
	public static final int CELL_WATER = 1;
	public static final int CELL_SEGMENT = 2;
	public static final int CELL_LEFT_CAP = 10;
	public static final int CELL_TOP_CAP = 11;
	public static final int CELL_BOTTOM_CAP = 12;
	public static final int CELL_RIGHT_CAP = 13;
	public static final int CELL_SUBMARINE = 14;
	public static final int CELL_MIDDLE = 15;
	
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
	
    public Map<String, Integer> getSelectableCells()
    {
        Map<String, Integer> tmp = new LinkedHashMap<String, Integer>();
        tmp.put("blank", CELL_UNKNOWN);
        tmp.put("water", CELL_WATER);
        tmp.put("segment", CELL_SEGMENT);
        tmp.put("left cap", CELL_LEFT_CAP);
        tmp.put("top cap", CELL_TOP_CAP);
        tmp.put("bottom cap", CELL_BOTTOM_CAP);
        tmp.put("right cap", CELL_RIGHT_CAP);
        tmp.put("submarine", CELL_SUBMARINE);
        tmp.put("middle", CELL_MIDDLE);
        return tmp;
    }
    public Map<String, Integer> getUnselectableCells()
    {
        Map<String, Integer> tmp = new LinkedHashMap<String, Integer>();
        return tmp;
    }

    public boolean isRemodifiable(int cellType) {
		return cellType == CELL_SEGMENT;
	}

	public void labelPressedEvent(BoardState state, int index, int side)
	{
		ArrayList<Point> points = new ArrayList<Point>();
	 	BoardState toModify = state.conditionalAddTransition();
		if (side == BoardState.LABEL_LEFT || side == BoardState.LABEL_RIGHT)
		{
			side = BoardState.LABEL_RIGHT;
			for (int i = 0; i < state.getWidth(); i++) points.add(new Point(i, index));
		}
		else
		{
			side = BoardState.LABEL_BOTTOM;
			for (int i = 0; i < state.getHeight(); i++) points.add(new Point(index, i));
		}

		int numShips = 0, numUnknown = 0;

		for (Point p : points)
		{
			int val = state.getCellContents(p.x, p.y);
			if (isShip(val)) numShips++;
			else if (val != CELL_WATER) numUnknown++;
		}

		if (numShips == toModify.getLabel(side, index)-40)
		{
			for (Point p : points) if (Math.abs(toModify.getCellContents(p.x, p.y)) == CELL_UNKNOWN)
				toModify.setCellContents(p.x, p.y, CELL_WATER);
		}
		else if (numShips+numUnknown == toModify.getLabel(side, index)-40)
		{
			for (Point p : points) if (toModify.getCellContents(p.x, p.y) == CELL_UNKNOWN)
				toModify.setCellContents(p.x, p.y, CELL_SEGMENT);
		}
	}

	public String getImageLocation(int cellValue)
	{
		switch (cellValue)
		{
		case CELL_UNKNOWN:
			return "images/blank.gif";
		case CELL_WATER:
			return "images/battleship/Water.png";
		case CELL_TOP_CAP:
			return "images/battleship/TopCap.png";
		case CELL_BOTTOM_CAP:
			return "images/battleship/BottomCap.png";
		case CELL_LEFT_CAP:
			return "images/battleship/LeftCap.png";
		case CELL_RIGHT_CAP:
			return "images/battleship/RightCap.png";
		case CELL_SUBMARINE:
			return "images/battleship/Submarine.png";
		case CELL_SEGMENT:
			return "images/battleship/UnknownSegment.png";
		case CELL_MIDDLE:
			return "images/battleship/Middle.png";
		}
		return "images/unknown.gif";
	}

	/*public BoardImage[] getAllBorderImages()
	{
		BoardImage[] s = new BoardImage[30];
		int count = 0;

		for (int x = 0; x < 20; ++x)
		{
			s[count++] = new BoardImage("images/treetent/" + (x)+ ".gif",40 + x);
		}

		for (int x = 0; x < 10; ++x)
		{
			s[count++] = new BoardImage("images/treetent/" + (char)('a' + (x)) + ".gif",30 + x);
		}

		return s;
	}*/

	public void drawLeftLabel(Graphics2D g, int val, int x, int y){
		drawText( g, x, y, String.valueOf( (char)( 'A'-30 + val ) ) );
	}

	public void drawRightLabel(Graphics2D g, int val, int x, int y){
		drawText(g,x, y, String.valueOf(val - 40));
	}

	public void drawTopLabel(Graphics2D g, int val, int x, int y){
		drawText(g,x, y, String.valueOf(x + 1));
	}

	public void drawBottomLabel(Graphics2D g, int val, int x, int y){
		drawText(g,x, y, String.valueOf(val - 40));
	}

	/*public int getNextCellValue(int x, int y, BoardState boardState)
			throws IndexOutOfBoundsException
	{
		int val = Math.abs(boardState.getCellContents(x,y));

		if ((val >= 0 && val < 2) || (val >= 10 && val < 15) || (val >= 20 && val < 26)) return val + 1;
		else if (val == 2) return 10;
		else if (val == 15) return 0;
		else if (val == 26) return 20;
		else return 0;
	}*/

	public Vector <PuzzleRule> getRules(){
		Vector<PuzzleRule> ruleList = new Vector<PuzzleRule>();
		ruleList.add(new WaterRowRule());
		ruleList.add(new RuleSurroundShip());
		ruleList.add(new RuleContinueShip());
		ruleList.add(new RuleSegmentType());
		return ruleList;
	}

	public Vector<Contradiction> getContradictions()
	{
		Vector<Contradiction> result = new Vector<Contradiction>();
		result.add(new ContradictionAdjacentShips());
		result.add(new ContradictionIncompleteShip());
		result.add(new ContradictionTooManyRowCol());
		result.add(new ContradictionTooFewRowCol());
		result.add(new ContradictionTooFewInFleet());
		return result;
	}

	public Vector<CaseRule> getCaseRules()
	{
		Vector<CaseRule> result = new Vector<CaseRule>();
		result.add(new CaseSegmentType());
		result.add(new CaseShipOrWater());
		result.add(new CaseShipLocations());
		/*result.add(new CaseRule()
		{
		    public String getImageName()
		    {
		    	return "images/questionmark.gif";
		    }
		    static final long serialVersionUID = 594123951L;
			public String checkCaseRuleRaw(BoardState state)
			{
				return null;
			}
		});*/
		return result;
	}

	public boolean checkValidBoardState(BoardState boardState){
	 /*int height = boardState.getHeight();
	int width = boardState.getWidth();
	BoardState identifiedShipSegments = identifyShipSegments(boardState);

	// Check if any two ships are adjacent
	for (int i=0;i<height;i++){
		for (int j=0;j<width;j++){
		try{
			if (identifiedShipSegments.getCellContents(i,j) == -1){
			return false;
			}
		} catch (Exception e){
			return false;
		}
		}
	}

	// Check if any row or column value is violated
	for (int i=0;i<height;i++){
		if (!checkRow(boardState, i)){
		return false;
		}
	}

	for (int i=0;i<width;i++){
		if (!checkCol(boardState, i)){
		return false;
		}
	}


	// Check if the number and sizes of ships has not been exceeded
	if (!countShips(identifiedShipSegments)){
		return false;
	}


*/
		return true;
	}
	
	/**
	 * Finds all possible locations for a specific sized ship aligned horizontally
	 * 
	 * @param state The BoardState to check for locations in.
	 * @param length The length of the ship.
	 * @return The set of positions that the ship could be in, where an offset <0,0> is the
	 * position of the left-most segment of the ship. 
	 */
	public static Set<Point> possibleHorizontalShipLocations(BoardState state, int length)
	{
        Map<Point,Set<Integer>> horizontalShipMask = new LinkedHashMap<Point,Set<Integer>>();
        // TODO Length 1 case
		for (int i = -1; i <= length; i++)
		{
			Set<Integer> middleHorizSet;
			if (i == -1)
				middleHorizSet = waterSet;
			else if (i == 0)
				middleHorizSet = leftSegmentSet;
			else if (i == length - 1)
				middleHorizSet = rightSegmentSet;
			else if (i == length)
				middleHorizSet = waterSet;
			else
				middleHorizSet = middleSegmentSet;
			horizontalShipMask.put(new Point(i, -1), waterSet);
			horizontalShipMask.put(new Point(i,  0), middleHorizSet);
			horizontalShipMask.put(new Point(i,  1), waterSet);
		}
		
		return PointSetAlgorithms.getPositionsForPointSet(state.getBoardCells(), horizontalShipMask);
	}
	
	/**
	 * Finds all possible locations for a specific sized ship aligned vertically
	 * 
	 * @param state The BoardState to check for locations in.
	 * @param length The length of the ship.
	 * @return The set of positions that the ship could be in, where an offset <0,0> is the
	 * position of the top-most segment of the ship. 
	 */
	public static Set<Point> possibleVerticalShipLocations(BoardState state, int length)
	{
        Map<Point,Set<Integer>> verticalShipMask = new LinkedHashMap<Point,Set<Integer>>();
        // TODO Length 1 case
		for (int i = -1; i <= length; i++)
		{
			Set<Integer> middleVertSet;
			if (i == -1)
				middleVertSet = waterSet;
			else if (i == 0)
				middleVertSet = topSegmentSet;
			else if (i == length - 1)
				middleVertSet = bottomSegmentSet;
			else if (i == length)
				middleVertSet = waterSet;
			else
				middleVertSet = middleSegmentSet;
			verticalShipMask.put(new Point(-1, i), waterSet);
			verticalShipMask.put(new Point( 0, i), middleVertSet);
			verticalShipMask.put(new Point( 1, i), waterSet);
		}
		
		return PointSetAlgorithms.getPositionsForPointSet(state.getBoardCells(), verticalShipMask);
	}
	
	/**
	 * Finds the current locations of all ships of a specific size aligned horizontally
	 * @param state The BoardState the check for ships in.
	 * @param length The length of the ships.
	 * @return The set of positions that ships of this length are in, where an offset <0,0> is the
	 * position of the left-most segment of a ship.
	 */
	public static Set<Point> horizontalShipLocations(BoardState state, int length)
	{
        Map<Point,Set<Integer>> horizontalShipMask = new LinkedHashMap<Point,Set<Integer>>();
        if (length > 1)
			for (int i = 0; i < length; i++)
			{
				Set<Integer> curSet;
				if (i == 0)
					curSet = new LinkedHashSet<Integer>(CELL_LEFT_CAP);
				else if (i == length - 1)
					curSet = new LinkedHashSet<Integer>(CELL_MIDDLE);
				else
					curSet = new LinkedHashSet<Integer>(CELL_RIGHT_CAP);
				horizontalShipMask.put(new Point( 0, i), curSet);
			}
        else
        	horizontalShipMask.put(new Point(0,0), new LinkedHashSet<Integer>(CELL_SUBMARINE));
		return PointSetAlgorithms.getPositionsForPointSet(state.getBoardCells(), horizontalShipMask);
	}
	
	/**
	 * Finds the current locations of all ships of a specific size aligned vertically
	 * @param state The BoardState the check for ships in.
	 * @param length The length of the ships.
	 * @return The set of positions that ships of this length are in, where an offset <0,0> is the
	 * position of the top-most segment of a ship.
	 */
	public static Set<Point> verticalShipLocations(BoardState state, int length)
	{
        Map<Point,Set<Integer>> verticalShipMask = new LinkedHashMap<Point,Set<Integer>>();
        if (length > 1)
			for (int i = 0; i < length; i++)
			{
				Set<Integer> curSet;
				if (i == 0)
					curSet = new LinkedHashSet<Integer>(CELL_TOP_CAP);
				else if (i == length - 1)
					curSet = new LinkedHashSet<Integer>(CELL_MIDDLE);
				else
					curSet = new LinkedHashSet<Integer>(CELL_BOTTOM_CAP);
				verticalShipMask.put(new Point( 0, i), curSet);
			}
        else
        	verticalShipMask.put(new Point(0,0), new LinkedHashSet<Integer>(CELL_SUBMARINE));
		return PointSetAlgorithms.getPositionsForPointSet(state.getBoardCells(), verticalShipMask);
	}
	
	/**
	 * 	Determines whether a specific cell value represents a ship segment
	 * 
	 * @param cellValue The value of the cell
	 * @return true if the cell is known to be a ship segment, false otherwise
	 */
	public static boolean isShip(int cellValue)
	{
		return (cellValue != CELL_UNKNOWN && cellValue != CELL_WATER);
	}
	
	/**
	 * 	Determines whether a specific cell value represents a ship segment
	 * 
	 * @param cellValue The value of the cell
	 * @return true if the cell is known to be water, false otherwise
	 */
	public static boolean isWater(int cellValue)
	{
		return (cellValue == CELL_WATER);
	}
	
	/**
	 * Checks if a ship segment is directly north of a specific tile
	 */
	public static boolean checkNorthForSegment(BoardState boardState, int column, int row)
	{
		if (row <= 0)
			return false;
		if (isShip(boardState.getCellContents(column, row-1)))
			return true;
		return false;
	}
	
	/**
	 * Checks if water is directly north of a specific tile
	 */
	public static boolean checkNorthForWater(BoardState boardState, int column, int row)
	{
		if (row <= 0)
			return true;
		if (isWater(boardState.getCellContents(column, row-1)))
			return true;
		return false;
	}
	
	/**
	 * Checks if a ship segment is directly south of a specific tile
	 */
	public static boolean checkSouthForSegment(BoardState boardState, int column, int row)
	{
		if (row >= boardState.getHeight() - 1)
			return false;
		if (isShip(boardState.getCellContents(column, row+1)))
			return true;
		return false;
	}
	
	/**
	 * Checks if water is directly south of a specific tile
	 */
	public static boolean checkSouthForWater(BoardState boardState, int column, int row)
	{
		if (row >= boardState.getHeight() - 1)
			return true;
		if (isWater(boardState.getCellContents(column, row+1)))
			return true;
		return false;
	}
	
	/**
	 * Checks if a ship segment is directly east of a specific tile
	 */
	public static boolean checkEastForSegment(BoardState boardState, int column, int row)
	{
		if (column >= boardState.getWidth() - 1)
			return false;
		if (isShip(boardState.getCellContents(column+1, row)))
			return true;
		return false;
	}
	
	/**
	 * Checks if water is directly east of a specific tile
	 */
	public static boolean checkEastForWater(BoardState boardState, int column, int row)
	{
		if (column >= boardState.getWidth() - 1)
			return true;
		if (isWater(boardState.getCellContents(column+1, row)))
			return true;
		return false;
	}
	
	/**
	 * Checks if a ship segment is directly west of a specific tile
	 */
	public static boolean checkWestForSegment(BoardState boardState, int column, int row)
	{
		if (column <= 0)
			return false;
		if (isShip(boardState.getCellContents(column-1, row)))
			return true;
		return false;
	}
	
	/**
	 * Checks if water is directly west of a specific tile
	 */
	public static boolean checkWestForWater(BoardState boardState, int column, int row)
	{
		if (column <= 0)
			return true;
		if (isWater(boardState.getCellContents(column-1, row)))
			return true;
		return false;
	}
	
	/**
	 * Checks if there are any segments directly diagonal (ne, nw, se, sw) to a specific tile
	 * 
	 * @param boardState The board
	 * @param column The column of the square to check around
	 * @param row The row of the square to check around
	 * @return true if a ship segment exists directly diagonal to the square, false otherwise 
	 */
	public static boolean checkDiagonalsForSegments(BoardState boardState, int column, int row)
	{
		for (int i = -1; i <= 1; i+=2)
		{
			for (int j = -1; j <= 1; j+=2)
			{
				int curCol = column + i;
				int curRow = row + j;
				if (curCol >= 0 && curCol < boardState.getWidth() &&
					curRow >= 0 && curRow < boardState.getHeight())
				{
					int cellValue = boardState.getCellContents(curCol, curRow);
					if (isShip(cellValue))
						return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks if there are any segments directly adjacent to a specific tile (n, s, e or w)
	 * 
	 * @param boardState The board
	 * @param column The column of the square to check around
	 * @param row The row of the square to check around
	 * @return true if a ship segment exists directly adjacent to the square, false otherwise 
	 */
	public static boolean checkAdjacentForSegments(BoardState boardState, int column, int row)
	{
		return (checkNorthForSegment(boardState, column, row) ||
				checkSouthForSegment(boardState, column, row) ||
				checkEastForSegment(boardState, column, row)  ||
				checkWestForSegment(boardState, column, row));
	}
	
	/**
	 * Counts the number of ship segments in a row
	 * 
	 * @param boardState The board
	 * @param row The row of the board to count
	 * @return The number of ships in that row 
	 */
	public static int countShipsInRow(BoardState boardState, int row)
	{
		int ships = 0;
		for (int i = 0; i < boardState.getHeight(); i++)
			if (isShip(boardState.getCellContents(i, row)))
				ships++;
		return ships;
	}
	
	/**
	 * Counts the number of ship segments in a column
	 * 
	 * @param boardState The board
	 * @param column The column of the board to count
	 * @return The number of ships in that column 
	 */
	public static int countShipsInColumn(BoardState boardState, int column)
	{
		int ships = 0;
		for (int i = 0; i < boardState.getWidth(); i++)
			if (isShip(boardState.getCellContents(column, i)))
				ships++;
		return ships;
	}

	/**
	 * Returns the number of segments in a row in the completed board
	 * 
	 * @param boardState The board
	 * @param row The row of the board
	 * @return The total number of segments in that row 
	 */
	public static int totalSegmentsInRow(BoardState boardState, int row)
	{
		return boardState.getLabel(BoardState.LABEL_RIGHT, row);
	}
	
	/**
	 * Returns the number of segments in a column in the completed board
	 * 
	 * @param boardState The board
	 * @param column The column of the board
	 * @return The total number of segments in that column 
	 */
	public static int totalSegmentsInColumn(BoardState boardState, int column)
	{
		return boardState.getLabel(BoardState.LABEL_BOTTOM, column);
	}

	// As recommended, these are scratched
	/**
	public static int translateNumShipSegments(int cellValue){
	return (cellValue - 10);
	}


	private boolean checkRow(BoardState boardState, int rowNum){
	int width = boardState.getWidth();

	int numShipSegments = 0;
	try{
		numShipSegments = BattleShip.translateNumShipSegments(boardState.getLabel(BoardState.LABEL_RIGHT, rowNum));
	} catch (Exception e){
	}

	for (int i=0;i<width;i++){
		try{
		if (boardState.getCellContents(rowNum,i) == 2 ||
			boardState.getCellContents(rowNum,i) == 3){
			numShipSegments--;
		}
		} catch (Exception e){
		}
	}

	if (numShipSegments < 0){
		return false;
	}
	else{
		return true;
	}
	}


	private boolean checkCol(BoardState boardState, int colNum){
	int height = boardState.getHeight();

	int numShipSegments = 0;
	try{
		numShipSegments = BattleShip.translateNumShipSegments(boardState.getLabel(BoardState.LABEL_BOTTOM, colNum));
	} catch (Exception e){
	}

	for (int i=0;i<height;i++){
		try{
		if (boardState.getCellContents(i,colNum) == 2 ||
			boardState.getCellContents(i,colNum) == 3){
			numShipSegments--;
		}
		} catch (Exception e){
		}
	}

	if (numShipSegments < 0){
		return false;
	}
	else{
		return true;
	}
	}

	private static BoardState identifyShipSegments(BoardState boardState){
	int width = boardState.getWidth();
	int height = boardState.getHeight();
	int shipNumber = 2;


	// Copy the board state
	BoardState identifiedShipSegments = boardState.copy();

	// Binarize it
	for (int i=0;i<height;i++){
		for (int j=0;j<width;j++){
		try{
			if (identifiedShipSegments.getCellContents(i,j) == 2 ||
			identifiedShipSegments.getCellContents(i,j) == 3){
			identifiedShipSegments.setCellContents(i,j,1);
			} else{
			identifiedShipSegments.setCellContents(i,j,0);
			}
		} catch (Exception e){
		}
		}
	}


	// Find the segments
	for (int i=0;i<height;i++){
		for (int j=0;j<width;j++){
		try{
			if (identifiedShipSegments.getCellContents(i,j) == 1){
			labelShip(identifiedShipSegments,i,j,shipNumber);
			shipNumber++;
			}
		} catch (Exception e){
		}
		}
	}


	// Return the identified board state
	return identifiedShipSegments;
	}


	private static void labelShip(BoardState identifiedBoardState,int row, int col,int shipNumber){
	try{
		identifiedBoardState.setCellContents(row,col,shipNumber);
	} catch (Exception e){
	}

	try{
		if (identifiedBoardState.getCellContents(row-1, col) == 1){
		labelShip(identifiedBoardState,row-1,col,shipNumber);
		} else if (identifiedBoardState.getCellContents(row-1, col) != shipNumber &&
			   identifiedBoardState.getCellContents(row-1, col) != 0){
		identifiedBoardState.setCellContents(row-1,col,-1);
		}
	} catch (Exception e){
	}

	try{
		if (identifiedBoardState.getCellContents(row+1, col) == 1){
		labelShip(identifiedBoardState,row+1,col,shipNumber);
		} else if (identifiedBoardState.getCellContents(row+1, col) != shipNumber &&
			   identifiedBoardState.getCellContents(row+1, col) != 0){
		identifiedBoardState.setCellContents(row+1,col,-1);
		}
	} catch (Exception e){
	}


	try{
		if (identifiedBoardState.getCellContents(row, col-1) == 1){
		labelShip(identifiedBoardState,row,col-1,shipNumber);
		} else if (identifiedBoardState.getCellContents(row, col-1) != shipNumber &&
			   identifiedBoardState.getCellContents(row, col-1) != 0){
		identifiedBoardState.setCellContents(row,col-1,-1);
		}
	} catch (Exception e){
	}


	try{
		if (identifiedBoardState.getCellContents(row, col+1) == 1){
		labelShip(identifiedBoardState,row,col+1,shipNumber);
		} else if (identifiedBoardState.getCellContents(row, col+1) != shipNumber &&
			   identifiedBoardState.getCellContents(row, col+1) != 0){
		identifiedBoardState.setCellContents(row,col+1,-1);
		}
	} catch (Exception e){
	}


	try{
		if (identifiedBoardState.getCellContents(row-1, col-1) != 0){
		identifiedBoardState.setCellContents(row-1, col-1, -1);
		}
	} catch (Exception e){
	}

	try{
		if (identifiedBoardState.getCellContents(row+1, col-1) != 0){
		identifiedBoardState.setCellContents(row+1, col-1, -1);
		}
	} catch (Exception e){
	}

	try{
		if (identifiedBoardState.getCellContents(row-1, col+1) != 0){
		identifiedBoardState.setCellContents(row-1, col+1, -1);
		}
	} catch (Exception e){
	}

	try{
		if (identifiedBoardState.getCellContents(row+1, col+1) != 0){
		identifiedBoardState.setCellContents(row+1, col+1, -1);
		}
	} catch (Exception e){
	}
	}


	public static boolean countShips(BoardState identifiedBoardState){
	int num_size4 = BattleShip.NUM_SHIPS_SIZE4;
	int num_size3 = BattleShip.NUM_SHIPS_SIZE3;
	int num_size2 = BattleShip.NUM_SHIPS_SIZE2;
	int num_size1 = BattleShip.NUM_SHIPS_SIZE1;
	int shipNumber = 2;
	int size = -1;
	int width = identifiedBoardState.getWidth();
	int height = identifiedBoardState.getHeight();


	while(size != 0){
		size = 0;
		for (int i=0;i<height;i++){
		for (int j=0;j<width;j++){
			try{
			if (identifiedBoardState.getCellContents(i,j) == shipNumber){
				size++;
			}
			} catch (Exception e){
			}
		}
		}
		if (size == 4){
		num_size4--;
		}
		else if (size == 3){
		num_size3--;
		}
		else if (size == 2){
		num_size2--;
		}
		else if (size == 1){
		num_size1--;
		}
		else if (size != 0){
		return false;
		}
		shipNumber++;
	}

	if (num_size4 < 0 || num_size3 < 0 || num_size2 < 0 || num_size1 <0){
		return false;
	} else {
		return true;
	}
	}
   */
}
