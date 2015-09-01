//
//  TreeTent.java
//  LEGUP
//
//  Created by Drew Housten on Wed Feb 16 2005.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//

package edu.rpi.phil.legup.puzzles.treetent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardImage;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.Selection;

/**
 * @TODO add link rule from the tree's perspective:
 * 	 grass / tree / linked tent
 */
public class TreeTent extends PuzzleModule
{
	public static int CELL_TREE = 1;
	public static int CELL_TENT = 2;
	public static int CELL_GRASS = 3;
	public static int CELL_UNKNOWN = 0;

    public Map<String, Integer> getSelectableCells()
    {
        Map<String, Integer> tmp = new LinkedHashMap<String, Integer>();
        tmp.put("blank", CELL_UNKNOWN);
        tmp.put("tent", CELL_TENT);
        tmp.put("grass", CELL_GRASS);
        return tmp;
    }
    public Map<String, Integer> getUnselectableCells()
    {
        Map<String, Integer> tmp = new LinkedHashMap<String, Integer>();
        tmp.put("tree", CELL_TREE);
        return tmp;
    }
	public int getNonunknownBlank() { return 2; } //the index into getStateName for grass

	public boolean hasLabels(){return true;}
	private static Stroke med = new BasicStroke(2);

	private static int[][] annotations = null;

	public TreeTent(){
	}

	/*
	 * The grass annotation will indicate that grass will fit in the specified cell.
	 * The following rules can be used for justification:
	 * -Finish Grass
	 * -Empty Field
	 * -Surround Tent
	 */
	static void setAnnotationsGrass(BoardState B)
	{
		int w = B.getWidth();
		int h = B.getHeight();

		int dx[] = { 0, -1, 1, 0 };
		int dy[] = { -1, 0, 0, 1 };

		//Apply the "Finish Grass" rule if an entire row or entire column has been filled with the required number of tents.

		//Check if all the tents have been filled in for each row.
		for (int y = 0; y < h; y++)
		{
			int numTentsInRow = TreeTent.translateNumTents(B.getLabel(BoardState.LABEL_RIGHT, y));
			for (int i = 0; i < w; i++)
			{
				//If the cell is a tent, subtract from the total tents remaining
				if (B.getCellContents(i, y) == TreeTent.CELL_TENT)
					numTentsInRow--;
			}

			//Fill in the rest of this row with grass and skip to the next row
			if (numTentsInRow == 0)
			{
				for (int i = 0; i < w; i++)
				{
					if (B.getCellContents(i, y) == TreeTent.CELL_UNKNOWN)
						annotations[i][y] = TreeTent.CELL_GRASS;
				}
			}
		}

		//Check if all the tents have been filled in for each column
		for (int x = 0; x < w; x++)
		{
			int numTentsInCol = TreeTent.translateNumTents(B.getLabel(BoardState.LABEL_BOTTOM, x));
			for (int i = 0; i < h; i++)
			{
				//If the cell is a tent, subtract from the total tents remaining
				if (B.getCellContents(x, i) == TreeTent.CELL_TENT)
					numTentsInCol--;
			}

			//Fill in the rest of this column with grass
			if (numTentsInCol == 0)
			{
				for (int i = 0; i < h; i++)
				{
					if (B.getCellContents(x, i) == TreeTent.CELL_UNKNOWN)
						annotations[x][i] = TreeTent.CELL_GRASS;
				}
			}
		}

		//Fill in individual cells
		for (int y = 0; y < h; y++)
		{
			for (int x = 0; x < w; x++)
			{
				//Skip this cell if it already has a known value
				if (B.getCellContents(x, y) != TreeTent.CELL_UNKNOWN || annotations[x][y] != 0)
					continue;

				//Check if there any trees are adjacent to this cell
				//If no trees nearby, the rule "Empty Field" can be used to justify that grass will fit in this cell.
				boolean isEmptyField = true;

				//Check if there are any tents nearby.
				//If there is a tent nearby, the rule "Suround Tent" can be used to justify that grass will fit in this cell.
				boolean isSurroundTent = false;

				//loop through all adjacent tiles
				for (int i = 0; i < 4; i++)
				{
					int nx = x + dx[i];
					int ny = y + dy[i];

					if (nx >= 0 && ny >= 0 && nx < w && ny < h)
					{
						if (B.getCellContents(nx, ny) == TreeTent.CELL_TREE)
							isEmptyField = false;

						if (B.getCellContents(nx, ny) == TreeTent.CELL_TENT)
							isSurroundTent = true;
					}
				}

				//No trees nearby, so note this can be a grass tile
				//OR
				//A tent is nearby, so note this can be a grass tile
				if (isEmptyField || isSurroundTent)
					annotations[x][y] = TreeTent.CELL_GRASS;
			}
		}

	}

