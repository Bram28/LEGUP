package edu.rpi.phil.legup;

/**
 *	Updated by Daniel Ploch 09/30/2008
 *	Constructors added for storage of parent PuzzleModule
 */
public abstract class Contradiction extends Justification
{
	private static final long serialVersionUID = 144549884L;

	/**
	 * this is how you check for contradictions using a boardstate
	 * This calls the protected method checkContradiction after
	 * making sure nothing on the board was changed and we're not in the initial step
	 *
	 * @param state the board state we're checking
	 * @return null if the contradiction was applied correctly, the error String otherwise
	 */
	public final String checkContradiction(BoardState state)
	{
		BoardState parent = state.getSingleParentState();

		if (parent == null)
		{
			return "You can not apply a contradiction to the initial board state.";
		}

		int w = state.getWidth();
		int h = state.getHeight();

		for (int y = 0; y < h; ++y)
		{
			for (int x = 0; x < w; ++x)
			{
				int contents = state.getCellContents(x, y);
				int p_contents = parent.getCellContents(x, y);

				if (contents != p_contents)
				{
					return "You can not change the board and apply a contradiction.";
				}
			}
		}

		return checkContradictionRaw(state);
	}

	/**
	 * Checks if the contradiction was applied correctly to this board state
	 *
	 * @param state The board state
	 * @return null if the contradiction was applied correctly, the error String otherwise
	 */
	protected abstract String checkContradictionRaw(BoardState state);

}
