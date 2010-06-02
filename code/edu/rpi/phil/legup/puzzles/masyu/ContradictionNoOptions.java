package edu.rpi.phil.legup.puzzles.masyu;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionNoOptions extends Contradiction {
	
	public ContradictionNoOptions()
	{
		name = "Path Dead End";
		description = "A path must be able to connect the loop and can't dead end.";
		image = new ImageIcon("images/masyu/Rules/ContradictionNoOptions.png");
	}

	/**
	 * Checks if the contradiction was applied correctly to this board state
	 *
	 * @param state The board state
	 * @return null if the contradiction was applied correctly, the error String otherwise
	 */
	protected String checkContradictionRaw(BoardState state)
	{
		int height = state.getHeight();
		int width = state.getWidth();

		BoardAccessor ba = new BoardAccessor(state, state.getSingleParentState(), 0, 0, 0);
		
		for (int y=0;y<height;y++)
		{
			ba.setY(y);
			for (int x=0;x<width;x++)
			{
				ba.setX(x);
				int walls = 0;
				int value = state.getCellContents(x, y);
				if(Masyu.hasNorth(value))
					walls++;
				if(Masyu.hasEast(value))
					walls++;
				if(Masyu.hasSouth(value))
					walls++;
				if(Masyu.hasWest(value))
					walls++;
				if(walls != 1)
					continue;
				boolean found = false;
				
				if(ba.validConnection(ba.getOrigCell(0, -1),BoardAccessor.NORTH))
					if(found)
						continue;
					else
						found = true;
				if(ba.validConnection(ba.getOrigCell(1, 0), BoardAccessor.WEST))
					if(found)
						continue;
					else
						found = true;
				if(ba.validConnection(ba.getOrigCell(-1, 0), BoardAccessor.EAST))
					if(found)
						continue;
					else
						found = true;
				if(ba.validConnection(ba.getOrigCell(0, 1), BoardAccessor.SOUTH))
					if(found)
						continue;
					else
						found = true;
				if(found)
					return null;
			}
		}

		return "No cells found with no possibilities";
	}
}