	/*
	 * The tent annotation will indicate that grass will fit in the specified cell.
	 * The following rules can be used for justification:
	 * -Finish Tents
	 * -Last Camping Spot
	 */
	static void setAnnotationsTents(BoardState B)
	{
		int w = B.getWidth();
		int h = B.getHeight();

		int dx[] = { 0, -1, 1, 0 };
		int dy[] = { -1, 0, 0, 1 };

		//Apply the "Finish Tents" rule if tents can be filled in for the remaining unknown spots.

		//Check if all the tents can be filled in for each row.
		for (int y = 0; y < h; y++)
		{
			int totalTentRow = TreeTent.translateNumTents(B.getLabel(BoardState.LABEL_RIGHT, y));
			if (totalTentRow == 0)
				continue;

			int tentCount = 0, unknownCount = 0;
			for (int i = 0; i < w; i++)
			{
				if (B.getCellContents(i, y) == TreeTent.CELL_TENT)
					tentCount++;

				if (B.getCellContents(i, y) == TreeTent.CELL_UNKNOWN)
					unknownCount++;
			}

			if (tentCount + unknownCount == totalTentRow)
			{
				for (int i = 0; i < w; i++)
				{
					if (B.getCellContents(i, y) == TreeTent.CELL_UNKNOWN)
						annotations[i][y] = TreeTent.CELL_TENT;
				}
			}
		}

		//Check if all the tents can be filled in for each col.
		for (int x = 0; x < w; x++)
		{
	    	int totalTentCol = TreeTent.translateNumTents(B.getLabel(BoardState.LABEL_BOTTOM, x));
	    	if (totalTentCol == 0)
	    		continue;

	    	int tentCount = 0, unknownCount = 0;
			for (int i = 0; i < h; i++)
			{
				if (B.getCellContents(x, i) == TreeTent.CELL_TENT)
					tentCount++;

				if (B.getCellContents(x, i) == TreeTent.CELL_UNKNOWN)
					unknownCount++;
			}

			if (tentCount + unknownCount == totalTentCol)
			{
				for (int i = 0; i < h; i++)
				{
					if (B.getCellContents(x, i) == TreeTent.CELL_UNKNOWN)
						annotations[x][i] = TreeTent.CELL_TENT;
				}
			}
		}

		//Fill in individual cells
		for (int y = 0; y < h; y++)
		{
			for (int x = 0; x < w; x++)
			{
				//Skip this cell if it isn't a tree or if an annotation has been placed already
				if (B.getCellContents(x, y) != TreeTent.CELL_TREE || annotations[x][y] != 0)
					continue;

				//Check if there are any tree with a lone tent spot
				//If so, then the rule "Last Camping Spot" can be applied.
				int numUnknown = 0;
				int numExistingTents = 0;
				int unknownX = 0, unknownY = 0;

				//Loop through all the adjacent tiles
				for (int i = 0; i < 4; i++)
				{
					int nx = x + dx[i];
					int ny = y + dy[i];

					//Make sure the tile is in bounds
					if (nx >= 0 && ny >= 0 && nx < w && ny < h)
					{
						if (B.getCellContents(nx, ny) == TreeTent.CELL_UNKNOWN)
						{
							unknownX = nx;
							unknownY = ny;
							numUnknown++;
						}
						else if (B.getCellContents(nx, ny) == TreeTent.CELL_TENT)
							numExistingTents++;
					}
				}

				//mark the spot where the tree should be placed
				if (numUnknown == 1 && numExistingTents == 0)
					annotations[unknownX][unknownY] = TreeTent.CELL_TENT;
			}
		}
	}

