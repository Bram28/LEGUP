//
//  Rule3.java
//  LEGUP
//
//  Created by Drew Housten on Tues May 3 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//
//  If there is a completed ship, any adjacent cells can be declared water

package edu.rpi.phil.legup.puzzles.battleship;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;

public class Rule3 extends PuzzleRule{

    public Rule3()
    {
    	setName("BattleShip Rule3");
        description = "Cells surrounding adjacent completed ships are water";
    }
	

	public boolean completedShip(BoardState boardState, int row, int col){
		// Check if the cell is a ship segment
		try{
			if (boardState.getCellContents(row,col) != 2 &&
				boardState.getCellContents(row,col) != 3){
				return false;
			}
		} catch (Exception e){
			return false;
		}
		
		// Check if the ship is a size-1 ship
		boolean size1Flag = true;
		try{
			if (boardState.getCellContents(row,col) == 3){
				try{
					if (boardState.getCellContents(row-1,col-1) == 2 ||
						boardState.getCellContents(row-1,col-1) == 3){
						size1Flag = false;
					}
				} catch (Exception e){
				}
				try{
					if (boardState.getCellContents(row-1,col+1) == 2 ||
						boardState.getCellContents(row-1,col+1) == 3){
						size1Flag = false;
					}
				} catch (Exception e){
				}
				try{
					if (boardState.getCellContents(row+1,col-1) == 2 ||
						boardState.getCellContents(row+1,col-1) == 3){
						size1Flag = false;
					}
				} catch (Exception e){
				}
				try{
					if (boardState.getCellContents(row+1,col+1) == 2 ||
						boardState.getCellContents(row+1,col+1) == 3){
						size1Flag = false;
					}
				} catch (Exception e){
				}
				if (size1Flag == true){
					return true;
				}
			}
		} catch (Exception e){
		}
		
		// Count the number of end-segments
		if (countEndSegments(boardState,row,col) == 2){
			return true;
		} else{
			return false;
		}
	}
	
	public int countEndSegments(BoardState boardState, int row, int col){
		try{
			if (boardState.getCellContents(row,col) != 2 &&
				boardState.getCellContents(row,col) != 3){
				return 0;
			}
		} catch (Exception e){
			return 0;
		}
		
		int count = 0;
		try{
			if (boardState.getCellContents(row,col) == 3){
				count = 1;
			}
		} catch (Exception e){
			count = 0;
		}
	
		return (count + countEndSegments(boardState, row-1, col-1) +
					   countEndSegments(boardState, row-1, col+1) +
					   countEndSegments(boardState, row+1, col-1) +
					   countEndSegments(boardState, row+1, col+1));
	}
	
	
	

	
	public boolean checkAdjacentShip(BoardState boardState, int row, int col){
		
		// Check each adjacent cell
		if (completedShip(boardState, row-1,col-1) ||
			completedShip(boardState, row-1,col) ||
			completedShip(boardState, row-1,col+1) ||
			completedShip(boardState, row, col-1) ||
			completedShip(boardState, row, col+1) ||
			completedShip(boardState, row+1, col-1) ||
			completedShip(boardState, row+1, col) ||
			completedShip(boardState, row+1, col+1)){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	protected String checkRuleRaw(BoardState origBoardState){
		return null;
		// Check for only one branch
		/*if (origBoardState.getAppliedRules().size() != 1){
			System.out.println("Too many branches!");
			return false;
		}


		// Get the cells that transitioned from the origBoardState
		Vector transitionsFrom = origBoardState.getTransitionsFrom();


		// Check if each cell is a water cell
		for (int i=0;i<transitionsFrom.size();i++){
			if (((TransitionCell)transitionsFrom.get(i)).getValue() != 1){
				System.out.println("Not all the transition cells are water");
				return false;
			}
		}

	
		// For each cell, check if there is an adjacent completed ship
		for (int i=0;i<transitionsFrom.size();i++){
			int row = ((TransitionCell)transitionsFrom.get(i)).getX();
			int col = ((TransitionCell)transitionsFrom.get(i)).getY();
	    
			if (!checkAdjacentShip(origBoardState, row, col)){
				System.out.println("The row ["+row+"] and column ["+col+"] doesn't have an adjacent completed ship");
				return false;
			}
		}	


		// return true
		System.out.println("Rule is valid");
			return true;*/
		}
	}
    