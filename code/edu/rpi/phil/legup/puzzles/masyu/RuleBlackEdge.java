package edu.rpi.phil.legup.puzzles.masyu;

import javax.swing.ImageIcon;

import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.PuzzleRule;
public class RuleBlackEdge extends PuzzleRule {
	private static final long serialVersionUID = 120937947L;
	
    public String getImageName()
    {
    	return "images/masyu/Rules/RuleBlackEdge.png";
    }

	
	/**
	 * Rule to make a path continuous between different cells.
	 */
	RuleBlackEdge() {
		setName("Black Turn");
		description = "Black cells must turn.";
		image = new ImageIcon("images/masyu/Rules/RuleBlackEdge.png");
	}

	/**
	 * Checks to see if the rule was correctly applied For this rule, for each
	 * added there must be a line on the other side
	 * 
	 * @param state
	 *			The board state
	 * @return null if the contradiction was applied correctly, the error String
	 *		 otherwise
	 */
	protected String checkRuleRaw(BoardState destBoardState)
	{
		String error = null;
		boolean changed = false;
		BoardState origBoardState = destBoardState.getSingleParentState();
		
		return error;
	}


	/**
	 * Tries to apply the rule everywhere and returns true if it can do so.
	 * 
	 * @author Bryan
	 * @param destBoardState
	 *			the board to work with
	 * @param pm
	 *			the puzzle module
	 * @see edu.rpi.phil.legup.PuzzleRule#doDefaultApplicationRaw(edu.rpi.phil.legup.BoardState,
	 *	  edu.rpi.phil.legup.PuzzleModule)
	 */
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();

		return changed;
	}
}

