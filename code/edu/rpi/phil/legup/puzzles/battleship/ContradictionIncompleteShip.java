package edu.rpi.phil.legup.puzzles.battleship;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionIncompleteShip extends Contradiction
{
	private static final long serialVersionUID = -8371270465029723898L;

	ContradictionIncompleteShip()
	{
		setName("Ships must not end abruptly");
		description = "End and middle ship segments must be connected to other suitable segments accordingly.";
	}
		
	public String getImageName()
	{
		return "images/battleship/contradictions/IncompleteShip.png";
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
    	for(int i = 0; i < width; i++)
    	{
    		for(int j = 0; j < height; j++)
    		{
    			int cellValue = state.getCellContents(i, j);
    			if(BattleShip.isShip(cellValue))
    			{
    				System.out.println(cellValue);
    				switch(cellValue)
    				{
    				case BattleShip.CELL_MIDDLE:
    					// ship on 2 non-opposite sides
    					if ((BattleShip.checkNorthForSegment(state, i, j) || BattleShip.checkSouthForSegment(state, i, j)) &&
    						(BattleShip.checkWestForSegment(state, i, j) || BattleShip.checkEastForSegment(state, i, j)))
    						return null;
    					// water on 2 non-opposite sides
    					if ((BattleShip.checkNorthForWater(state, i, j) || BattleShip.checkSouthForWater(state, i, j)) &&
    						(BattleShip.checkWestForWater(state, i, j) || BattleShip.checkEastForWater(state, i, j)))
    						return null;
    					break;
    					
    				case BattleShip.CELL_LEFT_CAP:
    					// water on east side
    					if (BattleShip.checkEastForWater(state, i, j))
    						return null;
    					break;
    					
    				case BattleShip.CELL_RIGHT_CAP:
    					// water on west side
    					if (BattleShip.checkWestForWater(state, i, j))
    						return null;
    					break;
    					
    				case BattleShip.CELL_TOP_CAP:
    					// water on south side
    					if (BattleShip.checkSouthForWater(state, i, j))
    						return null;
    					break;
    					
    				case BattleShip.CELL_BOTTOM_CAP:
    					// water on north side
    					if (BattleShip.checkNorthForWater(state, i, j))
    						return null;
    					break;
    				}
    			}
    		}
    	}
        return "No ships are malformed";
    }
}