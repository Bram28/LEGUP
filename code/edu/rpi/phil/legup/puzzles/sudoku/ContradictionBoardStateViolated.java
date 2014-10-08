package edu.rpi.phil.legup.puzzles.sudoku;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.Contradiction;

public class ContradictionBoardStateViolated extends Contradiction
{
    private static final long serialVersionUID = 500264282L;

	 ContradictionBoardStateViolated()
	 {
		setName("Board State Violated");
		description = "Two identical numbers are placed in the same group, which is illegal";
		image = new ImageIcon("images/sudoku/BoardStateViolated.png");
	 }

	 /**
     * Checks if the contradiction was applied correctly to this board state
     *
     * @param state The board state
     * @return null if the contradiction was applied correctly, the error String otherwise
     */
    protected String checkContradictionRaw(BoardState state)
    {
    	if (Sudoku.s_checkValidBoardState(state))
    		return "Contradiction does not apply, Sudoku is valid";
    	else
    		return null;
    }
}
