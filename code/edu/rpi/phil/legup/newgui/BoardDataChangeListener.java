package edu.rpi.phil.legup.newgui;

import edu.rpi.phil.legup.BoardState;

/**
 * Change in board data, such as a cell value, or extra data change
 */
public interface BoardDataChangeListener
{
	/**
	 * The board state data has been changed
	 * @param state The updated board state
	 */
	public void boardDataChanged(BoardState state);
}
