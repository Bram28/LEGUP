package edu.rpi.phil.legup.puzzles.masyu;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionOnly2 extends Contradiction {
	
	public ContradictionOnly2()
	{
		name = "Only Once Through A Cell";
		description = "Only one path can exist through a cell.";
		image = new ImageIcon("images/masyu/Rules/ContradictionOnly2.png");
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

		for (int y=0;y<height;y++)
		{
			for (int x=0;x<width;x++)
			{
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
				if(walls > 2)
					return null;
			}
		}

		return "No cells with more than 2 walls";
	}
}
