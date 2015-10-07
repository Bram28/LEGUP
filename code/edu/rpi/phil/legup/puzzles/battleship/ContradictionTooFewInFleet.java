package edu.rpi.phil.legup.puzzles.battleship;

import java.awt.Point;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionTooFewInFleet extends Contradiction
{
	private static final long serialVersionUID = 8682414285813765536L;

	ContradictionTooFewInFleet()
	{
		setName("Too few ships in the fleet");
		description = "The number of ships of a specific size is too low, and there is not sufficient space for more.";
	}
		
	public String getImageName()
	{
		return "images/battleship/contradictions/too_few_in_fleet.png";
	}
	
	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    public String checkContradictionRaw(BoardState state)
    {
		Contradiction contra = new ContradictionTooManyRowCol();
    	for (int curLength = 1; curLength <= BattleShip.numShips.length; curLength++)
    	{
    		int totalShipLocations = 0;
    		
    		for (Point curPoint : BattleShip.possibleHorizontalShipLocations(state, curLength))
            {
            	BoardState transition = state.copy();
            	int x = curPoint.x;
            	int y = curPoint.y;
            	for (int i = 0; i < curLength; i++)
            	{
            		int value = BattleShip.CELL_MIDDLE;
            		if (i == 0)
            			value = BattleShip.CELL_LEFT_CAP;
            		else if (i == curLength - 1)
            			value = BattleShip.CELL_RIGHT_CAP;

            		// TODO SetCellContents on a virtual board triggered a stack overflow, must fix
        			transition.getBoardCells()[y][x+i] = value;
            	}
            	if (contra.checkContradictionRaw(transition) != null)
            		totalShipLocations++;
            	transition.deleteState();
            	transition = null;
            }
    		
    		if (curLength > 1) // Prevent submarines from being counted twice
    		{
	            for (Point curPoint : BattleShip.possibleVerticalShipLocations(state, curLength))
	            {
	            	BoardState transition = state.copy();
	            	int x = curPoint.x;
	            	int y = curPoint.y;
	            	for (int i = 0; i < curLength; i++)
	            	{
	            		int value = BattleShip.CELL_MIDDLE;
	            		if (i == 0)
	            			value = BattleShip.CELL_TOP_CAP;
	            		else if (i == curLength - 1)
	            			value = BattleShip.CELL_BOTTOM_CAP;
	            		
	            		// TODO SetCellContents on a virtual board triggered a stack overflow, must fix
            			transition.getBoardCells()[y+i][x] = value;
	            	}
	            	if (contra.checkContradictionRaw(transition) != null)
	            		totalShipLocations++;
	            	transition.deleteState();
	            	transition = null;
	            }
    		}
    		
    		if (totalShipLocations < BattleShip.numShips[curLength - 1])
    			return null;
    	}
        return "There are enough locations for ships of every size";
    }
}