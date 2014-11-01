//
//  TreeTent.java
//  LEGUP
//
//  Created by Drew Housten on Wed Feb 16 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//

package edu.rpi.phil.legup.puzzles.nurikabe;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.rpi.phil.legup.BoardImage;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;


public class Nurikabe extends PuzzleModule
{	
	public static int CELL_BLACK = 1;
	public static int CELL_WHITE = 2;

    public List<String> getCellNames()
    { return Arrays.asList(new String[] {"blank", "black", "white"}); }
    public Set<Integer> getUnselectableCells()
    { return new HashSet(Arrays.asList(new Integer[] {})); }
	
	//0 - 9 on the board are represented internally as 10 - 19
	//int CELL_BLOCK0 = 10, CELL_BLOCK1 = 11, etc...
	
	public Nurikabe(){
	}

	public void drawCell( Graphics2D g, int x, int y, int state ){
		if( state != 0 ){
			g.setColor( (state == 1) ? Color.black : Color.white );
			g.fill( getCellBounds(x,y) );
			if( state > 10 )
				drawText( g, x, y, String.valueOf(state - 10) );
		}
	}

	/*public int getAbsoluteNextCellValue(int x, int y, BoardState boardState)
	{
		int contents = boardState.getCellContents(x,y);
		int rv = CELL_UNKNOWN;
		
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
			return 10;
		}
		else
		{
			if (contents >= 10 && contents < 19)
				return contents + 1;
		}

		return rv;
	}*/
	
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
