package edu.rpi.phil.legup.puzzles.treetent;

import java.awt.Point;
import java.util.ArrayList;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionNoTreeForTent extends Contradiction
{
	static final long serialVersionUID = 9510L;
	public String getImageName() {return "images/treetent/contra_NoTreeForTent.png";}
	public ContradictionNoTreeForTent()
	 {
		setName("No Tree For Tent");
		description = "Each tent must link to a tree.";
	 }

	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    public String checkContradictionRaw(BoardState state)
    {
    	//String error = "No unlinked tent is not adjacent to an unlinked tree.";
		String error = "All tents can still be linked to a tree.";
		int height = state.getHeight();
    	int width = state.getWidth();
    	boolean found;
    	ArrayList<Object> validLinks = state.getExtraData();

    	// Check all tents to see if they are adjacent to a tree
    	for (int y=0;y<height;y++)
    	{
    	    for (int x=0;x<width;x++)
    	    {
    	    	if (state.getCellContents(x,y) == TreeTent.CELL_TENT)
    	    	{
    	    		Point tent = new Point(x,y);
    	    		if(TreeTent.isLinked(validLinks, tent))
    	    			continue;

    	    		found = false;
    	    		if(y > 0)
    	    		{
    	    			if(state.getCellContents( x, y-1 ) == TreeTent.CELL_TREE)
    	    			{
    	    				if(!TreeTent.isLinked(validLinks, new Point(x,y-1)))
    	    					found = true;
    	    			}
    	    		}
    	    		if(y < height-1)
    	    		{
    	    			if(state.getCellContents( x, y+1 ) == TreeTent.CELL_TREE)
    	    			{
    	    				if(!TreeTent.isLinked(validLinks, new Point(x,y+1)))
    	    					found = true;
    	    			}
    	    		}
    	    		if(x > 0)
    	    		{
    	    			if(state.getCellContents( x-1, y ) == TreeTent.CELL_TREE)
    	    			{
    	    				if(!TreeTent.isLinked(validLinks, new Point(x-1,y)))
    	    					found = true;
    	    			}
    	    		}
    	    		if(x < width-1)
    	    		{
    	    			if(state.getCellContents( x+1, y ) == TreeTent.CELL_TREE)
    	    			{
    	    				if(!TreeTent.isLinked(validLinks, new Point(x+1,y)))
    	    					found = true;
    	    			}
    	    		}
    	    		if(found == false)
    	    			return null;
    	    	}
    	    }
    	}

		return error;
    }
}
