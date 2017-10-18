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
    	if (Fillapix.s_checkValidBoardState(state))
    		return "Contradiction does not apply, Fillapix is valid";
    	else
    		return null;
    }
}