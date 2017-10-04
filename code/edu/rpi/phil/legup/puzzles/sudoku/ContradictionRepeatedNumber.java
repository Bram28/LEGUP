package edu.rpi.phil.legup.puzzles.sudoku;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionRepeatedNumber extends Contradiction
{
    private static final long serialVersionUID = 500264282L;

	 ContradictionRepeatedNumber()
	 {
		setName("Repeated Number");
		description = "Two identical numbers are placed in the same group.";
		image = new ImageIcon("images/sudoku/RepeatedNumber.png");
	 }
		
	public String getImageName()
	{
		return "images/sudoku/RepeatedNumber.png";
	}


	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    public String checkContradictionRaw(BoardState state)
    {
    	if (Sudoku.s_checkValidBoardState(state))
    		return "Contradiction does not apply, Sudoku is valid";
    	else
    		return null;
    }
}
