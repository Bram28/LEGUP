//
//  BattleShip.java
//  LEGUP
//
//  Created by Drew Housten on Wed April 27 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//


package edu.rpi.phil.legup.puzzles.battleship;
import edu.rpi.phil.legup.*;

import java.awt.Point;
import java.awt.Image;

import javax.swing.ImageIcon;

import java.awt.Graphics2D;
import java.util.ArrayList;
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
	public static final int CELL_SHIP = 2;
	public static final int CELL_FIXED_UNKNOWN = 26;
	public static final int CELL_LEFT_CAP = 10;
	public static final int CELL_TOP_CAP = 11;
	public static final int CELL_BOTTOM_CAP = 12;
	public static final int CELL_RIGHT_CAP = 13;
	public static final int CELL_CENTER = 14;
	public static final int CELL_MIDDLE = 15;
	public static final int FIXED_LEFT_CAP = 20;
	public static final int FIXED_TOP_CAP = 21;
	public static final int FIXED_BOTTOM_CAP = 22;
	public static final int FIXED_RIGHT_CAP = 23;
	public static final int FIXED_CENTER = 24;
	public static final int FIXED_MIDDLE = 25;
	

	public BattleShip()
	{

	}

	 public void mousePressedEvent(BoardState state, Point where)
	 {
	 	super.mousePressedEvent(state, where);
		if (!state.isModifiableCell(where.x, where.y) && Math.abs(state.getCellContents(where.x, where.y)) == CELL_FIXED_UNKNOWN)
	 	{
			state.setModifiableCell(where.x, where.y, true);
			state.setCellContents(where.x, where.y, FIXED_LEFT_CAP);
		}
	 }

	 public void labelPressedEvent(BoardState state, int index, int side)
	 {
	 	ArrayList<Point> points = new ArrayList<Point>();
	 	BoardState state2 = state.getSingleParentState();
		if (side == Math.abs(BoardState.LABEL_LEFT) || side == Math.abs(BoardState.LABEL_RIGHT))
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
			int val = Math.abs(state2.getCellContents(p.x, p.y));
			if (isShipPart(val)) numShips++;
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
				state.setCellContents(p.x, p.y, CELL_SHIP);
		}
	 }

	 public boolean isShipPart(int value)
	 {
	 	return (value != CELL_WATER && value != CELL_UNKNOWN);
	 }
	 public boolean isConcreteShipPart(int value)
	 {
	 	return (isShipPart(value) && value != CELL_SHIP && value != CELL_FIXED_UNKNOWN);
	 }

	public String getImageLocation(int cellValue)
	{
		if (cellValue <= 15)
			return "images/battleship/image["+cellValue+"].gif";
		else if (cellValue >= 20 && cellValue <= 24)
			return "images/battleship/image["+(cellValue-10)+"].gif";
		else if (cellValue == 25)
			return "images/battleship/image[0].gif";
		else if (cellValue >= 30 && cellValue <= 39)
			return "images/treetent/" + (char)('a' + (cellValue-30)) + ".gif";
		else if (cellValue >= 40 && cellValue < 60)
			return "images/treetent/" + (cellValue - 40) + ".gif";
		else return "images/battleship/image[0].gif";
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

	public int getNextCellValue(int x, int y, BoardState boardState)
			throws IndexOutOfBoundsException
	{
		int val = Math.abs(boardState.getCellContents(x,y));

		if ((val >= 0 && val < 2) || (val >= 10 && val < 15) || (val >= 20 && val < 26)) return val + 1;
		else if (val == 2) return 10;
		else if (val == 15) return 0;
		else if (val == 26) return 20;
		else return 0;
	}

	public Vector <PuzzleRule> getRules(){
		Vector<PuzzleRule> ruleList = new Vector<PuzzleRule>();
		ruleList.add(new WaterRowRule());
		return ruleList;
	}

	public Vector<Contradiction> getContradictions()
	{
		Vector<Contradiction> result = new Vector<Contradiction>();
		result.add(new Contradiction()
		{
		    public String getImageName()
		    {
		    	return "images/unknown.gif";
		    }
		    static final long serialVersionUID = 532394123951L;
			public String checkContradictionRaw(BoardState state)
			{
				return null;
			}
		});
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
