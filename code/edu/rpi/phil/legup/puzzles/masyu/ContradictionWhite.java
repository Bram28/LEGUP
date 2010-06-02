package edu.rpi.phil.legup.puzzles.masyu;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionWhite extends Contradiction {
	
	public ContradictionWhite()
	{
		name = "White Rule";
		description = "Straight through white, must bend on one side.";
		image = new ImageIcon("images/masyu/Rules/ContradictionWhite.png");
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
				if(Masyu.isWhite(cell))
				{
					//check to see which type
					/*if(Masyu.hasEast(cell) || Masyu.hasWest(cell))
					{
						if(Masyu.hasNorth(cell) || Masyu.hasSouth(cell))
							return null;
						BoardAccessor ba = new BoardAccessor(null, state, BoardAccessor.NORTH, x, y);
						int left = ba.getDestCell(-1, 0);
						int right = ba.getDestCell(1, 0);
						if(left == -1 || right == -1)
							return null;
						if(Masyu.hasWest(left) && Masyu.hasEast(right))
							return null;
					}
					if(Masyu.hasNorth(cell) || Masyu.hasSouth(cell))
					{
						BoardAccessor ba = new BoardAccessor(null, state, BoardAccessor.NORTH, x, y);
						int up = ba.getDestCell(0, -1);
						int down = ba.getDestCell(0, 1);
						if(up == -1 || down == -1)
							return null;
						if(Masyu.hasNorth(up) && Masyu.hasSouth(down))
							return null;
					}*/
				}
			}
		}
		
		return "No white cells which are invalid";
	}
}
