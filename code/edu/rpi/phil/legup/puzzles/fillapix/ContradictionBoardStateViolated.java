package edu.rpi.phil.legup.puzzles.fillapix;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionBoardStateViolated extends Contradiction
{
    private static final long serialVersionUID = 855439484L;

	 ContradictionBoardStateViolated()
	 {
		setName("Board State Violated");
		description = "There exists a pixel number that cannot be satisfied";
		image = new ImageIcon("images/fillapix/BoardStateViolated.png");
	 }

	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    protected String checkContradictionRaw(BoardState state)
    {
    	if (Fillapix.s_checkValidBoardState(state))
    		return "Contradiction does not apply, Fillapix is valid";
    	else
    		return null;
    }
}