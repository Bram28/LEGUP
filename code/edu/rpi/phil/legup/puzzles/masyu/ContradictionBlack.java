package edu.rpi.phil.legup.puzzles.masyu;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionBlack extends Contradiction {
	
	public ContradictionBlack()
	{
		name = "Black Not Bent";
		description = "Black must bend.";
		image = new ImageIcon("images/masyu/Rules/ContradictionBlack.png");
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
		
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				int cell = state.getCellContents(x, y);
				if(Masyu.isBlack(cell))
				{
					if(Masyu.hasNorth(cell) && Masyu.hasSouth(cell))
						return null;
					if(Masyu.hasEast(cell) && Masyu.hasWest(cell))
						return null;
					BoardAccessor ba = new BoardAccessor(state,null,BoardAccessor.NORTH,x,y);
					for(int i = 0; i < 4; i++)
					{
						int one = ba.getDestCell(0,1);
						if(ba.hasDir(cell, BoardAccessor.NORTH) || 
								ba.hasDir(one,BoardAccessor.SOUTH))
						{
							//check for turns...
							if(ba.hasDir(one, BoardAccessor.EAST) || ba.hasDir(one, BoardAccessor.WEST))
								return null;
							int two = ba.getDestCell(0, 2);
							if(!ba.validConnection(two, BoardAccessor.SOUTH))
								return null;
						}
						
					}
				}
			}
		}
		
		return "All black cells are valid";
	}
}
