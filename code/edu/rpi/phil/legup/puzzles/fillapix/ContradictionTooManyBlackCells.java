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
		String error = null;
		int height = state.getHeight();
		int width = state.getWidth();
		int cellvalue = 0;
		int blackCells = 0;

		for(int x = 0; x < width; ++x)
		{
			for(int y = 0; y < height; ++y)
			{
				cellvalue = state.getCellContents(x,y);
				if(cellvalue > 0)
				{
					blackCells = 0;
					if(x > 0) // left
						if(Fillapix.isBlack(state.getCellContents(x-1, y)))
							++blackCells;
					if(x < width - 1) // right
						if(Fillapix.isBlack(state.getCellContents(x+1, y)))
							++blackCells;
					if(y > 0) // above
						if(Fillapix.isBlack(state.getCellContents(x, y-1)))
							++blackCells;
					if(y < height - 1) // below
						if(Fillapix.isBlack(state.getCellContents(x, y+1)))
							++blackCells;
					if(x > 0 && y > 0) // upper left diagonal
						if(Fillapix.isBlack(state.getCellContents(x-1, y-1)))
							++blackCells;
					if(x > 0 && y < height - 1) // bottom left diagonal
						if(Fillapix.isBlack(state.getCellContents(x-1, y+1)))
							++blackCells;
					if(x < width - 1 && y > 0) // upper right diagonal
						if(Fillapix.isBlack(state.getCellContents(x+1, y-1)))
							++blackCells;
					if(x < width - 1 && y < height - 1) // bottom right diagonal
						if(Fillapix.isBlack(state.getCellContents(x+1, y+1)))
							++blackCells;
					if(Fillapix.isBlack(state.getCellContents(x, y)))
							++blackCells;

					if(blackCells > cellvalue)
						return null;
				}
			}
		}

		error = "No block with too many black cells exists.";
		return error;
	}
}