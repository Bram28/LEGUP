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
import java.awt.Point;

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
		ruleList.add(new RuleCornerBlack());
		ruleList.add(new RuleOneUnknownRegion());//Same as one unknown white make it one rule.
		ruleList.add(new RuleOneUnknownBlack());
		ruleList.add(new RuleOneUnknownWhite());
		ruleList.add(new RuleBetweenRegions());
		ruleList.add(new RuleUnknownSurrounded());
		ruleList.add(new RuleBottleNeck());
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
	/* AI stuff */
	public BoardState guess(BoardState Board) {
		// out of forced moves, need to guess
		Point guess = GenerateBestGuess(Board);
		// guess, if we found one
		if (guess.x != -1 && guess.y != -1) {
			BoardState Parent = Board.getSingleParentState();
			BoardState CaseBlack = Board;
			BoardState CaseWhite = Parent.addTransitionFrom();
			CaseBlack.setCellContents(guess.x, guess.y, CELL_BLACK);
			CaseWhite.setCellContents(guess.x, guess.y, CELL_WHITE);
			Parent.setCaseSplitJustification(new CaseBlackOrWhite());
			System.out.println("Guessed at "+guess.x+","+guess.y);
			//Legup.setSelection(CaseTent,false);
			return CaseBlack;
		}
		// if we didn't then the board is full, and we are finished (thus, the returned board will be the same as the one we were given
		System.out.println("Statement: Your puzzle has been solved already. Why do you persist?");
		return Board;
	}
   



	private Point GenerateBestGuess(BoardState Board) {
		// this should more properly be some kind of ranking system whereby different
		// conditions scored points and the highest scoring square was chosen.
		// until there is more time to actually watch the AI, it scores based on closeness
		// to a probability. In the future, it might include points for having only one extra
		// free space or something like that.
		int currentX=-1;
		int currentY=-1;
		int height = Board.getHeight();
		int width = Board.getWidth();
		double currentOff = Double.POSITIVE_INFINITY;
		double BESTPROB = .25;
		
		//Create a counter that will hold the number of white regions found
		int regioncount = 0;
		//Holds whether or not a cell has been visited
		//1 represents visited and white
		//0 not visited
		//- visited but not white
		boolean[][] visited = new boolean[width][height];
		//Booleans which hold whether or not a cell is valid for the rule
		boolean[][]white = new boolean[width][height];
		
		Point temp;
		//For each cell
		for(int x = 0; x < width; ++x)
		{
			for(int y = 0; y < height; ++y)
			{
				//If the cell is white and we haven't visited it we need to loop through it and check it out
				if((Board.getCellContents(x,y)>0 || Board.getCellContents(x,y) == Nurikabe.CELL_WHITE )&& visited[y][x] == false)
				{
					//Since we have found a white region previously unvisited, add one to the region count
					++regioncount;
					//This loops through and returns the number of unknowns surrounding the region
					//If the loop finds a number then it returns -1 signifying that we can't apply the rule on this region
					//Since visited is by reference it should get updated
					temp = loopConnected(visited, Board,x,y,width,height);
					//If there is only 1 unknown around the region than it must be white
					if(temp.x<1 || temp.y<1)
						continue;
					double myProb = temp.x/temp.y;
					//System.out.println("Square "+r+","+c+" prob: "+myProb);
					double myOff = Math.abs(BESTPROB-myProb);
					if (myOff < currentOff) 
					{
						setWhite(white, new boolean[width][height] ,Board,x,y,width,height);
						for(int r =0;r<width;r++)
						{
							for(int c = 0; c<height;c++)
							{
								if(white[r][c])
								{
									temp.x=c;
									temp.y=r;
								}
							}
						}
						System.out.println("Got new guess square: "+temp.x+","+temp.y+", off ="+myOff);
						currentX = temp.x;
						currentY = temp.y;
						currentOff = myOff;
					}
				}
			}
		}
		return new Point(currentX,currentY);
	}
	private Point loopConnected(boolean[][] neighbors,BoardState boardState, int x, int y, int width, int height)
	{
		//x == how many desired whites - actual
		//y == how many surrounding unknowns
		Point ret = new Point(0,0);
		if(neighbors[y][x] == true)
			return ret;
		neighbors[y][x] = true;
		if(boardState.getCellContents(x,y) == Nurikabe.CELL_BLACK)
			return ret;
		if(boardState.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN)
		{
			++ret.y;
			return ret;
		}
		
		--ret.x;
		if(boardState.getCellContents(x,y) > 0)
			ret.x += boardState.getCellContents(x,y);
		
		Point temp;
		if(x+1 < width)
		{
			temp = loopConnected(neighbors, boardState, x+1, y, width, height);
			ret.x += temp.x;
			ret.y += temp.y;
		}
		if(x-1 >= 0)
		{
			temp = loopConnected(neighbors, boardState, x-1, y, width, height);
			ret.x += temp.x;
			ret.y += temp.y;
		}
		if(y+1 < height)
		{
			temp = loopConnected(neighbors, boardState, x, y+1, width, height);
			ret.x += temp.x;
			ret.y += temp.y;
		}
		if(y-1 >= 0)
		{
			temp = loopConnected(neighbors, boardState, x, y-1, width, height);
			ret.x += temp.x;
			ret.y += temp.y;
		}
		return ret;
	}
	private boolean[][] setWhite(boolean[][] white, boolean[][] neighbors ,BoardState boardState, int x, int y, int width, int height)
	{
		if(neighbors[y][x] == true)
			return white;
		neighbors[y][x] = true;
		
		if(boardState.getCellContents(x,y) == Nurikabe.CELL_UNKNOWN)
		{
			white[y][x] = true;
			return white;
		}
		if(boardState.getCellContents(x,y) == Nurikabe.CELL_BLACK)
			return white;

		
		if(x+1 < width)
		{
			white = setWhite(white, neighbors, boardState, x+1, y, width, height);
		}
		if(x-1 >= 0)
		{
			white = setWhite(white, neighbors, boardState, x-1, y, width, height);
		}
		if(y+1 < height)
		{
			white = setWhite(white, neighbors, boardState, x, y+1, width, height);
		}
		if(y-1 >= 0)
		{
			white = setWhite(white, neighbors, boardState, x, y-1, width, height);
		}
		return white;
	}
	
}
