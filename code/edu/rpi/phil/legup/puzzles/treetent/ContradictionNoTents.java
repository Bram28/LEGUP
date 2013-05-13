package edu.rpi.phil.legup.puzzles.treetent;

import javax.swing.ImageIcon;
import java.awt.Point;
import java.util.ArrayList;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;


public class ContradictionNoTents extends Contradiction
{	 
	public String getImageName() {return "images/treetent/contra_noNeighbors.png";}
	public ContradictionNoTents()
	 {
		setName("No Tents For Tree");
		description = "Each tree must have a tent.";
		//image = new ImageIcon("images/treetent/contra_noNeighbors.png");
	 }
	 
	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    protected String checkContradictionRaw(BoardState state)
    {
    	String error = "No tree is surrounding by only grass and tree squares.";
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
    	    		
    	    		if (surrounded)
    	    			error = null;
    	    	}
    	    }
    	}

		return error;
    }
}
