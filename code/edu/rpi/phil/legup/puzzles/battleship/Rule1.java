//
//  Rule1.java
//  LEGUP
//
//  Created by Drew Housten on Tues May 3 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//
//  If the number of ship segments in a row or column have been declared,
//  any other cell in that row or column can be declared water


package edu.rpi.phil.legup.puzzles.battleship;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class Rule1 extends PuzzleRule
{
	static final long serialVersionUID = 133809047L;
	
    public Rule1()
    {
    	setName("BattleShip Rule1");
    	description = "Cells in a row or column that has enough ship segments";
    }
	
	
	/*private boolean checkRow(BoardState boardState, int rowNum){
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
	
		if (numShipSegments != 0){
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
	
	if (numShipSegments != 0){
	    return false;
	}
	else{
	    return true;
	}

    }*/
	
	
	
	public String getImageName()
	{
		return "images/questionmark.gif";
	}

	protected String checkRuleRaw(BoardState state){
	return null;
		// Check for only one branch
	/*if (origBoardState.getAppliedRules().size() != 1){
	    System.out.println("Too many branches!");
	    return false;
	}

	// Get the cells that transitioned from the origBoardState
	Vector children = origBoardState.getChildren();


	// Check if each cell is a water cell
	for (int i=0;i<children.size();i++){
	    if (((TransitionCell)children.get(i)).getValue() != 3){
		System.out.println("Not all the transition cells are water");
		return false;
	    }
	}
	
	// For each cell, check if the row or column has a sufficient number of ship segments in it
	for (int i=0;i<children.size();i++){
	    int row = ((TransitionCell)children.get(i)).getX();
	    int col = ((TransitionCell)children.get(i)).getY();
	    
	    if (!checkRow(destBoardState, row) && !checkCol(destBoardState, col)){
		System.out.println("The row ["+row+"] and column ["+col+"] have too many or too few ship segments");
		return false;
	    }
	}
	
	// return true
	System.out.println("Rule is valid");
	return true;*/
    }
}
	
