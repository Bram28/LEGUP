package edu.rpi.phil.legup.puzzles.fillapix;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionTooManyBlackCells extends Contradiction
{
    private static final long serialVersionUID = 855439484L;
	
    ContradictionTooManyBlackCells()
	{
		setName("Too Many Black Cells");
		description = "There may not be more black cells than the number.";
		image = new ImageIcon("images/fillapix/contradictions/TooManyBlackCells.png");
	}

	 public String getImageName()
	{
		return "images/fillapix/contradictions/TooManyBlackCells.png";
	}

	/**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    public String checkContradictionRaw(BoardState state)
    {
		String error = "";
		int height = state.getHeight();
		int width = state.getWidth();
		int cellvalue = 0;
		int blackCells = 0;

		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				cellvalue = state.getCellContents(x,y);
				// cell with a clue
				if(Fillapix.hasClue(cellvalue))
				{
					blackCells = 0;
					for (int i = -1; i < 2; ++i) {
						for (int j = -1; j < 2; ++j) {
							int xpos = x + i;
							int ypos = y + j;
							if (Fillapix.inBounds(width, height, xpos, ypos)) {
								if (Fillapix.isBlack(cellvalue)) {
									blackCells += 1;
								}
							}
						}
					}

					if (blackCells > (cellvalue%10)) {
						return null;
					}
				}
			}
		}

		error = "No block with too many black cells exists.";
		return error;
	}
}