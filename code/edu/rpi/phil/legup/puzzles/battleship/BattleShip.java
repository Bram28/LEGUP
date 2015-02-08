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
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;

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

	public BattleShip()
	{

	}

	 public void mousePressedEvent(BoardState state, Point where)
	 {
	 	super.mousePressedEvent(state, where);
	 }

	 /*public void labelPressedEvent(BoardState state, int index, int side)
	 {
	 	ArrayList<Point> points = new ArrayList<Point>();
	 	BoardState state2 = state.getSingleParentState();
	 	if (state2 == null)
	 	{
	 		state.addTransitionFrom();
	 		state2 = state.getOriginalState();
	 	}
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

		int numShips = 0, numWater = 0, numUnknown = 0;

		for (Point p : points)
		{
			int val = state2.getCellContents(p.x, p.y);
			if (isShip(val)) numShips++;
			else if (val == CELL_WATER) numWater++;
			else numUnknown++;
		}

		if (numShips == state2.getLabel(side, index)-40)
		{
			for (Point p : points) if (Math.abs(state2.getCellContents(p.x, p.y)) == CELL_UNKNOWN)
				state.setCellContents(p.x, p.y, CELL_WATER);
		}
		else if (numShips+numUnknown == state2.getLabel(side, index)-40)
		{
			for (Point p : points) if (Math.abs(state2.getCellContents(p.x, p.y)) == CELL_UNKNOWN)
				state.setCellContents(p.x, p.y, CELL_MIDDLE);
		}
	 }*/

	public String getImageLocation(int cellValue)
	{
		return "images/battleship/image["+cellValue+"].gif";
	}

	public BoardImage[] getAllBorderImages()
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
	}

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
		return ruleList;
	}

	public Vector<Contradiction> getContradictions()
	{
		Vector<Contradiction> result = new Vector<Contradiction>();
		result.add(new ContradictionAdjacentShips());
		return result;
	}

	public Vector<CaseRule> getCaseRules()
	{
		Vector<CaseRule> result = new Vector<CaseRule>();
		result.add(new CaseRule()
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
		});
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
	 * Checks if a ship segment is directly north of a specific tile
	 */
	public static boolean checkNorthForSegment(BoardState boardState, int column, int row)
	{
		if (row <= 0)
			return true;
		if (isShip(boardState.getCellContents(column, row-1)))
			return true;
		return false;
	}
	
	/**
	 * Checks if a ship segment is directly south of a specific tile
	 */
	public static boolean checkSouthForSegment(BoardState boardState, int column, int row)
	{
		if (row >= boardState.getHeight() - 1)
			return true;
		if (isShip(boardState.getCellContents(column, row+1)))
			return true;
		return false;
	}
	
	/**
	 * Checks if a ship segment is directly east of a specific tile
	 */
	public static boolean checkEastForSegment(BoardState boardState, int column, int row)
	{
		if (column >= boardState.getWidth() - 1)
			return true;
		if (isShip(boardState.getCellContents(column+1, row)))
			return true;
		return false;
	}
	
	/**
	 * Checks if a ship segment is directly west of a specific tile
	 */
	public static boolean checkWestForSegment(BoardState boardState, int column, int row)
	{
		if (column <= 0)
			return true;
		if (isShip(boardState.getCellContents(column-1, row)))
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
