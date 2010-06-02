//
//  TreeTent.java
//  LEGUP
//
//  Created by Drew Housten on Wed Feb 16 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//

package edu.rpi.phil.legup.puzzles.nurikabe;
//import java.awt.Color;
import java.util.Vector;

import edu.rpi.phil.legup.BoardImage;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;


public class Nurikabe extends PuzzleModule
{	
	public static int CELL_BLACK = -1;
	public static int CELL_WHITE = -2;
	
	public Nurikabe(){
	}
	
	
	public String getImageLocation(int cellValue)
	{
		if (cellValue == CELL_UNKNOWN)
		{
			return "images/nurikabe/unknown.gif";
		} 
		else if (cellValue == CELL_BLACK)
		{
			return "images/nurikabe/black.gif";
		} 
		else if (cellValue == CELL_WHITE)
		{
		    return "images/nurikabe/white.gif";
		} 
		else if (cellValue > 0 && cellValue <= 33)
		{
		    return "images/nurikabe/" + (cellValue)+".gif"; 
		}
		else
		{
		    return "images/nurikabe/unknown.gif";
		}	
	}
	
	public void initBoard(BoardState state)
	{
;
	}
    
    /**
     * Get all the images (as strings to the image path) used by this puzzle in the center part
     * @return an array of strings to image paths
     */
    public BoardImage[] getAllCenterImages()
    {
    	BoardImage[] s = 
    	{
    			new BoardImage("images/nurikabe/white.gif",-2),
    			new BoardImage("images/nurikabe/black.gif",-1),
    			new BoardImage("images/nurikabe/unknown.gif",0),
    			new BoardImage("images/nurikabe/1.gif",1),
    			new BoardImage("images/nurikabe/2.gif",2),
    			new BoardImage("images/nurikabe/3.gif",3),
    			new BoardImage("images/nurikabe/4.gif",4),
    			new BoardImage("images/nurikabe/5.gif",5),
    			new BoardImage("images/nurikabe/6.gif",6),
    			new BoardImage("images/nurikabe/7.gif",7),
    			new BoardImage("images/nurikabe/8.gif",8),
    			new BoardImage("images/nurikabe/9.gif",9),
    			new BoardImage("images/nurikabe/10.gif",10),
    			new BoardImage("images/nurikabe/11.gif",11),
    			new BoardImage("images/nurikabe/12.gif",12),
    			new BoardImage("images/nurikabe/13.gif",13),
    			new BoardImage("images/nurikabe/14.gif",14),
    			new BoardImage("images/nurikabe/15.gif",15),
    			new BoardImage("images/nurikabe/16.gif",16),
    			new BoardImage("images/nurikabe/17.gif",17),
    			new BoardImage("images/nurikabe/18.gif",18),
    			new BoardImage("images/nurikabe/19.gif",19),
    			new BoardImage("images/nurikabe/20.gif",20),
    			new BoardImage("images/nurikabe/21.gif",21),
    			new BoardImage("images/nurikabe/22.gif",22),
    			new BoardImage("images/nurikabe/23.gif",23),
    			new BoardImage("images/nurikabe/24.gif",24),
    			new BoardImage("images/nurikabe/25.gif",25),
    			new BoardImage("images/nurikabe/26.gif",26),
    			new BoardImage("images/nurikabe/27.gif",27),
    			new BoardImage("images/nurikabe/28.gif",28),
    			new BoardImage("images/nurikabe/29.gif",29),
    			new BoardImage("images/nurikabe/30.gif",30),
    			new BoardImage("images/nurikabe/31.gif",31),
    			new BoardImage("images/nurikabe/32.gif",32),
    			new BoardImage("images/nurikabe/33.gif",33)
    	};
    	
    	return s;
    }
    
    /**
     * Get all the images (as strings to the image path) used by this puzzle in the border part
     * @return an array of strings to image paths
     */
    public BoardImage[] getAllBorderImages()
    {
    	BoardImage[] s = new BoardImage[0];
    	
    	return s;
    }
	
	/**
     * Get the next label value if we're at this one (like the numbers around the border)
     * This is used when we're creating puzzles
     * 
     * @param curValue the current value of the label
     * @return the next value of the label
     */
    public int getNextLabelValue(int curValue)
    {
    	return 0;
    }
	
	public int getAbsoluteNextCellValue(int x, int y, BoardState boardState)
    {
    	int contents = boardState.getCellContents(x,y);
    	int rv = CELL_UNKNOWN;
    	
		rv = contents + 1;
		if(rv > 0)
			rv = -2;

		return rv;
    }
	
    public int getNextCellValue(int x, int y, BoardState boardState)
    {
    	int contents = boardState.getCellContents(x,y);
    	
		if (contents == CELL_UNKNOWN)
		{
			return CELL_BLACK;
		}
		else if (contents == CELL_BLACK)
		{
			return CELL_WHITE;
		}
		else if (contents == CELL_WHITE)
		{
			return CELL_UNKNOWN;
		}
		else
		{
			return contents;
		}
    }
	
	public boolean checkGoal(BoardState currentBoard, BoardState goalBoard){
		return currentBoard.compareBoard(goalBoard);
	}
	
	public Vector <PuzzleRule> getRules(){
		Vector <PuzzleRule>ruleList = new Vector <PuzzleRule>();
		//ruleList.add(new PuzzleRule());
		ruleList.add(new RuleNoBlackSquare());
		ruleList.add(new RuleSurroundRegion());
		ruleList.add(new RuleBetweenRegions());
		ruleList.add(new RuleOneUnknownRegion());
		ruleList.add(new RuleOneUnknownBlack());
		ruleList.add(new RuleOneUnknownWhite());
		
		return ruleList;
	}
	
	 /**
     * Gets a list of Contradictions associated with this puzzle
     *
     * @return A Vector of Contradictions
     */   
    public Vector <Contradiction> getContradictions()
    {
		Vector <Contradiction>contradictionList = new Vector <Contradiction>();
		contradictionList.add(new ContradictionBlackArea());
		contradictionList.add(new ContradictionBlackSquare());
		contradictionList.add(new ContradictionNoNumber());
		contradictionList.add(new ContradictionMultipleNumbers());
		contradictionList.add(new ContradictionTooManySpaces());
		contradictionList.add(new ContradictionTooFewSpaces());
		
		return contradictionList;
    }
    
    public Vector <CaseRule> getCaseRules()
    {
    	Vector <CaseRule> caseRules = new Vector <CaseRule>();
    	caseRules.add(new CaseBlackOrWhite());
    	
    	return caseRules;
    }


    public boolean checkValidBoardState(BoardState boardState){


	return true;

    }
    
}
