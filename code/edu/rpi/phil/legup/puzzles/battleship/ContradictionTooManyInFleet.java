package edu.rpi.phil.legup.puzzles.battleship;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionTooManyInFleet extends Contradiction
{
	private static final long serialVersionUID = 578676887886576082L;

	ContradictionTooManyInFleet()
	{
		setName("Too many ships in the fleet");
		description = "The number of ships of a specific size is too high.";
	}
		
	public String getImageName()
	{
		return "images/battleship/contradictions/too_many_in_fleet.png";
	}
	
	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    public String checkContradictionRaw(BoardState state)
    {
    	for (int curLength = 1; curLength <= BattleShip.numShips.length; curLength++)
    	{
    		int totalShips = BattleShip.horizontalShipLocations(state, curLength).size();
    		if (curLength != 1)
    			totalShips += BattleShip.verticalShipLocations(state, curLength).size();
    		if (totalShips > BattleShip.numShips[curLength - 1])
    		{
    			return null;
    		}
    	}
        return "There are not too many ships of any size";
    }
}