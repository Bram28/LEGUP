package edu.rpi.phil.legup;

/**
 * This class represents a selection in the tree view
 *
 * Currently it is either a board state, or a transition, which is stored as the parent board state
 * and applies to all states the emerge from it
 * @author Stan
 *
 */
public class Selection
{
	private BoardState state = null;
	private boolean transition = false;

	/**
	 * Create a new Selection
	 * @param s the BoardState of the state, or parent state of the transition
	 * @param trans true iff this is a transition
	 */
	public Selection(BoardState s, boolean trans)
	{
		state = s;
		transition = trans;
	}

	/**
	 * Retrieves the associated BoardState
	 * @return BoardState of the state, or parent state of the transition
	 */
	public BoardState getState()
	{
		return state;
	}

	/**
	 * Determines if the Selection is a state or a transition
	 * @return True is the Selection is a state, false if it is a transition
	 */
	public boolean isState()
	{
		return true;//!transition;
	}

	/**
	 * Determines if the selection is a state or a transition
	 * @return True is the selection is a state, false if it is a transition
	 */
	public boolean isTransition()
	{
		return false;//transition;
	}

	public boolean equals(Object otherObj)
	{
		if (otherObj instanceof Selection)
		{
			Selection other = (Selection) otherObj;

			return (other.state == state && other.transition == transition);
		}

		return false;
	}
}