	static void setAnnotations(BoardState B)
	{
		//0 - unknown
		//1 - tree
		//2 - tent
		//3 - grass

		int w = B.getWidth();
		int h = B.getHeight();

		annotations = new int[w][h];
		for (int x = 0; x < w; x++)
		{
			for (int y = 0; y < h; y++)
			{
				annotations[x][y] = 0;
			}
		}

		setAnnotationsGrass(B);
		setAnnotationsTents(B);
	}

	public void disableAnnotationsForCell(int x, int y)
	{
		if (annotations != null)
			annotations[x][y] = 0;
	}

	public Object extraDataFromString(String str)
	{
		String[] data = str.split(",");
		if(data.length != 4)return null;
		ExtraTreeTentLink link = new ExtraTreeTentLink(new Point(Integer.valueOf(data[0]).intValue(),Integer.valueOf(data[1]).intValue()),new Point(Integer.valueOf(data[2]).intValue(),Integer.valueOf(data[3]).intValue()));
		return link;
	}

	//returns true iff there are no invalid links
    public static boolean noInvalidLinks(BoardState state)
    {
    	for(Object obj : state.getExtraData())
		{
			ExtraTreeTentLink link = (ExtraTreeTentLink)obj;
			int numTreesInLink = ((state.getCellContents(link.pos1.x,link.pos1.y) == CELL_TREE)?1:0) + ((state.getCellContents(link.pos2.x,link.pos2.y) == CELL_TREE)?1:0);
			int numTentsInLink = ((state.getCellContents(link.pos1.x,link.pos1.y) == CELL_TENT)?1:0) + ((state.getCellContents(link.pos2.x,link.pos2.y) == CELL_TENT)?1:0);
			if((numTreesInLink != 1) || (numTentsInLink != 1))return false; //since there is a link that's not between a tree and a tent
		}
    	return true;
    }

