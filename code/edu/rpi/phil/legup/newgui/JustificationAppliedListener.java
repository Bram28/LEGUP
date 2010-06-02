package edu.rpi.phil.legup.newgui;

import edu.rpi.phil.legup.BoardState;

/**
 * Justification(rule, case rule, contradiction) applied
 *
 */
public interface JustificationAppliedListener
{
	/**
	 * A justification has been applied
	 * @param state The current board state
	 * @param j The justification that was applied
	 */
	public void justificationApplied(BoardState state, Object j);
}
