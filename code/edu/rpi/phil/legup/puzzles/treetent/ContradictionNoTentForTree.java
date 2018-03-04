package edu.rpi.phil.legup.puzzles.treetent;

import java.awt.Point;
import java.util.ArrayList;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;


public class ContradictionNoTentForTree extends Contradiction
{
	static final long serialVersionUID = 9509L;
	private Point loneTree;
	public String getImageName() {return "images/treetent/contra_NoTentForTree.png";}
	public ContradictionNoTentForTree()
	 {
		setName("No Tents For Tree");
		description = "Each tree must link to a tent.";
	 }

	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    public String checkContradictionRaw(BoardState state)
    {
    	//String error = "No tree is surrounding by only grass and tree squares.";
    	String error = "All trees can still be linked to a tent";
    	int height = state.getHeight();
    	int width = state.getWidth();
    	ArrayList<Object> validLinks = state.getExtraData();

    	// Check all unlinked tree to see if they are adjacent to a tree
    	for (int y=0;y<height;y++)
    	{
    	    for (int x=0;x<width;x++)
    	    {
    	    	Point tree = new Point(x,y);
    	    	if (state.getCellContents(x,y) == TreeTent.CELL_TREE)
    	    	{
    	    		//If linked, skip it
    	    		if(TreeTent.isLinked(validLinks, tree))
    	    		{
    	    			continue;
    	    		}

    	    		boolean surrounded = true;

    	    		for (int cx = -1; cx < 2; ++cx)
    	    		{
    	    			for (int cy = -1; cy < 2; ++cy)
    	    			{
    	    				int curX = x + cx;
    	    				int curY = y + cy;

    	    				if (curX >= width)
    	    					continue;
    	    				else if (curY >= height)
    	    					continue;
    	    				else if (curX < 0)
    	    					continue;
    	    				else if (curY < 0)
    	    					continue;
    	    				else if (cx == 0 && cy == 0)
    	    					continue;
    	    				else if(!(cx == 0) && !(cy ==0))
    	    					continue;

    	    				if (state.getCellContents(curX,curY) == TreeTent.CELL_TENT )
    						{
    	    					if(!TreeTent.isLinked(validLinks, new Point(curX, curY)))
    	    					{
    	    						surrounded = false;
    	    					}
    						}
    	    				else if	(state.getCellContents(curX,curY) == TreeTent.CELL_UNKNOWN)
    	    				{ // correct application
    	    					surrounded = false;
    	    				}
    	    			}
    	    		}

    	    		if (surrounded) {
						loneTree = tree;
						error = null;
					}
    	    	}
    	    }
    	}

		return error;
    }

	public Point getLoneTree() {
		return loneTree;
	}
}
