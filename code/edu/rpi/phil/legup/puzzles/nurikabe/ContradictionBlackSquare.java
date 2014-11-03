package edu.rpi.phil.legup.puzzles.nurikabe;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionBlackSquare extends Contradiction
{	 
    private static final long serialVersionUID = 903191288L;
	
	ContradictionBlackSquare()
	{
		setName("Black Square");
		description = "There cannot be a 2x2 square of black.";
		image = new ImageIcon("images/nurikabe/contradictions/BlackSquare.png");
	}
		
	public String getImageName()
	{
		return "images/nurikabe/contradictions/BlackSquare.png";
	}
	 
	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    protected String checkContradictionRaw(BoardState state)
    {
    	String error = null;
    	int height = state.getHeight();
    	int width = state.getWidth();

    	for(int x = 0; x < width - 1; ++x)
    	{
    		for(int y = 0; y < height - 1; ++y)
    		{
    			if(state.getCellContents(x,y) == Nurikabe.CELL_BLACK && state.getCellContents(x + 1,y) == Nurikabe.CELL_BLACK && state.getCellContents(x,y + 1) == Nurikabe.CELL_BLACK && state.getCellContents(x+1,y+1) == Nurikabe.CELL_BLACK)
    			{
    				return null;
    			}
    		}
    	}
    	
    	error = "No 2x2 square of black exists.";

		return error;
    }
}
