/**
 *  PuzzleRule.java
 **/

package edu.rpi.phil.legup;

import java.awt.Point;

/**
 * Handles functionality for a Puzzle Rule.  This is a generic rule that should be
 * extended by any puzzle-specific rule.
 *
 * @author Drew Housten
 * @version 1.0
 */
public abstract class PuzzleRule extends Justification
{
	private static final long serialVersionUID = 640025267L;

	protected RuleApplication canApplyAt(BoardState state, Point at)
	{
		return new RuleApplication(at); //Default invalid rule
	}

	/**
	 * Checks if the rule was applied correctly
	 *
	 * @param state The board state we're checking
	 * @return null if the rule was applied correctly, the error String otherwise
	 */
	public final String checkRule(BoardState state)
	{
		BoardState parent = state.getSingleParentState();

		if (parent == null)
		{
			return "You can not apply a rule to the initial board state.";
		}

		return checkRuleRaw(state);
	}

	protected String checkRuleRaw(BoardState state)
	{
		String error = null;
		String lasterror = null;
    	boolean changed = false;
    	BoardState origBoardState = state.getSingleParentState();
    	int width = state.getWidth();
    	int height = state.getHeight();
    	int errorcount = 0;

    	// Check for only one branch
		if (state.getTransitionsTo().size() != 1)
		{
			return "Basic rules require a single branch only!";
		}

		//Loop through the board and check the rule at each cell
		for (int y = 0; y < height && error == null; ++y)
		{
			for (int x = 0; x < width; ++x)
			{
				int origState = origBoardState.getCellContents(x,y);
				int newState = state.getCellContents(x,y);

				if (origState != newState)
				{
					changed = true;

					error = checkRuleAt(state, origBoardState, new Point(x,y));
					if(error != null)
					{
						lasterror = error;
						++errorcount;
						error = null;
					}
				}
			}
		}

		if (!changed)
		{
			return "The board must be changed to apply a rule!";
		}

		//If there is an error, tell them how many
		if(lasterror != null)
		{
			return lasterror + " (" + Integer.toString(errorcount) + " error" + ((errorcount == 1) ? "" : "s") + " total)";
		}

		return null; //Null = no errors
	}

	protected final String checkRuleAt(BoardState destBoardState, BoardState origBoardState, Point at)
	{
		if(destBoardState.getCellContents(at.x, at.y) == origBoardState.getCellContents(at.x, at.y))
			return null;
		RuleApplication application = canApplyAt(origBoardState, at);
		if(application.isValid && !application.isParent)
		{
			if(origBoardState.getCellContents(at.x, at.y) == PuzzleModule.CELL_UNKNOWN && destBoardState.getCellContents(at.x, at.y) == application.newValue)
			{
				return null;
			}
		}
		return getErrorReasonAt(destBoardState, origBoardState, at, application);
	}

	protected String getErrorReasonAt(BoardState destBoardState, BoardState origBoardState, Point at, RuleApplication application)
	{
		return "You have made an error.";
	}

	public final boolean doDefaultApplication(BoardState state)
	{
		if(state.getSingleParentState() == null)
		{
			return false;
		}

		boolean rv = doDefaultApplicationRaw(state);

		if (rv)
		{
			state.boardDataChanged();
		}

		return rv;
	}

	/**
	 * Apply the default application of this rule
	 * @param state the board we're using
	 * @param pm the puzzle module we're using
	 * @return true iff we have applied a rule correctly
	 */
	protected boolean doDefaultApplicationRaw(BoardState state)
	{
		BoardState origBoardState = state.getSingleParentState();
    	boolean changed = false;
    	int width = state.getWidth();
    	int height = state.getHeight();
    	int cellvalue = 0;
    	boolean[][] litup = new boolean[origBoardState.getHeight()][origBoardState.getWidth()];

    	if (origBoardState != null && state.getTransitionsTo().size() == 1)
    	{
    		for (int y = 0; y < origBoardState.getHeight(); ++y)
			{
				for (int x = 0; x < origBoardState.getWidth(); ++x)
				{
					doApplicationAt(state, origBoardState, new Point(x,y));
				}
			}

	    	String error = checkRuleRaw(state);

			if (error == null)
			{
				changed = true;
				// valid change
			}
    	}

    	if(!changed)
    	{
    		state = origBoardState.copy();
    	}

    	Legup.getInstance().getPuzzleModule().updateState(state);

	    return changed;
	}

	/**
	 *
	 * @param state
	 * @param pm
	 * @param at
	 * @return
	 */
	protected boolean doApplicationAt(BoardState destBoardState, BoardState origBoardState, Point at)
	{
		RuleApplication application = canApplyAt(origBoardState, at);
		if(application.isValid && !application.isParent)
		{
			int x = application.location.x;
			int y = application.location.y;
			if(destBoardState.getCellContents(x, y) == PuzzleModule.CELL_UNKNOWN && origBoardState.getCellContents(x, y) == PuzzleModule.CELL_UNKNOWN)
			{
				destBoardState.setCellContents(x, y, application.newValue);
				return true;
			}
		}
		return false;
	}
}
