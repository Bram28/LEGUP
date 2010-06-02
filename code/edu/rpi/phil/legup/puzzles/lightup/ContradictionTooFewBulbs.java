package edu.rpi.phil.legup.puzzles.lightup;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionTooFewBulbs extends Contradiction
{

	 ContradictionTooFewBulbs()
	 {
		name = "Too Few Bulbs";
		description = "There cannot be less bulbs around a block than its number states.";
		image = new ImageIcon("images/lightup/contradictions/TooFewBulbs.png");
	 }

	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    protected String checkContradictionRaw(BoardState state)
    {
    	String error = "foo";
    	int height = state.getHeight();
    	int width = state.getWidth();
    	int cellvalue = 0;
    	int blanks = 0;
    	int lights = 0;
    	boolean[][] litup = new boolean[state.getHeight()][state.getWidth()];
    	LightUp.determineLight(state, litup);

    	//System.out.println("Too Few Bulbs started");
    	for(int x = 0; x < width; ++x)
    	{
    		for(int y = 0; y < height; ++y)
    		{
    			//System.out.println("Checking " + x + ", " +y);
    			cellvalue = state.getCellContents(x,y);
    			if(cellvalue >= 10  && cellvalue < 15)
    			{
    				blanks = 0;
    				lights = 0;
    				if(x > 0)
    				{
    					/*
    					if(state.getCellContents(x-1, y) == LightUp.CELL_BLANK || (litup[y][x-1] && state.getCellContents(x, y+1) == LightUp.CELL_UNKNOWN)||state.getCellContents(x-1, y) >= 10)
    						++blanks;
    					*/
    					if((state.getCellContents(x-1,y) == LightUp.CELL_LIGHT) || ((!litup[y][x-1]) && (state.getCellContents(x-1,y) == LightUp.CELL_UNKNOWN))) {
    						lights++;
    					}
    				}
    				else
    					++blanks;

    				if(x < width - 1)
    				{
    					/*
    					if(state.getCellContents(x+1, y) == LightUp.CELL_BLANK|| (litup[y][x+1] && state.getCellContents(x, y+1) == LightUp.CELL_UNKNOWN)||state.getCellContents(x+1, y) >= 10)
    						++blanks;
    					*/
    					if((state.getCellContents(x+1,y) == LightUp.CELL_LIGHT) || ((!litup[y][x+1]) && (state.getCellContents(x+1,y) == LightUp.CELL_UNKNOWN))) {
    						lights++;
    					}
    				}
    				else
    					++blanks;

    				if(y > 0)
    				{
    					/*
    					if(state.getCellContents(x, y-1) == LightUp.CELL_BLANK|| (litup[y-1][x] && state.getCellContents(x, y+1) == LightUp.CELL_UNKNOWN)||state.getCellContents(x, y-1) >= 10)
    						++blanks;
    					*/
    					if((state.getCellContents(x,y-1) == LightUp.CELL_LIGHT) || ((!litup[y-1][x]) && (state.getCellContents(x,y-1) == LightUp.CELL_UNKNOWN))) {
    						lights++;
    					}
    				}
    				else
    					++blanks;

    				if(y < height - 1)
    				{
    					/*
    					if(state.getCellContents(x, y+1) == LightUp.CELL_BLANK|| (litup[y+1][x] && state.getCellContents(x, y+1) == LightUp.CELL_UNKNOWN)||state.getCellContents(x, y+1) >= 10)
    						++blanks;
    					*/
    					if((state.getCellContents(x,y+1) == LightUp.CELL_LIGHT) || ((!litup[y+1][x]) && (state.getCellContents(x,y+1) == LightUp.CELL_UNKNOWN))) {
    						lights++;
    					}
    				}
    				else {
    					++blanks;
    				}
    				//if(blanks > 4 - (cellvalue - 10)) {
    				if (lights < (cellvalue - 10)) {
    					/*
    					System.out.println("Cell " + x + ", " + y + " has too few lights!");
    					System.out.println(lights + " lights and " + (cellvalue - 10) + " needed ");
    					try {
    						java.lang.Thread.sleep(5000);
    					}
    					catch (Exception e) {
    						System.err.println("Thread sleep failed!");
    					}
    					*/
    					return null;
    				}
    			}
    			//System.out.println("Cell " + x + ", " + y + " is fine");
    		}
    	}

    	error = "No block with too few bulbs exists.";
    	//System.out.println("Too Few Bulbs finished without error");
		return error;
    }
}
