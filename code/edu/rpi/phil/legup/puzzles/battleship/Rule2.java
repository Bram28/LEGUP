//
//  Rule2.java
//  LEGUP
//
//  Created by Drew Housten on Tues May 3 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//
//  If there are a number of unknown cells in any row or column that equals
//  the number of shipsegments left to place, then they can be declared a shipsegment


package edu.rpi.phil.legup.puzzles.battleship;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class Rule2 extends PuzzleRule{

    public Rule2()
    {
    	setName("Battleship Rule2");
    	description = "Unknown cells equal number of Ship Segments";
    }
    

   /* private boolean checkRow(BoardState boardState, int rowNum){
	int width = boardState.getWidth();
	int numShipSegments = 0;
	int numUnknown = 0;
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
		else if (boardState.getCellContents(rowNum,i) == 0){
		    numUnknown++;
		}
	    } catch (Exception e){
	    }
	}
	
	if (numShipSegments != numUnknown){
	    return false;
	}
	else{
	    return true;
	}
    }



    private boolean checkCol(BoardState boardState, int colNum){
	int height = boardState.getHeight();
	int numShipSegments = 0;
	int numUnknown = 0;
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
		else if (boardState.getCellContents(i, colNum) == 0){
		    numUnknown++;
		}
	    } catch (Exception e){
	    }
	}
	
	if (numShipSegments != numUnknown){
	    return false;
	}
	else{
	    return true;
	}

    }*/


    
    protected String checkRuleRaw(BoardState state){
	
    	return null;
    	
    	/*
	// Check for only one branch
	if (origBoardState.getAppliedRules().size() != 1){
	    System.out.println("Too many branches!");
	    return false;
	}


	// Get the cells that transitioned from the origBoardState
	Vector transitionsFrom = origBoardState.getTransitionsFrom();


	// Check if each cell is a ship segment cell
	for (int i=0;i<transitionsFrom.size();i++){
	    if (((TransitionCell)transitionsFrom.get(i)).getValue() != 2 &&
			((TransitionCell)transitionsFrom.get(i)).getValue() != 3){
		System.out.println("Not all the transition cells are ship segments");
		return false;
	    }
	}

	
	// For each cell, check the row or column to determine if the number of
	// unknown cells matches the number of ship segments left to place
	for (int i=0;i<transitionsFrom.size();i++){
	    int row = ((TransitionCell)transitionsFrom.get(i)).getX();
	    int col = ((TransitionCell)transitionsFrom.get(i)).getY();
	    
	    if (!checkRow(origBoardState, row) && !checkCol(origBoardState, col)){
		System.out.println("The row ["+row+"] and column ["+col+"] have too many or too few unknown cells");
		return false;
	    }
	}	


	// return true
	System.out.println("Rule is valid");
	return true;*/
    }
}