	public boolean checkBoardComplete(BoardState finalstate)
	{
		if(super.checkBoardComplete(finalstate)) //make sure there are no blanks (which is what PuzzleModule.checkBoardComplete() does)
		{
			Vector<Point> pointsThatAreLinked = new Vector<Point>();
			for(Object obj : finalstate.getExtraData())
			{
				ExtraTreeTentLink link = (ExtraTreeTentLink)obj;
				int numTreesInLink = ((finalstate.getCellContents(link.pos1.x,link.pos1.y) == CELL_TREE)?1:0) + ((finalstate.getCellContents(link.pos2.x,link.pos2.y) == CELL_TREE)?1:0);
				int numTentsInLink = ((finalstate.getCellContents(link.pos1.x,link.pos1.y) == CELL_TENT)?1:0) + ((finalstate.getCellContents(link.pos2.x,link.pos2.y) == CELL_TENT)?1:0);
				if((numTreesInLink != 1) || (numTentsInLink != 1))return false; //since there is a link that's not between a tree and a tent
				pointsThatAreLinked.add(link.pos1);
				pointsThatAreLinked.add(link.pos2);
			}
			for(int y=0;y<finalstate.getHeight();y++)
			{
				for(int x=0;x<finalstate.getWidth();x++)
				{
					//System.out.println("Contents of ("+x+","+y+") is "+finalstate.getCellContents(x,y));
					if((finalstate.getCellContents(x,y) == CELL_TREE)||(finalstate.getCellContents(x,y) == CELL_TENT))
					{
						if(!pointsThatAreLinked.contains(new Point(x,y)))
						{
							return false; //since the current cell is a tree or tent, but not linked
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Take an action when the left mouse button is pressed
	 * @param state the current board state
	 * @param x the x position where the pressed event occured
	 * @param y the y position where the pressed event occured
	 */
	public void mousePressedEvent(BoardState state, Point where)
	{
		//get rid of annotation
		disableAnnotationsForCell(where.x, where.y);

		super.mousePressedEvent(state,where);
	}
	public void mouseDraggedEvent(BoardState state, Point where)
	{
        state.setCellContents(where.x,where.y,CELL_GRASS);
	}
	public void labelPressedEvent(BoardState state, int index, int side)
	{
		//System.out.println(index);
		//System.out.println(side);
		BoardState next = state.conditionalAddTransition();
		if(next != null)
		{
			boolean horizontal = (side == 2 || side == 3);
			int max = horizontal ? next.getWidth() : next.getHeight();
			for(int i = 0; i < max; ++i)
			{
				int x = horizontal ? i : index;
				int y = horizontal ? index : i;
				if(next.isModifiableCell(x,y))
				{
					next.setCellContents(x,y,CELL_GRASS);

					//make sure to disable annotations since the cell will be filled with grass
					disableAnnotationsForCell(x, y);
				}
			}
		}
	}

	/**
	 * Take an action when a left mouse drag (or click) event occurs
	 * @param state
	 * @param from
	 * @param to
	 */
	public void mouseDraggedEvent(BoardState state, Point from, Point to)
	{
		if (from.equals(to))
		{ // click
			//Warning: Legup doesn't check whether or not a cell can be modified when a dragged event occurs
			//Already handled by PuzzleModule.mousePressedEvent()
			/*if(state.isModifiableCell(to.x, to.y))
			{
				int next = getNextCellValue(from.x,from.y,state);
				state.setCellContents(from.x,from.y,next);
			}*/
		}
		else
		{ // drag, create link, or remove it
			ExtraTreeTentLink e = new ExtraTreeTentLink(from,to);
			boolean removed = false;

			if(((to.x-from.x)^2 + (to.y-from.y)^2) != 1 && ((from.x-to.x)^2 + (from.y-to.y)^2) != 1)
				return;

			//check if exactly one point is a tree
			if (!((state.getCellContents(from.x, from.y) == CELL_TREE && state.getCellContents(to.x, to.y) != CELL_TREE) ||
			(state.getCellContents(from.x, from.y) != CELL_TREE && state.getCellContents(to.x, to.y) == CELL_TREE)))
				return;
			BoardState next = ((state.isModifiable())? state : BoardState.addTransition());
			if(next == null)return;
			Legup.setCurrentState(next);
			ArrayList<Object> extra = next.getExtraData();
			removed = extra.remove(e);
			if(!removed)
			{
				next.addExtraData(e);
			}
			next.boardDataChanged();
			if(!next.extraDataDelta.remove(e))
			{
				next.extraDataDelta.add(e);
			}

			next.propagateExtraData(e,!removed);
		}
	}

	/**
	 * Draw any extra data for the board
	 * @param g the Graphics to draw with
	 * @param extraData the extra data of the current board state we're drawing
	 * @param bounds the bounds of the grid
	 * @param w the width (in boxes) of the puzzle
	 * @param h the height (in boxes) of the puzzle
	 */
	public void drawExtraData(Graphics gr, ArrayList<Object> extraData, ArrayList<Object> extraDataDelta, Rectangle bounds, int w, int h)
	{
		Graphics2D g = (Graphics2D)gr;
		Stroke preStroke = g.getStroke();
		Color preColor = g.getColor();
		g.setColor(Color.red);
		g.setStroke(med);
		Stroke highlightStroke = new BasicStroke(2f);
		double dx = bounds.width / (double)w;
		double dy = bounds.height / (double)h;
		double halfX = dx/2;
		double halfY = dy/2;

		for (int x = 0; x < extraData.size(); ++x)
		{
			ExtraTreeTentLink e = (ExtraTreeTentLink)extraData.get(x);

			double x1 = bounds.x + e.pos1.x * dx + halfX;
			double y1 = bounds.y + e.pos1.y * dx + halfY;

			double x2 = bounds.x + e.pos2.x * dx + halfX;
			double y2 = bounds.y + e.pos2.y * dx + halfY;
			int width_mult = (x1 == x2)? 1 : 2;
			int height_mult = (y1 == y2)? 1 : 2;
			g.drawLine((int)x1,(int)y1,(int)x2,(int)y2);

			if(extraDataDelta.contains(e))
			{
				g.setColor(Color.green);
				g.setStroke(highlightStroke);

				g.drawRect((int)(x1-halfX+2),(int)(y1-halfY+2),(int)(2*width_mult*halfX-4),(int)(2*height_mult*halfY-4));

				g.setColor(Color.red);
				g.setStroke(med);
			}
		}

		g.setColor(preColor);
		g.setStroke(preStroke);
	}

	public String getImageLocation(int cellValue) {
		switch(cellValue) {
			case 1: return "images/treetent/tree.png";
			case 2: return "images/treetent/tent.png";
			case 3: return "images/treetent/grass.png";
			default: return "images/treetent/unknown.gif";
		}
	}

	public void initBoard(BoardState state)
	{
		int[] dir =
		{
			BoardState.LABEL_LEFT,
			BoardState.LABEL_RIGHT,
			BoardState.LABEL_TOP,
			BoardState.LABEL_BOTTOM
		};

		int[] sizes =
		{
			state.getHeight(),
			state.getHeight(),
			state.getWidth(),
			state.getWidth(),
		};

		int[] type =
		{
			1,
			0,
			2,
			0
		};

		final int TYPE_ZERO = 0;
		final int TYPE_LETTER = 1;
		final int TYPE_NUMBER = 2;
		final int numberOffset = 10;
		final int letterOffset = 30;

		for (int x = 0; x < dir.length; ++x)
		{
			for (int c = 0; c < sizes[x]; ++c)
			{
				if (type[x] == TYPE_ZERO)
					state.setLabel(dir[x],c, numberOffset);
				else if (type[x] == TYPE_LETTER)
					state.setLabel(dir[x],c, letterOffset + c);
				else if (type[x] == TYPE_NUMBER)
					state.setLabel(dir[x],c, numberOffset + c + 1);
			}
		}
	}

	/**
	 * Get all the images (as strings to the image path) used by this puzzle in the center part
	 * @return an array of strings to image paths
	 */
	public BoardImage[] getAllCenterImages()
	{
		BoardImage[] s =
		{
				new BoardImage("images/treetent/unknown.gif",0),
				new BoardImage("images/treetent/tree.gif",1),
				new BoardImage("images/treetent/tent.gif",2),
				new BoardImage("images/treetent/grass.gif",3)

		};

		return s;
	}

	/**
	 * Get all the images (as strings to the image path) used by this puzzle in the border part
	 * @return an array of strings to image paths
	 */
	public BoardImage[] getAllBorderImages()
	{
		BoardImage[] s = new BoardImage[30];
		int count = 0;

		for (int x = 0; x < 20; ++x)
		{
			s[count++] = new BoardImage("images/treetent/" + (x)+ ".gif",10 + x);
		}

		for (int x = 0; x < 10; ++x)
		{
			s[count++] = new BoardImage("images/treetent/" + (char)('a' + (x)) + ".gif",30 + x);
		}

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
		if (curValue < 10)
			curValue = 9; // will get incremented

		return (curValue + 1 <= 39 ? curValue + 1 : 10);
	}

	public boolean checkGoal(BoardState currentBoard, BoardState goalBoard){
		return currentBoard.compareBoard(goalBoard);
	}

	public Vector <PuzzleRule> getRules(){
		Vector <PuzzleRule>ruleList = new Vector <PuzzleRule>();
		//ruleList.add(new PuzzleRule());
		ruleList.add(new RuleFinishWithGrass());
		ruleList.add(new RuleFinishWithTents());
		ruleList.add(new RuleSurroundTentWithGrass()); //surround tent with grass
		ruleList.add(new RuleEmptyField());
		ruleList.add(new RuleTentForTree());
		ruleList.add(new RuleTreeForTent());
		ruleList.add(new RuleLastCampingSpot());

		//ruleList.add(new RuleNewLink());
		//ruleList.add(new RuleNewLink());
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

		contradictionList.add(new ContradictionAdjacentTents());
		contradictionList.add(new ContradictionMiscount());
		contradictionList.add(new ContradictionNoTentsForTree());
		contradictionList.add(new ContradictionTentNotNearTree());

		return contradictionList;
	}

	public Vector <CaseRule> getCaseRules()
	{
		Vector <CaseRule> caseRules = new Vector <CaseRule>();

		caseRules.add(new CaseTentOrGrass());
		caseRules.add(new CaseFillInRow());
		caseRules.add(new CaseLinkTree());
		caseRules.add(new CaseLinkTent());

		return caseRules;
	}

	public static int translateNumTents(int cellValue){
		return (cellValue - 10);
	}

	public boolean checkValidBoardState(BoardState boardState)
	{
		int height = boardState.getHeight();
		int width = boardState.getWidth();

		// Check all tents to see if they are adjacent to a tree
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				try
				{
					if (boardState.getCellContents(i,j) == 2)
					{
						// Check if it is adjacent to a tree
						if (!checkAdjacentTree(boardState, i, j))
						{
							System.out.println("A tent is not adjacent to a tree");
							return false;
						}

						// Check if it is adjacent to another tent
						if (checkAdjacentTent(boardState, i, j))
						{
							System.out.println("A tent is adjacent to another tent");
							return false;
						}
					}
				}
				catch (Exception e)
				{
				}
			}
		}

		// Check that the number of tents in a row or column do not exceed the
		// number allowed
		for (int i = 0; i < height; i++)
		{
			if (!checkRow(boardState, i))
			{
				return false;
			}
		}

		for (int i = 0; i < width; i++)
		{
			if (!checkCol(boardState, i))
			{
				return false;
			}
		}

		return true;
	}


	private boolean checkRow(BoardState boardState, int rowNum)
	{
		int width = boardState.getWidth();
		int numTents = 0;
		try
		{
			numTents = TreeTent.translateNumTents(boardState.getLabel(BoardState.LABEL_RIGHT, rowNum));
		}
		catch (Exception e)
		{
		}

		for (int i = 0; i < width; i++)
		{
			try
			{
				if (boardState.getCellContents(rowNum,i) == 2)
				{
					numTents--;
				}
			}
			catch (Exception e)
			{
			}
		}

		if (numTents < 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private boolean checkCol(BoardState boardState, int colNum)
	{
		int height = boardState.getHeight();
		int numTents = 0;
		try
		{
			numTents = TreeTent.translateNumTents(boardState.getLabel(BoardState.LABEL_BOTTOM, colNum));
		}
		catch (Exception e)
		{
		}

		for (int i = 0; i < height; i++)
		{
			try
			{
				if (boardState.getCellContents(i,colNum) == 2)
				{
					numTents--;
				}
			}
			catch (Exception e)
			{
			}
		}

		if (numTents < 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private boolean checkAdjacentTent(BoardState boardState, int row, int col)
	{
		// Check Up
		try
		{
			if (boardState.getCellContents(row-1, col) == 2)
			{
				return true;
			}
		}
		catch (Exception e)
		{
		}

		// Check Left
		try
		{
			if (boardState.getCellContents(row, col-1) == 2)
			{
				return true;
			}
		}
		catch (Exception e)
		{
		}


		// Check Right
		try
		{
			if (boardState.getCellContents(row, col+1) == 2)
			{
				return true;
			}
		}
		catch (Exception e)
		{
		}

		// Check Down
		try
		{
			if (boardState.getCellContents(row+1, col) == 2)
			{
				return true;
			}
		}
		catch (Exception e)
		{
		}

		return false;
	}

	private boolean checkAdjacentTree(BoardState boardState, int row, int col)
	{
		// Check Up
		try
		{
			if (boardState.getCellContents(row-1, col) == 1)
			{
				return true;
			}
		}
		catch (Exception e)
		{
		}

		// Check Left
		try
		{
			if (boardState.getCellContents(row, col-1) == 1)
			{
				return true;
			}
		}
		catch (Exception e)
		{
		}


		// Check Right
		try
		{
			if (boardState.getCellContents(row, col+1) == 1)
			{
				return true;
			}
		}
		catch (Exception e)
		{
		}

		// Check Down
		try
		{
			if (boardState.getCellContents(row+1, col) == 1)
			{
				return true;
			}
		}
		catch (Exception e)
		{
		}

		return false;
	}

	public static boolean isLinked(ArrayList<Object> links, Point cell)
	{
		for (int a = 0; a < links.size(); ++a)
		{
			ExtraTreeTentLink e = (ExtraTreeTentLink)links.get(a);
			if (e.pos1.equals(cell) || e.pos2.equals(cell))
			{
				return true;
			}
		}
		return false;
	}
	/* AI stuff */
	public BoardState guess(BoardState Board) {
		// out of forced moves, need to guess
		Point guess = GenerateBestGuess(Board);
		// guess, if we found one
		if (guess.x != -1 && guess.y != -1) {
			BoardState Parent = Board.getSingleParentState();
			BoardState CaseTent = Board;
			BoardState CaseGrass = Parent.addTransitionFrom();
			CaseTent.setCellContents(guess.x, guess.y, CELL_TENT);
			CaseGrass.setCellContents(guess.x, guess.y, CELL_GRASS);
			Parent.setCaseSplitJustification(new CaseTentOrGrass());
			System.out.println("Guessed at "+guess.x+","+guess.y);
			//Legup.setSelection(CaseTent,false);
			return CaseTent;
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
		for (int r = 0; r < width; r++ ) {
			for (int c = 0; c < height; c++) {
				if (Board.getCellContents(r,c) == 0) {
					// compute probability of a hit
					double myProb = HitProb(r,c,Board);
					//System.out.println("Square "+r+","+c+" prob: "+myProb);
					double myOff = Math.abs(BESTPROB-myProb);
					if (myOff < currentOff) {
						System.out.println("Got new guess square: "+r+","+c+", off ="+myOff);
						currentX = r;
						currentY = c;
						currentOff = myOff;
					}
				}
			}
		}
		return new Point(currentX,currentY);
	}
	private double HitProb (int row, int column, BoardState Board) {
		double R;
		double C;
		int width = Board.getHeight();
		int height = Board.getWidth();
		// Row
		double neededTents = TreeTent.translateNumTents(Board.getLabel(BoardState.LABEL_RIGHT, row));
		double currentTents = 0;
		double OpenSpace = 0;
		for (int i = 0; i<width; i++) {
			int cell = Board.getCellContents(row, i);
			if (cell == CELL_TENT) {
				currentTents++;
			} else if (cell == 0) {
				OpenSpace++;
			}
		}
		//	   Tents to be placed		places to put them
		//System.out.println("Row "+row+": "+neededTents+"-"+currentTents+"/"+OpenSpace);
		R = (neededTents-currentTents)/OpenSpace;
		//System.out.println(R);
		neededTents = TreeTent.translateNumTents(Board.getLabel(BoardState.LABEL_BOTTOM, column));
		currentTents = 0;
		OpenSpace = 0;
		for (int i = 0; i<height; i++) {
			int cell = Board.getCellContents(i, column);
			if (cell == CELL_TENT) {
				currentTents++;
			} else if (cell == 0) {
				OpenSpace++;
			}
		}
		//System.out.println("Column "+column+": "+neededTents+"-"+currentTents+"/"+OpenSpace);
		//	 Tents to be placed		places to put them
		C = (neededTents-currentTents)/OpenSpace;
		//System.out.println(C);
		return R*C;
	}

	public String numberToLetters(int number)
	{
		String s = "";
		while(number != 0)
		{
			int rem = (number % 26) + 64;
			number = number / 26;
			char c = (char)rem;
			s = c + s;
		}
		return s;
	}

	public void drawLeftLabel(Graphics2D g, int val, int x, int y){
		drawText(g,x, y, numberToLetters(y + 1));
	}

	public void drawRightLabel(Graphics2D g, int val, int x, int y){
		drawText(g,x, y, String.valueOf(val - 10));
	}

	public void drawTopLabel(Graphics2D g, int val, int x, int y){
		drawText(g,x, y, String.valueOf(x + 1));
	}

	public void drawBottomLabel(Graphics2D g, int val, int x, int y){
		drawText(g,x, y, String.valueOf(val - 10));
	}

}
