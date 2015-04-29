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
import java.util.Map;
import java.util.Vector;

public class BattleShip extends PuzzleModule
{
	static final long serialVersionUID = 532393951L;

	public static int NUM_SHIPS_SIZE4 = 1;
	public static int NUM_SHIPS_SIZE3 = 2;
	public static int NUM_SHIPS_SIZE2 = 3;
	public static int NUM_SHIPS_SIZE1 = 4;

	public static final int CELL_UNKNOWN = 0;
	public static final int CELL_WATER = 1;
	public static final int CELL_SEGMENT = 2;
	public static final int CELL_LEFT_CAP = 10;
	public static final int CELL_TOP_CAP = 11;
	public static final int CELL_BOTTOM_CAP = 12;
	public static final int CELL_RIGHT_CAP = 13;
	public static final int CELL_SUBMARINE = 14;
	public static final int CELL_MIDDLE = 15;
	
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
        tmp.put("center", CELL_SUBMARINE);
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
		    	return "images/unknown.gif";
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
