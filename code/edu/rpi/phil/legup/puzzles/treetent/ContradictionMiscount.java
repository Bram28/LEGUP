package edu.rpi.phil.legup.puzzles.treetent;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionMiscount extends Contradiction
{	 
	
	public ContradictionMiscount()
	 {
		name = "Clue Miscount";
		description = "Rows and columns must have exactly their clue in tents.";
		image = new ImageIcon("images/treetent/contra_miscount.png");
	 }
	 
	
	private int countRow(int y, BoardState state, int type)
	{
		int width = state.getWidth();
		int count = 0;
		
		for (int x=0;x<width;x++)
	    {
			if (state.getCellContents(x,y) == type)
			{
				++count;
			}
	    }
		
		return count;
	}
	
	private int countCol(int x, BoardState state, int type)
	{
		int height = state.getHeight();
		int count = 0;
		
		for (int y=0;y<height;y++)
	    {
			if (state.getCellContents(x,y) == type)
			{
				++count;
			}
	    }
		
		return count;
	}
	
	private int getColNum(int x, BoardState state)
	{
		int label = state.getLabel(BoardState.LABEL_BOTTOM, x);
		return TreeTent.translateNumTents(label);
	}
	
	private int getRowNum(int y, BoardState state)
	{
		int label = state.getLabel(BoardState.LABEL_RIGHT, y);
		return TreeTent.translateNumTents(label);
	}
	
	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    protected String checkContradictionRaw(BoardState state)
    {
    	String error = "No rows or columns have impossible number labels.";
    	int height = state.getHeight();
    	int width = state.getWidth();

    	// Check all tents to see if they are adjacent to a tree
    	for (int y=0;y<height;y++)
    	{
			int n = getRowNum(y,state);
			int u = countRow(y,state,TreeTent.CELL_UNKNOWN);
			int k = countRow(y,state,TreeTent.CELL_TENT);
			   
			if (n < k || (n > (k + u)))
			{
				error = null;
			}
    	}
    	
    	for (int x=0;x<width;x++)
 	    {
			int n = getColNum(x,state);
			int u = countCol(x,state,TreeTent.CELL_UNKNOWN);
			int k = countCol(x,state,TreeTent.CELL_TENT);
			   
			if (n < k || (n > (k + u)))
			{
				error = null;
			}
 	    }

		return error;
    }
}
