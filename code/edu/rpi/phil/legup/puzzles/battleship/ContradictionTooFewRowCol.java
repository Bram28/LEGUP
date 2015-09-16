package edu.rpi.phil.legup.puzzles.battleship;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionTooFewRowCol extends Contradiction
{	 
    private static final long serialVersionUID = 450786104L;
	
    
    ContradictionTooFewRowCol()
	{
		setName("Too few segments in a row/column");
		description = "The number of segments in a row/column cannot be less than the label.";
	}
		
	public String getImageName()
	{
		return "images/battleship/contradictions/too_few_segments.png";
	}
	
	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    public String checkContradictionRaw(BoardState state)
    {
    	int height = state.getHeight();
    	int width = state.getWidth();
    	for(int i = 0; i < width; i++) // per column
    	{
    		int possibleSegments = 0;
    		int maxSegments = state.getLabel(BoardState.LABEL_BOTTOM, i)-40;
    		for(int j = 0; j < height; j++)
    		{
    			int cellValue = state.getCellContents(i, j);
    			if(BattleShip.isShip(cellValue) || cellValue == BattleShip.CELL_UNKNOWN) possibleSegments++;
    		}
    		if (possibleSegments<maxSegments) return null;
    	}
    	for (int j = 0; j < height; j++) // per row
    	{
    		int possibleSegments = 0;
    		int maxSegments = state.getLabel(BoardState.LABEL_RIGHT, j)-40;
    		for(int i = 0; i < width; i++)
    		{
    			int cellValue = state.getCellContents(i, j);
    			if(BattleShip.isShip(cellValue) || cellValue == BattleShip.CELL_UNKNOWN) possibleSegments++;
    		}
    		if (possibleSegments<maxSegments) return null;
    	}
        return "No row/column has too few ship segments";
    }
}